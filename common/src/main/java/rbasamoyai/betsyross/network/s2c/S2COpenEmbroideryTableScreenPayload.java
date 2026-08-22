package rbasamoyai.betsyross.network.s2c;

import java.util.concurrent.Executor;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.crafting.EmbroideryTableScreen;
import rbasamoyai.betsyross.network.BetsyRossPayload;

public record S2COpenEmbroideryTableScreenPayload(int slot, int minResolution, int maxResolution, boolean showOtherPlayerPaintings,
                                                  int permissionLevel) implements BetsyRossPayload {

    public static final CustomPacketPayload.Type<S2COpenEmbroideryTableScreenPayload> TYPE = new Type<>(BetsyRoss.path("open_embroidery_table_screen"));

    public static final StreamCodec<FriendlyByteBuf, S2COpenEmbroideryTableScreenPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, S2COpenEmbroideryTableScreenPayload::slot,
        ByteBufCodecs.VAR_INT, S2COpenEmbroideryTableScreenPayload::minResolution,
        ByteBufCodecs.VAR_INT, S2COpenEmbroideryTableScreenPayload::maxResolution,
        ByteBufCodecs.BOOL, S2COpenEmbroideryTableScreenPayload::showOtherPlayerPaintings,
        ByteBufCodecs.VAR_INT, S2COpenEmbroideryTableScreenPayload::permissionLevel,
        S2COpenEmbroideryTableScreenPayload::new);

    @Override
    public void handle(Player player, Executor exec) {
        exec.execute(() -> ScreenProxy.load(this, player));
    }

    private static class ScreenProxy {
        public static void load(S2COpenEmbroideryTableScreenPayload payload, Player player) {
            Minecraft.getInstance().setScreen(new EmbroideryTableScreen(payload.slot, player, payload.minResolution,
                payload.maxResolution, payload.showOtherPlayerPaintings, payload.permissionLevel));
        }
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
