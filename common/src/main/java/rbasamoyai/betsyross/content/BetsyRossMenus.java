package rbasamoyai.betsyross.content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import rbasamoyai.betsyross.BetsyRoss;
import rbasamoyai.betsyross.crafting.EmbroideryTableMenu;
import rbasamoyai.betsyross.foundation.ObjectHolder;

public class BetsyRossMenus {

    private static final Map<String, Supplier<? extends MenuType<?>>> ENTRIES = new LinkedHashMap<>();

    public static final Supplier<MenuType<EmbroideryTableMenu>> EMBROIDERY_TABLE_MENU = register("embroidery_table",
        () -> new MenuType<>(EmbroideryTableMenu::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(String id, Supplier<MenuType<T>> blockEntityType) {
        if (ENTRIES.containsKey(id))
            throw new IllegalStateException("Cannot register id '" + id + "' more than once");
        ObjectHolder<MenuType<T>> holder = new ObjectHolder<>(blockEntityType);
        ENTRIES.put(id, holder);
        return holder;
    }

    public static void registerAll(BiConsumer<ResourceLocation, MenuType<?>> cons) {
        for (Map.Entry<String, Supplier<? extends MenuType<?>>> entry : ENTRIES.entrySet())
            cons.accept(BetsyRoss.path(entry.getKey()), entry.getValue().get());
    }


}
