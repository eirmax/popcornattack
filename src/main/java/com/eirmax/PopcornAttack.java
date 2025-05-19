package com.eirmax;

import com.eirmax.Items.ModItems;
import com.eirmax.sounds.ModSounds;
import com.eirmax.network.ModNetworking;
import com.eirmax.network.PopcornProjectilesManager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopcornAttack implements ModInitializer {
	public static final String MOD_ID = "popcornattack";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PopcornConfig.load();
		ModItems.registerModItems();
		ModSounds.registerSounds();
		ModNetworking.registerPayloads();
		ModNetworking.registerServerReceivers();
		PopcornProjectilesManager.registerTickHandler();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			PopcornCommand.register(dispatcher);
		});
	}
}