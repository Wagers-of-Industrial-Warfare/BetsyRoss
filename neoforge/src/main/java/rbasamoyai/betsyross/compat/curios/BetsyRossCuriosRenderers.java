package rbasamoyai.betsyross.compat.curios;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import rbasamoyai.betsyross.content.BetsyRossItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

// Adapted from Create's CuriosRenderers --ritchie
public class BetsyRossCuriosRenderers {

	public static void register(IEventBus modBus) {
		modBus.addListener(BetsyRossCuriosRenderers::onClientSetup);
		modBus.addListener(BetsyRossCuriosRenderers::onLayerRegister);
	}

	private static void onClientSetup(FMLClientSetupEvent event) {
		CuriosRendererRegistry.register(BetsyRossItems.ARMOR_BANNER.get(),
			() -> new ArmorBannerCurioRenderer(Minecraft.getInstance().getEntityModels().bakeLayer(ArmorBannerCurioRenderer.LAYER)));
	}

	private static void onLayerRegister(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ArmorBannerCurioRenderer.LAYER, () -> LayerDefinition.create(ArmorBannerCurioRenderer.mesh(), 1, 1));
	}

}
