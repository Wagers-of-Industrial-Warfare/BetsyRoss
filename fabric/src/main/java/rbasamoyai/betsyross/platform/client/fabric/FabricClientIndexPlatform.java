package rbasamoyai.betsyross.platform.client.fabric;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import rbasamoyai.betsyross.platform.client.IClientIndexPlatform;

public class FabricClientIndexPlatform implements IClientIndexPlatform {

    public void registerItemProperty(Item item, ResourceLocation location, ItemPropertyFunction func) {
        ItemProperties.register(item, location, func::call);
    }

}
