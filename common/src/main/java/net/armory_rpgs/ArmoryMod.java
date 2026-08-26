package net.armory_rpgs;

import net.armory_rpgs.item.ArmorSets;
import net.armory_rpgs.item.Group;
import net.armory_rpgs.item.SmithingTemplates;
import net.armory_rpgs.item.SmithingIngredients;
import net.armory_rpgs.spell.ArmorySounds;
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
        SmithingTemplates.register();
        SmithingIngredients.register();
        // Creative-tab order: item-group modify callbacks run in registration order on both loaders
        // (Fabric `ItemGroupEvents`, SpellEngine's NeoForge dispatcher), so this callback must be
        // registered BEFORE `ArmorSets.register`, whose `Armor.register(..., Group.KEY)` appends the sets.
        // Resulting order: smithing templates, upgrade crystals, then armor sets in registration order.
        PlatformEvents.onItemGroupModify(Group.KEY, (content, context) -> {
            for (var entry : SmithingTemplates.ENTRIES) {
                content.add(entry.item().get());
            }
            for (var entry : SmithingIngredients.ENTRIES) {
                content.add(entry.item().get());
            }
        });
        ArmorSets.register(itemConfig.value.armor_sets);
        itemConfig.save();
    }

    public static void registerEffects() {
        // ArmoryEffects.register(effectConfig.value);
        effectConfig.save();
    }
}
