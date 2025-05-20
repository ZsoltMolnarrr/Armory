package net.armory_rpgs.client.armor;

import mod.azure.azurelibarmor.rewrite.render.AzRendererConfig;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererConfig;
import net.armory_rpgs.ArmoryMod;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CustomArmorRenderer extends AzArmorRenderer {
    public CustomArmorRenderer(AzRendererConfig<ItemStack> config) {
        super(config);
    }

    public static CustomArmorRenderer justicar_armor() {
        return new CustomArmorRenderer("justicar_armor", "justicar_armor");
    }

    public CustomArmorRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(ArmoryMod.NAMESPACE, "geo/" + modelName + "geo.json"),
                Identifier.of(ArmoryMod.NAMESPACE, "textures/armor/" + textureName + ".png")
        ).build());
    }
}
