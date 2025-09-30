package rbasamoyai.betsyross.neoforge;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.BetsyRossCommonEvents;

@EventBusSubscriber(modid = BetsyRoss.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class BetsyRossEventBus {

    @SubscribeEvent
    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer splayer)
            BetsyRossCommonEvents.onPlayerLogin(splayer);
    }

}
