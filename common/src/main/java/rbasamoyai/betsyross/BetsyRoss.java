package rbasamoyai.betsyross;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import rbasamoyai.betsyross.config.BetsyRossConfig;
import rbasamoyai.betsyross.foundation.BetsyRossUtils;

public class BetsyRoss {

	public static final String MOD_ID = "betsyross";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation DEFAULT_FLAG = path("paintings/default_flag.png");

    public static void init() {
        BetsyRossConfig.init();
	}

    public static ResourceLocation path(String path) { return BetsyRossUtils.location(MOD_ID, path); }

    public static String key(String prefix, String suffix) { return prefix + "." + MOD_ID + "." + suffix; }

}
