package rbasamoyai.betsyross.foundation;

import net.minecraft.resources.ResourceLocation;

public class BetsyRossUtils {

    public static ResourceLocation location(String id) { return ResourceLocation.parse(id); }

    public static ResourceLocation location(String namespace, String path) { return ResourceLocation.fromNamespaceAndPath(namespace, path); }

}
