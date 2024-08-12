package rbasamoyai.betsyross.forge;

import java.util.function.BiConsumer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.BetsyRossCommonEvents;
import rbasamoyai.betsyross.config.BetsyRossConfig;
import rbasamoyai.betsyross.content.BetsyRossBlockEntities;
import rbasamoyai.betsyross.content.BetsyRossBlocks;
import rbasamoyai.betsyross.content.BetsyRossCreativeModeTab;
import rbasamoyai.betsyross.content.BetsyRossItems;
import rbasamoyai.betsyross.content.BetsyRossStats;
import rbasamoyai.betsyross.network.BetsyRossNetwork;

@Mod(BetsyRoss.MOD_ID)
public class BetsyRossForge {

    public BetsyRossForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        BetsyRoss.init();

        modBus.addListener(this::onRegister);
        modBus.addListener(this::onCommonSetup);

        forgeBus.addListener(this::onPlayerLogin);

        BetsyRossConfig.init(ModLoadingContext.get()::registerConfig);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> BetsyRossClientForge.onCtor(modBus, forgeBus));
    }

    private void onRegister(final RegisterEvent event) {
        if (event.getRegistryKey() == Registries.BLOCK) {
            BetsyRossBlocks.registerAll(registryConsumer(Registries.BLOCK, event));
        } else if (event.getRegistryKey() == Registries.ITEM) {
            BetsyRossItems.registerAll(registryConsumer(Registries.ITEM, event));
        } else if (event.getRegistryKey() == Registries.BLOCK_ENTITY_TYPE) {
            BetsyRossBlockEntities.registerAll(registryConsumer(Registries.BLOCK_ENTITY_TYPE, event));
        } else if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
            BetsyRossCreativeModeTab.create(registryConsumer(Registries.CREATIVE_MODE_TAB, event));
        } else if (event.getRegistryKey() == Registries.CUSTOM_STAT) {
            BetsyRossStats.registerAll(registryConsumer(Registries.CUSTOM_STAT, event));
        }
    }

    private static <T> BiConsumer<ResourceLocation, T> registryConsumer(ResourceKey<? extends Registry<T>> key, RegisterEvent event) {
        return (loc, block) -> event.register(key, loc, () -> block);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BetsyRossNetwork.init();
            BetsyRossStats.activateAllStats();
        });
    }

    private void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer splayer)
            BetsyRossCommonEvents.onPlayerLogin(splayer);
    }

}
