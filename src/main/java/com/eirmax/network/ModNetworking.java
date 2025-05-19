package com.eirmax.network;

import com.eirmax.PopcornAttack;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

                        Vec3d look = player.getRotationVector();
                        Vec3d startPos = player.getEyePos();

                        PopcornProjectilesManager.add(new PopcornProjectile(serverWorld, player, startPos, look));
                    }
                }
        );
    }
}