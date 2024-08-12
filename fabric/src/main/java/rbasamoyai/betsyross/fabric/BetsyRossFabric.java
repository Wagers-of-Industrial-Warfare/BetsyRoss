package rbasamoyai.betsyross.fabric;

import java.util.function.BiConsumer;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.BetsyRossCommonEvents;
import rbasamoyai.betsyross.config.BetsyRossConfig;
import rbasamoyai.betsyross.content.BetsyRossBlockEntities;
import rbasamoyai.betsyross.content.BetsyRossBlocks;
import rbasamoyai.betsyross.content.BetsyRossCreativeModeTab;
import rbasamoyai.betsyross.content.BetsyRossItems;
import rbasamoyai.betsyross.content.BetsyRossStats;
import rbasamoyai.betsyross.network.BetsyRossNetwork;

public class BetsyRossFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BetsyRoss.init();

        BetsyRossBlocks.registerAll(registerConsumer(BuiltInRegistries.BLOCK));
        BetsyRossItems.registerAll(registerConsumer(BuiltInRegistries.ITEM));
        BetsyRossBlockEntities.registerAll(registerConsumer(BuiltInRegistries.BLOCK_ENTITY_TYPE));
        BetsyRossCreativeModeTab.create(registerConsumer(BuiltInRegistries.CREATIVE_MODE_TAB));
        BetsyRossStats.registerAll(registerConsumer(BuiltInRegistries.CUSTOM_STAT));

        BetsyRossNetwork.init();
        BetsyRossStats.activateAllStats();

        ServerPlayConnectionEvents.JOIN.register(this::onPlayerLogin);

        BetsyRossConfig.init((t, c) -> ForgeConfigRegistry.INSTANCE.register(BetsyRoss.MOD_ID, t, c));
    }

    private static <T> BiConsumer<ResourceLocation, T> registerConsumer(Registry<T> registry) {
        return (loc, block) -> Registry.register(registry, loc, block);
    }

    private void onPlayerLogin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        BetsyRossCommonEvents.onPlayerLogin(handler.getPlayer());
    }

}
