package rbasamoyai.betsyross.content;

import java.util.function.BiConsumer;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.platform.BetsyRossServices;

public class BetsyRossCreativeModeTab {

    public static void create(BiConsumer<ResourceLocation, CreativeModeTab> cons) {
        CreativeModeTab tab = BetsyRossServices.INDEX_PLATFORM.tabBuilder()
            .title(Component.translatable("itemGroup." + BetsyRoss.MOD_ID))
            .icon(() -> BetsyRossItems.FLAG_ITEM.get().getDefaultInstance())
            .displayItems((param, output) -> {
                output.accept(BetsyRossItems.EMBROIDERY_TABLE_ITEM.get().getDefaultInstance());
                output.accept(BetsyRossItems.FLAG_ITEM.get().getDefaultInstance());
                output.accept(BetsyRossItems.FLAG_ITEM.get().getLogoStack());
                output.accept(BetsyRossItems.FLAG_STANDARD.get().getDefaultInstance());
                output.accept(BetsyRossItems.BANNER_STANDARD.get().getDefaultInstance());
                output.accept(BetsyRossItems.ARMOR_BANNER.get().getDefaultInstance());
            }).build();
        cons.accept(BetsyRoss.path("base"), tab);
    }

}
