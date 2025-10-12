package net.armory_rpgs.item;

import com.google.common.base.Suppliers;
import net.armory_rpgs.ArmoryMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SmithingTemplates {
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_helmet");
    private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_chestplate");
    private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_leggings");
    private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_boots");

    public record Translations(String itemName, String upgradeName, String appliesTo, String ingredients, String baseSlotDescription, String additionsSlotDescription) { }
    public record Entry(String name, List<FightClass> classes, Translations translations, Supplier<SmithingTemplateItem> item) {
        public static Entry of(String name, List<FightClass> classes, Translations translations) {
            var entry = new Entry(name, classes, translations, null);
            var factory = Suppliers.memoize(() -> new SmithingTemplateItem(
                    entry.appliesToText(),
                    entry.ingredientsText(),
                    entry.upgradeText(),
                    entry.baseSlotDescriptionText(),
                    entry.additionsSlotDescriptionText(),
                    baseSlotTextures(),
                    additionsTextures(), new FeatureFlag[0]
            ));
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
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register((content) -> {
            for (var entry : ENTRIES) {
                content.add(entry.item().get());
            }
        });
    }
}
