package rbasamoyai.betsyross.network;

import java.util.concurrent.Executor;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface BetsyRossPayload extends CustomPacketPayload {
    void handle(Player player, Executor exec);
}
