package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ArmorySpells {
    public enum Category {
        MELEE, RANGED, SPELL, HEAL, SHIELD
    }
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator, EnumSet<Category> categories) {
        public Entry(Identifier id, Spell spell, String title, String description,
                     @Nullable SpellTooltip.DescriptionMutator mutator, Category category) {
            this(id, spell, title, description, mutator, EnumSet.of(category));
        }
    }

    public static final List<Entry> all = new ArrayList<>();
    private static Entry add(Entry entry) {
        all.add(entry);
        return entry;
    }

    private static Spell modifierSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 1;

        spell.type = Spell.Type.MODIFIER;

        spell.tooltip = new Spell.Tooltip();
        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, true);
        spell.tooltip.description.color = Formatting.GRAY.asString();
        spell.tooltip.description.show_in_compact = true;
        spell.tooltip.name.show_in_compact = false;
        spell.tooltip.name.show_in_details = false;
        spell.tooltip.show_header = false;

        return spell;
    }

    public static Entry improved_arcane_beam = add(improved_arcane_beam());
    private static Entry improved_arcane_beam() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_arcane_beam");
        var title = "Improved Arcane Beam";
        var description = "Increases critical chance of Arcane Beam by {critical_chance_bonus}";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.ARCANE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "wizards:arcane_beam";
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.critical_chance_bonus = 0.05F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    public static Entry improved_meteor = add(improved_meteor());
    private static Entry improved_meteor() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_meteor");
        var title = "Improved Meteor";
        var description = "Reduces cooldown of Meteor by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.ARCANE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "wizards:fire_meteor";
        modifier.cooldown_duration_deduct = 3;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    public static Entry improved_frost_shield = add(improved_frost_shield());
    private static Entry improved_frost_shield() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_frost_shield");
        var title = "Improved Frost Shield";
        var description = "Increases duration of Frost Shield by {effect_duration_add} sec";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.ARCANE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "wizards:frost_shield";
        modifier.effect_duration_add = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    public static Entry improved_entangling_roots = add(improved_entangling_roots());
    private static Entry improved_entangling_roots() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_entangling_roots");
        var title = "Improved Entangling Roots";
        var description = "Increases duration of Entangling Roots by {effect_duration_add} sec";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "archers:entangling_roots";
        modifier.effect_duration_add = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.RANGED);
    }

    public static Entry improved_barrier = add(improved_barrier());
    private static Entry improved_barrier() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_barrier");
        var title = "Improved Barrier";
        var description = "Increases duration of Barrier by {effect_duration_add} sec";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.HEALING;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "paladins:barrier";
        modifier.effect_duration_add = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.HEAL);
    }

    public static Entry improved_circle_of_healing = add(improved_circle_of_healing());
    private static Entry improved_circle_of_healing() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_circle_of_healing");
        var title = "Improved Circle of Healing";
        var description = "Increases duration of Circle of Healing Absorption by {effect_duration_add} sec";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.HEALING;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "paladins:circle_of_healing";
        modifier.effect_duration_add = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.HEAL);
    }

    public static Entry improved_judgement = add(improved_judgement());
    private static Entry improved_judgement() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_judgement");
        var title = "Improved Judgement";
        var description = "Reduces cooldown of Judgement by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.HEALING;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "paladins:judgement";
        modifier.cooldown_duration_deduct = 3;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry improved_whirlwind = add(improved_whirlwind());
    private static Entry improved_whirlwind() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_whirlwind");
        var title = "Improved Whirlwind";
        var description = "Reduces cooldown of Whirlwind by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "rogues:whirlwind";
        modifier.cooldown_duration_deduct = 4;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry improved_charge = add(improved_charge());
    private static Entry improved_charge() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_charge");
        var title = "Improved Charge";
        var description = "Reduces cooldown of Charge by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "rogues:charge";
        modifier.cooldown_duration_deduct = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry improved_shadow_step = add(improved_shadow_step());
    private static Entry improved_shadow_step() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_shadow_step");
        var title = "Improved Shadowstep";
        var description = "Reduces cooldown of Shadowstep by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "rogues:shadow_step";
        modifier.cooldown_duration_deduct = 4;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }
}