package rbasamoyai.betsyross.network;

import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import immersive_paintings.resources.Painting;
import immersive_paintings.resources.ServerPaintingManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;

public record ServerboundModifyFlagBlockPacket(BlockPos pos, ResourceLocation flagId) implements CommonPacket {

    public static final ResourceLocation ID = BetsyRoss.path("modify_flag_block");

    public ServerboundModifyFlagBlockPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readResourceLocation());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos).writeResourceLocation(this.flagId);
    }

    @Override
    public void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender) {
        if (sender == null || !(sender.level().getBlockEntity(this.pos) instanceof FlagBlockEntity flag))
            return;
        Painting painting = ServerPaintingManager.getPainting(this.flagId);
        if (painting == null)
            return;
        flag.setFlag(this.flagId);
        flag.setChanged();
        if (sender.level() instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(flag.getBlockPos());
    }

    @Override public ResourceLocation name() { return ID; }

}
