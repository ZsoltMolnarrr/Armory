package net.armory_rpgs.item;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.spell.ArmorySounds;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.armor.Armor;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorSets {
    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability, int tier,
                                      Armor.Set.ItemFactory factory, ArmorSetConfig defaults, Armor.ItemSettingsTweaker settings) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults,
                Equipment.LootProperties.of(tier)
        );
        entries.add(entry);
        return entry;
    }

    public static RegistryEntry<ArmorMaterial> material(
            String name, int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
            int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient) {

        var material = new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.HELMET, protectionHead,
                        ArmorItem.Type.CHESTPLATE, protectionChest,
                        ArmorItem.Type.LEGGINGS, protectionLegs,
                        ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                List.of(new ArmorMaterial.Layer(Identifier.of(ArmoryMod.NAMESPACE, name))),
                0,0
        );
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(ArmoryMod.NAMESPACE, name), material);
    }


    private static final Identifier ATTACK_DAMAGE_ID = Identifier.ofVanilla("generic.attack_damage");
    private static final Identifier ARMOR_TOUGHNESS_ID = Identifier.ofVanilla("generic.armor_toughness");
    private static AttributeModifier damageMultiplier(float value) {
        return new AttributeModifier(
                ATTACK_DAMAGE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier toughnessBonus(float value) {
        return new AttributeModifier(
                ARMOR_TOUGHNESS_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }

    public static RegistryEntry<ArmorMaterial> netherite_crusader_armor = material(
            "netherite_crusader_armor",
            3, 8, 6, 3,
            15,
            ArmorySounds.plate_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> netherite_prior_robe = material(
            "netherite_prior_robe",
            1, 3, 2, 1,
            15,
            ArmorySounds.plate_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    private static final float paladin_t1_spell_power = 0.5F;
    private static final float paladin_t2_spell_power = 1F;
    private static final float paladin_t3_spell_power = 1F;
    private static final float paladin_t3_toughness = 1F;

    public static final Armor.Entry justicar = create(
            netherite_crusader_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "justicar_armor"),
            15,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t1_spell_power)),
                    new ArmorSetConfig.Piece(6)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t1_spell_power)),
                    new ArmorSetConfig.Piece(5)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t1_spell_power)),
                    new ArmorSetConfig.Piece(2)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t1_spell_power))
            ), Armor.ItemSettingsTweaker.standard(itemSettings -> {
                // itemSettings.component() ??
            }))
            .translatedName("Justicar Faceguard", "Justicar Chestplate", "Justicar Legguards", "Justicar Boots");

    public static final Armor.Entry destroyer = create(
            netherite_crusader_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "destroyer_armor"),
            15,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t2_spell_power)),
                    new ArmorSetConfig.Piece(6)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t2_spell_power)),
                    new ArmorSetConfig.Piece(5)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t2_spell_power)),
                    new ArmorSetConfig.Piece(2)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t2_spell_power))
            ), Armor.ItemSettingsTweaker.standard(itemSettings -> {
                // itemSettings.component() ??
            }))
            .translatedName("Destroyer Greathelm", "Destroyer Chestplate", "Destroyer Greaves", "Destroyer Boots");

    public static final Armor.Entry deathmantle = create(
            netherite_crusader_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "deathmantle_armor"),
            15,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t3_spell_power)),
                    new ArmorSetConfig.Piece(6)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t3_spell_power)),
                    new ArmorSetConfig.Piece(5)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t3_spell_power)),
                    new ArmorSetConfig.Piece(2)
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_t3_spell_power))
            ), Armor.ItemSettingsTweaker.standard(itemSettings -> {
                // itemSettings.component() ??
            }))
            .translatedName("Deathmantle Hood", "Deathmantle Tunic", "Deathmantle Leggings", "Deathmantle Boots");


    public static void register(Map<String, ArmorSetConfig> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}
