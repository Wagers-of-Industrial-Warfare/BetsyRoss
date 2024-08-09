package rbasamoyai.betsyross.content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.flags.FlagBlockEntity;
import rbasamoyai.betsyross.foundation.ObjectHolder;

public class BetsyRossBlockEntities {

    private static final Map<String, Supplier<? extends BlockEntityType<?>>> ENTRIES = new LinkedHashMap<>();

    public static final Supplier<BlockEntityType<FlagBlockEntity>> FLAG_BLOCK_ENTITY = register("flag",
        () -> BlockEntityType.Builder.of(FlagBlockEntity::new, BetsyRossBlocks.FLAG_BLOCK.get(), BetsyRossBlocks.DRAPED_FLAG_BLOCK.get()).build(null));

    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String id, Supplier<BlockEntityType<T>> blockEntityType) {
        if (ENTRIES.containsKey(id))
            throw new IllegalStateException("Cannot register id '" + id + "' more than once");
        ObjectHolder<BlockEntityType<T>> holder = new ObjectHolder<>(blockEntityType);
        ENTRIES.put(id, holder);
        return holder;
    }

    public static void registerAll(BiConsumer<ResourceLocation, BlockEntityType<?>> cons) {
        for (Map.Entry<String, Supplier<? extends BlockEntityType<?>>> entry : ENTRIES.entrySet())
            cons.accept(BetsyRoss.path(entry.getKey()), entry.getValue().get());
    }

}
