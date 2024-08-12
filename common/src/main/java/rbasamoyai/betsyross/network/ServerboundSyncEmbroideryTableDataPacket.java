package rbasamoyai.betsyross.network;

import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.betsyross.BetsyRoss;

public record ServerboundSyncEmbroideryTableDataPacket(int slot, ResourceLocation loc) implements CommonPacket {

    public static final ResourceLocation ID = BetsyRoss.path("sync_embroidery_table_data");

	public ServerboundSyncEmbroideryTableDataPacket(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readResourceLocation());
    }

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.slot).writeResourceLocation(this.loc);
	}

    @Override
    public void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender) {
        if (sender == null)
            return;
        ItemStack itemStack = sender.getInventory().getItem(this.slot);
        itemStack.getOrCreateTag().putString("FlagId", this.loc.toString());
    }

    @Override public ResourceLocation name() { return ID; }

}
