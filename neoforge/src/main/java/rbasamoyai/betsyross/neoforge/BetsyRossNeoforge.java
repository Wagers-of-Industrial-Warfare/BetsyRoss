package rbasamoyai.betsyross.neoforge;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.content.BetsyRossBlockEntities;
import rbasamoyai.betsyross.content.BetsyRossBlocks;
import rbasamoyai.betsyross.content.BetsyRossCreativeModeTab;
import rbasamoyai.betsyross.content.BetsyRossDataComponents;
import rbasamoyai.betsyross.content.BetsyRossItems;
import rbasamoyai.betsyross.content.BetsyRossStats;
import rbasamoyai.betsyross.network.BetsyRossNetwork;
import rbasamoyai.betsyross.network.BetsyRossNetworkHandler;
import rbasamoyai.betsyross.network.BetsyRossPayload;

@Mod(BetsyRoss.MOD_ID)
@EventBusSubscriber(modid = BetsyRoss.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class BetsyRossNeoforge {

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        BetsyRoss.init();
        BetsyRossStats.activateAllStats();
    }

    private static <T> void registryHelper(RegisterEvent event, ResourceKey<? extends Registry<T>> key, Consumer<BiConsumer<ResourceLocation, T>> cons) {
        event.register(key, rh -> cons.accept(rh::register));
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        registryHelper(event, Registries.BLOCK, BetsyRossBlocks::registerAll);
        registryHelper(event, Registries.ITEM, BetsyRossItems::registerAll);
        registryHelper(event, Registries.DATA_COMPONENT_TYPE, BetsyRossDataComponents::registerAll);
        registryHelper(event, Registries.BLOCK_ENTITY_TYPE, BetsyRossBlockEntities::registerAll);
        registryHelper(event, Registries.CREATIVE_MODE_TAB, BetsyRossCreativeModeTab::create);
        registryHelper(event, Registries.CUSTOM_STAT, BetsyRossStats::registerAll);
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        BetsyRossNetwork.register(neoforgeNetworkRegistrar(event.registrar("1")));
        BetsyRossNetworkHandler.registerSender(PacketDistributor::sendToPlayer);
    }

    public static BetsyRossNetwork.Registrar neoforgeNetworkRegistrar(final PayloadRegistrar registrar) {
        return new BetsyRossNetwork.Registrar() {
            @Override
            public <T extends BetsyRossPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, boolean isServerbound) {
                if (isServerbound) {
                    registrar.playToServer(type, codec, (payload, ctx) -> payload.handle(ctx.player(), ctx::enqueueWork));
                } else {
                    registrar.playToClient(type, codec, (payload, ctx) -> payload.handle(ctx.player(), ctx::enqueueWork));
                }
            }
        };
    }

}
