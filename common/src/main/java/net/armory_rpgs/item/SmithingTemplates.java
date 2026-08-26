package net.armory_rpgs.item;

import com.google.common.base.Suppliers;
import net.armory_rpgs.ArmoryMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SmithingTemplates {
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    // 1.21.4 moved the empty-slot placeholders onto the GUI atlas under `container/slot/`
    private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.ofVanilla("container/slot/helmet");
    private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.ofVanilla("container/slot/chestplate");
    private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.ofVanilla("container/slot/leggings");
    private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.ofVanilla("container/slot/boots");

    /// 1.21.11 dropped the per-item title text from `SmithingTemplateItem` (the tooltip's first line is
    /// hard-coded to "Smithing Template"), so the custom "Superior Armor Upgrade" title lives here.
    public static class UpgradeTemplateItem extends SmithingTemplateItem {
        private static final Text APPLIES_TO_LABEL = Text.translatable(
                Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.applies_to"))).formatted(TITLE_FORMATTING);
        private static final Text INGREDIENTS_LABEL = Text.translatable(
                Util.createTranslationKey("item", Identifier.ofVanilla("smithing_template.ingredients"))).formatted(TITLE_FORMATTING);

        private final Text titleText;
        private final Text ownAppliesToText;
        private final Text ownIngredientsText;

        public UpgradeTemplateItem(Text titleText, Text appliesToText, Text ingredientsText,
                                   Text baseSlotDescriptionText, Text additionsSlotDescriptionText,
                                   List<Identifier> emptyBaseSlotTextures, List<Identifier> emptyAdditionsSlotTextures,
                                   Item.Settings settings) {
            super(appliesToText, ingredientsText, baseSlotDescriptionText, additionsSlotDescriptionText,
                    emptyBaseSlotTextures, emptyAdditionsSlotTextures, settings);
            this.titleText = titleText;
            this.ownAppliesToText = appliesToText;
            this.ownIngredientsText = ingredientsText;
        }

        @Override
        public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent,
                                  Consumer<Text> textConsumer, TooltipType type) {
            textConsumer.accept(titleText);
            textConsumer.accept(ScreenTexts.EMPTY);
            textConsumer.accept(APPLIES_TO_LABEL);
            textConsumer.accept(ScreenTexts.space().append(ownAppliesToText));
            textConsumer.accept(INGREDIENTS_LABEL);
            textConsumer.accept(ScreenTexts.space().append(ownIngredientsText));
        }
    }

    public record Translations(String itemName, String upgradeName, String appliesTo, String ingredients, String baseSlotDescription, String additionsSlotDescription) { }
    public record Entry(String name, List<FightClass> classes, Translations translations, Supplier<UpgradeTemplateItem> item) {
        public static Entry of(String name, List<FightClass> classes, Translations translations) {
            var entry = new Entry(name, classes, translations, null);
            Supplier<UpgradeTemplateItem> factory = Suppliers.memoize(() -> new UpgradeTemplateItem(
                    entry.upgradeText(),
                    entry.appliesToText(),
                    entry.ingredientsText(),
                    entry.baseSlotDescriptionText(),
                    entry.additionsSlotDescriptionText(),
                    baseSlotTextures(),
                    additionsTextures(),
                    // Every `Item.Settings` built in a factory needs its registry key since 1.21.2
                    new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, entry.id()))
            ))::get;
            return new Entry(name, classes, translations, factory);
        }

        public Identifier id() {
            return Identifier.of(ArmoryMod.NAMESPACE, name + "_upgrade");
        }

        public String upgradeTranslationKey() {
            return Util.createTranslationKey("upgrade", Identifier.of(ArmoryMod.NAMESPACE, name + "_upgrade"));
        }
        public Text upgradeText() {
            return Text.translatable(upgradeTranslationKey()).formatted(TITLE_FORMATTING);
        }

        public String appliesToTranslationKey() {
            return Util.createTranslationKey("item", Identifier.of(ArmoryMod.NAMESPACE, "smithing_template." + name + "_upgrade.applies_to"));
        }
        public Text appliesToText() {
            return Text.translatable(appliesToTranslationKey()).formatted(DESCRIPTION_FORMATTING);
        }

        public String ingredientsTranslationKey() {
            return Util.createTranslationKey("item", Identifier.of(ArmoryMod.NAMESPACE, "smithing_template." + name + "_upgrade.ingredients"));
        }
        public Text ingredientsText() {
            var key = ingredientsTranslationKey();
            return Text.translatable(key).formatted(DESCRIPTION_FORMATTING);
        }

        public String baseSlotDescriptionTranslationKey() {
            return Util.createTranslationKey("item", Identifier.of(ArmoryMod.NAMESPACE, "smithing_template." + name + "_upgrade.base_slot_description"));
        }
        public Text baseSlotDescriptionText() {
            return Text.translatable(baseSlotDescriptionTranslationKey());
        }

        public String additionsSlotDescriptionTranslationKey() {
            return Util.createTranslationKey("item", Identifier.of(ArmoryMod.NAMESPACE, "smithing_template." + name + "_upgrade.additions_slot_description"));
        }
        public Text additionsSlotDescriptionText() {
            return Text.translatable(additionsSlotDescriptionTranslationKey());
        }

        private static List<Identifier> baseSlotTextures() {
            return List.of(EMPTY_ARMOR_SLOT_HELMET_TEXTURE,
                    EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE,
                    EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE,
                    EMPTY_ARMOR_SLOT_BOOTS_TEXTURE);
        }
        private static List<Identifier> additionsTextures() {
            return List.of();
        }
    }

    public static final ArrayList<Entry> ENTRIES = new ArrayList<>();
    public static Entry add(Entry entry) {
        ENTRIES.add(entry);
        return entry;
    }
    public static final Entry EPIC_UPGRADE = add(Entry.of("epic_armor",
        List.of(FightClass.ARCANE_WIZARD, FightClass.FIRE_WIZARD, FightClass.FROST_WIZARD, FightClass.ARCHER),
        new Translations(
                "Smithing Template",
                "Superior Armor Upgrade",
                "Specialized Armor",
                "Upgrade Crystal",
                "Add a piece of armor",
                "Add upgrade crystal"
        ))
    );

    public static void register() {
        for (var entry : ENTRIES) {
            Registry.register(Registries.ITEM, entry.id(), entry.item().get());
        }
        // Creative-tab placement: see ArmoryMod.registerItems (single ordered callback).
    }
}
