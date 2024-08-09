package rbasamoyai.betsyross.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class BetsyRossClientHandlers {

    public static void checkVersion(ClientboundCheckChannelVersionPacket pkt) {
        if (BetsyRossNetwork.checkVersion(pkt.serverVersion()))
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null)
            mc.getConnection().onDisconnect(Component.literal("Betsy Ross on the client uses a different network format than the server.")
                .append(" Please use a matching format."));
    }

    private BetsyRossClientHandlers() {}

}
