package rbasamoyai.betsyross.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.Minecraft;
import rbasamoyai.betsyross.BetsyRossClient;
import rbasamoyai.betsyross.compat.trinkets.BetsyRossTrinketsClient;

public class BetsyRossClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BetsyRossClient.init(BlockRenderLayerMap.INSTANCE::putBlock);
        BetsyRossClient.registerBlockEntityRenderers();
        BetsyRossClient.registerLayerDefinitions((loc, sup) -> EntityModelLayerRegistry.registerModelLayer(loc, sup::get));
        ModelLoadingPlugin.register(new BetsyRossModelLoading());

        ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientStarted);
    }

    private void onClientStarted(Minecraft minecraft) {
        BetsyRossModsFabric.TRINKETS.executeIfLoaded(() -> () -> BetsyRossTrinketsClient.initClient());
    }

}
