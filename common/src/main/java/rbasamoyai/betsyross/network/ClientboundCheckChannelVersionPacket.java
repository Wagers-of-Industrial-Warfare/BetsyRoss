package rbasamoyai.betsyross.network;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.EnvExecute;

public record ClientboundCheckChannelVersionPacket(String serverVersion) implements CommonPacket {

    public static final ResourceLocation ID = BetsyRoss.path("version_check");

	public ClientboundCheckChannelVersionPacket(FriendlyByteBuf buf) {
		this(buf.readUtf());
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(this.serverVersion);
	}

	@Override
	public void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender) {
		EnvExecute.executeOnClient(() -> () -> BetsyRossClientHandlers.checkVersion(this));
	}

    @Override public ResourceLocation name() { return ID; }

}
