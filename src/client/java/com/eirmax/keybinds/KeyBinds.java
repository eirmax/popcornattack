package com.eirmax.keybinds;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
public class KeyBinds {
    public static KeyBinding throwPopcornKey;
    public static void registerKeyBinds() {
        throwPopcornKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.popcornattack.throw_popcorn",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "category.popcornattack"
        ));
    }
}