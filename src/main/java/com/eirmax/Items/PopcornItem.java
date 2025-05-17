package com.eirmax.Items;

import com.eirmax.sounds.ModSounds;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class PopcornItem extends Item {
    private static final int COOLDOWN_TICKS = 20;
    private static final Random RANDOM = new Random();
    private static final SoundEvent[] EAT_SOUNDS = {
            ModSounds.POPCORN_ONE,
            ModSounds.POPCORN_TWO,
            ModSounds.POPCORN_THREE,
            ModSounds.POPCORN_FOUR,
            ModSounds.POPCORN_FIVE
    };

    public PopcornItem(Settings settings) {
        super(settings
                .maxCount(1).maxDamage(20)
                .food(new FoodComponent.Builder()
                        .nutrition(1)
                        .saturationModifier(0.1F)
                        .alwaysEdible()
                        .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            if (player.getItemCooldownManager().isCoolingDown(this)) {
                return stack;
            }
            SoundEvent sound = EAT_SOUNDS[RANDOM.nextInt(EAT_SOUNDS.length)];
            if (!world.isClient) {
                player.getHungerManager().add(1, 0.1F);
                playEatSoundInWorld(world, player.getPos(), sound);
            }
            if (world.isClient) {
                player.playSound(sound, 1.0f, 1.0f);
                for (int i = 0; i < 10; i++) {
                    double dx = user.getX() + (RANDOM.nextDouble() - 0.5) * 0.5;
                    double dy = user.getY() + user.getStandingEyeHeight() + (RANDOM.nextDouble() - 0.5) * 0.2;
                    double dz = user.getZ() + (RANDOM.nextDouble() - 0.5) * 0.5;
                    world.addParticle(ParticleTypes.POOF, dx, dy, dz, 0, 0.05, 0);
                }
            }
            player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
            stack.damage(1, player, EquipmentSlot.MAINHAND);
        }
        return stack;
    }

    private void playEatSoundInWorld(World world, Vec3d eaterPos, SoundEvent sound) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        double radius = 16.0;
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            double distance = player.getPos().distanceTo(eaterPos);
            System.out.println("Pos: " + distance);
            if (distance <= radius) {
                // Линейное затухание: 1.0f рядом, 0.0f на границе радиуса
                float volume = (float) (Math.max(0.0, 1.0 - (distance / radius)) * 0.15);
                System.out.println("Volume: " + volume);
                serverWorld.playSound(
                        null,
                        eaterPos.x, eaterPos.y, eaterPos.z,
                        sound,
                        SoundCategory.PLAYERS,
                        volume,
                        1.0f
                );
            }
        }
    }
}