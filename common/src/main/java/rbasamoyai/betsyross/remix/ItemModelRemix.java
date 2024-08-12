package rbasamoyai.betsyross.remix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.betsyross.BetsyRossClient;
import rbasamoyai.betsyross.content.BetsyRossItems;

public class ItemModelRemix {

    public static void renderRemix(ItemModelShaper itemModelShaper, ItemDisplayContext displayContext, ItemStack itemStack, LocalRef<BakedModel> modelRef) {
        boolean bl = displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.GROUND || displayContext == ItemDisplayContext.FIXED;
        if (!bl)
            return;
        if (itemStack.is(BetsyRossItems.ARMOR_BANNER.get())
            || itemStack.is(BetsyRossItems.FLAG_STANDARD.get())
            || itemStack.is(BetsyRossItems.BANNER_STANDARD.get())) {
            modelRef.set(itemModelShaper.getItemModel(itemStack));
        }
    }

    public static boolean isSpecialItem(ItemStack itemStack) {
        return itemStack.is(BetsyRossItems.ARMOR_BANNER.get()) || itemStack.is(BetsyRossItems.FLAG_STANDARD.get())
            || itemStack.is(BetsyRossItems.BANNER_STANDARD.get());
    }

    public static BakedModel getModelRemix(ItemModelShaper instance, ItemStack itemStack, Operation<BakedModel> original) {
        BakedModel model;
        if (itemStack.is(BetsyRossItems.ARMOR_BANNER.get())) {
            model = instance.getModelManager().getModel(BetsyRossClient.SPECIAL_ARMOR_BANNER_MODEL);
        } else if (itemStack.is(BetsyRossItems.FLAG_STANDARD.get())) {
            model = instance.getModelManager().getModel(BetsyRossClient.SPECIAL_FLAG_STANDARD_MODEL);
        } else if (itemStack.is(BetsyRossItems.BANNER_STANDARD.get())) {
            model = instance.getModelManager().getModel(BetsyRossClient.SPECIAL_BANNER_STANDARD_MODEL);
        } else {
            model = original.call(instance, itemStack);
        }
        return model;
    }

    private ItemModelRemix() {}

}
