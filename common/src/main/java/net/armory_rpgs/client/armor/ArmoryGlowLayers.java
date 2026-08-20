package net.armory_rpgs.client.armor;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.spell_engine.client.compatibility.ShaderCompatibility;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.function.Function;

/// The two layers [RadiantGlowLayer] draws its glow with: the fill, and the additive burn on top of it.
///
/// The fill alone cannot be bright. `rendertype_entity_translucent_emissive` computes
/// `color = texel * vertexColor * ColorModulator`, and with the modulator left white the brightest it
/// can put on screen is the texel itself - dimmer, in fact, since its vertex shader runs `Color`
/// through `minecraft_mix_light`, which scales by `0.4` to `1.0` depending on which way the face
/// points. A gold texel glowing is no brighter than that same gold texel in daylight, which is why the
/// emissive pass is nearly invisible in a lit area, and why swapping which *layer* draws it changes
/// nothing.
///
/// So the burn takes the one lever that does exist. `ColorModulator` is the shader color, and nothing
/// clamps it on the way in: driven above one it scales the emissive output past the texel's own color.
/// Blended additively over the fill that already filled those pixels, it drives them up toward and
/// through white. Same trick, and same reasoning, as `CustomLayers.itemGlowGain` in Spell Engine.
///
/// The burn is drawn under the vanilla renderer only - see [RadiantGlowLayer#renderBurn].
public class ArmoryGlowLayers extends RenderLayer {
    public ArmoryGlowLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    /// How hard the burn is driven into the frame buffer. Read live, so it can be tuned at runtime.
    ///
    /// Vanilla's frame buffer is fixed point and clamps the fragment to `[0, 1]`, so under vanilla a
    /// gain above one buys brightness as *coverage* rather than peak: the mid tones of the emissive
    /// climb into the clamp and more of the mask reaches full white. Raising this is what turns a lit
    /// surface into a hot one; too far and the mask flattens into a white blob.
    ///
    /// **Under a shader pack this value is ignored and the burn is a straight doubling** - see
    /// [#effectiveGain]. What the burn still buys there is the second additive copy of the fill, which
    /// lands in a floating point gbuffer and gives the pack's bloom threshold something to find.
    /// Setting this to `1` or below turns the burn off everywhere.
    public static float gain = 2.5F;

    /// Shader color is global render state and nothing restores it for us, so it is saved on the way in
    /// and put back on the way out rather than reset to a presumed default, which would trample whatever
    /// another mod had set. Layer draws are sequential and their setup and teardown are paired, so a
    /// single slot is enough.
    private static final float[] shaderColorToRestore = new float[4];

    /// GL guarantees at least this many draw buffers, so indices `0` to `7` are always valid to
    /// address. Confining the loop below to that range keeps it from raising `GL_INVALID_VALUE` on a
    /// driver that offers no more than the minimum.
    private static final int GUARANTEED_DRAW_BUFFERS = 8;

