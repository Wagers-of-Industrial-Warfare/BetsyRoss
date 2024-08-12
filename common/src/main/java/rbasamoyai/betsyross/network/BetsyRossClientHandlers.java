package rbasamoyai.betsyross.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import rbasamoyai.betsyross.crafting.EmbroideryTableScreen;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockScreen;

public class BetsyRossClientHandlers {

    public static void checkVersion(ClientboundCheckChannelVersionPacket pkt) {
        if (BetsyRossNetwork.checkVersion(pkt.serverVersion()))
            return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null)
            mc.getConnection().onDisconnect(Component.literal("Betsy Ross on the client uses a different network format than the server.")
                .append(" Please use a matching format."));
    }

    public static void openFlagBlockScreen(ClientboundOpenFlagBlockScreenPacket pkt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.level.getBlockEntity(pkt.pos()) instanceof FlagBlockEntity flag))
            return;
        mc.setScreen(new FlagBlockScreen(pkt.pos(), flag, pkt.minResolution(), pkt.maxResolution(), pkt.showOtherPlayerPaintings(), pkt.permissionLevel()));
    }

    public static void openEmbroideryTableScreen(ClientboundOpenEmbroideryTableScreenPacket pkt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;
        mc.setScreen(new EmbroideryTableScreen(pkt.slot(), mc.player, pkt.minResolution(), pkt.maxResolution(), pkt.showOtherPlayerPaintings(), pkt.permissionLevel()));
    }

    private BetsyRossClientHandlers() {}

}
