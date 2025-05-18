package com.eirmax.Items;


import com.eirmax.sounds.ModSounds;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class PopcornItem extends Item {
    private static final int COOLDOWN_TICKS = 20 * 1;
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
        ItemStack stack = player.getStackInHand(hand);

        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        player.playSound(ModSounds.POPCORN_EAT, 0.8f, 1.0f);
        player.setCurrentHand(hand);
        player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
        return TypedActionResult.success(stack, world.isClient);
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
                world.playSound(player, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 0.6f, 1);
            } else {
                player.playSound(sound, 0.6f, 1.0f);
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
//    @Override  НЕ МОГУ ОТКЛЮЧИТЬ ЗВУК, возмоно не правильно возвращаю null
//    public SoundEvent getEatSound() {
//        return null;
//    }
}