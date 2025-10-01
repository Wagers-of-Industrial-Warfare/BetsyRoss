package rbasamoyai.betsyross.flags.flag_block;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import immersive_paintings.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.betsyross.network.BetsyRossNetwork;
import rbasamoyai.betsyross.network.ClientboundOpenFlagBlockScreenPacket;

public class DrapedFlagBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	/** Taken from {@link WallBannerBlock} */
	private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
			Direction.NORTH, Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D),
			Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D),
			Direction.WEST, Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
			Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D)));

	public DrapedFlagBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING).add(BlockStateProperties.WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState();
		return state.setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FlagBlockEntity(pos, state);
	}

	@Override public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).isSolid();
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
		return dir == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, dir, otherState, level, pos, otherPos);
	}

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof FlagBlockEntity))
            return super.use(state, level, pos, player, hand, hit);
        if (level.isClientSide || !(player instanceof ServerPlayer splayer) || splayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE)
            return InteractionResult.SUCCESS;
        Config config = Config.getInstance();
        BetsyRossNetwork.sendToPlayer(splayer, new ClientboundOpenFlagBlockScreenPacket(pos, config.minPaintingResolution,
            config.maxPaintingResolution, config.showOtherPlayersPaintings, config.uploadPermissionLevel));
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

}
