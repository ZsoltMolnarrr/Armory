package net.armory.fabric.datagen;

import net.armory.fabric.datagen.recipe.SmithingRecipeProvider;
import net.armory.fabric.datagen.recipe.SmithingUpgradeRecipe;
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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
import net.spell_engine.api.item.set.EquipmentSet;
import net.spell_engine.api.item.set.EquipmentSetRegistry;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.item.Armor;
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
            var armorTagOptions = new ArmorOptions(false, true);
            generateArmorTags(
                    List.of(ArmorSets.strider, ArmorSets.riftstalker),
                    RPGSeriesItemTags.ArmorMetaType.ARCHERY,
                    armorTagOptions
            );
            generateArmorTags(
                    List.of(ArmorSets.destroyer, ArmorSets.deathmantle, ArmorSets.justicar,
                            ArmorSets.onslaught, ArmorSets.slayer, ArmorSets.lightbringer),
                    RPGSeriesItemTags.ArmorMetaType.MELEE,
                    armorTagOptions
            );
            generateArmorTags(
                    ArmorSets.entries.stream().filter(entry -> entry.name().contains("robe")).toList(),
                    RPGSeriesItemTags.ArmorMetaType.MAGIC,
                    armorTagOptions
            );
            var tierTag = RPGSeriesItemTags.LootTiers.get(ArmorSets.astral.lootProperties().tier(), RPGSeriesItemTags.LootCategory.ARMORS);
            SmithingTemplates.ENTRIES.forEach(entry -> {
                var tag = getOrCreateTagBuilder(tierTag);
                tag.addOptional(entry.id());
            });
            SmithingIngredients.ENTRIES.forEach(entry -> {
                var tag = getOrCreateTagBuilder(tierTag);
                tag.addOptional(entry.id());
            });

            // Loot-filtering tags splitting the two crystal batches, so a boss can drop one batch each.
            // The upgrade template belongs in both, since either batch needs it to craft.
            var epicArmorA = TagKey.of(RegistryKeys.ITEM, Identifier.of(ArmoryMod.NAMESPACE, "loot/epic_armor_a"));
            var epicArmorB = TagKey.of(RegistryKeys.ITEM, Identifier.of(ArmoryMod.NAMESPACE, "loot/epic_armor_b"));
            SmithingTemplates.ENTRIES.forEach(entry -> {
                getOrCreateTagBuilder(epicArmorA).addOptional(entry.id());
                getOrCreateTagBuilder(epicArmorB).addOptional(entry.id());
            });
            SmithingIngredients.ENTRIES.forEach(entry -> {
                // Forgotten crystals are the second (new) batch; the rest are the first batch.
                var tag = entry.name().contains("forgotten") ? epicArmorB : epicArmorA;
                getOrCreateTagBuilder(tag).addOptional(entry.id());
            });
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

            translationBuilder.add(SmithingIngredients.UpgradeCrystal.HINT_TRANSLATION_KEY, "Armor upgrade crystal");
            SmithingIngredients.ENTRIES.forEach(entry -> {
                translationBuilder.add(entry.item().get().getTranslationKey(), entry.translations().itemName());
                translationBuilder.add(entry.appliesToTranslationKey(), entry.appliesToClassesTranslation());
            });
            SmithingTemplates.ENTRIES.forEach(entry -> {
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
            SmithingIngredients.ENTRIES.forEach(entry -> {
                itemModelGenerator.register(entry.item().get(), Models.GENERATED);
            });
            SmithingTemplates.ENTRIES.forEach(entry -> {
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
        @Override
        public void generate(RecipeExporter recipeExporter) {
            SmithingTemplates.ENTRIES.forEach(entry -> {
                FabricRecipeProvider.offerSmithingTemplateCopyingRecipe(recipeExporter, entry.item().get(), Items.DIAMOND);
            });
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

        public record Upgrade(ArmorIdSet from, Armor.Set to, SmithingIngredients.Entry ingredient) {
            public Upgrade prefixVariant(String variant) {
                return new Upgrade(
                        new ArmorIdSet(from.namespace(), variant + "_" + from.name()),
                        to,
                        ingredient
                );
            }

            /// Base armor id -> upgraded armor id, per equipment slot
            public List<Map.Entry<Identifier, Identifier>> slots() {
                return List.of(
                        Map.entry(from.headId(), to.idOf(to.head)),
                        Map.entry(from.chestId(), to.idOf(to.chest)),
                        Map.entry(from.legsId(), to.idOf(to.legs)),
                        Map.entry(from.feetId(), to.idOf(to.feet))
                );
            }
        }

        /// Each class has two legendary sets, crafted from the very same base armor.
        /// The crystal (the smithing addition) is what decides which of the two is produced.
        public Map<FightClass, List<Upgrade>> UPGRADES = Map.of(
                FightClass.ARCHER, List.of(
                        new Upgrade(
                                new ArmorIdSet("archers", "ranger_armor"),
                                ArmorSets.strider.armorSet(),
                                SmithingIngredients.VANQUISHER
                        ),
                        new Upgrade(
                                new ArmorIdSet("archers", "ranger_armor"),
                                ArmorSets.riftstalker.armorSet(),
                                SmithingIngredients.VANQUISHER_FORGOTTEN
                        )),
                FightClass.ARCANE_WIZARD, List.of(
                        new Upgrade(
                                new ArmorIdSet("wizards", "arcane_robe"),
                                ArmorSets.astral.armorSet(),
                                SmithingIngredients.CONQUEROR
                        ),
                        new Upgrade(
                                new ArmorIdSet("wizards", "arcane_robe"),
                                ArmorSets.tempest.armorSet(),
                                SmithingIngredients.CONQUEROR_FORGOTTEN
                        )),
                FightClass.FIRE_WIZARD, List.of(
                        new Upgrade(
                                new ArmorIdSet("wizards", "fire_robe"),
                                ArmorSets.scarlet.armorSet(),
                                SmithingIngredients.CONQUEROR
                        ),
                        new Upgrade(
                                new ArmorIdSet("wizards", "fire_robe"),
                                ArmorSets.smouldering.armorSet(),
                                SmithingIngredients.CONQUEROR_FORGOTTEN
                        )),
                FightClass.FROST_WIZARD, List.of(
                        new Upgrade(
                                new ArmorIdSet("wizards", "frost_robe"),
                                ArmorSets.glacier.armorSet(),
                                SmithingIngredients.VANQUISHER
                        ),
                        new Upgrade(
                                new ArmorIdSet("wizards", "frost_robe"),
                                ArmorSets.rimeweave.armorSet(),
                                SmithingIngredients.VANQUISHER_FORGOTTEN
                        )),
                FightClass.PRIEST, List.of(
                        new Upgrade(
                                new ArmorIdSet("paladins", "prior_robe"),
                                ArmorSets.avatar.armorSet(),
                                SmithingIngredients.REDEEMER
                        ),
                        new Upgrade(
                                new ArmorIdSet("paladins", "prior_robe"),
                                ArmorSets.absolution.armorSet(),
                                SmithingIngredients.REDEEMER_FORGOTTEN
                        )),
                FightClass.PALADIN, List.of(
                        new Upgrade(
                                new ArmorIdSet("paladins", "crusader_armor"),
                                ArmorSets.justicar.armorSet(),
                                SmithingIngredients.REDEEMER
                        ),
                        new Upgrade(
                                new ArmorIdSet("paladins", "crusader_armor"),
                                ArmorSets.lightbringer.armorSet(),
                                SmithingIngredients.REDEEMER_FORGOTTEN
                        )),
                FightClass.ROGUE, List.of(
                        new Upgrade(
                                new ArmorIdSet("rogues", "assassin_armor"),
                                ArmorSets.deathmantle.armorSet(),
                                SmithingIngredients.CHAMPION
                        ),
                        new Upgrade(
                                new ArmorIdSet("rogues", "assassin_armor"),
                                ArmorSets.slayer.armorSet(),
                                SmithingIngredients.CHAMPION_FORGOTTEN
                        )),
                FightClass.WARRIOR, List.of(
                        new Upgrade(
                                new ArmorIdSet("rogues", "berserker_armor"),
                                ArmorSets.destroyer.armorSet(),
                                SmithingIngredients.CHAMPION
                        ),
                        new Upgrade(
                                new ArmorIdSet("rogues", "berserker_armor"),
                                ArmorSets.onslaught.armorSet(),
                                SmithingIngredients.CHAMPION_FORGOTTEN
                        ))
        );


        @Override
        public void generateRecipes(Builder builder) {
            var includeNetherite = true;

            for (var template: SmithingTemplates.ENTRIES) {
                for (var upgrades: UPGRADES.values()) {
                    if (includeNetherite) {
                        var list = new ArrayList<Upgrade>(upgrades);
                        for (var upgrade: upgrades) {
                            list.add(upgrade.prefixVariant("netherite"));
                        }
                        upgrades = list;
                    }

                    for (var upgrade: upgrades) {
                        var ingredientId = upgrade.ingredient().id().toString();
                        for (var slot: upgrade.slots()) {
                            var baseId = slot.getKey();
                            var resultId = slot.getValue();
                            var recipe = SmithingUpgradeRecipe.ofStringsWithConditions(
                                    template.id().toString(),
                                    baseId.toString(),
                                    ingredientId,
                                    resultId.toString(),
                                    upgrade.from.namespace()
                            );
                            var id = Identifier.of(resultId.getNamespace(),
                                    "smithing_" + resultId.getPath() + "_" + baseId.getPath());
                            builder.entries.add(new Entry(id, recipe));
                        }
                    }
                }
            }
        }
    }
}
