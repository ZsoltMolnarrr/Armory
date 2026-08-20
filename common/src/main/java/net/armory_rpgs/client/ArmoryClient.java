package net.armory_rpgs.client;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.client.armor.CustomArmorRenderer;
import net.armory_rpgs.item.ArmorSets;
import net.rpg_foundation.armor_api.client.ArmorRenderers;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;
import net.spell_engine.rpg_series.item.Armor;
import net.tiny_config.ConfigManager;

public class ArmoryClient {
    public static ConfigManager<ArmoryClientConfig> config = new ConfigManager<>
            ("client", new ArmoryClientConfig())
            .builder()
            .setDirectory(ArmoryMod.DIRECTORY)
            .sanitize(true)
            .build();

    public static void init() {
        config.refresh();

        registerArmorRenderer(ArmorSets.justicar.armorSet(), CustomArmorRenderer.justicar_armor());
        registerArmorRenderer(ArmorSets.destroyer.armorSet(), CustomArmorRenderer.destroyer_armor());
        registerArmorRenderer(ArmorSets.deathmantle.armorSet(), CustomArmorRenderer.deathmantle_armor());
        registerArmorRenderer(ArmorSets.avatar.armorSet(), CustomArmorRenderer.avatar_robe());
        registerArmorRenderer(ArmorSets.scarlet.armorSet(), CustomArmorRenderer.scarlet_robe());
        registerArmorRenderer(ArmorSets.astral.armorSet(), CustomArmorRenderer.astral_robe());
        registerArmorRenderer(ArmorSets.glacier.armorSet(), CustomArmorRenderer.glacier_robe());
        registerArmorRenderer(ArmorSets.strider.armorSet(), CustomArmorRenderer.strider_armor());

        registerArmorRenderer(ArmorSets.tempest.armorSet(), CustomArmorRenderer.tempest_robe());
        registerArmorRenderer(ArmorSets.smouldering.armorSet(), CustomArmorRenderer.smouldering_robe());
        registerArmorRenderer(ArmorSets.rimeweave.armorSet(), CustomArmorRenderer.rimeweave_robe());
        registerArmorRenderer(ArmorSets.absolution.armorSet(), CustomArmorRenderer.absolution_robe());
        registerArmorRenderer(ArmorSets.lightbringer.armorSet(), CustomArmorRenderer.lightbringer_armor(config.value.allow_high_luminance_armor));
        registerArmorRenderer(ArmorSets.onslaught.armorSet(), CustomArmorRenderer.onslaught_armor());
        registerArmorRenderer(ArmorSets.slayer.armorSet(), CustomArmorRenderer.slayer_armor());
        registerArmorRenderer(ArmorSets.riftstalker.armorSet(), CustomArmorRenderer.riftstalker_armor());
    }

    private static void registerArmorRenderer(Armor.Set set, GeoArmorRenderer renderer) {
        ArmorRenderers.register(renderer, set.head, set.chest, set.legs, set.feet);
    }
}
