package rbasamoyai.betsyross.flags;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArmorBannerItem extends StandardItem implements Equipable {

	public ArmorBannerItem(Properties properties) { super(properties); }

	@Override public EquipmentSlot getEquipmentSlot() { return EquipmentSlot.HEAD; }

	@Override public int getUseDuration(ItemStack stack) { return 0; }

	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

}
