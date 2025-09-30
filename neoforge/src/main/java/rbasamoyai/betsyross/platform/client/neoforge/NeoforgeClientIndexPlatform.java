package rbasamoyai.betsyross.platform.client.neoforge;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import rbasamoyai.betsyross.platform.client.IClientIndexPlatform;

public class NeoforgeClientIndexPlatform implements IClientIndexPlatform {

    public void registerItemProperty(Item item, ResourceLocation location, ItemPropertyFunction func) {
        ItemProperties.register(item, location, func::call);
    }

    @Override
    public BakedModel getFlagItemModel(ItemModelShaper shaper, ResourceLocation modelLoc) {
        return shaper.getModelManager().getModel(ModelResourceLocation.standalone(modelLoc));
    }

}
