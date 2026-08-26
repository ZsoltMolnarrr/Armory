package net.armory_rpgs.item;

import com.google.common.base.Suppliers;
import net.armory_rpgs.ArmoryMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SmithingIngredients {
    public static class UpgradeCrystal extends Item {
        public static final Component APPLIES_TO_TEXT = Component.translatable(
                Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.applies_to")))
                .withStyle(ChatFormatting.GRAY);
        public static final String HINT_TRANSLATION_KEY = Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "smithing_template.hint"));
        public static final Component HINT_TEXT = Component.translatable(HINT_TRANSLATION_KEY)
                .withStyle(ChatFormatting.GRAY);

        private final String appliesToTranslationKey;
        public UpgradeCrystal(Item.Properties settings, String appliesToTranslationKey) {
            super(settings);
            this.appliesToTranslationKey = appliesToTranslationKey;
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent,
                                  Consumer<Component> textConsumer, TooltipFlag type) {
            super.appendHoverText(stack, context, displayComponent, textConsumer, type);
            textConsumer.accept(HINT_TEXT);
            textConsumer.accept(CommonComponents.EMPTY);
            textConsumer.accept(APPLIES_TO_TEXT);
            textConsumer.accept(CommonComponents.space().append(Component.translatable(appliesToTranslationKey)).withStyle(ChatFormatting.BLUE));
        }
    }

    public record Translations(String itemName) { }
    public record Entry(String name, List<FightClass> classes, Translations translations, Supplier<UpgradeCrystal> item) {
        public static Entry of(String name, List<FightClass> classes, Translations translations) {
            Supplier<UpgradeCrystal> factory = Suppliers.memoize(() ->
                    new UpgradeCrystal(new Item.Properties()
                            // Every `Item.Settings` built in a factory needs its registry key since 1.21.2
                            .setId(ResourceKey.create(Registries.ITEM, idOf(name)))
                            .rarity(Rarity.EPIC)
                            .fireResistant(),
                            appliesToTranslationKey(name)
            ))::get;
            return new Entry(name, classes, translations, factory);
        }
        public static Identifier idOf(String name) {
            return Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, name + "_upgrade_crystal");
        }
        public Identifier id() {
            return idOf(name);
        }
        public static String appliesToTranslationKey(String name) {
            return Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "upgrade_crystal." + name + ".applies_to"));
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
            Registry.register(BuiltInRegistries.ITEM, entry.id(), entry.item().get());
        }
        // Creative-tab placement: see ArmoryMod.registerItems (single ordered callback).
    }
}
