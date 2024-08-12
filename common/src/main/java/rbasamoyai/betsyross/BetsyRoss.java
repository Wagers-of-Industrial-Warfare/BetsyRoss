package rbasamoyai.betsyross;

import net.minecraft.resources.ResourceLocation;
import rbasamoyai.betsyross.foundation.BetsyRossUtils;

public class BetsyRoss {

	public static final String MOD_ID = "betsyross";

    public static final ResourceLocation DEFAULT_FLAG = path("paintings/default_flag.png");

    public static void init() {
	}

    public static ResourceLocation path(String path) { return BetsyRossUtils.location(MOD_ID, path); }

    public static String key(String prefix, String suffix) { return prefix + "." + MOD_ID + "." + suffix; }

}
