package net.armory_rpgs.item;

import com.google.common.base.Suppliers;
import net.armory_rpgs.ArmoryMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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

public class SmithingUpgrades {
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_helmet");
    private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_chestplate");
    private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_leggings");
    private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.ofVanilla("item/empty_armor_slot_boots");

    public enum FightClass {
        ARCANE_WIZARD("Arcane Wizard"),
        FIRE_WIZARD("Fire Wizard"),
        FROST_WIZARD("Frost Wizard"),
        PRIEST("Priest"),
        PALADIN("Paladin"),
        ROGUE("Rogue"),
        WARRIOR("Warrior"),
        ARCHER("Archer");

        final String translation;
        FightClass(String translation) {
            this.translation = translation;
        }
    }

    public record Translations(String itemName, String upgradeName, String appliesTo, String ingredients, String baseSlotDescription, String additionsSlotDescription) { }
    public record Entry(String name, List<FightClass> classes, Translations translations, Supplier<SmithingTemplateItem> item, Item ingedientItem) {
        public static Entry of(String name, List<FightClass> classes, Translations translations, Item ingedientItem) {
            var entry = new Entry(name, classes, translations, null, ingedientItem);
            var factory = Suppliers.memoize(() -> new SmithingTemplateItem(
                    entry.appliesToText(),
                    entry.ingredientsText(),
                    entry.upgradeText(),
                    entry.baseSlotDescriptionText(),
                    entry.additionsSlotDescriptionText(),
                    baseSlotTextures(),
                    additionsTextures(), new FeatureFlag[0]
            ));
            return new Entry(name, classes, translations, factory, ingedientItem);
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
            var key = ingedientItem != null ? ingedientItem.getTranslationKey() : ingredientsTranslationKey();
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
    public static final Entry FALLEN_VANQUISHER = add(Entry.of("fallen_vanquisher",
        List.of(FightClass.ARCANE_WIZARD, FightClass.FIRE_WIZARD, FightClass.FROST_WIZARD, FightClass.ARCHER),
        new Translations(
                "Smithing Template",
                "Fallen Vanquisher’s Sigil",
                "",
                "???",
                "Add a piece of armor",
                "Add ingot or crystal" // ??
        ), Items.EMERALD)
    );
    public static final Entry FALLEN_HERO = add(Entry.of("fallen_hero",
        List.of(FightClass.PALADIN, FightClass.PRIEST, FightClass.ROGUE, FightClass.WARRIOR),
        new Translations(
                "Smithing Template",
                "Fallen Hero’s Sigil",
                "",
                "???",
                "Add a piece of armor",
                "Add ingot or crystal" // ??
        ), Items.EMERALD)
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
