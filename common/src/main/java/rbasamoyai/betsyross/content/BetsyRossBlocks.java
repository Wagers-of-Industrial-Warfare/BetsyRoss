package rbasamoyai.betsyross.content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.crafting.EmbroideryTableBlock;
import rbasamoyai.betsyross.flags.DrapedFlagBlock;
import rbasamoyai.betsyross.flags.FlagBlock;
import rbasamoyai.betsyross.foundation.ObjectHolder;

public class BetsyRossBlocks {

    private static final Map<String, Supplier<? extends Block>> ENTRIES = new LinkedHashMap<>();

    public static final Supplier<FlagBlock> FLAG_BLOCK = register("flag_block", () -> new FlagBlock(FlagBlock.properties()));
    public static final Supplier<DrapedFlagBlock> DRAPED_FLAG_BLOCK = register("draped_flag_block", () -> new DrapedFlagBlock(FlagBlock.properties()));
    public static final Supplier<EmbroideryTableBlock> EMBROIDERY_TABLE_BLOCK = register("embroidery_table", () -> new EmbroideryTableBlock(EmbroideryTableBlock.properties()));

    private static <T extends Block> Supplier<T> register(String id, Supplier<T> block) {
        if (ENTRIES.containsKey(id))
            throw new IllegalStateException("Cannot register id '" + id + "' more than once");
        ObjectHolder<T> holder = new ObjectHolder<>(block);
        ENTRIES.put(id, holder);
        return holder;
    }

    public static void registerAll(BiConsumer<ResourceLocation, Block> cons) {
        for (Map.Entry<String, Supplier<? extends Block>> entry : ENTRIES.entrySet())
            cons.accept(BetsyRoss.path(entry.getKey()), entry.getValue().get());
    }

}
