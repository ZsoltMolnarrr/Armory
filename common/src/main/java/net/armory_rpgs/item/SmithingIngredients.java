package net.armory_rpgs.item;

import com.google.common.base.Suppliers;
import net.armory_rpgs.ArmoryMod;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SmithingIngredients {
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

        @Override
        public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent,
                                  Consumer<Text> textConsumer, TooltipType type) {
            super.appendTooltip(stack, context, displayComponent, textConsumer, type);
            textConsumer.accept(HINT_TEXT);
            textConsumer.accept(ScreenTexts.EMPTY);
            textConsumer.accept(APPLIES_TO_TEXT);
            textConsumer.accept(ScreenTexts.space().append(Text.translatable(appliesToTranslationKey)).formatted(Formatting.BLUE));
        }
    }

    public record Translations(String itemName) { }
    public record Entry(String name, List<FightClass> classes, Translations translations, Supplier<UpgradeCrystal> item) {
        public static Entry of(String name, List<FightClass> classes, Translations translations) {
            Supplier<UpgradeCrystal> factory = Suppliers.memoize(() ->
                    new UpgradeCrystal(new Item.Settings()
                            // Every `Item.Settings` built in a factory needs its registry key since 1.21.2
                            .registryKey(RegistryKey.of(RegistryKeys.ITEM, idOf(name)))
                            .rarity(Rarity.EPIC)
                            .fireproof(),
                            appliesToTranslationKey(name)
            ))::get;
            return new Entry(name, classes, translations, factory);
        }
        public static Identifier idOf(String name) {
            return Identifier.of(ArmoryMod.NAMESPACE, name + "_upgrade_crystal");
        }
        public Identifier id() {
            return idOf(name);
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

    // Lost crystals - upgrade base class armor into the first legendary set of the class
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

    // Forgotten crystals - upgrade the same base class armor into the second legendary set of the class
    public static final Entry CONQUEROR_FORGOTTEN = add(Entry.of("conqueror_forgotten", List.of(FightClass.ARCANE_WIZARD, FightClass.FIRE_WIZARD),
            new Translations("Conqueror's Forgotten Crystal")
    ));
    public static final Entry VANQUISHER_FORGOTTEN = add(Entry.of("vanquisher_forgotten", List.of(FightClass.FROST_WIZARD, FightClass.ARCHER),
            new Translations("Vanquisher's Forgotten Crystal")
    ));
    public static final Entry REDEEMER_FORGOTTEN = add(Entry.of("redeemer_forgotten", List.of(FightClass.PALADIN, FightClass.PRIEST),
            new Translations("Redeemer's Forgotten Crystal")
    ));
    public static final Entry CHAMPION_FORGOTTEN = add(Entry.of("champion_forgotten", List.of( FightClass.ROGUE, FightClass.WARRIOR),
            new Translations("Champion's Forgotten Crystal")
    ));
    public static void register() {
        for (var entry : ENTRIES) {
            Registry.register(Registries.ITEM, entry.id(), entry.item().get());
        }
        // Creative-tab placement: see ArmoryMod.registerItems (single ordered callback).
    }
}
