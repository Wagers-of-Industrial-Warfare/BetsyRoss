package rbasamoyai.betsyross.network.c2s;

import java.util.concurrent.Executor;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.content.BetsyRossDataComponents;
import rbasamoyai.betsyross.network.BetsyRossPayload;

public record C2SSyncEmbroideryTableDataPayload(int slot, ResourceLocation loc) implements BetsyRossPayload {

    public static final CustomPacketPayload.Type<C2SSyncEmbroideryTableDataPayload> TYPE = new Type<>(BetsyRoss.path("sync_embroidery_table_data"));

    public static final StreamCodec<FriendlyByteBuf, C2SSyncEmbroideryTableDataPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, C2SSyncEmbroideryTableDataPayload::slot,
        ResourceLocation.STREAM_CODEC, C2SSyncEmbroideryTableDataPayload::loc,
        C2SSyncEmbroideryTableDataPayload::new);

    @Override
    public void handle(Player player, Executor exec) {
        exec.execute(() -> {
            ItemStack itemStack = player.getInventory().getItem(this.slot);
            itemStack.set(BetsyRossDataComponents.FLAG_ID.get(), this.loc);
        });
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
