package com.eirmax.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PopcornKeyPressedPayload() implements CustomPayload {
    public static final Id<PopcornKeyPressedPayload> ID = new Id<>(Identifier.of("popcornattack", "popcorn_key_pressed"));

    public static final PacketCodec<PacketByteBuf, PopcornKeyPressedPayload> CODEC =
            PacketCodec.unit(new PopcornKeyPressedPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}