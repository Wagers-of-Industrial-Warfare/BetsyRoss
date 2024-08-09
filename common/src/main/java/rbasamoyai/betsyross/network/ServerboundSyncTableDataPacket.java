package rbasamoyai.betsyross.network;

import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.crafting.EmbroideryTableMenu;

public record ServerboundSyncTableDataPacket(String url, byte width, byte height) implements CommonPacket {

    public static final ResourceLocation ID = BetsyRoss.path("sync_table_data");

	public ServerboundSyncTableDataPacket(FriendlyByteBuf buf) { this(buf.readUtf(), buf.readByte(), buf.readByte()); }

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(this.url);
		buf.writeByte(this.width);
		buf.writeByte(this.height);
	}

    @Override
    public void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender) {
        exec.execute(() -> {
            if (sender != null && sender.containerMenu instanceof EmbroideryTableMenu menu)
                menu.setDataOnServer(this.url, this.width, this.height);
        });
    }

    @Override public ResourceLocation name() { return ID; }

}
