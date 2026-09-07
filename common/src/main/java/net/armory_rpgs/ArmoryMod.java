package net.armory_rpgs;

import net.armory_rpgs.item.ArmorSets;
import net.armory_rpgs.item.Group;
import net.armory_rpgs.item.SmithingTemplates;
import net.armory_rpgs.item.SmithingIngredients;
import net.armory_rpgs.spell.ArmorySounds;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.spell_engine.PlatformEvents;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.tiny_config.ConfigManager;

public class ArmoryMod {
    public static final String NAMESPACE = "armory_rpgs";
    public static final String DIRECTORY = NAMESPACE;
    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("equipment_v3", new ConfigFile.Equipment())
            .builder()
            .setDirectory(DIRECTORY)
            .sanitize(true)
            .build();
    public static ConfigManager<ConfigFile.Effects> effectConfig = new ConfigManager<>
            ("effects", new ConfigFile.Effects())
            .builder()
            .setDirectory(DIRECTORY)
            .sanitize(true)
            .build();

    public static void init() {
        itemConfig.refresh();
        effectConfig.refresh();
    }

    public static void registerSounds() {
        ArmorySounds.register();
    }

    public static void registerItems() {
        // `ItemGroup.Builder` is a vanilla type on this line (the 1.21 `ItemGroup.builder()` static is a
        // Fabric API interface injection); the ITEM_GROUP registry is vanilla-only and stays unfrozen for
        // the whole Forge RegisterEvent phase, so registering it from the ITEM window is fine.
        Group.GROUP = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
                .icon(Group.ICON)
                .displayName(Text.translatable(Group.translationKey))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.GROUP);

        // Smithing templates + ingredients into the Armory creative tab. Dispatched by SpellEngine on both
        // loaders (Fabric `ItemGroupEvents` / Forge `BuildCreativeModeTabContentsEvent`); the armor sets
        // are placed by SpellEngine's own `Armor.register(..., Group.KEY)`.
        //
        // ORDER MATTERS: on both loaders the group modifiers run in *registration* order, so this listener
        // has to be installed BEFORE `ArmorSets.register(...)` installs SpellEngine's armor-set listeners —
        // that is what puts the templates + crystals at the front of the tab, ahead of the armor sets.
        // The lambda only reads the ENTRIES lists when the tab is built, long after `register()` filled them.
        PlatformEvents.onItemGroupModify(Group.KEY, (content, context) -> {
            for (var entry : SmithingTemplates.ENTRIES) {
                content.add(entry.item().get());
            }
            for (var entry : SmithingIngredients.ENTRIES) {
                content.add(entry.item().get());
            }
        });

        SmithingTemplates.register();
        SmithingIngredients.register();
        ArmorSets.register(itemConfig.value.armor_sets);
        itemConfig.save();
    }

    public static void registerEffects() {
        // ArmoryEffects.register(effectConfig.value);
        effectConfig.save();
    }
}
