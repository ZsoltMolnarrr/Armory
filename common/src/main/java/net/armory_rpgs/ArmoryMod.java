package net.armory_rpgs;

import net.armory_rpgs.item.ArmorSets;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.armory_rpgs.item.Group;
import net.armory_rpgs.spell.ArmorySounds;
import net.spell_engine.api.config.ConfigFile;
import net.tiny_config.ConfigManager;

public class ArmoryMod {
    public static final String NAMESPACE = "armory_rpgs";
    public static final String DIRECTORY = NAMESPACE;
    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("equipment_v2", new ConfigFile.Equipment())
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
        ArmorySounds.register();

        // ArmoryEffects.register(effectConfig.value);
        effectConfig.save();

        Group.GROUP = FabricItemGroup.builder()
                .icon(Group.ICON)
                .displayName(Text.translatable(Group.translationKey))
                .build();
        Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.GROUP);

        ArmorSets.register(itemConfig.value.armor_sets);
        itemConfig.save();
    }
}
