package com.eirmax.Items;


import com.eirmax.PopcornConfig;
import com.eirmax.sounds.ModSounds;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Random;
import java.util.List;

public class PopcornItem extends Item {
    //private static final int COOLDOWN_TICKS = PopcornConfig.INSTANCE.cooldownTicks;
    private static final Random RANDOM = new Random();
    private static final SoundEvent[] EAT_SOUNDS = {
            ModSounds.POPCORN_ONE,
            ModSounds.POPCORN_TWO,
            ModSounds.POPCORN_THREE,
            ModSounds.POPCORN_FOUR,
            ModSounds.POPCORN_FIVE,
    };

    public PopcornItem(Settings settings) {
        super(settings
                .maxCount(1)
                .maxDamage(20)
                .food(new FoodComponent
                        .Builder()
                        .nutrition(1)
                        .saturationModifier(0.1F)
                        .alwaysEdible()
                        .build()));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        int COOLDOWN_TICKS = PopcornConfig.INSTANCE.cooldownTicks;
        ItemStack stack = player.getStackInHand(hand);

//        if (player.getItemCooldownManager().isCoolingDown(this)) {
//            return TypedActionResult.fail(stack);
//        }
        player.playSound(ModSounds.POPCORN_EAT, 0.8f, 1.0f);
        player.setCurrentHand(hand);
        //player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
//        System.out.println("Cooldown: " + COOLDOWN_TICKS);
        return TypedActionResult.success(stack, world.isClient);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        int COOLDOWN_TICKS = PopcornConfig.INSTANCE.cooldownTicks;
        if (user instanceof PlayerEntity player) {
            if (player.getItemCooldownManager().isCoolingDown(this)) {
                return stack;//-------------------------------------------------------------
            }
            SoundEvent sound = EAT_SOUNDS[RANDOM.nextInt(EAT_SOUNDS.length)];
            if (!world.isClient) {
                player.getHungerManager().add(1, 0.1F);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 0.6f, 1);

                if (world instanceof ServerWorld serverWorld) {
                    for (int i = 0; i < 10; i++) {
                        double dx = user.getX() + (RANDOM.nextDouble() - 0.5) * 0.5;
                        double dy = user.getY() + user.getStandingEyeHeight() + (RANDOM.nextDouble() - 0.5) * 0.2;
                        double dz = user.getZ() + (RANDOM.nextDouble() - 0.5) * 0.5;
                        serverWorld.spawnParticles(ParticleTypes.POOF, dx, dy, dz, 1, 0, 0.05, 0, 0);
                    }
                }
            } else {
                player.playSound(sound, 0.6f, 1.0f);
            }
            player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
            System.out.println("Cooldown: " + COOLDOWN_TICKS);
            stack.damage(1, player, EquipmentSlot.MAINHAND);
        }
        return stack;
    }

    @Override
    public SoundEvent getEatSound() {
        return ModSounds.SILENT;
    }
}