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
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SmithingTemplates {
    private static final ChatFormatting TITLE_FORMATTING = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMATTING = ChatFormatting.BLUE;
    // 1.21.4 moved the empty-slot placeholders onto the GUI atlas under `container/slot/`
    private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.withDefaultNamespace("container/slot/helmet");
    private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.withDefaultNamespace("container/slot/chestplate");
    private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.withDefaultNamespace("container/slot/leggings");
    private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.withDefaultNamespace("container/slot/boots");

    /// 1.21.11 dropped the per-item title text from `SmithingTemplateItem` (the tooltip's first line is
    /// hard-coded to "Smithing Template"), so the custom "Superior Armor Upgrade" title lives here.
    public static class UpgradeTemplateItem extends SmithingTemplateItem {
        private static final Component APPLIES_TO_LABEL = Component.translatable(
                Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.applies_to"))).withStyle(TITLE_FORMATTING);
        private static final Component INGREDIENTS_LABEL = Component.translatable(
                Util.makeDescriptionId("item", Identifier.withDefaultNamespace("smithing_template.ingredients"))).withStyle(TITLE_FORMATTING);

        private final Component titleText;
        private final Component ownAppliesToText;
        private final Component ownIngredientsText;

        public UpgradeTemplateItem(Component titleText, Component appliesToText, Component ingredientsText,
                                   Component baseSlotDescriptionText, Component additionsSlotDescriptionText,
                                   List<Identifier> emptyBaseSlotTextures, List<Identifier> emptyAdditionsSlotTextures,
                                   Item.Properties settings) {
            super(appliesToText, ingredientsText, baseSlotDescriptionText, additionsSlotDescriptionText,
                    emptyBaseSlotTextures, emptyAdditionsSlotTextures, settings);
            this.titleText = titleText;
            this.ownAppliesToText = appliesToText;
            this.ownIngredientsText = ingredientsText;
        }

        @Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent,
                                  Consumer<Component> textConsumer, TooltipFlag type) {
            textConsumer.accept(titleText);
            textConsumer.accept(CommonComponents.EMPTY);
            textConsumer.accept(APPLIES_TO_LABEL);
            textConsumer.accept(CommonComponents.space().append(ownAppliesToText));
            textConsumer.accept(INGREDIENTS_LABEL);
            textConsumer.accept(CommonComponents.space().append(ownIngredientsText));
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
                    new Item.Properties().setId(ResourceKey.create(Registries.ITEM, entry.id()))
            ))::get;
            return new Entry(name, classes, translations, factory);
        }

        public Identifier id() {
            return Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, name + "_upgrade");
        }

        public String upgradeTranslationKey() {
            return Util.makeDescriptionId("upgrade", Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, name + "_upgrade"));
        }
        public Component upgradeText() {
            return Component.translatable(upgradeTranslationKey()).withStyle(TITLE_FORMATTING);
        }

        public String appliesToTranslationKey() {
            return Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "smithing_template." + name + "_upgrade.applies_to"));
        }
        public Component appliesToText() {
            return Component.translatable(appliesToTranslationKey()).withStyle(DESCRIPTION_FORMATTING);
        }

        public String ingredientsTranslationKey() {
            return Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "smithing_template." + name + "_upgrade.ingredients"));
        }
        public Component ingredientsText() {
            var key = ingredientsTranslationKey();
            return Component.translatable(key).withStyle(DESCRIPTION_FORMATTING);
        }

        public String baseSlotDescriptionTranslationKey() {
            return Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "smithing_template." + name + "_upgrade.base_slot_description"));
        }
        public Component baseSlotDescriptionText() {
            return Component.translatable(baseSlotDescriptionTranslationKey());
        }

        public String additionsSlotDescriptionTranslationKey() {
            return Util.makeDescriptionId("item", Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "smithing_template." + name + "_upgrade.additions_slot_description"));
        }
        public Component additionsSlotDescriptionText() {
            return Component.translatable(additionsSlotDescriptionTranslationKey());
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
            Registry.register(BuiltInRegistries.ITEM, entry.id(), entry.item().get());
        }
        // Creative-tab placement: see ArmoryMod.registerItems (single ordered callback).
    }
}
