package com.eirmax.Items;

import com.eirmax.sounds.ModSounds;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class PopcornItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 15;
    private static final Random RANDOM = new Random();
    private static final SoundEvent THROW_SOUND = SoundEvents.ENTITY_SNOWBALL_THROW;
    private static final SoundEvent[] EAT_SOUNDS = {
            ModSounds.POPCORN_ONE,
            ModSounds.POPCORN_TWO,
            ModSounds.POPCORN_THREE,
            ModSounds.POPCORN_FOUR,
            ModSounds.POPCORN_FIVE
    };

    public PopcornItem(Settings settings) {
        super(settings
                .maxCount(1)
                .food(new FoodComponent.Builder()
                        .nutrition(1)
                        .saturationModifier(0.1F)
                        .alwaysEdible()
                        .build()));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }
        if (!world.isClient) {
            SoundEvent sound = EAT_SOUNDS[RANDOM.nextInt(EAT_SOUNDS.length)];
            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    sound,
                    SoundCategory.RECORDS,
                    10.0F,
                    1.0F
            );
        }
        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }
    public void firePopcornStream(World world, PlayerEntity player) {
        double maxDistance = 8 + RANDOM.nextDouble() * 7;
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d start = player.getCameraPosVec(1.0F);

        if (!world.isClient) {
            for (double d = 0; d < maxDistance; d += 0.3) {
                Vec3d pos = start.add(lookVec.multiply(d));
                world.addParticle(ParticleTypes.POOF, pos.x, pos.y, pos.z, 0, 0, 0);
            }
        }

        if (!world.isClient) {
            LivingEntity hit = raycastEntity(world, player, maxDistance);
            if (hit != null) {
                DamageSource damageSource = player.getDamageSources().generic();
                hit.damage(damageSource, 1.0F);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), THROW_SOUND, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }
    public static LivingEntity raycastEntity(World world, PlayerEntity player, double maxDistance) {
        Vec3d start = player.getCameraPosVec(1.0F);
        Vec3d look = player.getRotationVec(1.0F);
        Vec3d end = start.add(look.multiply(maxDistance));

        LivingEntity closest = null;
        double closestDist = maxDistance * maxDistance;

        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                player.getBoundingBox().stretch(look.multiply(maxDistance)).expand(1.0D),
                e -> e != player
        );

        for (LivingEntity entity : entities) {
            Box box = entity.getBoundingBox().expand(0.3D);
            Vec3d intersection = rayIntersectsBox(start, end, box);
            if (intersection != null) {
                double dist = start.squaredDistanceTo(intersection);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
            }
        }
        return closest;
    }
    public static Vec3d rayIntersectsBox(Vec3d start, Vec3d end, Box box) {
        double tmin = (box.minX - start.x) / (end.x - start.x);
        double tmax = (box.maxX - start.x) / (end.x - start.x);
        if (tmin > tmax) { double tmp = tmin; tmin = tmax; tmax = tmp; }

        double tymin = (box.minY - start.y) / (end.y - start.y);
        double tymax = (box.maxY - start.y) / (end.y - start.y);
        if (tymin > tymax) { double tmp = tymin; tymin = tymax; tymax = tmp; }

        if ((tmin > tymax) || (tymin > tmax))
            return null;

        if (tymin > tmin)
            tmin = tymin;
        if (tymax < tmax)
            tmax = tymax;

        double tzmin = (box.minZ - start.z) / (end.z - start.z);
        double tzmax = (box.maxZ - start.z) / (end.z - start.z);
        if (tzmin > tzmax) { double tmp = tzmin; tzmin = tzmax; tzmax = tmp; }

        if ((tmin > tzmax) || (tzmin > tmax))
            return null;

        if (tzmin > tmin)
            tmin = tzmin;
        if (tzmax < tmax)
            tmax = tzmax;

        if (tmin < 0 || tmin > 1) return null;

        return start.add(end.subtract(start).multiply(tmin));
    }


    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;
            if (player.getItemCooldownManager().isCoolingDown(this)) {
                return stack;
            }

            if (!world.isClient) {
                player.getHungerManager().add(1, 0.1F);
                SoundEvent sound = EAT_SOUNDS[RANDOM.nextInt(EAT_SOUNDS.length)];
                System.out.println("Playing sound: " + sound);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }


            if (world.isClient) {
                for (int i = 0; i < 10; i++) {
                    double dx = user.getX() + (RANDOM.nextDouble() - 0.5) * 0.5;
                    double dy = user.getY() + user.getStandingEyeHeight() + (RANDOM.nextDouble() - 0.5) * 0.2;
                    double dz = user.getZ() + (RANDOM.nextDouble() - 0.5) * 0.5;
                    world.addParticle(ParticleTypes.POOF, dx, dy, dz, 0, 0.05, 0);
                }
            }

            player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
        }
        return stack;
    }
    @Override
    public SoundEvent getEatSound() {
        return EAT_SOUNDS[RANDOM.nextInt(EAT_SOUNDS.length)];
    }
}