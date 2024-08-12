package rbasamoyai.betsyross.content;

import java.util.function.BiConsumer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import rbasamoyai.betsyross.BetsyRoss;

public class BetsyRossCreativeModeTab {

    public static void create(BiConsumer<ResourceLocation, CreativeModeTab> cons) {
        CreativeModeTab tab = tabBuilder()
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

    @ExpectPlatform public static CreativeModeTab.Builder tabBuilder() { throw new AssertionError(); }

}
