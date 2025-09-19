package net.armory.neoforge;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.item.Group;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(ArmoryMod.NAMESPACE)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Run our common setup.
        ArmoryMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            ArmoryMod.registerSounds();
        });
        event.register(RegistryKeys.ITEM_GROUP, reg -> {
            // Create and register item group (NeoForge-specific)
            Group.GROUP = ItemGroup.builder()
                    .icon(Group.ICON)
                    .displayName(Text.translatable(Group.translationKey))
                    .build();
            Registry.register(Registries.ITEM_GROUP, Group.KEY, Group.GROUP);
        });
        event.register(RegistryKeys.ITEM, reg -> {
            ArmoryMod.registerItems();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            ArmoryMod.registerEffects();
        });
    }
}
