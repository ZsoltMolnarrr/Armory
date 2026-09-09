package net.armory_rpgs;

import net.armory_rpgs.item.ArmorSets;
import net.armory_rpgs.item.Group;
import net.armory_rpgs.item.SmithingTemplates;
import net.armory_rpgs.item.SmithingIngredients;
import net.armory_rpgs.spell.ArmorySounds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spell_engine.PlatformEvents;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.tiny_config.ConfigManager;

import java.util.LinkedHashMap;
import java.util.Map;

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
        registerItemGroup();
        itemsToRegister().forEach((id, item) -> Registry.register(Registries.ITEM, id, item));
    }

    /// Builds the Armory creative tab. Creation only — see {@link #itemsToRegister()}.
    ///
    /// `ItemGroup.Builder` is a vanilla type on this line (the 1.21 `ItemGroup.builder()` static is a
    /// Fabric API interface injection).
    public static ItemGroup createItemGroup() {
        Group.GROUP = new ItemGroup.Builder(ItemGroup.Row.TOP, 0)
                .icon(Group.ICON)
                .displayName(Text.translatable(Group.translationKey))
                .build();
        return Group.GROUP;
    }

    public static void registerItemGroup() {
        Registry.register(Registries.ITEM_GROUP, Group.KEY, createItemGroup());
    }

    /// Every Armory item keyed by the id it registers under, plus the creative-tab contents callbacks.
    /// Creation only — nothing is written into the ITEM registry here, so a loader that registers items
    /// itself (Forge) iterates this instead of calling {@link #registerItems()}.
    /// **Must run inside the ITEM registration window** (`Item`'s constructor takes an intrusive holder).
    public static Map<Identifier, Item> itemsToRegister() {
        var items = new LinkedHashMap<Identifier, Item>();

        // Smithing templates + ingredients into the Armory creative tab. Dispatched by SpellEngine on both
        // loaders (Fabric `ItemGroupEvents` / Forge `BuildCreativeModeTabContentsEvent`); the armor sets
        // are placed by SpellEngine's own `Armor.itemsToRegister(..., Group.KEY)`.
        //
        // ORDER MATTERS: on both loaders the group modifiers run in *registration* order, so this listener
        // has to be installed BEFORE `ArmorSets.itemsToRegister(...)` installs SpellEngine's armor-set
        // listeners — that is what puts the templates + crystals at the front of the tab, ahead of the armor
        // sets. The lambda only reads the ENTRIES lists when the tab is built, long after they are filled.
        // (The ITEM_GROUP registry entry itself is written elsewhere — on Forge in the `creative_mode_tab`
        // window, registry event 65 — which does not affect this ordering: both loaders keep the callbacks
        // in a list keyed by `Group.KEY` and never consult the registry to build it.)
        PlatformEvents.onItemGroupModify(Group.KEY, (content, context) -> {
            for (var entry : SmithingTemplates.ENTRIES) {
                content.add(entry.item().get());
            }
            for (var entry : SmithingIngredients.ENTRIES) {
                content.add(entry.item().get());
            }
        });

        items.putAll(SmithingTemplates.itemsToRegister());
        items.putAll(SmithingIngredients.itemsToRegister());
        items.putAll(ArmorSets.itemsToRegister(itemConfig.value.armor_sets));
        itemConfig.save();
        return items;
    }

    public static void registerEffects() {
        // ArmoryEffects.register(effectConfig.value);
        effectConfig.save();
    }
}
