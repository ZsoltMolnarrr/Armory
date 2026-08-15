package net.armory_rpgs.item;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.spell.ArmorySounds;
import net.armory_rpgs.spell.SetBonuses;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
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
import net.spell_engine.rpg_series.config.ArmorSetConfig;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.api.entity.SpellEngineAttributes;
import net.spell_engine.api.spell.SpellDataComponents;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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
    private static final String CRIT_MOD_ID = "critical_strike";
    private static final Identifier CRIT_CHANCE_ID = Identifier.of(CRIT_MOD_ID, "chance");
    private static final Identifier CRIT_DAMAGE_ID = Identifier.of(CRIT_MOD_ID, "damage");

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

    private static AttributeModifier evasionBonus(float value) {
        return new AttributeModifier(
                SpellEngineAttributes.EVASION_CHANCE.id.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier toughnessBonus(float value) {
        return new AttributeModifier(
                ARMOR_TOUGHNESS_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier rangedDamageMultiplier(float value) {
        return new AttributeModifier(
                EntityAttributes_RangedWeapon.DAMAGE.id,
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier rangedHasteMultiplier(float value) {
        return new AttributeModifier(
                EntityAttributes_RangedWeapon.HASTE.id,
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier critChance(float value) {
        return new AttributeModifier(
                CRIT_CHANCE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier critDamage(float value) {
        return new AttributeModifier(
                CRIT_DAMAGE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }


    public static final int enchantability = 18;

    public static RegistryEntry<ArmorMaterial> wizard_robe = material(
            "wizard_robe",
            1, 3, 2, 1,
            enchantability,
            ArmorySounds.cloth_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static RegistryEntry<ArmorMaterial> priest_robe = material(
            "priest_robe",
            1, 3, 2, 1,
            enchantability,
            ArmorySounds.cloth_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> archer_armor = material(
            "archer_armor",
            2, 4, 4, 2,
            enchantability,
            ArmorySounds.leather_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> rogue_armor = material(
            "rogue_armor",
            2, 4, 4, 2,
            enchantability,
            ArmorySounds.leather_equip.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

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

    private static final float caster_spell_power = 0.35F;
    private static final float priest_haste = 0.05F;
    private static final float paladin_spell_power = 1.5F;
    // Lightbringer trades a slice of Justicar's healing power for offense
    private static final float lightbringer_spell_power = 1.25F;
    private static final float lightbringer_damage = 0.03F;

    public static final float rogue_evasion = 0.05F;
    public static final float rogue_haste = 0.05F;
    public static final float rogue_damage = 0.06F;
    public static final float rogue_crit_chance = 0.03F;
    public static final float rogue_crit_damage = 0.05F;
    public static final float rogue_movement_speed = 0.04F;

    public static final float warrior_damage = 0.06F;
    public static final float warrior_knockback = 0.1F;
    public static final float warrior_crit_damage = 0.06F;
    public static final float warrior_haste = 0.04F;
    public static final float warrior_crit_chance = 0.03F;

    public static final float arrow_haste = 0.05F;
    public static final float arrow_damage = 0.1F;
    public static final float archer_evasion = 0.04F;
    public static final float archer_crit_chance = 0.03F;

    private static final float crit_damage_t3 = 0.08F;
    private static final float crit_chance_t3 = 0.03F;
    private static final float haste_t3 = 0.03F;

    public static final int durability = 40;

    private static Armor.ItemSettingsTweaker commonSettings(Identifier equipmentSetId) {
        return Armor.ItemSettingsTweaker.standard(itemSettings -> {
            itemSettings
                    .component(SpellDataComponents.EQUIPMENT_SET, equipmentSetId)
                    .component(DataComponentTypes.RARITY, Rarity.RARE);
        });
    }

    public static final Armor.Entry astral = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "astral_robe"),
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
            commonSettings(SetBonuses.astral.id()) )
            .translatedName("Astral Hat", "Astral Robe", "Astral Breeches", "Astral Boots");


    public static final Armor.Entry scarlet = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "scarlet_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3))
            ),
            commonSettings(SetBonuses.scarlet.id()) )
            .translatedName("Scarlet Hat", "Scarlet Robe", "Scarlet Breeches", "Scarlet Boots");

    public static final Armor.Entry glacier = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "glacier_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(3)
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(2)
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3)),
                    new ArmorSetConfig.Piece(1)
                            .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                            .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3))
            ),
            commonSettings(SetBonuses.glacier.id()) )
            .translatedName("Glacier Hat", "Glacier Robe", "Glacier Breeches", "Glacier Boots");

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
                            .add(knockbackBonus(warrior_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_damage),
                                    toughnessBonus(plate_toughness),
                                    critDamage(warrior_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(8)
                            .add(damageMultiplier(warrior_damage))
                            .add(toughnessBonus(plate_toughness))
                            .add(knockbackBonus(warrior_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_damage),
                                    toughnessBonus(plate_toughness),
                                    critDamage(warrior_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(6)
                            .add(damageMultiplier(warrior_damage))
                            .add(toughnessBonus(plate_toughness))
                            .add(knockbackBonus(warrior_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_damage),
                                    toughnessBonus(plate_toughness),
                                    critDamage(warrior_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .add(damageMultiplier(warrior_damage))
                            .add(toughnessBonus(plate_toughness))
                            .add(knockbackBonus(warrior_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_damage),
                                    toughnessBonus(plate_toughness),
                                    critDamage(warrior_crit_damage)
                            ))
            ),
            commonSettings(SetBonuses.destroyer.id()) )
            .translatedName("Destroyer Greathelm", "Destroyer Chestplate", "Destroyer Greaves", "Destroyer Boots");

    public static final Armor.Entry deathmantle = create(
            rogue_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "deathmantle_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .add(evasionBonus(rogue_evasion))
                            .add(hasteMultiplier(rogue_haste))
                            .add(damageMultiplier(rogue_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_evasion),
                                    hasteMultiplier(rogue_haste),
                                    critChance(rogue_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(4)
                            .add(evasionBonus(rogue_evasion))
                            .add(hasteMultiplier(rogue_haste))
                            .add(damageMultiplier(rogue_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_evasion),
                                    hasteMultiplier(rogue_haste),
                                    critChance(rogue_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(4)
                            .add(evasionBonus(rogue_evasion))
                            .add(hasteMultiplier(rogue_haste))
                            .add(damageMultiplier(rogue_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_evasion),
                                    hasteMultiplier(rogue_haste),
                                    critChance(rogue_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .add(evasionBonus(rogue_evasion))
                            .add(hasteMultiplier(rogue_haste))
                            .add(damageMultiplier(rogue_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_evasion),
                                    hasteMultiplier(rogue_haste),
                                    critChance(rogue_crit_chance)
                            ))
            ),
            commonSettings(SetBonuses.deathmantle.id()) )
            .translatedName("Deathmantle Hood", "Deathmantle Tunic", "Deathmantle Leggings", "Deathmantle Boots");

    public static final Armor.Entry strider = create(
            archer_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "strider_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .add(rangedDamageMultiplier(arrow_damage))
                            .add(rangedHasteMultiplier(arrow_haste)),
                    new ArmorSetConfig.Piece(4)
                            .add(rangedDamageMultiplier(arrow_damage))
                            .add(rangedHasteMultiplier(arrow_haste)),
                    new ArmorSetConfig.Piece(4)
                            .add(rangedDamageMultiplier(arrow_damage))
                            .add(rangedHasteMultiplier(arrow_haste)),
                    new ArmorSetConfig.Piece(2)
                            .add(rangedDamageMultiplier(arrow_damage))
                            .add(rangedHasteMultiplier(arrow_haste))
            ),
            commonSettings(SetBonuses.strider.id()) )
            .translatedName("Strider Hood", "Strider Tunic", "Strider Leggings", "Strider Boots");

    /// Builds an armor set config where every piece carries the same modifiers, only the armor value differs.
    private static ArmorSetConfig pieces(int head, int chest, int legs, int feet, UnaryOperator<ArmorSetConfig.Piece> modifiers) {
        return ArmorSetConfig.with(
                modifiers.apply(new ArmorSetConfig.Piece(head)),
                modifiers.apply(new ArmorSetConfig.Piece(chest)),
                modifiers.apply(new ArmorSetConfig.Piece(legs)),
                modifiers.apply(new ArmorSetConfig.Piece(feet))
        );
    }

    // MARK: - Forgotten sets
    // Second legendary set of each class, crafted from the same base armor using a Forgotten crystal.

    public static final Armor.Entry tempest = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "tempest_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            pieces(1, 3, 2, 1, piece -> piece
                    .add(AttributeModifier.multiply(SpellSchools.ARCANE.id, caster_spell_power))
                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_DAMAGE.id, crit_damage_t3))),
            commonSettings(SetBonuses.tempest.id()) )
            .translatedName("Tempest Hat", "Tempest Robe", "Tempest Breeches", "Tempest Boots");

    public static final Armor.Entry smouldering = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "smouldering_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            pieces(1, 3, 2, 1, piece -> piece
                    .add(AttributeModifier.multiply(SpellSchools.FIRE.id, caster_spell_power))
                    .add(AttributeModifier.multiply(SpellPowerMechanics.HASTE.id, haste_t3))),
            commonSettings(SetBonuses.smouldering.id()) )
            .translatedName("Smouldering Hat", "Smouldering Robe", "Smouldering Breeches", "Smouldering Boots");

    public static final Armor.Entry rimeweave = create(
            wizard_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "rimeweave_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            pieces(1, 3, 2, 1, piece -> piece
                    .add(AttributeModifier.multiply(SpellSchools.FROST.id, caster_spell_power))
                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3))),
            commonSettings(SetBonuses.rimeweave.id()) )
            .translatedName("Rimeweave Hat", "Rimeweave Robe", "Rimeweave Breeches", "Rimeweave Boots");

    public static final Armor.Entry absolution = create(
            priest_robe,
            Identifier.of(ArmoryMod.NAMESPACE, "absolution_robe"),
            durability,
            5,
            Armor.CustomItem::new,
            pieces(1, 3, 2, 1, piece -> piece
                    .add(AttributeModifier.multiply(SpellSchools.HEALING.id, caster_spell_power))
                    .add(AttributeModifier.multiply(SpellPowerMechanics.CRITICAL_CHANCE.id, crit_chance_t3))),
            commonSettings(SetBonuses.absolution.id()) )
            .translatedName("Absolution Cowl", "Absolution Vestment", "Absolution Breeches", "Absolution Boots");

    public static final Armor.Entry lightbringer = create(
            paladin_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "lightbringer_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            pieces(3, 8, 6, 3, piece -> piece
                    .add(toughnessBonus(plate_toughness))
                    .add(damageMultiplier(lightbringer_damage))
                    .addAll(AttributeModifier.bonuses(List.of(SpellSchools.HEALING.id), lightbringer_spell_power))),
            commonSettings(SetBonuses.lightbringer.id()) )
            .translatedName("Lightbringer Crown", "Lightbringer Chestplate", "Lightbringer Legguards", "Lightbringer Boots");

    public static final Armor.Entry onslaught = create(
            warrior_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "onslaught_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            pieces(3, 8, 6, 3, piece -> piece
                    .add(damageMultiplier(warrior_damage))
                    .add(toughnessBonus(plate_toughness))
                    .add(hasteMultiplier(warrior_haste))
                    .addConditional(CRIT_MOD_ID, List.of(
                            damageMultiplier(warrior_damage),
                            toughnessBonus(plate_toughness),
                            critChance(warrior_crit_chance)
                    ))),
            commonSettings(SetBonuses.onslaught.id()) )
            .translatedName("Onslaught Greathelm", "Onslaught Chestplate", "Onslaught Greaves", "Onslaught Boots");

    public static final Armor.Entry slayer = create(
            rogue_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "slayer_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            pieces(2, 4, 4, 2, piece -> piece
                    .add(evasionBonus(rogue_evasion))
                    .add(hasteMultiplier(rogue_haste))
                    .add(damageMultiplier(rogue_damage))
                    .addConditional(CRIT_MOD_ID, List.of(
                            evasionBonus(rogue_evasion),
                            hasteMultiplier(rogue_haste),
                            critDamage(rogue_crit_damage)
                    ))),
            commonSettings(SetBonuses.slayer.id()) )
            .translatedName("Slayer Hood", "Slayer Tunic", "Slayer Leggings", "Slayer Boots");

    public static final Armor.Entry riftstalker = create(
            archer_armor,
            Identifier.of(ArmoryMod.NAMESPACE, "riftstalker_armor"),
            durability,
            5,
            Armor.CustomItem::new,
            pieces(2, 4, 4, 2, piece -> piece
                    .add(rangedDamageMultiplier(arrow_damage))
                    .add(evasionBonus(archer_evasion))
                    .addConditional(CRIT_MOD_ID, List.of(
                            rangedDamageMultiplier(arrow_damage),
                            critChance(archer_crit_chance)
                    ))),
            commonSettings(SetBonuses.riftstalker.id()) )
            .translatedName("Riftstalker Hood", "Riftstalker Tunic", "Riftstalker Leggings", "Riftstalker Boots");

    public static void register(Map<String, ArmorSetConfig> configs) {
//        for (var entry: entries) {
//            System.out.println("ASD -> /isorender entity minecraft:armor_stand {ShowArms:1b,ArmorItems:[{id:\""
//                    + entry.armorSet().idStrings().get(3)
//                    + "\"},{id:\""+ entry.armorSet().idStrings().get(2)
//                    + "\"},{id:\"" + entry.armorSet().idStrings().get(1)
//                    +  "\"},{id:\"" + entry.armorSet().idStrings().get(0) +  "\"}]}");
//        }
        Armor.register(configs, entries, Group.KEY);
    }
}
