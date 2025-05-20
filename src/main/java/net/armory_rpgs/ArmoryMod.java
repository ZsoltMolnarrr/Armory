package net.armory_rpgs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.armory_rpgs.item.Group;
import net.armory_rpgs.spell.ArmorySounds;
import net.spell_engine.api.config.ConfigFile;
import net.tinyconfig.ConfigManager;

public class ArmoryMod implements ModInitializer {
    public static final String NAMESPACE = "armory_rpgs";
    public static final String DIRECTORY = NAMESPACE;
    public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
            ("equipment", new ConfigFile.Equipment())
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

    @Override
    public void onInitialize() {
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

        // TODO: Add Armory Registration here

        itemConfig.save();
    }
}
