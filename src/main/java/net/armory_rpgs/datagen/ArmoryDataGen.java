package net.armory_rpgs.datagen;

import net.armory_rpgs.item.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.spell.ArmoryEffects;
import net.armory_rpgs.spell.ArmorySounds;
import net.armory_rpgs.spell.ArmorySpells;
import net.spell_engine.api.datagen.SimpleSoundGeneratorV2;
import net.spell_engine.api.datagen.SpellGenerator;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
}
