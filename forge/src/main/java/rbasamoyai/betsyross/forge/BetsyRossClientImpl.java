package rbasamoyai.betsyross.forge;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class BetsyRossClientImpl {

    public static void registerItemProperty(Item item, ResourceLocation location, ItemPropertyFunction func) {
        ItemProperties.register(item, location, func);
    }

}
