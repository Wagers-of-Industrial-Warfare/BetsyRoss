package rbasamoyai.betsyross.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import rbasamoyai.betsyross.BetsyRossClient;

public class BetsyRossClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BetsyRossClient.init();
        BetsyRossClient.registerBlockEntityRenderers();
        BetsyRossClient.registerLayerDefinitions((loc, sup) -> EntityModelLayerRegistry.registerModelLayer(loc, sup::get));
    }

}
