package net.armory_rpgs.client.armor;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

/// The pass that makes an armor emissive actually burn, drawn on top of the ordinary glow.
///
/// The glow pass alone cannot be bright. `rendertype_entity_translucent_emissive` computes
/// `color = texel * vertexColor * ColorModulator`, and with the modulator left white and the vertex
/// color capped at one, the brightest it can ever put on screen is the texel itself. A gold texel
/// glowing is exactly as bright as that same gold texel in daylight - which is why the emissive pass
/// is nearly invisible in a lit area, and why swapping which *layer* draws it changes nothing.
///
/// So this pass takes the one lever that does exist. `ColorModulator` is the shader color, and nothing
/// clamps it on the way in: driven above one it scales the emissive output past the texel's own color.
/// Blended additively over the glow that already filled those pixels, it drives them up toward and
/// through white, which is the luminance a shader pack's bloom threshold is looking for. This is the
/// same trick - and the same reasoning - as `CustomLayers.itemGlowGain` in Spell Engine.
///
/// It still runs the emissive program, so a shader pack classifies the draw exactly as it classifies
/// a spell projectile.
public class ArmoryGlowLayers extends RenderLayer {
    public ArmoryGlowLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    /// How hard the burn is driven into the frame buffer. Read live, so it can be tuned at runtime.
    ///
    /// Vanilla's frame buffer is fixed point and clamps the fragment to `[0, 1]`, so under vanilla a
    /// gain above one buys brightness as *coverage* rather than peak: the mid tones of the emissive
    /// climb into the clamp and more of the mask reaches full white. Under a shader pack the gbuffer is
    /// floating point, so the excess survives the write and is there for bloom to find. Raising this is
    /// what turns a lit surface into a hot one; too far and the mask flattens into a white blob.
    public static float gain = 2.5F;

    /// Shader color is global render state and nothing restores it for us, so it is saved on the way in
    /// and put back on the way out rather than reset to a presumed default, which would trample whatever
    /// another mod had set. Layer draws are sequential and their setup and teardown are paired, so a
    /// single slot is enough.
    private static final float[] shaderColorToRestore = new float[4];

    /// Plain additive. The glow underneath has already filled these pixels with the texel's own color;
    /// this adds on top of that rather than replacing it, which is the only way the result climbs past
    /// what the texture alone could show.
    private static final Transparency ADDITIVE = new Transparency("armory_radiant_burn_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    private static final Texturing GAIN = new Texturing("armory_radiant_burn_gain",
            () -> {
                // `getShaderColor` hands out the live array, not a copy of it
                System.arraycopy(RenderSystem.getShaderColor(), 0, shaderColorToRestore, 0, 4);
                RenderSystem.setShaderColor(gain, gain, gain, 1F);
            },
            () -> RenderSystem.setShaderColor(
                    shaderColorToRestore[0], shaderColorToRestore[1],
                    shaderColorToRestore[2], shaderColorToRestore[3]));

    /// Writes no depth: the glow pass below it already did, at the same coordinates, and this only adds
    /// light to what is there.
    private static final Function<Identifier, RenderLayer> RADIANT_BURN = Util.memoize(texture ->
            RenderLayer.of("armory_radiant_burn",
                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                    VertexFormat.DrawMode.QUADS,
                    256,
                    false,
                    true,
                    MultiPhaseParameters.builder()
                            .program(ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM)
                            .texture(new RenderPhase.Texture(texture, false, false))
                            .transparency(ADDITIVE)
                            .cull(DISABLE_CULLING)
                            .writeMaskState(COLOR_MASK)
                            .overlay(ENABLE_OVERLAY_COLOR)
                            .texturing(GAIN)
                            .build(false)));

    /// @param emissiveTexture the generated `_glowmask` texture, not the base armor texture
    public static RenderLayer radiantBurn(Identifier emissiveTexture) {
        return RADIANT_BURN.apply(emissiveTexture);
    }
}
