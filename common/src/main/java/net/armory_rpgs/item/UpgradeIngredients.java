package net.armory_rpgs.item;

import com.google.common.base.Suppliers;
import net.armory_rpgs.ArmoryMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UpgradeIngredients {
    public static class UpgradeCrystal extends Item {
        public static final Text APPLIES_TO_TEXT = Text.translatable(
                Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.applies_to")))
                .formatted(Formatting.GRAY);
        public static final String HINT_TRANSLATION_KEY = Util.createTranslationKey("item", Identifier.of(ArmoryMod.NAMESPACE, "smithing_template.hint"));
        public static final Text HINT_TEXT = Text.translatable(HINT_TRANSLATION_KEY)
                .formatted(Formatting.GRAY);

        private final String appliesToTranslationKey;
        public UpgradeCrystal(Item.Settings settings, String appliesToTranslationKey) {
            super(settings);
            this.appliesToTranslationKey = appliesToTranslationKey;
        }

        public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
            super.appendTooltip(stack, context, tooltip, type);
            tooltip.add(HINT_TEXT);
            tooltip.add(ScreenTexts.EMPTY);
            tooltip.add(APPLIES_TO_TEXT);
            tooltip.add(ScreenTexts.space().append(Text.translatable(appliesToTranslationKey)).formatted(Formatting.BLUE));
        }
    }

    public record Translations(String itemName) { }
    public record Entry(String name, List<FightClass> classes, Translations translations, Supplier<UpgradeCrystal> item) {
        public static Entry of(String name, List<FightClass> classes, Translations translations) {
            var factory = Suppliers.memoize(() ->
                    new UpgradeCrystal(new Item.Settings()
                            .rarity(Rarity.EPIC),
                            appliesToTranslationKey(name)
            ));
            return new Entry(name, classes, translations, factory);
        }
        public Identifier id() {
            return Identifier.of(ArmoryMod.NAMESPACE, name + "_upgrade_crystal");
        }
        public static String appliesToTranslationKey(String name) {
            return Util.createTranslationKey("item", Identifier.of(ArmoryMod.NAMESPACE, "upgrade_crystal." + name + ".applies_to"));
        }
        public String appliesToTranslationKey() {
            return appliesToTranslationKey(name);
        }
        public String appliesToClassesTranslation() {
            var classNames = classes.stream().map(c -> c.translation).toList();
            var list = "";
            if (classNames.size() == 1) {
                list = classNames.get(0);
            } else if (classNames.size() == 2) {
                list = classNames.get(0) + " and " + classNames.get(1);
            } else {
                var allButLast = classNames.subList(0, classNames.size() - 1);
                var last = classNames.get(classNames.size() - 1);
                list = String.join(", ", allButLast) + ", and " + last;
            }
            return list + " armor";
        }
    }
    public static final ArrayList<Entry> ENTRIES = new ArrayList<>();
    public static Entry add(Entry entry) {
        ENTRIES.add(entry);
        return entry;
    }

    public static final Entry CONQUEROR = add(Entry.of("conqueror", List.of(FightClass.ARCANE_WIZARD, FightClass.FIRE_WIZARD),
            new Translations("Conqueror's Lost Crystal")
    ));
    public static final Entry VANQUISHER = add(Entry.of("vanquisher", List.of(FightClass.FROST_WIZARD, FightClass.ARCHER),
            new Translations("Vanquisher's Lost Crystal")
    ));
    public static final Entry REDEEMER = add(Entry.of("redeemer", List.of(FightClass.PALADIN, FightClass.PRIEST),
            new Translations("Redeemer's Lost Crystal")
    ));
    public static final Entry CHAMPION = add(Entry.of("champion", List.of( FightClass.ROGUE, FightClass.WARRIOR),
            new Translations("Champion's Lost Crystal")
    ));
    public static void register() {
        for (var entry : ENTRIES) {
            Registry.register(Registries.ITEM, entry.id(), entry.item().get());
        }
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry : ENTRIES) {
                content.add(entry.item().get());
            }
        });
    }
}
