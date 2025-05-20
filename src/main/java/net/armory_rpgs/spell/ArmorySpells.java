package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.fx.SpellEngineSounds;
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

    private static long HOLY_COLOR = Color.HOLY.toRGBA();
    public static Entry radiance_melee = add(radiance_melee());
    private static Entry radiance_melee() {
        var id = Identifier.of(ArmoryMod.NAMESPACE, "radiance_melee");
        var title = "Radiance";
        var description = "On melee hit: {trigger_chance} chance to heal yourself and nearby allies by {heal}.";
        var spell = passiveSpellBase();
        spell.school = SpellSchools.HEALING;
        spell.range = 2F;

        var trigger = new Spell.Trigger();
        trigger.chance = 0.25F;
        trigger.chance_batching = true;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        trigger.aoe_source_override = Spell.Trigger.TargetSelector.CASTER;
        spell.passive.triggers = List.of(trigger);

        // radianceTargetAndImpact(spell, EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString());
        configureCooldown(spell, 3F);
        spell.cost.cooldown.hosting_item = false;

        return new Entry(id, spell, title, description, null, Category.MELEE);
    }
}