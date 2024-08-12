package rbasamoyai.betsyross.crafting;

import immersive_paintings.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import rbasamoyai.betsyross.content.BetsyRossItems;
import rbasamoyai.betsyross.content.BetsyRossStats;
import rbasamoyai.betsyross.network.BetsyRossNetwork;
import rbasamoyai.betsyross.network.ClientboundOpenEmbroideryTableScreenPacket;

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
		if (hand == InteractionHand.MAIN_HAND) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (isValidEmbroideryTableItem(itemStack)) {
                if (player instanceof ServerPlayer splayer) {
                    Config config = Config.getInstance();
                    BetsyRossNetwork.sendToPlayer(splayer, new ClientboundOpenEmbroideryTableScreenPacket(player.getInventory().selected,
                        config.minPaintingResolution, config.maxPaintingResolution, config.showOtherPlayersPaintings, config.uploadPermissionLevel));
                    player.awardStat(BetsyRossStats.INTERACT_WITH_EMBROIDERY_TABLE);
                }
            } else if (isInvalidFlagItem(itemStack)) {
                player.displayClientMessage(Component.translatable("gui.betsyross.embroidery_table.invalid_flag"), true);
            } else {
                player.displayClientMessage(Component.translatable("gui.betsyross.embroidery_table.invalid_item"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
		}
        return super.use(state, level, pos, player, hand, result);
	}

	public static Properties properties() {
		return Properties.of().strength(2.5F).sound(SoundType.WOOD).mapColor(MapColor.WOOD);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

    public static boolean isValidEmbroideryTableItem(ItemStack itemStack) {
        if (itemStack.is(BetsyRossItems.FLAG_STANDARD.get()))
            return true;
        if (itemStack.is(BetsyRossItems.BANNER_STANDARD.get()))
            return true;
        return itemStack.is(BetsyRossItems.ARMOR_BANNER.get());
    }

    public static boolean isInvalidFlagItem(ItemStack itemStack) {
        return itemStack.is(BetsyRossItems.FLAG_ITEM.get());
    }

}
