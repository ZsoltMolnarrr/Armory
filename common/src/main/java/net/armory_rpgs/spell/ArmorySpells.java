package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_power.api.SpellSchools;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/// Spell modifiers granted by the 4-piece bonus of each epic armor set.
///
/// Every set targets one of its class's two **tier 3** book spells, so the two sets of a class cover
/// one spell each. The chosen axis is always one the Skill Tree leaves free for that spell — neither
/// its weak root node nor either of its two powerful mutex nodes touches it — so a set bonus and the
/// tree stack instead of restating the same number. See `SkillsCommon` in the SkillTree mod for the
/// root-node palette these magnitudes are pitched against.
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

    // Tier 3 book spells, paired per class: [set A spell, set B spell]
    private static final String ARCANE_BEAM = "wizards:arcane_beam";
    private static final String ARCANE_BARRAGE = "wizards:arcane_barrage";
    private static final String FIRE_METEOR = "wizards:fire_meteor";
    private static final String FIRE_STORM = "wizards:fire_storm";
    private static final String FROST_SHIELD = "wizards:frost_shield";
    private static final String FROST_LANCE = "wizards:frost_lance";
    private static final String CIRCLE_OF_HEALING = "paladins:circle_of_healing";
    private static final String PENANCE = "paladins:penance";
    private static final String DIVINE_PROTECTION = "paladins:divine_protection";
    private static final String JUDGEMENT = "paladins:judgement";
    private static final String SHADOW_STEP = "rogues:shadow_step";
    private static final String BEAR_TRAP = "rogues:bear_trap";
    private static final String CHARGE = "rogues:charge";
    private static final String SHOUT = "rogues:shout";
    private static final String BARRAGE = "archers:barrage";
    private static final String SPIRIT_WOLF = "archers:spirit_wolf";

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

    // MARK: - Arcane (Wizard)

    public static Entry improved_arcane_beam = add(improved_arcane_beam());
    private static Entry improved_arcane_beam() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_arcane_beam");
        var title = "Improved Arcane Beam";
        var description = "Arcane Beam releases {channel_ticks_add} additional times";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.ARCANE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = ARCANE_BEAM;
        // Cast duration is unchanged, so the extra releases make the beam denser rather than longer.
        modifier.channel_ticks_add = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    public static Entry improved_arcane_barrage = add(improved_arcane_barrage());
    private static Entry improved_arcane_barrage() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_arcane_barrage");
        var title = "Improved Arcane Barrage";
        var seconds = 5;
        // No auto-token exists for a modifier's summon lifespan, so the number is baked in.
        var description = "Arcane Barrage emitters last " + seconds + " sec longer";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.ARCANE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = ARCANE_BARRAGE;
        modifier.summon_behaviour.lifespan.active_seconds_add = seconds;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    // MARK: - Fire (Wizard)

    public static Entry improved_meteor = add(improved_meteor());
    private static Entry improved_meteor() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_meteor");
        var title = "Improved Meteor";
        var description = "Increases critical chance of Meteor by {critical_chance_bonus}";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.FIRE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = FIRE_METEOR;
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.critical_chance_bonus = 0.05F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    public static Entry improved_firestorm = add(improved_firestorm());
    private static Entry improved_firestorm() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_firestorm");
        var title = "Improved Firestorm";
        var description = "Increases radius of Firestorm by {range_add} blocks";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.FIRE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = FIRE_STORM;
        // Firestorm targets by AREA, so its range is the radius of the storm.
        modifier.range_add = 1F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    // MARK: - Frost (Wizard)

    public static Entry improved_frost_shield = add(improved_frost_shield());
    private static Entry improved_frost_shield() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_frost_shield");
        var title = "Improved Frost Shield";
        var description = "Frost Shield also dispels a harmful effect from you";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.FROST;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = FROST_SHIELD;
        // Frost Shield's only two scalar axes (cooldown, effect duration) are both taken by the
        // Skill Tree, so this set grants a behaviour instead: one random harmful effect removed.
        modifier.mutate_impacts = Spell.Modifier.ImpactListModifier.APPEND;
        modifier.impacts = List.of(SpellBuilder.Impacts.effectCleanse());
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    public static Entry improved_ice_lance = add(improved_ice_lance());
    private static Entry improved_ice_lance() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_ice_lance");
        var title = "Improved Ice Lance";
        var description = "Increases critical damage of Ice Lance by {critical_damage_bonus}";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.FROST;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = FROST_LANCE;
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.critical_damage_bonus = 0.25F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.SPELL);
    }

    // MARK: - Priest

    public static Entry improved_circle_of_healing = add(improved_circle_of_healing());
    private static Entry improved_circle_of_healing() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_circle_of_healing");
        var title = "Improved Circle of Healing";
        var description = "Increases power of Circle of Healing by {power_multiplier}";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.HEALING;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = CIRCLE_OF_HEALING;
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.power_multiplier = 0.1F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.HEAL);
    }

    public static Entry improved_penance = add(improved_penance());
    private static Entry improved_penance() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_penance");
        var title = "Improved Penance";
        var description = "Increases critical chance of Penance by {critical_chance_bonus}";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.HEALING;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = PENANCE;
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.critical_chance_bonus = 0.05F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.HEAL);
    }

    // MARK: - Paladin

    public static Entry improved_divine_protection = add(improved_divine_protection());
    private static Entry improved_divine_protection() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_divine_protection");
        var title = "Improved Divine Protection";
        var description = "Reduces cooldown of Divine Protection by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.HEALING;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = DIVINE_PROTECTION;
        modifier.cooldown_duration_deduct = 5;
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
        modifier.spell_pattern = JUDGEMENT;
        modifier.cooldown_duration_deduct = 3;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    // MARK: - Rogue

    public static Entry improved_shadow_step = add(improved_shadow_step());
    private static Entry improved_shadow_step() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_shadow_step");
        var title = "Improved Shadowstep";
        var description = "Reduces cooldown of Shadowstep by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = SHADOW_STEP;
        modifier.cooldown_duration_deduct = 3;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry improved_bear_trap = add(improved_bear_trap());
    private static Entry improved_bear_trap() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_bear_trap");
        var title = "Improved Bear Trap";
        var description = "Increases power of Bear Trap by {power_multiplier}";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = BEAR_TRAP;
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.power_multiplier = 0.1F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    // MARK: - Warrior

    public static Entry improved_charge = add(improved_charge());
    private static Entry improved_charge() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_charge");
        var title = "Improved Charge";
        var description = "Reduces cooldown of Charge by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = CHARGE;
        modifier.cooldown_duration_deduct = 2;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    public static Entry improved_shout = add(improved_shout());
    private static Entry improved_shout() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_shout");
        var title = "Improved Shout";
        // The {power_multiplier} token would render "400%", which reads absurd next to what is still
        // a small absolute number, so the effect is described in words instead.
        var description = "Shout deals substantially increased damage";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = SHOUT;
        // Shout is a debuff spell carrying only incidental chip damage (0.05 coefficient). Power is
        // applied as `1 + sum(power_multiplier)`, so 4.0 takes it 5x, to an effective 0.25.
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.power_multiplier = 4.0F;
        // Restricted to the damage impact: unfiltered, this would also scale the Demoralize impact's
        // `apply_limit` (health_base + power * multiplier), letting Shout debuff far tankier targets.
        var damageOnly = new Spell.Modifier.ImpactFilter();
        damageOnly.type = Spell.Impact.Action.Type.DAMAGE;
        modifier.impact_filters = List.of(damageOnly);
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }

    // MARK: - Archer

    public static Entry improved_barrage = add(improved_barrage());
    private static Entry improved_barrage() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_barrage");
        var title = "Improved Barrage";
        // No auto-token exists for arrow perks, so the number is baked in.
        var description = "Barrage arrows pierce 1 additional enemy";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = BARRAGE;
        // Barrage deals its damage through the bow and its own `arrow_perks`, never through spell
        // impacts, so `arrow_perks` is the only modifier route that reaches it at all.
        modifier.arrow_perks = Spell.ArrowPerks.EMPTY();
        modifier.arrow_perks.pierce = 1;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.RANGED);
    }

    public static Entry improved_spirit_wolf = add(improved_spirit_wolf());
    private static Entry improved_spirit_wolf() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "improved_spirit_wolf");
        var title = "Improved Spirit Wolf";
        var description = "Reduces cooldown of Spirit Wolf by {cooldown_duration_deduct} sec";
        var spell = modifierSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_RANGED;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = SPIRIT_WOLF;
        modifier.cooldown_duration_deduct = 5;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null, Category.RANGED);
    }
}
