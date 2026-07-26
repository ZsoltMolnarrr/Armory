package net.armory_rpgs.client.armor;

import mod.azure.azurelibarmor.common.cache.texture.AzAbstractTexture;
import mod.azure.azurelibarmor.common.render.AzRendererPipelineContext;
import mod.azure.azurelibarmor.common.render.layer.AzAutoGlowingLayer;
import net.armory_rpgs.client.ArmoryClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.spell_engine.api.render.LightEmission;

/// Makes AzureLib's auto generated `_glowmask` burn like a spell projectile, rather than merely
/// resist the dark.
///
/// The emissive pass on its own cannot be bright, whichever layer draws it.
/// `rendertype_entity_translucent_emissive` computes `texel * vertexColor * ColorModulator`, and with
/// the modulator white the most it can ever put on screen is the texel's own color - a gold pixel
/// glowing is exactly as bright as that same gold pixel in daylight. So this draws twice:
///
/// 1. the inherited pass, which fills the pixels the glowmask punched out of the base texture, and
/// 2. an additive pass over it with the shader color driven past one, which is what actually climbs
///    toward white and gives a shader pack's bloom something to find. See [ArmoryGlowLayers].
///
/// The first pass is switched to [ArmoryGlowLayers#radiantFill], Spell Engine's
/// [LightEmission#RADIATE] layer with its particles target dropped, though that is the smaller half of
/// it: against a fully opaque glowmask it draws the same pixels AzureLib's own `az_glowing_layer`
/// would, since `SRC_ALPHA` blending at alpha one is a passthrough.
///
/// Everything else - which texture the glow comes from, that the mask is punched out of the base
/// texture, that the model is re-rendered to draw it - is inherited untouched from
/// [AzAutoGlowingLayer].
public class RadiantGlowLayer<K, T> extends AzAutoGlowingLayer<K, T> {

    /// Read live rather than decided when the renderer is built, so the option takes hold on the next
    /// frame instead of the next launch. Spell Engine reads `renderBeamsHighLuminance` the same way.
    private static boolean isEnabled() {
        return ArmoryClient.config.value.allow_high_luminance_armor;
    }

    @Override
    protected RenderLayer determineRenderType(AzRendererPipelineContext<K, T> context) {
        // Turned off, this hands back exactly what AzureLib's own layer would have drawn
        return isEnabled()
                ? ArmoryGlowLayers.radiantFill(emissiveTexture(context))
                : super.determineRenderType(context);
    }

    private Identifier emissiveTexture(AzRendererPipelineContext<K, T> context) {
        var config = context.rendererPipeline().config();
        var texture = config.textureLocation(context.currentEntity(), context.animatable());
        // Resolved per frame rather than folded into a memo: `getEmissiveResource` is also what registers
        // the generated glowmask texture, and that has to happen again after every resource reload.
        return AzAbstractTexture.getEmissiveResource(texture);
    }

    /// Draws the burn after the fill, then puts the world light back.
    ///
    /// The restore is for the layer drawn after this one. Render layers share a single mutable context
    /// and none of them put back what they change, so without it the full bright value survives into
    /// `AzArmorTrimLayer`'s re-render - whose layer does sample the lightmap - and lights armor trims as
    /// if it were noon.
    @Override
    public void render(AzRendererPipelineContext<K, T> context) {
        var packedLight = context.packedLight();
        super.render(context);
        renderBurn(context);
        context.setPackedLight(packedLight);
    }

    /// The additive pass has to be a separate draw rather than a brighter first one, because the first
    /// pass is what *fills* these pixels: the glowmask is punched out of the base texture, so the base
    /// pass leaves a hole there and an additive draw alone would add light to the world showing through
    /// it. Full bright for the same reason Spell Engine draws every spell object at
    /// [LightmapTextureManager#MAX_LIGHT_COORDINATE] - vanilla's emissive program ignores the coordinate,
    /// but under a shader pack the pack's own `gbuffers_entities` runs instead, and that one lights by it.
    ///
    /// Under a shader pack this doubles rather than gains - the pack ignores `ColorModulator` - and the
    /// additive blend has to be kept off the pack's other attachments to do even that safely. Both are
    /// [ArmoryGlowLayers#gain] and `ADDITIVE_COLOR_ONLY`'s to explain.
    private void renderBurn(AzRendererPipelineContext<K, T> context) {
        if (!isEnabled() || ArmoryGlowLayers.gain <= 1F) {
            return;
        }
        var burn = ArmoryGlowLayers.radiantBurn(emissiveTexture(context));
        context.setRenderType(burn);
        context.setPackedLight(LightmapTextureManager.MAX_LIGHT_COORDINATE);
        context.setVertexConsumer(context.multiBufferSource().getBuffer(burn));
        context.rendererPipeline().reRender(context);
    }
}
