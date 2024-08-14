package rbasamoyai.betsyross.flags.flag_block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.content.BetsyRossBlockEntities;
import rbasamoyai.betsyross.foundation.BetsyRossUtils;

public class FlagBlockEntity extends BlockEntity {

	private ResourceLocation flagId = BetsyRoss.DEFAULT_FLAG;
	private BlockState flagPole = Blocks.AIR.defaultBlockState();

	public FlagBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public FlagBlockEntity(BlockPos pos, BlockState state) { this(BetsyRossBlockEntities.FLAG_BLOCK_ENTITY.get(), pos, state); }

    public void setFlag(ResourceLocation flagId) { this.flagId = flagId; }
	public ResourceLocation getFlagId() { return this.flagId; }

	public void setFlagPole(BlockState state) {
		this.flagPole = state;
		this.setChanged();
        if (this.level instanceof ServerLevel slevel)
            slevel.getChunkSource().blockChanged(this.worldPosition);
	}

	public BlockState getFlagPole() { return this.flagPole; }

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putString("FlagId", this.flagId.toString());
		if (this.flagPole != null)
            tag.put("Flagpole", NbtUtils.writeBlockState(this.flagPole));
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.flagId = BetsyRossUtils.location(tag.getString("FlagId"));
		HolderGetter<Block> holder = this.level == null ? BuiltInRegistries.BLOCK.asLookup() : this.level.holderLookup(Registries.BLOCK);
		this.flagPole = NbtUtils.readBlockState(holder, tag.getCompound("Flagpole"));
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override public CompoundTag getUpdateTag() { return this.saveWithoutMetadata(); }

}
