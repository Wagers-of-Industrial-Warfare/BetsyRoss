package rbasamoyai.betsyross.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import rbasamoyai.betsyross.network.c2s.C2SModifyFlagBlockPayload;
import rbasamoyai.betsyross.network.c2s.C2SSyncEmbroideryTableDataPayload;
import rbasamoyai.betsyross.network.s2c.S2COpenEmbroideryTableScreenPayload;
import rbasamoyai.betsyross.network.s2c.S2COpenFlagBlockScreenPayload;

public class BetsyRossNetwork {

    public static void register(Registrar cons) {
        cons.register(C2SModifyFlagBlockPayload.TYPE, C2SModifyFlagBlockPayload.STREAM_CODEC, true);
        cons.register(C2SSyncEmbroideryTableDataPayload.TYPE, C2SSyncEmbroideryTableDataPayload.STREAM_CODEC, true);

        cons.register(S2COpenEmbroideryTableScreenPayload.TYPE, S2COpenEmbroideryTableScreenPayload.STREAM_CODEC, false);
        cons.register(S2COpenFlagBlockScreenPayload.TYPE, S2COpenFlagBlockScreenPayload.STREAM_CODEC, false);
    }

    public interface Registrar {
        <T extends BetsyRossPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, boolean isServerbound);
    }

    private BetsyRossNetwork() {}

}
