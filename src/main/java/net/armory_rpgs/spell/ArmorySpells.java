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

    private static Spell activeSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 8;

        spell.type = Spell.Type.ACTIVE;
        spell.active = new Spell.Active();

        spell.tooltip = new Spell.Tooltip();
        spell.tooltip.show_header = false;
        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, true);
        spell.tooltip.description.color = Formatting.DARK_GREEN.asString();
        spell.tooltip.description.show_in_compact = true;

        return spell;
    }

    private static Spell passiveSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 8;

        spell.type = Spell.Type.PASSIVE;
        spell.passive = new Spell.Passive();

//        spell.tooltip = new Spell.Tooltip();
//        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, true);
//        spell.tooltip.description.color = Formatting.DARK_GREEN.asString();
//        spell.tooltip.description.show_in_compact = true;
        // spell.tooltip.show_header = false;

        return spell;
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


    private static Spell.Impact createEffectImpact(String effectIdString, float duration) {
        var buff = new Spell.Impact();
        buff.action = new Spell.Impact.Action();
        buff.action.type = Spell.Impact.Action.Type.STATUS_EFFECT;
        buff.action.status_effect = new Spell.Impact.Action.StatusEffect();
        buff.action.status_effect.effect_id = effectIdString;
        buff.action.status_effect.duration = duration;
        return buff;
    }

    private static void configureCooldown(Spell spell, float duration) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        if (spell.cost.cooldown == null) {
            spell.cost.cooldown = new Spell.Cost.Cooldown();
        }
        spell.cost.cooldown.duration = duration;
        spell.cost.cooldown.hosting_item = false;
    }

    private static final Identifier HOLY_DECELERATE = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.HOLY,
            SpellEngineParticles.MagicParticles.Motion.DECELERATE
    ).id();
    private static final Identifier SPARK_DECELERATE = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.SPARK,
            SpellEngineParticles.MagicParticles.Motion.DECELERATE
    ).id();
    private static final Identifier SPARK_FLOAT = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.SPARK,
            SpellEngineParticles.MagicParticles.Motion.FLOAT
    ).id();
    private static final Identifier STRIPE_FLOAT = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.STRIPE,
            SpellEngineParticles.MagicParticles.Motion.FLOAT
    ).id();
    private static final Identifier SPELL_ASCEND = SpellEngineParticles.MagicParticles.get(
            SpellEngineParticles.MagicParticles.Shape.SPELL,
            SpellEngineParticles.MagicParticles.Motion.ASCEND
    ).id();

    private static Spell.Trigger killedByMeleeTrigger() {
        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        var deadCondition = deadCondition();
        trigger.target_conditions = List.of(deadCondition);
        return trigger;
    }

    private static Spell.TargetCondition deadCondition() {
        var deadCondition = new Spell.TargetCondition();
        deadCondition.health_percent_below = 0F;
        deadCondition.health_percent_above = 0F;
        return deadCondition;
    }

    private static Spell.TargetCondition weakCondition() {
        var deadCondition = new Spell.TargetCondition();
        deadCondition.health_percent_below = 0.5F;
        deadCondition.health_percent_above = 0.01F;
        return deadCondition;
    }

    private static Spell.Trigger killedBySpellTrigger() {
        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        trigger.impact = new Spell.Trigger.ImpactCondition();
        trigger.impact.impact_type = Spell.Impact.Action.Type.DAMAGE.toString();
        var deadCondition = deadCondition();
        trigger.target_conditions = List.of(deadCondition);
        return trigger;
    }

    private static List<Spell.Trigger> killedByRangedTrigger() {
        var deadCondition = deadCondition();

        var arrowTrigger = new Spell.Trigger();
        arrowTrigger.type = Spell.Trigger.Type.ARROW_IMPACT;
        arrowTrigger.equipment_condition = EquipmentSlot.MAINHAND;
        arrowTrigger.target_conditions = List.of(deadCondition);

        var skillTrigger = new Spell.Trigger();
        skillTrigger.type = Spell.Trigger.Type.SPELL_IMPACT_SPECIFIC;
        skillTrigger.spell = new Spell.Trigger.SpellCondition();
        skillTrigger.spell.school = ExternalSpellSchools.PHYSICAL_RANGED.id.toString();
        skillTrigger.target_conditions = List.of(deadCondition);

        return List.of(arrowTrigger, skillTrigger);
    }

    private static void areaTarget(Spell spell, Identifier particleId, long particleColor) {
        spell.release.particles_scaled_with_ranged = new ParticleBatch[]{
                new ParticleBatch(particleId.toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.GROUND,
                        1, 0.0F, 0.F)
                        .color(particleColor)
        };

        spell.target = new Spell.Target();
        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
    }

    private static void buffAreaTarget(Spell spell, Identifier particleId, long particleColor) {
        areaTarget(spell, particleId, particleColor);
        spell.target.area.include_caster = true;
    }

    private static Spell.Impact damageImpact(float coefficient, float knockback) {
        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = coefficient;
        damage.action.damage.knockback = knockback;
        return damage;
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
        var description = "Meteor launches {critical_chance_bonus} extra projectile";
        var spell = modifierSpellBase();
        spell.school = SpellSchools.ARCANE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "wizards:fire_meteor";
        modifier.projectile_launch = Spell.LaunchProperties.EMPTY();
        modifier.projectile_launch.extra_launch_count = 1;
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