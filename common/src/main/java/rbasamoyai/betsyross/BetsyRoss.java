package rbasamoyai.betsyross;

import net.minecraft.resources.ResourceLocation;

public class BetsyRoss {

	public static final String MOD_ID = "betsyross";

    public static void init() {
	}

    public static ResourceLocation path(String path) { return new ResourceLocation(MOD_ID, path); }

    public static String key(String prefix, String suffix) { return prefix + "." + MOD_ID + "." + suffix; }

}
