package rbasamoyai.betsyross.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import rbasamoyai.betsyross.BetsyRoss;

public class BetsyRossTags {

	public static final TagKey<Block> FLAGPOLE = TagKey.create(Registries.BLOCK, BetsyRoss.path("flagpole"));

	public static final TagKey<Item> FLAG_MATERIAL = TagKey.create(Registries.ITEM, BetsyRoss.path("flag_material"));
	public static final TagKey<Item> FLAG_STICK_MATERIAL = TagKey.create(Registries.ITEM, BetsyRoss.path("flag_stick_material"));

}
