package net.armory_rpgs.client.armor;

import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelibarmor.common.render.layer.AzArmorTrimLayer;
import mod.azure.azurelibarmor.common.render.layer.AzAutoGlowingLayer;
import mod.azure.azurelibarmor.common.render.layer.AzRenderLayer;
import net.armory_rpgs.ArmoryMod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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

    private static CustomArmorRenderer tempest(String texture) {
        return new CustomArmorRenderer("tempest_robe", texture, "tempest_robe_generic");
    }

    public static CustomArmorRenderer tempest_robe() {
        return tempest("tempest_robe");
    }

    public static CustomArmorRenderer smouldering_robe() {
        return tempest("smouldering_robe");
    }

    public static CustomArmorRenderer rimeweave_robe() {
        return tempest("rimeweave_robe");
    }

    public static CustomArmorRenderer absolution_robe() {
        return new CustomArmorRenderer("absolution_robe", "absolution_robe", "absolution_robe_generic");
    }

    public static CustomArmorRenderer lightbringer_armor() {
        return new CustomArmorRenderer("lightbringer_armor", "lightbringer_armor", "lightbringer_armor_generic", new RadiantGlowLayer<>());
    }

    public static CustomArmorRenderer onslaught_armor() {
        return new CustomArmorRenderer("onslaught_armor", "onslaught_armor", "onslaught_armor_generic");
    }

    public static CustomArmorRenderer slayer_armor() {
        return new CustomArmorRenderer("slayer_armor", "slayer_armor", "slayer_armor_generic");
    }

    public static CustomArmorRenderer riftstalker_armor() {
        return new CustomArmorRenderer("riftstalker_armor", "riftstalker_armor", "riftstalker_armor_generic", false);
    }

    public CustomArmorRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(ArmoryMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                Identifier.of(ArmoryMod.NAMESPACE, "textures/armor/" + textureName + ".png"))
                .build()
        );
    }

    public CustomArmorRenderer(String modelName, String textureName, String trimTexture) {
        this(modelName, textureName, trimTexture, true);
    }

    public CustomArmorRenderer(String modelName, String textureName, String trimTexture, boolean addGlow) {
        super(config(modelName, textureName, trimTexture, addGlow ? new AzAutoGlowingLayer<>() : null));
    }

    /// Same as the trim constructor, but with the emissive pass drawn by a layer of your choosing
    /// instead of AzureLib's stock one - see [RadiantGlowLayer].
    public CustomArmorRenderer(String modelName, String textureName, String trimTexture, AzRenderLayer<UUID, ItemStack> glowLayer) {
        super(config(modelName, textureName, trimTexture, glowLayer));
    }

    private static AzArmorRendererConfig config(String modelName, String textureName, String trimTexture, @Nullable AzRenderLayer<UUID, ItemStack> glowLayer) {
        var builder = AzArmorRendererConfig.builder(
                Identifier.of(ArmoryMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                Identifier.of(ArmoryMod.NAMESPACE, "textures/armor/" + textureName + ".png"));
        if (glowLayer != null) {
            // Drawn before the trim, so the trim lands on top of it
            builder.addRenderLayer(glowLayer);
        }
        builder.addRenderLayer(new AzArmorTrimLayer(Identifier.of(ArmoryMod.NAMESPACE, "armor/trim/" + trimTexture), false));
        return builder.build();
    }
}
