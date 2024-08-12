package rbasamoyai.betsyross.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.betsyross.remix.ItemModelRemix;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModelShaper itemModelShaper;

    @Inject(method = "render", at = @At("HEAD"))
    private void betsyross$render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand,
                                  PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay,
                                  BakedModel model, CallbackInfo ci,
                                  @Local(argsOnly = true) LocalRef<BakedModel> modelRef) {
        ItemModelRemix.renderRemix(this.itemModelShaper, displayContext, itemStack, modelRef);
    }

    @WrapOperation(method = "getModel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;"))
    private BakedModel betsyross$getModel(ItemModelShaper instance, ItemStack itemStack, Operation<BakedModel> original) {
        return ItemModelRemix.getModelRemix(instance, itemStack, original);
    }

}
