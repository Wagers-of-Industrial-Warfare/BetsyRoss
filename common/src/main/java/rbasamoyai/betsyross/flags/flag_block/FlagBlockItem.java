package rbasamoyai.betsyross.flags.flag_block;

import org.jetbrains.annotations.Nullable;

import net.conczin.immersive_paintings.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.content.BetsyRossDataComponents;

public class FlagBlockItem extends StandingAndWallBlockItem {

	public FlagBlockItem(Block standing, Block wall, Properties properties) {
		super(standing, wall, properties, Direction.DOWN);
	}

	public ItemStack getLogoStack() {
		ItemStack result = new ItemStack(this);
        result.set(BetsyRossDataComponents.FLAG_ID.get(), Main.locate("datapack/betsyrosspaintingslogopng"));
        result.set(DataComponents.CUSTOM_NAME, Component.translatable("item.betsyross.logo_flag").withStyle(style -> style.withItalic(false)));
		return result;
	}

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        boolean result = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (level.getBlockEntity(pos) instanceof FlagBlockEntity flag) {
            flag.setFlag(stack.getOrDefault(BetsyRossDataComponents.FLAG_ID.get(), BetsyRoss.DEFAULT_FLAG));
            flag.setChanged();
        }
        return result;
    }
}
