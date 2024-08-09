package rbasamoyai.betsyross;

import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.betsyross.network.BetsyRossNetwork;

public class BetsyRossCommonEvents {

    public static void onPlayerLogin(ServerPlayer player) {
        BetsyRossNetwork.sendVersionCheck(player);
    }

}
