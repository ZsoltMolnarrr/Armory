package net.armory.neoforge;

import net.armory_rpgs.ArmoryMod;
import net.neoforged.fml.common.Mod;

import net.armory.ExampleMod;

@Mod(ArmoryMod.NAMESPACE)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
