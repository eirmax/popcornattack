package com.eirmax.events;

import com.eirmax.Items.PopcornItem;
import com.eirmax.network.PopcornKeyPressedPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import static com.eirmax.keybinds.KeyBinds.throwPopcornKey;

public class KeyBindThrowPopcorn {
    public static void Init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (throwPopcornKey.wasPressed()) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    ItemStack stack = player.getMainHandStack();
                    if (stack.getItem() instanceof PopcornItem) {
                        PopcornItem popcornItem = (PopcornItem) stack.getItem();
                        stack.use(client.world, player, Hand.MAIN_HAND);
                        ClientPlayNetworking.send(new PopcornKeyPressedPayload());
                    }
                }
            }
        });
    }
}
