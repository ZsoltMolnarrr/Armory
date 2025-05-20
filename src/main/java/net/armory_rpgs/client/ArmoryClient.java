package net.armory_rpgs.client;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererRegistry;
import net.armory_rpgs.client.armor.CustomArmorRenderer;
import net.armory_rpgs.item.ArmorSets;
import net.fabricmc.api.ClientModInitializer;
import net.armory_rpgs.spell.ArmorySpells;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.client.gui.SpellTooltip;

import java.util.function.Supplier;

public class ArmoryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (var entry: ArmorySpells.all) {
            if (entry.mutator() != null) {
                SpellTooltip.addDescriptionMutator(entry.id(), entry.mutator());
            }
        }

        registerArmorRenderer(ArmorSets.paladinArmorSet_t1.armorSet(), CustomArmorRenderer::justicar_armor);
    }

    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }
}
