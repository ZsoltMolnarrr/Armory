package net.armory_rpgs.client.armor;

import net.armory_rpgs.ArmoryMod;
import net.minecraft.util.Identifier;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;

public final class CustomArmorRenderer {
    private CustomArmorRenderer() { }

    public static GeoArmorRenderer justicar_armor() {
        return glowing("justicar_armor", "justicar_armor", "justicar_armor_generic");
    }

    public static GeoArmorRenderer destroyer_armor() {
        return glowing("destroyer_armor", "destroyer_armor", "destroyer_armor_generic");
    }

    public static GeoArmorRenderer deathmantle_armor() {
        return glowing("deathmantle_armor", "deathmantle_armor", "deathmantle_armor_generic");
    }

    public static GeoArmorRenderer avatar_robe() {
        return glowing("avatar_robe", "avatar_robe", "avatar_robe_generic");
    }

    public static GeoArmorRenderer strider_armor() {
        return plain("strider_armor", "strider_armor", "strider_armor_generic");
    }

    public static GeoArmorRenderer astral_robe() {
        return tirisfal("astral_robe");
    }

    public static GeoArmorRenderer scarlet_robe() {
        return tirisfal("scarlet_robe");
    }

    public static GeoArmorRenderer glacier_robe() {
        return tirisfal("glacier_robe");
    }

    private static GeoArmorRenderer tirisfal(String texture) {
        return glowing("tirisfal_robe", texture, "tirisfal_robe_generic");
    }

    private static GeoArmorRenderer tempest(String texture) {
        return glowing("tempest_robe", texture, "tempest_robe_generic");
    }

    public static GeoArmorRenderer tempest_robe() {
        return tempest("tempest_robe");
    }

    public static GeoArmorRenderer smouldering_robe() {
        return tempest("smouldering_robe");
    }

    public static GeoArmorRenderer rimeweave_robe() {
        return tempest("rimeweave_robe");
    }

    public static GeoArmorRenderer absolution_robe() {
        return glowing("absolution_robe", "absolution_robe", "absolution_robe_generic");
    }

    /// Lightbringer's emissive burns past its own texture brightness (radiant) when high-luminance
    /// armor is enabled; otherwise it falls back to a plain fullbright glow (one draw pass instead of two).
    public static GeoArmorRenderer lightbringer_armor(boolean highLuminance) {
        var renderer = of("lightbringer_armor", "lightbringer_armor");
        renderer = highLuminance ? renderer.radiant() : renderer.glow();
        return renderer.trim(trim("lightbringer_armor_generic"), false);
    }

    public static GeoArmorRenderer onslaught_armor() {
        return glowing("onslaught_armor", "onslaught_armor", "onslaught_armor_generic");
    }

    public static GeoArmorRenderer slayer_armor() {
        return glowing("slayer_armor", "slayer_armor", "slayer_armor_generic");
    }

    public static GeoArmorRenderer riftstalker_armor() {
        return plain("riftstalker_armor", "riftstalker_armor", "riftstalker_armor_generic");
    }

    // --- builders ---

    /// Base texture + an auto-derived emissive glowmask (`<texture>_glowmask.png`) + trim.
    private static GeoArmorRenderer glowing(String modelName, String textureName, String trimTexture) {
        return of(modelName, textureName).glow().trim(trim(trimTexture), false);
    }

    /// Base texture + trim only, no emissive glow.
    private static GeoArmorRenderer plain(String modelName, String textureName, String trimTexture) {
        return of(modelName, textureName).trim(trim(trimTexture), false);
    }

    private static GeoArmorRenderer of(String modelName, String textureName) {
        return GeoArmorRenderer.of(
                Identifier.of(ArmoryMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                Identifier.of(ArmoryMod.NAMESPACE, "textures/armor/" + textureName + ".png"));
    }

    private static Identifier trim(String trimTexture) {
        return Identifier.of(ArmoryMod.NAMESPACE, "armor/trim/" + trimTexture);
    }
}
