package net.armory_rpgs.item;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.spell.ArmorySounds;
import net.armory_rpgs.spell.SetBonuses;
import net.minecraft.component.DataComponentTypes;
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
import net.minecraft.util.Rarity;
import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.spell.SpellDataComponents;
import net.spell_power.api.SpellPowerMechanics;
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
                Equipment.LootProperties.of(tier),
                settings
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
    private static final Identifier ATTACK_SPEED_ID = Identifier.ofVanilla("generic.attack_speed");
    private static final Identifier KNOCKBACK_ID = Identifier.ofVanilla("generic.knockback_resistance");
    private static final Identifier MOVEMENT_SPEED_ID = Identifier.ofVanilla("generic.movement_speed");
    private static final Identifier ARMOR_TOUGHNESS_ID = Identifier.ofVanilla("generic.armor_toughness");

    private static AttributeModifier damageMultiplier(float value) {
        return new AttributeModifier(
                ATTACK_DAMAGE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier hasteMultiplier(float value) {
        return new AttributeModifier(
                ATTACK_SPEED_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier knockbackBonus(float value) {
        return new AttributeModifier(
                KNOCKBACK_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier movementSpeed(float value) {
        return new AttributeModifier(
                MOVEMENT_SPEED_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier toughnessBonus(float value) {
        return new AttributeModifier(
                ARMOR_TOUGHNESS_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }


    public static final int enchantability = 18;

    public static RegistryEntry<ArmorMaterial> wizard_robe = material(
            "wizard_robe",
            1, 3, 2, 1,
            enchantability,
            ArmorySounds.plate_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static RegistryEntry<ArmorMaterial> priest_robe = material(
            "priest_robe",
            1, 3, 2, 1,
            enchantability,
            ArmorySounds.plate_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> archer_armor = material(
            "archer_armor",
            2, 4, 4, 2,
            enchantability,
            ArmorySounds.plate_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> rogue_armor = material(
            "rogue_armor",
            2, 4, 4, 2,
            enchantability,
            ArmorySounds.plate_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> paladin_armor = material(
            "paladin_armor",
            3, 8, 6, 3,
            enchantability,
            ArmorySounds.plate_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static RegistryEntry<ArmorMaterial> warrior_armor = material(
            "warrior_armor",
            3, 8, 6, 2,
            enchantability,
            ArmorySounds.plate_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    private static final float plate_toughness = 1F;

    private static final float caster_spell_power = 0.3F;
    private static final float priest_haste = 0.05F;
    private static final float paladin_spell_power = 1.5F;

    public static final float rogue_speed = 0.05F;
    public static final float rogue_haste = 0.05F;
    public static final float rogue_damage = 0.05F;

    public static final float warrior_damage = 0.05F;
    public static final float warrior_knockback = 0.1F;

    public static final float arrow_haste = 0.05F;
    public static final float arrow_damage = 0.1F;

    private static final float crit_damage_t3 = 0.1F;
    private static final float crit_chance_t3 = 0.02F;
    private static final float haste_t3 = 0.03F;

    public static final int durability = 40;

    private static Armor.ItemSettingsTweaker commonSettings(Identifier equipmentSetId) {
        return Armor.ItemSettingsTweaker.standard(itemSettings -> {
            itemSettings
                    .component(SpellDataComponents.EQUIPMENT_SET, equipmentSetId)
                    .component(DataComponentTypes.RARITY, Rarity.RARE);
        });
    }

    public static final Armor.Entry justicar = create(
            paladin_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "justicar_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(3)
                            .add(toughnessBonus(plate_toughness))
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_spell_power)),
                    new ArmorSetConfig.Piece(8)
                            .add(toughnessBonus(plate_toughness))
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_spell_power)),
                    new ArmorSetConfig.Piece(6)
                            .add(toughnessBonus(plate_toughness))
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_spell_power)),
                    new ArmorSetConfig.Piece(3)
                            .add(toughnessBonus(plate_toughness))
                            .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), paladin_spell_power))
            ),
            commonSettings(SetBonuses.justicar.id()) )
            .translatedName("Justicar Faceguard", "Justicar Chestplate", "Justicar Legguards", "Justicar Boots");


    public static final Armor.Entry avatar = create(
            priest_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "avatar_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.HEALING.id, caster_spell_power),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, priest_haste)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.HEALING.id, caster_spell_power),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, priest_haste)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.HEALING.id, caster_spell_power),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, priest_haste)
                            )),
                    new ArmorSetConfig.Piece(1)
                            .addAll(List.of(
                                    AttributeModifier.multiply(SpellSchools.HEALING.id, caster_spell_power),
                                    AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, priest_haste)
                            ))
            ),
            commonSettings(SetBonuses.avatar.id()) )
            .translatedName("Avatar Cowl", "Avatar Vestment", "Avatar Breeches", "Avatar Boots");


    public static final Armor.Entry deathmantle = create(
            rogue_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "deathmantle_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .add(movementSpeed(rogue_speed))
                            .add(hasteMultiplier(rogue_haste))
                            .add(damageMultiplier(rogue_damage)),
                    new ArmorSetConfig.Piece(4)
                            .add(movementSpeed(rogue_speed))
                            .add(hasteMultiplier(rogue_haste))
                            .add(damageMultiplier(rogue_damage)),
                    new ArmorSetConfig.Piece(4)
                            .add(movementSpeed(rogue_speed))
                            .add(hasteMultiplier(rogue_haste))
                            .add(damageMultiplier(rogue_damage)),
                    new ArmorSetConfig.Piece(2)
                            .add(movementSpeed(rogue_speed))
                            .add(hasteMultiplier(rogue_haste))
                            .add(damageMultiplier(rogue_damage))
            ),
            commonSettings(SetBonuses.deathmantle.id()) )
            .translatedName("Deathmantle Hood", "Deathmantle Tunic", "Deathmantle Leggings", "Deathmantle Boots");

    public static final Armor.Entry destroyer = create(
            warrior_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "destroyer_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(3)
                            .add(damageMultiplier(warrior_damage))
                            .add(toughnessBonus(plate_toughness))
                            .add(knockbackBonus(warrior_knockback)),
                    new ArmorSetConfig.Piece(8)
                            .add(damageMultiplier(warrior_damage))
                            .add(toughnessBonus(plate_toughness))
                            .add(knockbackBonus(warrior_knockback)),
                    new ArmorSetConfig.Piece(6)
                            .add(damageMultiplier(warrior_damage))
                            .add(toughnessBonus(plate_toughness))
                            .add(knockbackBonus(warrior_knockback)),
                    new ArmorSetConfig.Piece(3)
                            .add(damageMultiplier(warrior_damage))
                            .add(toughnessBonus(plate_toughness))
                            .add(knockbackBonus(warrior_knockback))
            ),
            commonSettings(SetBonuses.destroyer.id()) )
            .translatedName("Destroyer Greathelm", "Destroyer Chestplate", "Destroyer Greaves", "Destroyer Boots");

    public static final Armor.Entry archer = create(
            archer_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "archer"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .add(damageMultiplier(arrow_damage))
                            .add(hasteMultiplier(arrow_haste)),
                    new ArmorSetConfig.Piece(4)
                            .add(damageMultiplier(arrow_damage))
                            .add(hasteMultiplier(arrow_haste)),
                    new ArmorSetConfig.Piece(4)
                            .add(damageMultiplier(arrow_damage))
                            .add(hasteMultiplier(arrow_haste)),
                    new ArmorSetConfig.Piece(2)
                            .add(damageMultiplier(arrow_damage))
                            .add(hasteMultiplier(arrow_haste))
            ),
            commonSettings(SetBonuses.archer.id()) )
            .translatedName("Archer Helm", "Archer Chestplate", "Archer Leggings", "Archer Boots");

    public static final Armor.Entry tempest = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "tempest_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3))
            ),
            commonSettings(SetBonuses.tempest.id()) )
            .translatedName("Tempest Cowl", "Tempest Robe", "Tempest Breeches", "Tempest Boots");

    public static final Armor.Entry smoldering = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "smoldering_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, crit_chance_t3))
            ),
            commonSettings(SetBonuses.smoldering.id()) )
            .translatedName("Smoldering Cowl", "Smoldering Robe", "Smoldering Breeches", "Smoldering Boots");

    public static final Armor.Entry glacier = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "glacier_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, crit_damage_t3))
            ),
            commonSettings(SetBonuses.glacier.id()) )
            .translatedName("Glacier Cowl", "Glacier Robe", "Glacier Breeches", "Glacier Boots");

    public static void register(Map<String, ArmorSetConfig> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}
