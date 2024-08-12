package rbasamoyai.betsyross.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import rbasamoyai.betsyross.BetsyRoss;

public class CfgServer {

	public final IntValue flagBlockMaxWidth;
	public final IntValue flagBlockMaxHeight;

	public final IntValue armorBannerMaxWidth;
	public final IntValue armorBannerMaxHeight;

	public final IntValue flagStandardMaxWidth;
	public final IntValue flagStandardMaxHeight;

	public final IntValue bannerStandardMaxWidth;
	public final IntValue bannerStandardMaxHeight;


	public CfgServer(ForgeConfigSpec.Builder builder) {
		this.flagBlockMaxWidth = builder.comment("Maximum width allowed when crafting a flag block. Set to 0 to disable the maximum limit.")
				.translation(BetsyRoss.key("config", "flagBlockMaxWidth"))
				.defineInRange("flagBlockMaxWidth", 0, 0, Byte.MAX_VALUE);
		this.flagBlockMaxHeight = builder.comment("Maximum height allowed when crafting a flag block. Set to 0 to disable the maximum limit.")
				.translation(BetsyRoss.key("config", "flagBlockMaxHeight"))
				.defineInRange("flagBlockMaxHeight", 0, 0, Byte.MAX_VALUE);

		this.armorBannerMaxWidth = builder.comment("Maximum width allowed when crafting an armor banner. Set to 0 to disable the maximum limit.")
				.translation(BetsyRoss.key("config", "armorBannerMaxWidth"))
				.defineInRange("armorBannerMaxWidth", 1, 0, Byte.MAX_VALUE);
		this.armorBannerMaxHeight = builder.comment("Maximum height allowed when crafting an armor banner. Set to 0 to disable the maximum limit.")
				.translation(BetsyRoss.key("config", "armorBannerMaxHeight"))
				.defineInRange("armorBannerMaxHeight", 2, 0, Byte.MAX_VALUE);

		this.flagStandardMaxWidth = builder.comment("Maximum width allowed when crafting a flag standard. Set to 0 to disable the maximum limit.")
				.translation(BetsyRoss.key("config", "flagStandardMaxWidth"))
				.defineInRange("flagStandardMaxWidth", 4, 0, Byte.MAX_VALUE);
		this.flagStandardMaxHeight = builder.comment("Maximum height allowed when crafting a flag standard. Set to 0 to disable the maximum limit.")
				.translation(BetsyRoss.key("config", "flagStandardMaxHeight"))
				.defineInRange("flagStandardMaxHeight", 2, 0, Byte.MAX_VALUE);

		this.bannerStandardMaxWidth = builder.comment("Maximum width allowed when crafting a banner standard. Set to 0 to disable the maximum limit.")
				.translation(BetsyRoss.key("config", "bannerStandardMaxWidth"))
				.defineInRange("bannerStandardMaxWidth", 2, 0, Byte.MAX_VALUE);
		this.bannerStandardMaxHeight = builder.comment("Maximum height allowed when crafting a banner standard. Set to 0 to disable the maximum limit.")
				.translation(BetsyRoss.key("config", "bannerStandardMaxHeight"))
				.defineInRange("bannerStandardMaxHeight", 3, 0, Byte.MAX_VALUE);
	}

}
