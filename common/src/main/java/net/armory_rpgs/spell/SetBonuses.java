package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.ArmorSets;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.set.EquipmentSet;
import net.spell_engine.api.spell.container.SpellContainers;
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

    private static AttributeModifiersComponent attribute(RegistryEntry<EntityAttribute> attribute, double value, EntityAttributeModifier.Operation operation, Identifier id) {
        return new AttributeModifiersComponent(
                List.of(
                        new AttributeModifiersComponent.Entry(
                                attribute,
                                new EntityAttributeModifier(
                                        id,
                                        value,
                                        operation
                                ),
                                AttributeModifierSlot.ARMOR)
                ),
                true
        );
    }

    public static Entry justicar = add(justicar());
    private static Entry justicar() {
        var id = Identifier.of(NAMESPACE, "justicar");
        return new Entry(id,
                "Justicar Regalia",
                () -> { return ArmorSets.justicar.armorSet().pieceIds(); },
                List.of(
                       EquipmentSet.Bonus.withAttributes(2, attribute(
                           SpellSchools.HEALING.attributeEntry,
                           2,
                           EntityAttributeModifier.Operation.ADD_VALUE,
                           id.withPath(SET_BONUS))
                       ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_judgement.id()))
                )
        );
    }

    public static Entry avatar = add(avatar());
    private static Entry avatar() {
        var id = Identifier.of(NAMESPACE, "avatar");
        return new Entry(id,
                "Avatar Raiment",
                () -> { return ArmorSets.avatar.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.HEALING.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_circle_of_healing.id()))
                )
        );
    }

    public static Entry destroyer = add(destroyer());
    private static Entry destroyer() {
        var id = Identifier.of(NAMESPACE, "destroyer");
        return new Entry(id,
                "Destroyer Armor",
                () -> { return ArmorSets.destroyer.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.05,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_charge.id()))
                )
        );
    }

    public static Entry deathmantle = add(deathmantle());
    private static Entry deathmantle() {
        var id = Identifier.of(NAMESPACE, "deathmantle");
        return new Entry(id,
                "Deathmantle",
                () -> { return ArmorSets.deathmantle.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.05,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_shadow_step.id()))
                )
        );
    }

    public static Entry strider = add(strider());
    private static Entry strider() {
        var id = Identifier.of(NAMESPACE, "strider");
        return new Entry(id,
                "Strider Armor",
                () -> { return ArmorSets.strider.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes_RangedWeapon.DAMAGE.entry,
                                0.05,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_entangling_roots.id()))
                )
        );
    }

    public static Entry astral = add(astral());
    private static Entry astral() {
        var id = Identifier.of(NAMESPACE, "astral");
        return new Entry(id,
                "Astral Regalia",
                () -> { return ArmorSets.astral.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.ARCANE.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_arcane_beam.id()))
                )
        );
    }

    public static Entry scarlet = add(scarlet());
    private static Entry scarlet() {
        var id = Identifier.of(NAMESPACE, "scarlet");
        return new Entry(id,
                "Scarlet Raiment",
                () -> { return ArmorSets.scarlet.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.FIRE.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_meteor.id()))
                )
        );
    }

    public static Entry glacier = add(glacier());
    private static Entry glacier() {
        var id = Identifier.of(NAMESPACE, "glacier");
        return new Entry(id,
                "Glacier Mantle",
                () -> { return ArmorSets.glacier.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                SpellSchools.FROST.attributeEntry,
                                0.1,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id.withPath(SET_BONUS))
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainers.forModifier(ArmorySpells.improved_frost_shield.id()))
                )
        );
    }
}
