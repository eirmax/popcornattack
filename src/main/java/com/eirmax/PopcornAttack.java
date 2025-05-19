package com.eirmax;

import com.eirmax.Items.ModItems;
import com.eirmax.sounds.ModSounds;
import com.eirmax.network.ModNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopcornAttack implements ModInitializer {
	public static final String MOD_ID = "popcornattack";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModSounds.registerSounds();
		ModNetworking.registerPayloads();      // Сначала регистрируем payload-ы!
		ModNetworking.registerServerReceivers();
	}
}