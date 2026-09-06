package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.ArmorSets;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.ItemAttributeModifiers;
import net.spell_engine.api.item.set.EquipmentSet;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.utils.AttributeModifierUtil;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SetBonuses {
    private static final String NAMESPACE = ArmoryMod.NAMESPACE;
    private static final String SET_BONUS = "set_bonus";
    public record Entry(Identifier id, String title, Supplier<List<Identifier>> itemSupplier, List<EquipmentSet.Bonus> bonuses) { }
    public static final List<Entry> all = new ArrayList<>();
    private static Entry add(Entry entry) {
        all.add(entry);
        return entry;
    }

    /// 1.20.1 stand-in for the 1.21 `AttributeModifiersComponent`. Serializes to the exact same
    /// equipment-set JSON, so the generated files are unchanged.
    private static ItemAttributeModifiers attribute(RegistryEntry<EntityAttribute> attribute, double value, EntityAttributeModifier.Operation operation, Identifier id) {
        return ItemAttributeModifiers.builder()
                .add(attribute,
                        AttributeModifierUtil.modifier(id, value, operation),
                        ItemAttributeModifiers.Slot.ARMOR)
                .build();
    }

    private static ItemAttributeModifiers attribute(EntityAttribute attribute, double value, EntityAttributeModifier.Operation operation, Identifier id) {
        return attribute(Registries.ATTRIBUTE.getEntry(attribute), value, operation, id);
    }

    // MARK: - RangedWeaponAPI attributes
    //
    // Armory does not compile against RangedWeaponAPI (a two-platform 1.20.1 artifact exists now, but
    // these two attributes are only ever *written into data*): `ranged_weapon:damage` and
    // `ranged_weapon:haste` are named by id only.
    private static final String RANGED_WEAPON_MOD_ID = "ranged_weapon";
    private static final Identifier RANGED_DAMAGE_ID = new Identifier(RANGED_WEAPON_MOD_ID, "damage");
    private static final Identifier RANGED_HASTE_ID = new Identifier(RANGED_WEAPON_MOD_ID, "haste");

    /// The id-only escape hatch of SpellEngine 1.10.5.004 (`spellengine-port-notes.md` §9.2):
    /// `ItemAttributeModifiers.Entry` stores an `Identifier`, so the modifier is **encoded whether or not
    /// the attribute is registered on the datagen runtime** — which is what let Armory drop its
    /// RangedWeaponAPI datagen pin. It decodes back to itself and is skipped by
    /// `ItemAttributeModifiers#forSlot` only while the attribute is unregistered, so a pack that adds RWA
    /// later picks the bonus up without regenerating data.
    private static ItemAttributeModifiers attributeById(Identifier attributeId, double value, EntityAttributeModifier.Operation operation, Identifier id) {
        return ItemAttributeModifiers.builder()
                .add(attributeId,
                        AttributeModifierUtil.modifier(id, value, operation),
                        ItemAttributeModifiers.Slot.ARMOR)
                .build();
    }

    /// `[attribute bonus, spell bonus]`
    private static List<EquipmentSet.Bonus> attributeAndSpellBonus(int requiredPieceCount, ItemAttributeModifiers attributes,
                                                                   EquipmentSet.Bonus spellBonus) {
        return List.of(EquipmentSet.Bonus.withAttributes(requiredPieceCount, attributes), spellBonus);
    }

    public static Entry justicar = add(justicar());
    private static Entry justicar() {
        var id = new Identifier(NAMESPACE, "justicar");
        return new Entry(id,
                "Justicar Regalia",
                () -> { return ArmorSets.justicar.armorSet().pieceIds(); },
                List.of(
                       EquipmentSet.Bonus.withAttributes(2, attribute(
                           SpellSchools.HEALING.attributeEntry,
                           2,
                           EntityAttributeModifier.Operation.ADDITION,
                           id.withPath(SET_BONUS))
                       ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_divine_protection.id()))
                )
        );
    }

    public static Entry avatar = add(avatar());
    private static Entry avatar() {
        var id = new Identifier(NAMESPACE, "avatar");
        return new Entry(id,
                "Avatar Raiment",
                () -> { return ArmorSets.avatar.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.HEALING.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_circle_of_healing.id()))
                )
        );
    }

    public static Entry destroyer = add(destroyer());
    private static Entry destroyer() {
        var id = new Identifier(NAMESPACE, "destroyer");
        return new Entry(id,
                "Destroyer Armor",
                () -> { return ArmorSets.destroyer.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.05,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_charge.id()))
                )
        );
    }

    public static Entry deathmantle = add(deathmantle());
    private static Entry deathmantle() {
        var id = new Identifier(NAMESPACE, "deathmantle");
        return new Entry(id,
                "Deathmantle",
                () -> { return ArmorSets.deathmantle.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                                0.05,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_shadow_step.id()))
                )
        );
    }

    public static Entry strider = add(strider());
    private static Entry strider() {
        var id = new Identifier(NAMESPACE, "strider");
        return new Entry(id,
                "Strider Armor",
                () -> { return ArmorSets.strider.armorSet().pieceIds(); },
                attributeAndSpellBonus(2,
                        attributeById(RANGED_DAMAGE_ID, 0.05, EntityAttributeModifier.Operation.MULTIPLY_BASE, id.withPath(SET_BONUS)),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_barrage.id()))
                )
        );
    }

    public static Entry astral = add(astral());
    private static Entry astral() {
        var id = new Identifier(NAMESPACE, "astral");
        return new Entry(id,
                "Astral Regalia",
                () -> { return ArmorSets.astral.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.ARCANE.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_arcane_beam.id()))
                )
        );
    }

    public static Entry scarlet = add(scarlet());
    private static Entry scarlet() {
        var id = new Identifier(NAMESPACE, "scarlet");
        return new Entry(id,
                "Scarlet Raiment",
                () -> { return ArmorSets.scarlet.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.FIRE.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_meteor.id()))
                )
        );
    }

    public static Entry glacier = add(glacier());
    private static Entry glacier() {
        var id = new Identifier(NAMESPACE, "glacier");
        return new Entry(id,
                "Glacier Mantle",
                () -> { return ArmorSets.glacier.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.FROST.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_frost_shield.id()))
                )
        );
    }

    // MARK: - Forgotten sets

    public static Entry tempest = add(tempest());
    private static Entry tempest() {
        var id = new Identifier(NAMESPACE, "tempest");
        return new Entry(id,
                "Tempest Regalia",
                () -> { return ArmorSets.tempest.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellPowerMechanics.HASTE.attributeEntry,
                                0.05,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_arcane_barrage.id()))
                )
        );
    }

    public static Entry smouldering = add(smouldering());
    private static Entry smouldering() {
        var id = new Identifier(NAMESPACE, "smouldering");
        return new Entry(id,
                "Smouldering Raiment",
                () -> { return ArmorSets.smouldering.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellPowerMechanics.CRITICAL_CHANCE.attributeEntry,
                                0.05,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_firestorm.id()))
                )
        );
    }

    public static Entry rimeweave = add(rimeweave());
    private static Entry rimeweave() {
        var id = new Identifier(NAMESPACE, "rimeweave");
        return new Entry(id,
                "Rimeweave Mantle",
                () -> { return ArmorSets.rimeweave.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellPowerMechanics.CRITICAL_DAMAGE.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_ice_lance.id()))
                )
        );
    }

    public static Entry absolution = add(absolution());
    private static Entry absolution() {
        var id = new Identifier(NAMESPACE, "absolution");
        return new Entry(id,
                "Absolution Raiment",
                () -> { return ArmorSets.absolution.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellPowerMechanics.HASTE.attributeEntry,
                                0.05,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_penance.id()))
                )
        );
    }

    public static Entry lightbringer = add(lightbringer());
    private static Entry lightbringer() {
        var id = new Identifier(NAMESPACE, "lightbringer");
        return new Entry(id,
                "Lightbringer Regalia",
                () -> { return ArmorSets.lightbringer.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.05,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_judgement.id()))
                )
        );
    }

    public static Entry onslaught = add(onslaught());
    private static Entry onslaught() {
        var id = new Identifier(NAMESPACE, "onslaught");
        return new Entry(id,
                "Onslaught Armor",
                () -> { return ArmorSets.onslaught.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes.GENERIC_ATTACK_SPEED,
                                0.05,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_shout.id()))
                )
        );
    }

    public static Entry slayer = add(slayer());
    private static Entry slayer() {
        var id = new Identifier(NAMESPACE, "slayer");
        return new Entry(id,
                "Slayer Armor",
                () -> { return ArmorSets.slayer.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.05,
                                EntityAttributeModifier.Operation.MULTIPLY_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_bear_trap.id()))
                )
        );
    }

    public static Entry riftstalker = add(riftstalker());
    private static Entry riftstalker() {
        var id = new Identifier(NAMESPACE, "riftstalker");
        return new Entry(id,
                "Riftstalker Armor",
                () -> { return ArmorSets.riftstalker.armorSet().pieceIds(); },
                attributeAndSpellBonus(2,
                        attributeById(RANGED_HASTE_ID, 0.05, EntityAttributeModifier.Operation.MULTIPLY_BASE, id.withPath(SET_BONUS)),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_spirit_wolf.id()))
                )
        );
    }
}
