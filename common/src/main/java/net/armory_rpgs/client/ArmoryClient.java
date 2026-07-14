package net.armory_rpgs.client;

import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererRegistry;
import net.armory_rpgs.client.armor.CustomArmorRenderer;
import net.armory_rpgs.item.ArmorSets;
import net.armory_rpgs.spell.ArmorySpells;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.rpg_series.item.Armor;

import java.util.function.Supplier;

public class ArmoryClient {
    public static void init() {
        for (var entry: ArmorySpells.all) {
            if (entry.mutator() != null) {
                SpellTooltip.addDescriptionMutator(entry.id(), entry.mutator());
            }
        }

        registerArmorRenderer(ArmorSets.justicar.armorSet(), CustomArmorRenderer::justicar_armor);
        registerArmorRenderer(ArmorSets.destroyer.armorSet(), CustomArmorRenderer::destroyer_armor);
        registerArmorRenderer(ArmorSets.deathmantle.armorSet(), CustomArmorRenderer::deathmantle_armor);
        registerArmorRenderer(ArmorSets.avatar.armorSet(), CustomArmorRenderer::avatar_robe);
        registerArmorRenderer(ArmorSets.scarlet.armorSet(), CustomArmorRenderer::scarlet_robe);
        registerArmorRenderer(ArmorSets.astral.armorSet(), CustomArmorRenderer::astral_robe);
        registerArmorRenderer(ArmorSets.glacier.armorSet(), CustomArmorRenderer::glacier_robe);
        registerArmorRenderer(ArmorSets.strider.armorSet(), CustomArmorRenderer::strider_armor);

        registerArmorRenderer(ArmorSets.tempest.armorSet(), CustomArmorRenderer::tempest_robe);
        registerArmorRenderer(ArmorSets.smouldering.armorSet(), CustomArmorRenderer::smouldering_robe);
        registerArmorRenderer(ArmorSets.rimeweave.armorSet(), CustomArmorRenderer::rimeweave_robe);
        registerArmorRenderer(ArmorSets.absolution.armorSet(), CustomArmorRenderer::absolution_robe);
        registerArmorRenderer(ArmorSets.lightbringer.armorSet(), CustomArmorRenderer::lightbringer_armor);
        registerArmorRenderer(ArmorSets.onslaught.armorSet(), CustomArmorRenderer::onslaught_armor);
        registerArmorRenderer(ArmorSets.slayer.armorSet(), CustomArmorRenderer::slayer_armor);
        registerArmorRenderer(ArmorSets.ranger.armorSet(), CustomArmorRenderer::ranger_armor);
    }

    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
}
