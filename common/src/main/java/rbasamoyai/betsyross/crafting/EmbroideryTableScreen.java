package rbasamoyai.betsyross.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.betsyross.config.BetsyRossConfig;
import rbasamoyai.betsyross.content.BetsyRossDataComponents;
import rbasamoyai.betsyross.content.BetsyRossItems;
import rbasamoyai.betsyross.flags.AbstractFlagScreen;
import rbasamoyai.betsyross.network.BetsyRossNetworkHandler;
import rbasamoyai.betsyross.network.c2s.C2SSyncEmbroideryTableDataPayload;

public class EmbroideryTableScreen extends AbstractFlagScreen {

    private final int selected;
    private final Player player;

    public EmbroideryTableScreen(int selected, Player player, int minResolution, int maxResolution,
                                 boolean showOtherPlayersPaintings, int uploadPermissionLevel) {
        super(minResolution, maxResolution, showOtherPlayersPaintings, uploadPermissionLevel);
        this.selected = selected;
        this.player = player;
    }

    private ItemStack getTargetedItemStack() {
        return this.player.getInventory().getItem(this.selected);
    }

    @Override
    protected int getConfigWidth() {
        ItemStack itemStack = this.getTargetedItemStack();
        if (itemStack.is(BetsyRossItems.FLAG_STANDARD.get()))
            return BetsyRossConfig.SERVER.flagStandardMaxWidth;
        if (itemStack.is(BetsyRossItems.BANNER_STANDARD.get()))
            return BetsyRossConfig.SERVER.bannerStandardMaxWidth;
        if (itemStack.is(BetsyRossItems.ARMOR_BANNER.get()))
            return BetsyRossConfig.SERVER.armorBannerMaxWidth;
        return -1;
    }

    @Override
    protected int getConfigHeight() {
        ItemStack itemStack = this.getTargetedItemStack();
        if (itemStack.is(BetsyRossItems.FLAG_STANDARD.get()))
            return BetsyRossConfig.SERVER.flagStandardMaxHeight;
        if (itemStack.is(BetsyRossItems.BANNER_STANDARD.get()))
            return BetsyRossConfig.SERVER.bannerStandardMaxHeight;
        if (itemStack.is(BetsyRossItems.ARMOR_BANNER.get()))
            return BetsyRossConfig.SERVER.armorBannerMaxHeight;
        return -1;
    }

    @Override
    protected void updateFlag(ResourceLocation loc) {
        ItemStack itemStack = this.getTargetedItemStack();
        itemStack.set(BetsyRossDataComponents.FLAG_ID.get(), loc);
        BetsyRossNetworkHandler.Client.sendToServer(new C2SSyncEmbroideryTableDataPayload(this.selected, loc));
    }

    @Override
    protected boolean canUpdateFlag() {
        return EmbroideryTableBlock.isValidEmbroideryTableItem(this.getTargetedItemStack());
    }

}
