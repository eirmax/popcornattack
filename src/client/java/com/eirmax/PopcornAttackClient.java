package com.eirmax;

import com.eirmax.events.KeyBindThrowPopcorn;
import com.eirmax.keybinds.KeyBinds;
import net.fabricmc.api.ClientModInitializer;

public class PopcornAttackClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyBindThrowPopcorn.Init();
        KeyBinds.registerKeyBinds();
    }
}