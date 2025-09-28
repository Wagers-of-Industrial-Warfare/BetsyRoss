package rbasamoyai.betsyross.flags.flag_block;

import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.content.BetsyRossDataComponents;

public class FlagBlockItem extends StandingAndWallBlockItem {

	public FlagBlockItem(Block standing, Block wall, Properties properties) {
		super(standing, wall, properties, Direction.DOWN);
	}

	public ItemStack getLogoStack() {
		ItemStack result = new ItemStack(this);
        result.set(BetsyRossDataComponents.FLAG_ID.get(), BetsyRoss.path("paintings/logo.png"));
        result.set(DataComponents.CUSTOM_NAME, Component.translatable("item.betsyross.logo_flag").withStyle(style -> style.withItalic(false)));
		return result;
	}

}
