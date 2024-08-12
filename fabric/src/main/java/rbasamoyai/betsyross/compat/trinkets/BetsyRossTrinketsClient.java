package rbasamoyai.betsyross.compat.trinkets;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import rbasamoyai.betsyross.content.BetsyRossItems;

public class BetsyRossTrinketsClient {

	public static void initClient() {
        TrinketRendererRegistry.registerRenderer(BetsyRossItems.ARMOR_BANNER.get(), new ArmorBannerTrinketRenderer());
	}

}
