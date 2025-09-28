package rbasamoyai.betsyross.platform.client;

import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface IClientIndexPlatform {

    void registerItemProperty(Item item, ResourceLocation location, ItemPropertyFunction func);

}
