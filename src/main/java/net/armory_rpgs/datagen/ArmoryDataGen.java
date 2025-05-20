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
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
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

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            var all = getOrCreateTagBuilder(ArmoryItemTags.ALL);
            ArmoryWeapons.entries.forEach(entry -> all.addOptional(entry.id()));
            generateWeaponTags(ArmoryWeapons.entries);

            var bowEntries = ArmoryBows.entries.stream().map(entry ->
                    new RPGSeriesDataGen.BowEntry(entry.id(), entry.weaponType, entry.lootProperties)
            ).toList();
            generateBowTags(bowEntries);

            var shieldEntries = ArmoryShields.entries.stream().map(entry ->
                    new RPGSeriesDataGen.ShieldEntry(entry.id(), entry.lootProperties)
            ).toList();
            generateShieldTags(shieldEntries);
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
//            ArmoryWeapons.entries.forEach(entry ->
//                translationBuilder.add(entry.item().getTranslationKey(), entry.translatedName())
//            );
//            ArmoryBows.entries.forEach(entry ->
//                translationBuilder.add(entry.item().getTranslationKey(), entry.translatedName())
//            );
//            ArmoryShields.entries.forEach(entry ->
//                translationBuilder.add(entry.translationKey(), entry.translatedName())
//            );
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
//            RelicItems.entries.forEach(entry -> {
//                itemModelGenerator.register(entry.item().get(), Models.GENERATED);
//            });
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
