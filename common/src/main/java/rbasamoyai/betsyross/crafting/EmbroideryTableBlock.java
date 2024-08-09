package rbasamoyai.betsyross.crafting;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.betsyross.content.BetsyRossStats;

public class EmbroideryTableBlock extends Block {

	private static final VoxelShape SHAPE = Shapes.or(
			Block.box(0, 12, 0, 16, 16, 16),
			Block.box(1, 0, 1, 5, 12, 5),
			Block.box(11, 0, 1, 15, 12, 5),
			Block.box(1, 0, 11, 5, 12, 15),
			Block.box(11, 0, 11, 15, 12, 15));

	public EmbroideryTableBlock(Properties properties) { super(properties); }

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (!level.isClientSide && player instanceof ServerPlayer splayer) {
            splayer.openMenu(state.getMenuProvider(level, pos));
			player.awardStat(BetsyRossStats.INTERACT_WITH_EMBROIDERY_TABLE);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Nullable
	@Override
	public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider((windowId, playerInv, player) -> {
			return new EmbroideryTableMenu(windowId, playerInv, ContainerLevelAccess.create(level, pos));
		}, Component.translatable(this.getDescriptionId()));
	}

	public static Properties properties() {
		return Properties.of().strength(2.5F).sound(SoundType.WOOD).mapColor(MapColor.WOOD);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

}
