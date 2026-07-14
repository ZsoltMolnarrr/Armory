package net.armory_rpgs.client.armor;

import mod.azure.azurelibarmor.common.render.AzRendererConfig;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelibarmor.common.render.layer.AzArmorTrimLayer;
import mod.azure.azurelibarmor.common.render.layer.AzAutoGlowingLayer;
import net.armory_rpgs.ArmoryMod;
import net.minecraft.util.Identifier;

public class CustomArmorRenderer extends AzArmorRenderer {
    public static CustomArmorRenderer justicar_armor() {
        return new CustomArmorRenderer("justicar_armor", "justicar_armor", "justicar_armor_generic");
    }

    public static CustomArmorRenderer destroyer_armor() {
        return new CustomArmorRenderer("destroyer_armor", "destroyer_armor", "destroyer_armor_generic");
    }

    public static CustomArmorRenderer deathmantle_armor() {
        return new CustomArmorRenderer("deathmantle_armor", "deathmantle_armor", "deathmantle_armor_generic");
    }

    public static CustomArmorRenderer avatar_robe() {
        return new CustomArmorRenderer("avatar_robe", "avatar_robe", "avatar_robe_generic");
    }

    public static CustomArmorRenderer strider_armor() {
        return new CustomArmorRenderer("strider_armor", "strider_armor", "strider_armor_generic", false);
    }

    public static CustomArmorRenderer astral_robe() {
        return tirisfal("astral_robe");
    }

    public static CustomArmorRenderer scarlet_robe() {
        return tirisfal("scarlet_robe");
    }

    public static CustomArmorRenderer glacier_robe() {
        return tirisfal("glacier_robe");
    }

    private static CustomArmorRenderer tirisfal(String texture) {
        return new CustomArmorRenderer("tirisfal_robe", texture, "tirisfal_robe_generic");
    }

    public static CustomArmorRenderer tempest_robe() {
        return new CustomArmorRenderer("tempest_robe", "tempest_robe", "tempest_robe_generic");
    }

    public static CustomArmorRenderer smouldering_robe() {
        return new CustomArmorRenderer("smouldering_robe", "smouldering_robe", "smouldering_robe_generic");
    }

    public static CustomArmorRenderer rimeweave_robe() {
        return new CustomArmorRenderer("rimeweave_robe", "rimeweave_robe", "rimeweave_robe_generic");
    }

    public static CustomArmorRenderer absolution_robe() {
        return new CustomArmorRenderer("absolution_robe", "absolution_robe", "absolution_robe_generic");
    }

    public static CustomArmorRenderer lightbringer_armor() {
        return new CustomArmorRenderer("lightbringer_armor", "lightbringer_armor", "lightbringer_armor_generic");
    }

    public static CustomArmorRenderer onslaught_armor() {
        return new CustomArmorRenderer("onslaught_armor", "onslaught_armor", "onslaught_armor_generic");
    }

    public static CustomArmorRenderer slayer_armor() {
        return new CustomArmorRenderer("slayer_armor", "slayer_armor", "slayer_armor_generic");
    }

    public static CustomArmorRenderer ranger_armor() {
        return new CustomArmorRenderer("ranger_armor", "ranger_armor", "ranger_armor_generic");
    }

    public CustomArmorRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(ArmoryMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                Identifier.of(ArmoryMod.NAMESPACE, "textures/armor/" + textureName + ".png"))
                .build()
        );
    }

    public CustomArmorRenderer(String modelName, String textureName, String trimTexture) {
        super(AzArmorRendererConfig.builder(
                        Identifier.of(ArmoryMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                        Identifier.of(ArmoryMod.NAMESPACE, "textures/armor/" + textureName + ".png"))
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .addRenderLayer(new AzArmorTrimLayer(Identifier.of(ArmoryMod.NAMESPACE, "armor/trim/" + trimTexture), false))
                .build()
        );
    }

    public CustomArmorRenderer(String modelName, String textureName, String trimTexture, boolean addGlow) {
        super(AzArmorRendererConfig.builder(
                        Identifier.of(ArmoryMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                        Identifier.of(ArmoryMod.NAMESPACE, "textures/armor/" + textureName + ".png"))
                .addRenderLayer(new AzArmorTrimLayer(Identifier.of(ArmoryMod.NAMESPACE, "armor/trim/" + trimTexture), false))
                .build()
        );
    }
}
