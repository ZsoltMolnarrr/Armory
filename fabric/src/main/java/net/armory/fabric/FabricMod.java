package net.armory.fabric;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.Group;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ArmoryMod.init();
        ArmoryMod.registerSounds();

        // Create and register item group (Fabric-specific)
        Group.GROUP = FabricItemGroup.builder()
                .icon(Group.ICON)
                .title(Component.translatable(Group.translationKey))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Group.KEY, Group.GROUP);

        ArmoryMod.registerItems();
        ArmoryMod.registerEffects();
    }
}
