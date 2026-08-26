package net.armory.fabric.datagen;

import net.armory.fabric.datagen.recipe.SmithingRecipeProvider;
import net.armory.fabric.datagen.recipe.SmithingUpgradeRecipe;
import net.armory_rpgs.item.*;
import net.armory_rpgs.spell.SetBonuses;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
        pack.addProvider(TemplateRecipeGenerator::new);
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
        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
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
                var tag = builder(tierTag);
                tag.addOptional(itemKey(entry.id()));
            });
            SmithingIngredients.ENTRIES.forEach(entry -> {
                var tag = builder(tierTag);
                tag.addOptional(itemKey(entry.id()));
            });

            // Loot-filtering tags splitting the two crystal batches, so a boss can drop one batch each.
            // The upgrade template belongs in both, since either batch needs it to craft.
            var epicArmorA = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "loot/epic_armor_a"));
            var epicArmorB = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "loot/epic_armor_b"));
            SmithingTemplates.ENTRIES.forEach(entry -> {
                builder(epicArmorA).addOptional(itemKey(entry.id()));
                builder(epicArmorB).addOptional(itemKey(entry.id()));
            });
            SmithingIngredients.ENTRIES.forEach(entry -> {
                // Forgotten crystals are the second (new) batch; the rest are the first batch.
                var tag = entry.name().contains("forgotten") ? epicArmorB : epicArmorA;
                builder(tag).addOptional(itemKey(entry.id()));
            });
        }

        /// Tag builders take a `RegistryKey` since 1.21.6, not an `Identifier`.
        private static ResourceKey<Item> itemKey(Identifier id) {
            return ResourceKey.create(Registries.ITEM, id);
        }
    }

    public static class SpellTagGenerator extends FabricTagProvider<Spell> {
        public SpellTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, SpellRegistry.KEY, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            ArmorySpells.all.forEach(entry -> {
                for (var category: entry.categories()) {
                    var tagKey = TagKey.create(SpellRegistry.KEY, Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, category.toString().toLowerCase()));
                    var tag = builder(tagKey);
                    tag.addOptional(ResourceKey.create(SpellRegistry.KEY, entry.id()));
                }
            });
        }
    }

    public static class LangGenerator extends FabricLanguageProvider {
        protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, "en_us", registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(Group.translationKey, "Armory");

            translationBuilder.add(SmithingIngredients.UpgradeCrystal.HINT_TRANSLATION_KEY, "Armor upgrade crystal");
            SmithingIngredients.ENTRIES.forEach(entry -> {
                translationBuilder.add(entry.item().get().getDescriptionId(), entry.translations().itemName());
                translationBuilder.add(entry.appliesToTranslationKey(), entry.appliesToClassesTranslation());
            });
            SmithingTemplates.ENTRIES.forEach(entry -> {
                translationBuilder.add(entry.item().get().getDescriptionId(), entry.translations().itemName());
                translationBuilder.add(entry.upgradeTranslationKey(), entry.translations().upgradeName());
                translationBuilder.add(entry.baseSlotDescriptionTranslationKey(), entry.translations().baseSlotDescription());
                translationBuilder.add(entry.additionsSlotDescriptionTranslationKey(), entry.translations().additionsSlotDescription());
                translationBuilder.add(entry.appliesToTranslationKey(), entry.translations().appliesTo());
                translationBuilder.add(entry.ingredientsTranslationKey(), entry.translations().ingredients());
            });
            ArmorSets.entries.forEach(entry -> {
                var translations = new LinkedHashMap<String, String>();
                translations.put(((Item)entry.armorSet().head).getDescriptionId(), entry.armorSet().headTranslation);
                translations.put(((Item)entry.armorSet().chest).getDescriptionId(), entry.armorSet().chestTranslation);
                translations.put(((Item)entry.armorSet().legs).getDescriptionId(), entry.armorSet().legsTranslation);
                translations.put(((Item)entry.armorSet().feet).getDescriptionId(), entry.armorSet().feetTranslation);
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
                translationBuilder.add(entry.effect.getDescriptionId(), entry.title);
                translationBuilder.add(entry.effect.getDescriptionId() + ".description", entry.description);
            });
        }
    }

    public static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerator) {
            SmithingIngredients.ENTRIES.forEach(entry -> {
                itemModelGenerator.generateFlatItem(entry.item().get(), ModelTemplates.FLAT_ITEM);
            });
            SmithingTemplates.ENTRIES.forEach(entry -> {
                itemModelGenerator.generateFlatItem(entry.item().get(), ModelTemplates.FLAT_ITEM);
            });
            ArmorSets.entries.forEach(entry -> {
                for (var piece: entry.armorSet().pieces()) {
                    itemModelGenerator.generateFlatItem((Item) piece, ModelTemplates.FLAT_ITEM);
                }
            });
        }
    }

    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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
        public SoundGen(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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

        public EquipmentSetGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            HolderGetter<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
            for (var set: SetBonuses.all) {
                var items = HolderSet.direct(
                        set.itemSupplier().get().stream()
                        .map(id -> itemLookup.getOrThrow(ResourceKey.create(Registries.ITEM, id)))
                        .toList()
                );
                entries.add(
                        ResourceKey.create(EquipmentSetRegistry.KEY, set.id()),
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

    /// 1.21.11 moved the recipe helpers off the provider onto `net.minecraft.data.recipe.RecipeGenerator`,
    /// which the provider now returns from `getRecipeGenerator`.
    public static class TemplateRecipeGenerator extends FabricRecipeProvider {
        public TemplateRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public String getName() {
            return "Armory Smithing Template Copying Recipes";
        }

        @Override
        protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
            return new net.minecraft.data.recipes.RecipeProvider(registries, exporter) {
                @Override
                public void buildRecipes() {
                    SmithingTemplates.ENTRIES.forEach(entry -> {
                        copySmithingTemplate(entry.item().get(), Items.DIAMOND);
                    });
                }
            };
        }
    }

    public static class SmithGen extends SmithingRecipeProvider {

        public SmithGen(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        public record ArmorIdSet(String namespace, String name) {
            public Identifier headId() {
                return Identifier.fromNamespaceAndPath(namespace, name + "_" + EquipmentSlot.HEAD.getSerializedName().toLowerCase());
            }
            public Identifier chestId() {
                return Identifier.fromNamespaceAndPath(namespace, name + "_" + EquipmentSlot.CHEST.getSerializedName().toLowerCase());
            }
            public Identifier legsId() {
                return Identifier.fromNamespaceAndPath(namespace, name + "_" + EquipmentSlot.LEGS.getSerializedName().toLowerCase());
            }
            public Identifier feetId() {
                return Identifier.fromNamespaceAndPath(namespace, name + "_" + EquipmentSlot.FEET.getSerializedName().toLowerCase());
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
                            var id = Identifier.fromNamespaceAndPath(resultId.getNamespace(),
                                    "smithing_" + resultId.getPath() + "_" + baseId.getPath());
                            builder.entries.add(new Entry(id, recipe));
                        }
                    }
                }
            }
        }
    }
}
