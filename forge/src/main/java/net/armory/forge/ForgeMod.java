package net.armory.forge;

import net.armory.forge.client.ForgeClientMod;
import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.Group;
import net.armory_rpgs.spell.ArmorySounds;
import net.minecraft.registry.RegistryKeys;
import net.spell_engine.fx.SpellEngineSounds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

@Mod(ArmoryMod.NAMESPACE)
public final class ForgeMod {
    @SuppressWarnings("removal")
    public ForgeMod() {
        // Run our common setup (configs only — registers nothing).
        ArmoryMod.init();
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, ForgeMod::register);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClientMod.register(modBus);
        }
    }

    /// One listener for every registry; `RegisterEvent#register(key, consumer)` only runs the consumer when
    /// the event is for that key, so each block below executes inside exactly its own registry's window.
    ///
    /// The loops are **duplicated here on purpose** rather than delegated to `common`'s `registerX()`
    /// methods: a plain `Registry.register` is not usable on this loader, because Forge only clears the
    /// vanilla registry's own lock from 47.4.0 onwards — on 47.0–47.3 and NeoForge 1.20.1 it throws
    /// "Can not register to a locked registry" even inside the correct `RegisterEvent` window, and our
    /// `mods.toml` declares `loaderVersion = "[47,)"`. The helper this event hands out is the API every
    /// build of `[47,)` sanctions, so Forge iterates the same content `common` exposes and registers it
    /// itself. `common` keeps its vanilla-shaped `registerX()` for Fabric, which is untouched.
    ///
    /// `event.register` has no `else` and no throw, so content filed under a key that does not match the
    /// event vanishes silently — the grouping below is deliberate.
    public static void register(RegisterEvent event) {
        // Sound events are registry event 1, well before the ITEM window (7) where `ArmorSets`' static
        // initializer reads `ArmorySounds.<entry>.entry()` for the armor materials' equip sounds.
        event.register(RegistryKeys.SOUND_EVENT, helper -> {
            SpellEngineSounds.soundsToRegister(ArmorySounds.entries).forEach(helper::register);
            // The helper returns void where `Registry.registerReference` returned the entry, and the armor
            // materials read `Entry#entry()` — link them back out of the registry after the loop.
            SpellEngineSounds.linkEntries(ArmorySounds.entries);
        });

        // Armory registers no status effects of its own; this only refreshes/saves the effects config.
        event.register(RegistryKeys.STATUS_EFFECT, helper -> ArmoryMod.registerEffects());

        // `Item`'s constructor takes an intrusive registry holder on 1.20.1, so the items are built here,
        // inside the ITEM window, not earlier. The item group is NOT registered here — see below.
        event.register(RegistryKeys.ITEM, helper ->
                ArmoryMod.itemsToRegister().forEach(helper::register));

        // `creative_mode_tab` is registry event 65, `item` is 7 — the group gets its own window. This does
        // not affect the tab's contents order: the modifier callbacks are collected by
        // `PlatformEvents.onItemGroupModify` into a list keyed by `Group.KEY` during the ITEM window above,
        // in exactly the order `common` installs them, and are replayed from that list when the tab is built.
        event.register(RegistryKeys.ITEM_GROUP, helper ->
                helper.register(Group.ID, ArmoryMod.createItemGroup()));
    }
}
