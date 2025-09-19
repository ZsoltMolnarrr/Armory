package net.armory.fabric;

import net.armory_rpgs.ArmoryMod;
import net.fabricmc.api.ModInitializer;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ArmoryMod.init();
    }
}
