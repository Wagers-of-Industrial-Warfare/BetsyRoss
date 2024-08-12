package rbasamoyai.betsyross.flags.flag_block;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;

public class FlagBlockItem extends StandingAndWallBlockItem {

	public FlagBlockItem(Block standing, Block wall, Properties properties) {
		super(standing, wall, properties, Direction.DOWN);
	}

	public ItemStack getLogoStack() {
		ItemStack result = new ItemStack(this);
		CompoundTag tag = result.getOrCreateTag();
		CompoundTag blockData = new CompoundTag();
		blockData.putString("FlagId", "betsyross:paintings/logo.png");
		tag.put("BlockEntityTag", blockData);
        result.setHoverName(Component.translatable("item.betsyross.logo_flag").withStyle(style -> style.withItalic(false)));
		return result;
	}

}
