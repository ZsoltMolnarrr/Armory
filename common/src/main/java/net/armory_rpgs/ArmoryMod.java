package net.armory_rpgs;

import net.armory_rpgs.item.ArmorSets;
import net.armory_rpgs.item.SmithingTemplates;
import net.armory_rpgs.item.SmithingIngredients;
import net.armory_rpgs.spell.ArmorySounds;
import net.spell_engine.api.config.ConfigFile;
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
        ArmorSets.register(itemConfig.value.armor_sets);
        itemConfig.save();
    }

    public static void registerEffects() {
        // ArmoryEffects.register(effectConfig.value);
        effectConfig.save();
    }
}
