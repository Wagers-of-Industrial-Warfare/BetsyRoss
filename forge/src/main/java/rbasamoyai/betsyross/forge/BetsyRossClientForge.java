package rbasamoyai.betsyross.forge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import rbasamoyai.betsyross.BetsyRossClient;
import rbasamoyai.betsyross.compat.curios.BetsyRossCuriosRenderers;

public class BetsyRossClientForge {

    public static void onCtor(IEventBus modBus, IEventBus forgeBus) {
        modBus.addListener(BetsyRossClientForge::onClientSetup);
        modBus.addListener(BetsyRossClientForge::onRendererRegistry);
        modBus.addListener(BetsyRossClientForge::onRegisterModelLayers);
        modBus.addListener(BetsyRossClientForge::onRegisterModelBakery);
        BetsyRossModsForge.CURIOS.executeIfLoaded(() -> () -> BetsyRossCuriosRenderers.register(modBus, forgeBus));
    }

    public static void onClientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
            BetsyRossClient.init(ItemBlockRenderTypes::setRenderLayer);
        });
    }

    public static void onRendererRegistry(EntityRenderersEvent.RegisterRenderers evt) {
        BetsyRossClient.registerBlockEntityRenderers();
    }

    public static void onRegisterModelLayers(EntityRenderersEvent.RegisterLayerDefinitions evt) {
        BetsyRossClient.registerLayerDefinitions(evt::registerLayerDefinition);
    }

    public static void onRegisterModelBakery(ModelEvent.RegisterAdditional evt) {
        BetsyRossClient.registerModels(evt::register);
    }

}
