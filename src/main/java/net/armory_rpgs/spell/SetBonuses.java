package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.ArmorSets;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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
}
