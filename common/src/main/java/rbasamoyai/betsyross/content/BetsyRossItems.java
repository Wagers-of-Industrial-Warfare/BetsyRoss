package rbasamoyai.betsyross.content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockItem;
import rbasamoyai.betsyross.flags.standards.ArmorBannerItem;
import rbasamoyai.betsyross.flags.standards.StandardItem;
import rbasamoyai.betsyross.foundation.ObjectHolder;

public class BetsyRossItems {

    private static final Map<String, Supplier<? extends Item>> ENTRIES = new LinkedHashMap<>();

    public static final Supplier<FlagBlockItem> FLAG_ITEM = register("flag_block",
        () -> new FlagBlockItem(BetsyRossBlocks.FLAG_BLOCK.get(), BetsyRossBlocks.DRAPED_FLAG_BLOCK.get(),
            new Item.Properties()));
    public static final Supplier<StandardItem> FLAG_STANDARD = register("flag_standard",
        () -> new StandardItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<StandardItem> BANNER_STANDARD = register("banner_standard",
        () -> new StandardItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<ArmorBannerItem> ARMOR_BANNER = register("armor_banner",
        () -> new ArmorBannerItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<BlockItem> EMBROIDERY_TABLE_ITEM = register("embroidery_table",
        () -> new BlockItem(BetsyRossBlocks.EMBROIDERY_TABLE_BLOCK.get(), new Item.Properties()));

    private static <T extends Item> Supplier<T> register(String id, Supplier<T> item) {
        if (ENTRIES.containsKey(id))
            throw new IllegalStateException("Cannot register id '" + id + "' more than once");
        ObjectHolder<T> holder = new ObjectHolder<>(item);
        ENTRIES.put(id, holder);
        return holder;
    }

    public static void registerAll(BiConsumer<ResourceLocation, Item> cons) {
        for (Map.Entry<String, Supplier<? extends Item>> entry : ENTRIES.entrySet())
            cons.accept(BetsyRoss.path(entry.getKey()), entry.getValue().get());
    }

}
