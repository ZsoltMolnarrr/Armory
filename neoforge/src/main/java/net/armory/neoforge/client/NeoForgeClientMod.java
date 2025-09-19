package net.armory.neoforge.client;

import net.armory_rpgs.ArmoryMod;
import net.armory_rpgs.client.ArmoryClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = ArmoryMod.NAMESPACE, value = Dist.CLIENT)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ArmoryClient.init();
    }
}