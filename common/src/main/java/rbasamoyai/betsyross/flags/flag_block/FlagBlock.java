package rbasamoyai.betsyross.flags.flag_block;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.conczin.immersive_paintings.registration.Configs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.betsyross.content.BetsyRossBlocks;
import rbasamoyai.betsyross.network.BetsyRossNetworkHandler;
import rbasamoyai.betsyross.network.s2c.S2COpenFlagBlockScreenPayload;
import rbasamoyai.betsyross.tags.BetsyRossTags;

public class FlagBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final MapCodec<FlagBlock> CODEC = simpleCodec(FlagBlock::new);

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

    public FlagBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROTATION).add(BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(-context.getRotation() + 180));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlagBlockEntity(pos, state);
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof FlagBlockEntity flag))
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (level.isClientSide || !(player instanceof ServerPlayer splayer) || splayer.gameMode.getGameModeForPlayer() == GameType.ADVENTURE)
            return ItemInteractionResult.SUCCESS;
        boolean sneaking = player.isShiftKeyDown();
        boolean emptyFlagPole = flag.getFlagPole().isAir();
        if (stack.isEmpty() && !emptyFlagPole && !sneaking) {
            BlockState oldFlagpole = flag.getFlagPole();
            flag.setFlagPole(Blocks.AIR.defaultBlockState());
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!player.isCreative())
                player.addItem(oldFlagpole.getBlock().getCloneItemStack(level, pos, oldFlagpole));
            return ItemInteractionResult.CONSUME;
        } else if (stack.getItem() instanceof BlockItem item) {
            if (emptyFlagPole && isFlagpole(item.getBlock()) && !sneaking) {
                BlockState state1 = item.getBlock().defaultBlockState();
                flag.setFlagPole(state1);
                SoundType soundType = state1.getSoundType();
                level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                if (!player.isCreative())
                    stack.shrink(1);
                return ItemInteractionResult.CONSUME;
            } else {
                return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
            }
        }
        BetsyRossNetworkHandler.sendToClient(splayer, new S2COpenFlagBlockScreenPayload(pos,
            Configs.COMMON.minPaintingResolution, Configs.COMMON.maxPaintingResolution,
            Configs.COMMON.showOtherPlayersPaintings, Configs.COMMON.uploadPermissionLevel));
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isSolid() || level.getBlockState(pos.below()).is(BetsyRossBlocks.FLAG_BLOCK.get());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState otherState, LevelAccessor level, BlockPos pos, BlockPos otherPos) {
        return dir == Direction.DOWN && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, dir, otherState, level, pos, otherPos);
    }

    public static boolean isFlagpole(Block block) {
        return block.defaultBlockState().is(BetsyRossTags.FLAGPOLE);
    }

    public static Properties blockProperties() {
        return Properties.of().sound(SoundType.WOOL).mapColor(MapColor.WOOL).noCollission().noOcclusion().instabreak();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> list = super.getDrops(state, params);
        BlockEntity be = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof FlagBlockEntity flag) {
            ItemStack itemStack = new ItemStack(flag.getFlagPole().getBlock());
            if (!itemStack.isEmpty())
                list.add(itemStack);
        }
        return list;
    }

    @Override protected MapCodec<? extends Block> codec() { return CODEC; }
}
