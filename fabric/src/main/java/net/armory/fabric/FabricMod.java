package net.armory.fabric;

import net.armory_rpgs.ArmoryMod;
import net.fabricmc.api.ModInitializer;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ArmoryMod.init();
        ArmoryMod.registerSounds();
        // Registers the creative tab too, and wires its contents through SpellEngine's
        // loader-neutral `PlatformEvents.onItemGroupModify`.
        ArmoryMod.registerItems();
        ArmoryMod.registerEffects();
    }
}
