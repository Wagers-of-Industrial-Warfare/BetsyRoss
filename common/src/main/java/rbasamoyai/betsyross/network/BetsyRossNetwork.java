package rbasamoyai.betsyross.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.betsyross.network.c2s.C2SModifyFlagBlockPayload;
import rbasamoyai.betsyross.network.c2s.C2SSyncEmbroideryTableDataPayload;
import rbasamoyai.betsyross.network.s2c.S2COpenEmbroideryTableScreenPayload;
import rbasamoyai.betsyross.network.s2c.S2COpenFlagBlockScreenPayload;

public class BetsyRossNetwork {

    private static final Map<ResourceLocation, Function<FriendlyByteBuf, BetsyRossPayload>> PACKETS_BY_ID = new HashMap<>();

    public static void register(Registrar cons) {
        cons.register(C2SModifyFlagBlockPayload.TYPE, C2SModifyFlagBlockPayload.STREAM_CODEC, true);
        cons.register(C2SSyncEmbroideryTableDataPayload.TYPE, C2SSyncEmbroideryTableDataPayload.STREAM_CODEC, true);

        cons.register(S2COpenEmbroideryTableScreenPayload.TYPE, S2COpenEmbroideryTableScreenPayload.STREAM_CODEC, false);
        cons.register(S2COpenFlagBlockScreenPayload.TYPE, S2COpenFlagBlockScreenPayload.STREAM_CODEC, false);
    }

    @Nullable
    public static BetsyRossPayload constructCommonPacket(ResourceLocation id, FriendlyByteBuf data) {
        if (!PACKETS_BY_ID.containsKey(id))
            return null;
        Function<FriendlyByteBuf, BetsyRossPayload> cons = PACKETS_BY_ID.get(id);
        return cons.apply(data);
    }

    public interface Registrar {
        <T extends BetsyRossPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec, boolean isServerbound);
    }

}
