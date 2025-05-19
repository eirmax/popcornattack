package com.eirmax.network;

import com.eirmax.Items.PopcornItem;
import com.eirmax.PopcornAttack;
import com.eirmax.PopcornConfig;
import com.eirmax.sounds.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class ModNetworking {

    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(PopcornKeyPressedPayload.ID, PopcornKeyPressedPayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(
                PopcornKeyPressedPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    if (player.getWorld() instanceof ServerWorld serverWorld) {
                        int COOLDOWN_TICKS = PopcornConfig.INSTANCE.cooldownTicks;
                        Vec3d look = player.getRotationVector();
                        Vec3d startPos = player.getEyePos();
                        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
                        if (!player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                            System.out.println("Player");
                            player.getItemCooldownManager().set(stack.getItem(), COOLDOWN_TICKS);
                            System.out.println("Cooldown: " + COOLDOWN_TICKS);
                            stack.damage(1, player, EquipmentSlot.MAINHAND);

                            player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.POPCORN_EAT, SoundCategory.PLAYERS, 0.6f, 1);
                            player.playSound(ModSounds.POPCORN_EAT, 0.8f, 1.0f); // разобраться со звуком для бросающего что-то со стороны сервера попробовать поднять сервер

                            PopcornProjectilesManager.add(new PopcornProjectile(serverWorld, player, startPos, look));

                        }

//                        PopcornItem.AfterUse(player.getWorld(), player);
                        // Все параметры берутся из PopcornConfig, ничего не надо менять здесь!

                    }
                }
        );
    }
}