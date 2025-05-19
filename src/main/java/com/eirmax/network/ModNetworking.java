package com.eirmax.network;

import com.eirmax.PopcornConfig;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

//                            player.getWorld().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.6f, 1);
//                            player.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 0.8f, 1.0f); // разобраться со звуком для бросающего что-то со стороны сервера попробовать поднять сервер
                            player.getWorld().playSound(
                                    null, // <-- чтобы услышали все, включая самого игрока!
                                    player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.ENTITY_SNOWBALL_THROW,
                                    SoundCategory.PLAYERS,
                                    0.8f, // громкость
                                    1.0f  // тон
                            );
                            System.out.println("Player: " + player.getName());
                            PopcornProjectilesManager.add(new PopcornProjectile(serverWorld, player, startPos, look));
                        }
                    }
                }
        );
    }
}