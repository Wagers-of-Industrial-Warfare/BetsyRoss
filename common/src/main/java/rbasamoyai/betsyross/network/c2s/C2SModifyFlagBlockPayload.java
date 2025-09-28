package rbasamoyai.betsyross.network.c2s;

import java.util.concurrent.Executor;

import net.conczin.immersive_paintings.ServerPaintingManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;
import rbasamoyai.betsyross.network.BetsyRossPayload;

public record C2SModifyFlagBlockPayload(BlockPos pos, ResourceLocation flagId) implements BetsyRossPayload {

    public static final CustomPacketPayload.Type<C2SModifyFlagBlockPayload> TYPE = new Type<>(BetsyRoss.path("modify_flag_block"));

    public static final StreamCodec<FriendlyByteBuf, C2SModifyFlagBlockPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, C2SModifyFlagBlockPayload::pos,
        ResourceLocation.STREAM_CODEC, C2SModifyFlagBlockPayload::flagId,
        C2SModifyFlagBlockPayload::new);

    @Override
    public void handle(Player player, Executor exec) {
        exec.execute(() -> {
            if (!(player.level().getBlockEntity(this.pos) instanceof FlagBlockEntity flag))
                return;
            if (ServerPaintingManager.getPainting(player.getServer(), this.flagId).isEmpty())
                return;
            flag.setFlag(this.flagId);
            flag.setChanged();
            if (player.level() instanceof ServerLevel slevel)
                slevel.getChunkSource().blockChanged(this.pos);
        });
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
