package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.ArmorSets;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.set.EquipmentSet;
import net.spell_engine.api.spell.container.SpellContainerHelper;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SetBonuses {
    private static final String NAMESPACE = ArmoryMod.NAMESPACE;
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
                           id)
                       ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainerHelper.createForModifier(ArmorySpells.improved_judgement.id()))
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
                                1,
                                EntityAttributeModifier.Operation.ADD_VALUE,
                                id)
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainerHelper.createForModifier(ArmorySpells.improved_circle_of_healing.id()))
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
                                0.1,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id)
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainerHelper.createForModifier(ArmorySpells.improved_charge.id()))
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
                                0.1,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id)
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainerHelper.createForModifier(ArmorySpells.improved_shadow_step.id()))
                )
        );
    }

    public static Entry archer = add(archer());
    private static Entry archer() {
        var id = Identifier.of(NAMESPACE, "archer");
        return new Entry(id,
                "Archer Armor",
                () -> { return ArmorSets.archer.armorSet().pieceIds(); },
                List.of(
                        EquipmentSet.Bonus.withAttributes(2, attribute(
                                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                                0.1,
                                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                id)
                        ),
                        EquipmentSet.Bonus.withSpells(4, SpellContainerHelper.createForModifier(ArmorySpells.improved_entangling_roots.id()))
                )
        );
    }
}
