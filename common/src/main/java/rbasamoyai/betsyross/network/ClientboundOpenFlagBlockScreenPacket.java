package rbasamoyai.betsyross.network;

import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.EnvExecute;

public record ClientboundOpenFlagBlockScreenPacket(BlockPos pos, int minResolution, int maxResolution, boolean showOtherPlayerPaintings,
                                                   int permissionLevel) implements CommonPacket {

    public static final ResourceLocation ID = BetsyRoss.path("open_flag_block_screen");

    public ClientboundOpenFlagBlockScreenPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readVarInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos)
            .writeVarInt(this.minResolution)
            .writeVarInt(this.maxResolution)
            .writeBoolean(this.showOtherPlayerPaintings);
        buf.writeVarInt(this.permissionLevel);
    }

    @Override
    public void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender) {
        EnvExecute.executeOnClient(() -> () -> BetsyRossClientHandlers.openFlagBlockScreen(this));
    }

    @Override public ResourceLocation name() { return ID; }

}
