package net.armory.fabric.client;

import net.armory_rpgs.client.ArmoryClient;
import net.fabricmc.api.ClientModInitializer;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ArmoryClient.init();
    }
}
