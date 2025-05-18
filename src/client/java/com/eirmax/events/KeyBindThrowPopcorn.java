package com.eirmax.events;

import com.eirmax.Items.PopcornItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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
                        stack.use(client.world, player, Hand.MAIN_HAND);
                    }
                }
            }
        });
    }
}
