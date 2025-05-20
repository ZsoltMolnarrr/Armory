package net.armory_rpgs.client;

import net.fabricmc.api.ClientModInitializer;
import net.armory_rpgs.spell.ArmorySpells;
import net.spell_engine.client.gui.SpellTooltip;

public class ArmoryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (var entry: ArmorySpells.all) {
            if (entry.mutator() != null) {
                SpellTooltip.addDescriptionMutator(entry.id(), entry.mutator());
            }
        }
    }
}
