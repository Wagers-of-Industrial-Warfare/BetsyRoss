package rbasamoyai.betsyross.neoforge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.BetsyRossClient;
import rbasamoyai.betsyross.compat.curios.BetsyRossCuriosRenderers;
import rbasamoyai.betsyross.network.BetsyRossNetworkHandler;

@Mod(value = BetsyRoss.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BetsyRoss.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class BetsyRossClientNeoforge {

    public BetsyRossClientNeoforge(IEventBus modBus) {
        BetsyRossModsNeoforge.CURIOS.executeIfLoaded(() -> () -> BetsyRossCuriosRenderers.register(modBus));
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
            BetsyRossClient.init(ItemBlockRenderTypes::setRenderLayer);
        });
    }

    @SubscribeEvent
    public static void registerNetwork(final RegisterPayloadHandlersEvent event) {
        BetsyRossNetworkHandler.Client.registerSender(PacketDistributor::sendToServer);
    }

    @SubscribeEvent
    public static void onRendererRegistry(EntityRenderersEvent.RegisterRenderers evt) {
        BetsyRossClient.registerBlockEntityRenderers();
    }

    @SubscribeEvent
    public static void onRegisterModelLayers(EntityRenderersEvent.RegisterLayerDefinitions evt) {
        BetsyRossClient.registerLayerDefinitions(evt::registerLayerDefinition);
    }

    @SubscribeEvent
    public static void onRegisterModelBakery(ModelEvent.RegisterAdditional evt) {
        BetsyRossClient.registerModels(rl -> evt.register(ModelResourceLocation.standalone(rl)));
    }

}
