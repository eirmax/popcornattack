package com.eirmax.sounds;

import com.eirmax.PopcornAttack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent POPCORN_ONE = registerSoundEvent("popcorn_one");
    public static final SoundEvent POPCORN_TWO = registerSoundEvent("popcorn_two");
    public static final SoundEvent POPCORN_THREE = registerSoundEvent("popcorn_three");
    public static final SoundEvent POPCORN_FOUR = registerSoundEvent("popcorn_four");
    public static final SoundEvent POPCORN_FIVE = registerSoundEvent("popcorn_five");
    public static final SoundEvent SODA_ONE = registerSoundEvent("soda_one");
    public static final SoundEvent SODA_TWO = registerSoundEvent("soda_two");
    public static final SoundEvent SODA_THREE = registerSoundEvent("soda_three");
    public static final SoundEvent SODA_FOUR = registerSoundEvent("soda_four");
    public static final SoundEvent SODA_FIVE = registerSoundEvent("soda_five");
    public static final SoundEvent POPCORN_EAT = registerSoundEvent("popcorn_eat");
    public static final SoundEvent SILENT = registerSoundEvent("silent");


    private static SoundEvent registerSoundEvent(String name) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(PopcornAttack.MOD_ID, name),
                SoundEvent.of(Identifier.of(PopcornAttack.MOD_ID, name)));
    }

    public static void registerSounds() {
        PopcornAttack.LOGGER.info("Registering Mod Sounds for " + PopcornAttack.MOD_ID);
    }
}