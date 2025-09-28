package rbasamoyai.betsyross.flags.standards;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.betsyross.content.BetsyRossDataComponents;

public class StandardItem extends Item {

	public StandardItem(Properties properties) { super(properties); }

	@Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 72000; }

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
        ItemStack stack = player.getItemInHand(hand);
        stack.set(BetsyRossDataComponents.RAISED.get(), true);
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        stack.set(BetsyRossDataComponents.RAISED.get(), false);
        super.releaseUsing(stack, level, livingEntity, timeCharged);
    }
}
