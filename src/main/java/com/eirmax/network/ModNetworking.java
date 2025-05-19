package com.eirmax.network;

import com.eirmax.PopcornAttack;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModNetworking {
    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(PopcornKeyPressedPayload.ID, PopcornKeyPressedPayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(
                PopcornKeyPressedPayload.ID,
                (payload, context) -> {
                    context.player().getServer().execute(() -> {
                        PopcornAttack.LOGGER.info("Игрок " + context.player().getName().getString() + " нажал кнопку броска попкорна!");
                    });
                }
        );
    }
}