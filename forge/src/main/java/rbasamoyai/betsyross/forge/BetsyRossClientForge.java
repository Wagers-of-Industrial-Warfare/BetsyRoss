package rbasamoyai.betsyross.forge;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import rbasamoyai.betsyross.BetsyRossClient;

public class BetsyRossClientForge {

    public static void onCtor(IEventBus modBus, IEventBus forgeBus) {
        modBus.addListener(BetsyRossClientForge::onClientSetup);
        modBus.addListener(BetsyRossClientForge::onRendererRegistry);
        modBus.addListener(BetsyRossClientForge::onRegisterModelLayers);
    }

    public static void onClientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(BetsyRossClient::init);
    }

    public static void onRendererRegistry(EntityRenderersEvent.RegisterRenderers evt) {
        BetsyRossClient.registerBlockEntityRenderers();
    }

    public static void onRegisterModelLayers(EntityRenderersEvent.RegisterLayerDefinitions evt) {
        BetsyRossClient.registerLayerDefinitions(evt::registerLayerDefinition);
    }

}
