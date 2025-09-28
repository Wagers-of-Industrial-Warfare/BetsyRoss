package rbasamoyai.betsyross.content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.foundation.ObjectHolder;

public class BetsyRossDataComponents {

    private static final Map<String, Supplier<? extends DataComponentType<?>>> ENTRIES = new LinkedHashMap<>();

    public static final Supplier<DataComponentType<ResourceLocation>> FLAG_ID = register("flag_id",
        builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));

    public static final Supplier<DataComponentType<Boolean>> RAISED = register("raised",
        builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    private static <T> Supplier<DataComponentType<T>> register(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        if (ENTRIES.containsKey(id))
            throw new IllegalStateException("Cannot register id '" + id + "' more than once");
        ObjectHolder<DataComponentType<T>> holder = new ObjectHolder<>(() -> builder.apply(DataComponentType.builder()).build());
        ENTRIES.put(id, holder);
        return holder;
    }

    public static void registerAll(BiConsumer<ResourceLocation, DataComponentType<?>> cons) {
        for (Map.Entry<String, Supplier<? extends DataComponentType<?>>> entry : ENTRIES.entrySet())
            cons.accept(BetsyRoss.path(entry.getKey()), entry.getValue().get());
    }

}
