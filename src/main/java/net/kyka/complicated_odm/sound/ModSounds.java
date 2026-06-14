package net.kyka.complicated_odm.sound;

import net.kyka.complicated_odm.Complicated_odm;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent GAS_HISS = register("gas_hiss");
    public static final SoundEvent FILLED = register("filled");
    public static final SoundEvent BOOM = register("boom");

    private static SoundEvent register(String id) {
        Identifier identifier = Identifier.of(Complicated_odm.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void registerSounds() {
        Complicated_odm.LOGGER.info("Registering sounds for " + Complicated_odm.MOD_ID);
    }
}