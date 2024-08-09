package rbasamoyai.betsyross.content;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import rbasamoyai.betsyross.BetsyRoss;

public class BetsyRossStats {

    private static final Set<ResourceLocation> CUSTOM_STATS = new LinkedHashSet<>();

    public static final ResourceLocation INTERACT_WITH_EMBROIDERY_TABLE = register("interact_with_embroidery_table");

    private static ResourceLocation register(String path) {
        ResourceLocation ret = BetsyRoss.path(path);
        CUSTOM_STATS.add(ret);
        return ret;
    }

    public static void registerAll(BiConsumer<ResourceLocation, ResourceLocation> cons) {
        for (ResourceLocation stat : CUSTOM_STATS)
            cons.accept(stat, stat);
    }

    public static void activateAllStats() {
        for (ResourceLocation stat : CUSTOM_STATS)
            Stats.CUSTOM.get(stat, StatFormatter.DEFAULT);
    }

}