    /// Plain additive, **confined to the color attachment**. The fill underneath has already filled
    /// these pixels with the texel's own color; this adds on top of that rather than replacing it,
    /// which is the only way the result climbs past what the texture alone could show.
    ///
    /// The confining is the whole trick, and skipping it is what turned Lightbringer's yellow glow
    /// green under Complementary r5.7 and up. Blending is framebuffer state, not color state: whatever
    /// function is set applies to every attachment a program writes. Vanilla's emissive program writes
    /// one, so there is nothing else to reach - but a shader pack's `gbuffers_entities` writes the
    /// color *and* a set of per pixel notes its later passes read back, on Complementary three of them
    /// (four above `BLOCK_REFLECT_QUALITY 2`): a material mask, a sky light factor, a reflection
    /// normal. Blended `ONE, ONE`, the burn added each of those to what the fill had just written and
    /// pinned it at its maximum. The pack read the ruined notes back as *maximally reflective, fully
    /// sky lit, facing thataway* and mirrored a blue sky onto pixels that were meant to be yellow.
    ///
    /// `glDisablei` turns blending off per attachment, which is the same lever the pack itself reaches
    /// for when it writes `blend.gbuffers_water.colortex4=off` in its `shaders.properties`. With
    /// blending off, those attachments take a plain write instead - and since both passes run the same
    /// program over the same geometry with the same lightmap and normals, the value written is the one
    /// the fill already put there. The notes come out identical to a single pass; only the color adds.
    private static final Transparency ADDITIVE_COLOR_ONLY = new Transparency("armory_radiant_burn_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);

        // `enableBlend` above is a plain `glEnable`, which turns every attachment on at once
        for (int attachment = 1; attachment < GUARANTEED_DRAW_BUFFERS; attachment++) {
            GL30.glDisablei(GL11.GL_BLEND, attachment);
        }
    }, () -> {
        // No matching `glEnablei` loop needed: `disableBlend` is a plain `glDisable`, which clears the
        // per attachment enables along with the global one, so the next `enableBlend` anywhere in the
        // frame hands every attachment back in its usual state.
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    /// The gain is a vanilla-only lever, and forcing it to `1` under a shader pack is not a fallback -
    /// driving it there is actively wrong.
    ///
    /// It rides on `ColorModulator`. Vanilla's emissive shader applies that at the very end, to a
    /// finished color, so a value above one simply scales the result. A pack has no such uniform, and
    /// Iris bridges the gap by folding the shader color into `gl_Color` instead - which arrives at the
    /// *start* of the pack's program, where it multiplies the texel before anything else runs. Every
    /// colour test and lighting term in `gbuffers_entities` is written against a texel in `[0, 1]`;
    /// handing it `(2.5, 2.5, 0.245)` puts the fragment somewhere none of that math was meant to go,
    /// and Lightbringer's yellow came back out green.
    ///
    /// At `1` the burn is a plain additive duplicate of the fill, which is all the brightness a pack
    /// was ever getting from it anyway. See [#gain].
    private static float effectiveGain() {
        return ShaderCompatibility.isShaderPackInUse() ? 1F : gain;
    }

    private static final Texturing GAIN = new Texturing("armory_radiant_burn_gain",
            () -> {
                // `getShaderColor` hands out the live array, not a copy of it
                System.arraycopy(RenderSystem.getShaderColor(), 0, shaderColorToRestore, 0, 4);
                var appliedGain = effectiveGain();
                RenderSystem.setShaderColor(appliedGain, appliedGain, appliedGain, 1F);
            },
            () -> RenderSystem.setShaderColor(
                    shaderColorToRestore[0], shaderColorToRestore[1],
                    shaderColorToRestore[2], shaderColorToRestore[3]));

    /// The pass that fills the pixels the glowmask punched out of the base texture.
    ///
    /// This is `CustomLayers.spellObject(texture, LightEmission.RADIATE, false)` verbatim, minus its
    /// `PARTICLES_TARGET`. Spell objects are drawn into the particles framebuffer so they composite
    /// with the particles they sit among; armor is not, and under Fabulous graphics that target would
    /// put the fill in a different framebuffer from both the armor underneath it and the burn on top -
    /// leaving the burn adding light to a hole. Everything else about the layer is Spell Engine's:
    /// the emissive program, `NO_TRANSPARENCY` (the identity Iris reads to sort the draw into the
    /// opaque bucket rather than the general transparent one), and no culling, matching the armor's
    /// own `armorCutoutNoCull` base pass.
    private static final Function<Identifier, RenderLayer> RADIANT_FILL = Util.memoize(texture ->
            RenderLayer.of("armory_radiant_fill",
                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                    VertexFormat.DrawMode.QUADS,
                    256,
                    false,
                    true,
                    MultiPhaseParameters.builder()
                            .program(ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM)
                            .texture(new RenderPhase.Texture(texture, false, false))
                            .transparency(NO_TRANSPARENCY)
                            .cull(DISABLE_CULLING)
                            .writeMaskState(ALL_MASK)
                            .overlay(ENABLE_OVERLAY_COLOR)
                            .build(false)));

    /// @param emissiveTexture the generated `_glowmask` texture, not the base armor texture
    public static RenderLayer radiantFill(Identifier emissiveTexture) {
        return RADIANT_FILL.apply(emissiveTexture);
    }

    /// Writes no depth: the fill below it already did, at the same coordinates, and this only adds
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
                            .transparency(ADDITIVE_COLOR_ONLY)
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
