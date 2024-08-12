package rbasamoyai.betsyross.flags.flag_block;

import javax.annotation.Nullable;

import immersive_paintings.resources.Painting;
import immersive_paintings.resources.ServerPaintingManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.AABB;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.content.BetsyRossBlockEntities;
import rbasamoyai.betsyross.content.BetsyRossBlocks;
import rbasamoyai.betsyross.foundation.BetsyRossUtils;

public class FlagBlockEntity extends BlockEntity {

	private ResourceLocation flagId = BetsyRoss.DEFAULT_FLAG;
	private BlockState flagPole = Blocks.AIR.defaultBlockState();
    private AABB renderBoundingBox = null;
    private BlockState oldState;

	public FlagBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
        this.oldState = this.getBlockState();
	}

	public FlagBlockEntity(BlockPos pos, BlockState state) { this(BetsyRossBlockEntities.FLAG_BLOCK_ENTITY.get(), pos, state); }

	public AABB commonGetRenderBoundingBox() {
        if (this.renderBoundingBox == null || !this.oldState.equals(this.getBlockState()))
            this.renderBoundingBox = this.calculateRenderBoundingBox();
        return this.renderBoundingBox;
	}

    protected AABB calculateRenderBoundingBox() {
        BlockState state = this.getBlockState();
        BlockPos pos = this.getBlockPos();
        Painting painting = ServerPaintingManager.getPainting(this.flagId);
        if (painting == null)
            return new AABB(pos);
        int flagWidth = painting.width;
        int flagHeight = painting.height;
        if (state.is(BetsyRossBlocks.FLAG_BLOCK.get())) {
            float dir = RotationSegment.convertToDegrees(state.getValue(FlagBlock.ROTATION));
            float f1 = Mth.sin(dir * Mth.DEG_TO_RAD);
            float f2 = Mth.cos(dir * Mth.DEG_TO_RAD);
            return new AABB(pos).expandTowards(f1 * flagWidth, flagHeight, f2 * flagWidth).inflate(1);
        }
        if (state.is(BetsyRossBlocks.DRAPED_FLAG_BLOCK.get())) {
            Direction dir = state.getValue(DrapedFlagBlock.FACING);
            return new AABB(pos.relative(dir.getOpposite()), pos.below(flagHeight).relative(dir.getCounterClockWise(), flagWidth)).inflate(1);
        }
        return new AABB(pos);
    }

    public void setFlag(ResourceLocation flagId) {
        this.flagId = flagId;
        this.renderBoundingBox = null;
    }

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
