package rbasamoyai.betsyross.network.s2c;

import java.util.concurrent.Executor;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockScreen;
import rbasamoyai.betsyross.network.BetsyRossPayload;

public record S2COpenFlagBlockScreenPayload(BlockPos pos, int minResolution, int maxResolution, boolean showOtherPlayerPaintings,
                                            int permissionLevel) implements BetsyRossPayload {

    public static final CustomPacketPayload.Type<S2COpenFlagBlockScreenPayload> TYPE = new Type<>(BetsyRoss.path("open_flag_block_screen"));

    public static final StreamCodec<FriendlyByteBuf, S2COpenFlagBlockScreenPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, S2COpenFlagBlockScreenPayload::pos,
        ByteBufCodecs.VAR_INT, S2COpenFlagBlockScreenPayload::minResolution,
        ByteBufCodecs.VAR_INT, S2COpenFlagBlockScreenPayload::maxResolution,
        ByteBufCodecs.BOOL, S2COpenFlagBlockScreenPayload::showOtherPlayerPaintings,
        ByteBufCodecs.VAR_INT, S2COpenFlagBlockScreenPayload::permissionLevel,
        S2COpenFlagBlockScreenPayload::new);

    @Override
    public void handle(Player player, Executor exec) {
        exec.execute(() -> ScreenProxy.load(this, player));
    }

    private static class ScreenProxy {
        public static void load(S2COpenFlagBlockScreenPayload payload, Player player) {
            if (player.level().getBlockEntity(payload.pos) instanceof FlagBlockEntity flag)
                Minecraft.getInstance().setScreen(new FlagBlockScreen(payload.pos, flag, payload.minResolution,
                    payload.maxResolution, payload.showOtherPlayerPaintings, payload.permissionLevel));
        }
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
