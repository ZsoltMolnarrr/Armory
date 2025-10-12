package net.armory_rpgs.datagen;

import net.armory_rpgs.datagen.recipe.SmithingRecipeProvider;
import net.armory_rpgs.datagen.recipe.SmithingUpgradeRecipe;
import net.armory_rpgs.item.*;
import net.armory_rpgs.spell.SetBonuses;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.spell.ArmoryEffects;
import net.armory_rpgs.spell.ArmorySounds;
import net.armory_rpgs.spell.ArmorySpells;
import net.spell_engine.api.datagen.SimpleSoundGeneratorV2;
import net.spell_engine.api.datagen.SpellGenerator;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.item.set.EquipmentSet;
import net.spell_engine.api.item.set.EquipmentSetRegistry;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ArmoryDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(SpellTagGenerator::new);
        pack.addProvider(LangGenerator::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(SpellGen::new);
        pack.addProvider(SoundGen::new);
        pack.addProvider(EquipmentSetGenerator::new);
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(SmithGen::new);
    }

    private static List<Item> allArmorPieces() {
        var items = new ArrayList<Item>();
        for (var entry: ArmorSets.entries) {
            entry.armorSet().pieces().forEach(item -> {
                items.add((Item)item);
            });
        }
        return items;
    }

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            generateArmorTags(
                    ArmorSets.entries.stream().filter(entry -> entry.name().contains("archer")).toList(),
                    RPGSeriesItemTags.ArmorMetaType.MELEE
            );
            generateArmorTags(
                    ArmorSets.entries.stream().filter(entry -> entry.name().contains("armor")).toList(),
                    RPGSeriesItemTags.ArmorMetaType.MELEE
            );
            generateArmorTags(
                    ArmorSets.entries.stream().filter(entry -> entry.name().contains("robe")).toList(),
                    RPGSeriesItemTags.ArmorMetaType.MAGIC
            );
        }
    }

    public static class SpellTagGenerator extends FabricTagProvider<Spell> {
        public SpellTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, SpellRegistry.KEY, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            ArmorySpells.all.forEach(entry -> {
                for (var category: entry.categories()) {
                    var tagKey = TagKey.of(SpellRegistry.KEY, Identifier.of(ArmoryMod.NAMESPACE, category.toString().toLowerCase()));
                    var tag = getOrCreateTagBuilder(tagKey);
                    tag.addOptional(entry.id());
                }
            });
        }
    }

    public static class LangGenerator extends FabricLanguageProvider {
        protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(Group.translationKey, "Armory");

            translationBuilder.add(UpgradeIngredients.UpgradeCrystal.HINT_TRANSLATION_KEY, "Armor upgrade crystal");
            UpgradeIngredients.ENTRIES.forEach(entry -> {
                translationBuilder.add(entry.item().get().getTranslationKey(), entry.translations().itemName());
                translationBuilder.add(entry.appliesToTranslationKey(), entry.appliesToClassesTranslation());
            });
            SmithingUpgrades.ENTRIES.forEach(entry -> {
                translationBuilder.add(entry.item().get().getTranslationKey(), entry.translations().itemName());
                translationBuilder.add(entry.upgradeTranslationKey(), entry.translations().upgradeName());
                translationBuilder.add(entry.baseSlotDescriptionTranslationKey(), entry.translations().baseSlotDescription());
                translationBuilder.add(entry.additionsSlotDescriptionTranslationKey(), entry.translations().additionsSlotDescription());
                translationBuilder.add(entry.appliesToTranslationKey(), entry.translations().appliesTo());
                translationBuilder.add(entry.ingredientsTranslationKey(), entry.translations().ingredients());
            });
            ArmorSets.entries.forEach(entry -> {
                var translations = new LinkedHashMap<String, String>();
                translations.put(((Item)entry.armorSet().head).getTranslationKey(), entry.armorSet().headTranslation);
                translations.put(((Item)entry.armorSet().chest).getTranslationKey(), entry.armorSet().chestTranslation);
                translations.put(((Item)entry.armorSet().legs).getTranslationKey(), entry.armorSet().legsTranslation);
                translations.put(((Item)entry.armorSet().feet).getTranslationKey(), entry.armorSet().feetTranslation);
                for (var armorEntry: translations.entrySet()) {
                    translationBuilder.add(armorEntry.getKey(), armorEntry.getValue());
                }
            });
            SetBonuses.all.forEach(entry -> {
                translationBuilder.add(EquipmentSet.translationKey(entry.id()), entry.title());
            });
            ArmorySpells.all.forEach(entry -> {
                var id = entry.id();
                translationBuilder.add("spell." + id.getNamespace() + "." + id.getPath() + ".name" , entry.title());
                translationBuilder.add("spell." + id.getNamespace() + "." + id.getPath() + ".description" , entry.description());
            });
            ArmoryEffects.entries.forEach(entry -> {
                translationBuilder.add(entry.effect.getTranslationKey(), entry.title);
                translationBuilder.add(entry.effect.getTranslationKey() + ".description", entry.description);
            });
        }
    }

    public static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            UpgradeIngredients.ENTRIES.forEach(entry -> {
                itemModelGenerator.register(entry.item().get(), Models.GENERATED);
            });
            SmithingUpgrades.ENTRIES.forEach(entry -> {
                itemModelGenerator.register(entry.item().get(), Models.GENERATED);
            });
            ArmorSets.entries.forEach(entry -> {
                for (var piece: entry.armorSet().pieces()) {
                    itemModelGenerator.register((Item) piece, Models.GENERATED);
                }
            });
        }
    }

    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSpells(Builder builder) {
            for (var entry: ArmorySpells.all) {
                builder.add(entry.id(), entry.spell());
            }
        }
    }

    public static class SoundGen extends SimpleSoundGeneratorV2 {
        public SoundGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSounds(Builder builder) {
            builder.entries.add(new Entry(ArmoryMod.NAMESPACE,
                            ArmorySounds.entries.stream()
                                    .map(entry -> SoundEntry.withVariants(entry.id().getPath(), entry.variants()))
                                    .toList()
                    )
            );
        }
    }

    public static class EquipmentSetGenerator extends FabricDynamicRegistryProvider {

        public EquipmentSetGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
            RegistryEntryLookup<Item> itemLookup = registries.createRegistryLookup().getOrThrow(RegistryKeys.ITEM);
            for (var set: SetBonuses.all) {
                var items = RegistryEntryList.of(
                        set.itemSupplier().get().stream()
                        .map(id -> itemLookup.getOrThrow(RegistryKey.of(RegistryKeys.ITEM, id)))
                        .toList()
                );
                entries.add(
                        RegistryKey.of(EquipmentSetRegistry.KEY, set.id()),
                        new EquipmentSet.Definition(
                                set.id().getPath(),
                                items,
                                set.bonuses()
                        )
                );
            }
        }

        @Override
        public String getName() {
            return "Equipment Set Generator";
        }
    }

    public static class RecipeGenerator extends FabricRecipeProvider {
        public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        public record ArmorIdSet(String namespace, String name) {
            public Identifier headId() {
                return Identifier.of(namespace, name + "_" + EquipmentSlot.HEAD.asString().toLowerCase());
            }
            public Identifier chestId() {
                return Identifier.of(namespace, name + "_" + EquipmentSlot.CHEST.asString().toLowerCase());
            }
            public Identifier legsId() {
                return Identifier.of(namespace, name + "_" + EquipmentSlot.LEGS.asString().toLowerCase());
            }
            public Identifier feetId() {
                return Identifier.of(namespace, name + "_" + EquipmentSlot.FEET.asString().toLowerCase());
            }
        }

        public record Upgrade(Armor.Set from, Armor.Set to, Item material) {}

        public final Map<FightClass, List<Upgrade>> UPGRADES = Map.of(
//                SmithingUpgrades.FightClass.ARCHER, List.of(
//                        new Upgrade(
//                                Armors.archerArmorSet_T2,
//                                ArmorSets.strider.armorSet(),
//                                Items.EMERALD
//                        ))
        );

        @Override
        public void generate(RecipeExporter recipeExporter) {
            for (var template: SmithingUpgrades.ENTRIES) {
                for (var figthClass: template.classes()) {
                    var upgrades = UPGRADES.get(figthClass);
                    if (upgrades == null) continue;
                    for (var upgradeType: upgrades) {

                        offerUpgradeRecipe(recipeExporter,
                                RecipeCategory.COMBAT,
                                template.item().get(),
                                upgradeType.from.head,
                                upgradeType.material,
                                upgradeType.to.head
                        );
                    }

                }
            }
        }

        public static void offerUpgradeRecipe(RecipeExporter exporter, RecipeCategory category, Item template, Item inputBase, Item inputAddition, Item result) {
            SmithingTransformRecipeJsonBuilder.create(
                    Ingredient.ofItems(new ItemConvertible[]{template}),
                    Ingredient.ofItems(new ItemConvertible[]{inputBase}),
                    Ingredient.ofItems(new ItemConvertible[]{inputAddition}),
                    category,
                    result).criterion("has_template", conditionsFromItem(template)).offerTo(exporter, getItemPath(result) + "_smithing");
        }
    }

    public static class SmithGen extends SmithingRecipeProvider {

        public SmithGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        public record ArmorIdSet(String namespace, String name) {
            public Identifier headId() {
                return Identifier.of(namespace, name + "_" + EquipmentSlot.HEAD.asString().toLowerCase());
            }
            public Identifier chestId() {
                return Identifier.of(namespace, name + "_" + EquipmentSlot.CHEST.asString().toLowerCase());
            }
            public Identifier legsId() {
                return Identifier.of(namespace, name + "_" + EquipmentSlot.LEGS.asString().toLowerCase());
            }
            public Identifier feetId() {
                return Identifier.of(namespace, name + "_" + EquipmentSlot.FEET.asString().toLowerCase());
            }
        }

        public record Upgrade(ArmorIdSet from, Armor.Set to, Item ingredient) {
            public Upgrade prefixVariant(String variant) {
                return new Upgrade(
                        new ArmorIdSet(from.namespace(), variant + "_" + from.name()),
                        to,
                        ingredient
                );
            }
        }

        public Map<FightClass, List<Upgrade>> UPGRADES = Map.of(
                FightClass.ARCHER, List.of(
                        new Upgrade(
                                new ArmorIdSet("archers", "ranger_armor"),
                                ArmorSets.strider.armorSet(),
                                null
                        )),
                FightClass.ARCANE_WIZARD, List.of(
                        new Upgrade(
                                new ArmorIdSet("wizards", "arcane_robe"),
                                ArmorSets.astral.armorSet(),
                                null
                        )),
                FightClass.FIRE_WIZARD, List.of(
                        new Upgrade(
                                new ArmorIdSet("wizards", "fire_robe"),
                                ArmorSets.scarlet.armorSet(),
                                null
                        )),
                FightClass.FROST_WIZARD, List.of(
                        new Upgrade(
                                new ArmorIdSet("wizards", "frost_robe"),
                                ArmorSets.glacier.armorSet(),
                                null
                        )),
                FightClass.PRIEST, List.of(
                        new Upgrade(
                                new ArmorIdSet("paladins", "prior_robe"),
                                ArmorSets.avatar.armorSet(),
                                null
                        )),
                FightClass.PALADIN, List.of(
                        new Upgrade(
                                new ArmorIdSet("paladins", "crusader_armor"),
                                ArmorSets.justicar.armorSet(),
                                null
                        )),
                FightClass.ROGUE, List.of(
                        new Upgrade(
                                new ArmorIdSet("rogues", "assassin_armor"),
                                ArmorSets.deathmantle.armorSet(),
                                null
                        )),
                FightClass.WARRIOR, List.of(
                        new Upgrade(
                                new ArmorIdSet("rogues", "berserker_armor"),
                                ArmorSets.destroyer.armorSet(),
                                null
                        ))
        );


        @Override
        public void generateRecipes(Builder builder) {
            // Add relevant Ingredient to all upgrades
            Map<FightClass, List<Upgrade>> autoAssignedUpgrades = new HashMap<>();
            UPGRADES.forEach((fightClass, upgrades) -> {
                var newList = new ArrayList<Upgrade>();
                for (var upgrade: upgrades) {
                    if (upgrade.ingredient == null) {
                        var ingredient = UpgradeIngredients.ENTRIES.stream()
                                .filter(entry -> entry.classes().contains(fightClass)).findFirst().orElse(null).item().get();
                        newList.add(new Upgrade(upgrade.from, upgrade.to, ingredient));
                    }
                }
                autoAssignedUpgrades.put(fightClass, newList);
            });

            var includeNetherite = true;

            for (var template: SmithingUpgrades.ENTRIES) {
                for (var entry: autoAssignedUpgrades.entrySet()) {
                    var figthClass = entry.getKey();
                    var upgrades = entry.getValue();
                    if (upgrades == null) continue;

                    if (includeNetherite) {
                        var list = new ArrayList<Upgrade>(upgrades);
                        for (var upgrade: upgrades) {
                            list.add(upgrade.prefixVariant("netherite"));
                        }
                        upgrades = list;
                    }

                    // Head
                    for (var upgradeType: upgrades) {
                        var ingredientId = upgradeType.ingredient().getRegistryEntry().getKey().get().getValue().toString();
                        var builderEntry = SmithingUpgradeRecipe.ofStringsWithConditions(
                                template.id().toString(),
                                upgradeType.from.headId().toString(),
                                ingredientId,
                                upgradeType.to.idOf(upgradeType.to.head).toString(),
                                upgradeType.from.namespace()
                        );
                        var itemId = upgradeType.to.idOf(upgradeType.to.head);
                        var id = Identifier.of(itemId.getNamespace(), "smithing_" + itemId.getPath() + "_" + upgradeType.from.headId().getPath());
                        builder.entries.add(new Entry(id, builderEntry));
                    }
                    // Chest
                    for (var upgradeType: upgrades) {
                        var ingredientId = upgradeType.ingredient().getRegistryEntry().getKey().get().getValue().toString();
                        var builderEntry = SmithingUpgradeRecipe.ofStringsWithConditions(
                                template.id().toString(),
                                upgradeType.from.chestId().toString(),
                                ingredientId,
                                upgradeType.to.idOf(upgradeType.to.chest).toString(),
                                upgradeType.from.namespace()
                        );
                        var itemId = upgradeType.to.idOf(upgradeType.to.chest);
                        var id = Identifier.of(itemId.getNamespace(), "smithing_" + itemId.getPath() + "_" + upgradeType.from.chestId().getPath());
                        builder.entries.add(new Entry(id, builderEntry));
                    }
                    // Legs
                    for (var upgradeType: upgrades) {
                        var ingredientId = upgradeType.ingredient().getRegistryEntry().getKey().get().getValue().toString();
                        var builderEntry = SmithingUpgradeRecipe.ofStringsWithConditions(
                                template.id().toString(),
                                upgradeType.from.legsId().toString(),
                                ingredientId,
                                upgradeType.to.idOf(upgradeType.to.legs).toString(),
                                upgradeType.from.namespace()
                        );
                        var itemId = upgradeType.to.idOf(upgradeType.to.legs);
                        var id = Identifier.of(itemId.getNamespace(), "smithing_" + itemId.getPath() + "_" + upgradeType.from.legsId().getPath());
                        builder.entries.add(new Entry(id, builderEntry));
                    }
                    // Feet
                    for (var upgradeType: upgrades) {
                        var ingredientId = upgradeType.ingredient().getRegistryEntry().getKey().get().getValue().toString();
                        var builderEntry = SmithingUpgradeRecipe.ofStringsWithConditions(
                                template.id().toString(),
                                upgradeType.from.feetId().toString(),
                                ingredientId,
                                upgradeType.to.idOf(upgradeType.to.feet).toString(),
                                upgradeType.from.namespace()
                        );
                        var itemId = upgradeType.to.idOf(upgradeType.to.feet);
                        var id = Identifier.of(itemId.getNamespace(), "smithing_" + itemId.getPath() + "_" + upgradeType.from.feetId().getPath());
                        builder.entries.add(new Entry(id, builderEntry));
                    }
                }
            }
        }
    }
}
