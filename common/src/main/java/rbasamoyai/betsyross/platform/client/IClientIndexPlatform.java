package rbasamoyai.betsyross.platform.client;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface IClientIndexPlatform {

    void registerItemProperty(Item item, ResourceLocation location, ItemPropertyFunction func);

    BakedModel getFlagItemModel(ItemModelShaper shaper, ResourceLocation modelLoc);

}
