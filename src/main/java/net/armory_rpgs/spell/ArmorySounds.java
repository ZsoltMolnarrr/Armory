package net.armory_rpgs.spell;

import net.armory_rpgs.ArmoryMod;
import net.minecraft.util.Identifier;
import net.spell_engine.fx.SpellEngineSounds;

import java.util.ArrayList;
import java.util.List;

public class ArmorySounds {
    public static final List<SpellEngineSounds.Entry> entries = new ArrayList<>();
    private static SpellEngineSounds.Entry add(SpellEngineSounds.Entry entry) {
        entries.add(entry);
        return entry;
    }
    private static SpellEngineSounds.Entry entry(String name) {
        return new SpellEngineSounds.Entry(Identifier.of(ArmoryMod.NAMESPACE, name));
    }

    public static final SpellEngineSounds.Entry plate_equip = add(entry("plate_equip").variants(3));

    public static void register() {
        for (var entry: entries) {
            entry.register();
        }
    }
}
