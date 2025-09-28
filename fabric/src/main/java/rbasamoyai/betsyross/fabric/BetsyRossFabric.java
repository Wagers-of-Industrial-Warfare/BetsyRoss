package rbasamoyai.betsyross.fabric;

import java.util.function.BiConsumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.BetsyRossCommonEvents;
import rbasamoyai.betsyross.content.BetsyRossBlockEntities;
import rbasamoyai.betsyross.content.BetsyRossBlocks;
import rbasamoyai.betsyross.content.BetsyRossCreativeModeTab;
import rbasamoyai.betsyross.content.BetsyRossDataComponents;
import rbasamoyai.betsyross.content.BetsyRossItems;
import rbasamoyai.betsyross.content.BetsyRossStats;
import rbasamoyai.betsyross.network.BetsyRossNetwork;
import rbasamoyai.betsyross.network.BetsyRossNetworkHandler;
import rbasamoyai.betsyross.network.BetsyRossPayload;

public class BetsyRossFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BetsyRoss.init();

        BetsyRossBlocks.registerAll(registerConsumer(BuiltInRegistries.BLOCK));
        BetsyRossItems.registerAll(registerConsumer(BuiltInRegistries.ITEM));
        BetsyRossDataComponents.registerAll(registerConsumer(BuiltInRegistries.DATA_COMPONENT_TYPE));
        BetsyRossBlockEntities.registerAll(registerConsumer(BuiltInRegistries.BLOCK_ENTITY_TYPE));
        BetsyRossCreativeModeTab.create(registerConsumer(BuiltInRegistries.CREATIVE_MODE_TAB));
        BetsyRossStats.registerAll(registerConsumer(BuiltInRegistries.CUSTOM_STAT));

        BetsyRossNetwork.register(fabricNetworkRegistrar());
        BetsyRossNetworkHandler.registerSender(ServerPlayNetworking::send);
        BetsyRossStats.activateAllStats();

        ServerPlayConnectionEvents.JOIN.register(this::onPlayerLogin);
    }

    private static <T> BiConsumer<ResourceLocation, T> registerConsumer(Registry<T> registry) {
        return (loc, block) -> Registry.register(registry, loc, block);
    }

    private static BetsyRossNetwork.Registrar fabricNetworkRegistrar() {
        return new BetsyRossNetwork.Registrar() {
            @Override
            public <T extends BetsyRossPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, boolean isServerbound) {
                if (isServerbound) {
                    PayloadTypeRegistry.playC2S().register(type, codec);
                    ServerPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> payload.handle(ctx.player(), ctx.server()::execute));
                } else {
                    PayloadTypeRegistry.playS2C().register(type, codec);
                    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
                        ClientProxy.register(type);
                }
            }
        };
    }

    private void onPlayerLogin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        BetsyRossCommonEvents.onPlayerLogin(handler.getPlayer());
    }

    /**
     * Copied from {@link net.conczin.immersive_paintings.fabric.CommonFabric}
     */
    private static final class ClientProxy {
        private ClientProxy() { throw new RuntimeException("Instantiated new ClientProxy()"); }

        public static <T extends BetsyRossPayload> void register(CustomPacketPayload.Type<T> type) {
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> payload.handle(ctx.player(), ctx.client()));
        }
    }

}
