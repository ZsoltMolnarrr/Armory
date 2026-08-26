package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.ArmorSets;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.rpg_foundation.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.spell_engine.api.item.set.EquipmentSet;
import net.spell_engine.api.spell.container.SpellContainers;
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

    private static ItemAttributeModifiers attribute(Holder<Attribute> attribute, double value, AttributeModifier.Operation operation, Identifier id) {
        return new ItemAttributeModifiers(
                List.of(
                        new ItemAttributeModifiers.Entry(
                                attribute,
                                new AttributeModifier(
                                        id,
                                        value,
                                        operation
                                ),
                                EquipmentSlotGroup.ARMOR)
                )
        );
    }

    public static Entry justicar = add(justicar());
    private static Entry justicar() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "justicar");
        return new Entry(id,
                "Justicar Regalia",
                () -> { return ArmorSets.justicar.armorSet().pieceIds(); },
                List.of(
                       EquipmentSet.Bonus.withAttributes(2, attribute(
                           SpellSchools.HEALING.attributeEntry,
                           2,
                           AttributeModifier.Operation.ADD_VALUE,
                           id.withPath(SET_BONUS))
                       ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_divine_protection.id()))
                )
        );
    }

    public static Entry avatar = add(avatar());
    private static Entry avatar() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "avatar");
        return new Entry(id,
                "Avatar Raiment",
                () -> { return ArmorSets.avatar.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.HEALING.attributeEntry,
                                0.1,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_circle_of_healing.id()))
                )
        );
    }

    public static Entry destroyer = add(destroyer());
    private static Entry destroyer() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "destroyer");
        return new Entry(id,
                "Destroyer Armor",
                () -> { return ArmorSets.destroyer.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                Attributes.ATTACK_DAMAGE,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_charge.id()))
                )
        );
    }

    public static Entry deathmantle = add(deathmantle());
    private static Entry deathmantle() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "deathmantle");
        return new Entry(id,
                "Deathmantle",
                () -> { return ArmorSets.deathmantle.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                Attributes.MOVEMENT_SPEED,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_shadow_step.id()))
                )
        );
    }

    public static Entry strider = add(strider());
    private static Entry strider() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "strider");
        return new Entry(id,
                "Strider Armor",
                () -> { return ArmorSets.strider.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes_RangedWeapon.DAMAGE.entry,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_barrage.id()))
                )
        );
    }

    public static Entry astral = add(astral());
    private static Entry astral() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "astral");
        return new Entry(id,
                "Astral Regalia",
                () -> { return ArmorSets.astral.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.ARCANE.attributeEntry,
                                0.1,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_arcane_beam.id()))
                )
        );
    }

    public static Entry scarlet = add(scarlet());
    private static Entry scarlet() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "scarlet");
        return new Entry(id,
                "Scarlet Raiment",
                () -> { return ArmorSets.scarlet.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.FIRE.attributeEntry,
                                0.1,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_meteor.id()))
                )
        );
    }

    public static Entry glacier = add(glacier());
    private static Entry glacier() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "glacier");
        return new Entry(id,
                "Glacier Mantle",
                () -> { return ArmorSets.glacier.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.FROST.attributeEntry,
                                0.1,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_frost_shield.id()))
                )
        );
    }

    // MARK: - Forgotten sets

    public static Entry tempest = add(tempest());
    private static Entry tempest() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "tempest");
        return new Entry(id,
                "Tempest Regalia",
                () -> { return ArmorSets.tempest.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellPowerMechanics.HASTE.attributeEntry,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_arcane_barrage.id()))
                )
        );
    }

    public static Entry smouldering = add(smouldering());
    private static Entry smouldering() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "smouldering");
        return new Entry(id,
                "Smouldering Raiment",
                () -> { return ArmorSets.smouldering.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellPowerMechanics.CRITICAL_CHANCE.attributeEntry,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_firestorm.id()))
                )
        );
    }

    public static Entry rimeweave = add(rimeweave());
    private static Entry rimeweave() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "rimeweave");
        return new Entry(id,
                "Rimeweave Mantle",
                () -> { return ArmorSets.rimeweave.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellPowerMechanics.CRITICAL_DAMAGE.attributeEntry,
                                0.1,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_ice_lance.id()))
                )
        );
    }

    public static Entry absolution = add(absolution());
    private static Entry absolution() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "absolution");
        return new Entry(id,
                "Absolution Raiment",
                () -> { return ArmorSets.absolution.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellPowerMechanics.HASTE.attributeEntry,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_penance.id()))
                )
        );
    }

    public static Entry lightbringer = add(lightbringer());
    private static Entry lightbringer() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "lightbringer");
        return new Entry(id,
                "Lightbringer Regalia",
                () -> { return ArmorSets.lightbringer.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                Attributes.ATTACK_DAMAGE,
                                0.1,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_judgement.id()))
                )
        );
    }

    public static Entry onslaught = add(onslaught());
    private static Entry onslaught() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "onslaught");
        return new Entry(id,
                "Onslaught Armor",
                () -> { return ArmorSets.onslaught.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                Attributes.ATTACK_SPEED,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_shout.id()))
                )
        );
    }

    public static Entry slayer = add(slayer());
    private static Entry slayer() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "slayer");
        return new Entry(id,
                "Slayer Armor",
                () -> { return ArmorSets.slayer.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                Attributes.ATTACK_DAMAGE,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_bear_trap.id()))
                )
        );
    }

    public static Entry riftstalker = add(riftstalker());
    private static Entry riftstalker() {
        var id = Identifier.fromNamespaceAndPath(NAMESPACE, "riftstalker");
        return new Entry(id,
                "Riftstalker Armor",
                () -> { return ArmorSets.riftstalker.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes_RangedWeapon.HASTE.entry,
                                0.05,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_spirit_wolf.id()))
                )
        );
    }
}
