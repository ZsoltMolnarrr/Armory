package net.armory.fabric;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.Group;
import net.armory_rpgs.item.SmithingIngredients;
import net.armory_rpgs.item.SmithingTemplates;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ArmoryMod.init();
        ArmoryMod.registerSounds();

        // Create and register item group (Fabric-specific)
        Group.GROUP = FabricItemGroup.builder()
                .icon(Group.ICON)
                .displayName(Text.translatable(Group.translationKey))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.GROUP);

        ArmoryMod.registerItems();
        ArmoryMod.registerEffects();

        // Smithing templates + ingredients into the Armory creative tab — Fabric API.
        // (Armor sets are placed by SpellEngine's own loader-neutral Armor.register(..., Group.KEY).)
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry : SmithingTemplates.ENTRIES) {
                content.add(entry.item().get());
            }
            for (var entry : SmithingIngredients.ENTRIES) {
                content.add(entry.item().get());
            }
        });
    }
}
