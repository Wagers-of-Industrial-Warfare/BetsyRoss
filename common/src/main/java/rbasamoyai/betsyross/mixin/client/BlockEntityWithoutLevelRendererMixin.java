package rbasamoyai.betsyross.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.betsyross.BetsyRossClient;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void betsyross$renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                                        MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci) {
        if (BetsyRossClient.renderCustomItem((BlockEntityWithoutLevelRenderer) (Object) this, stack, displayContext,
            poseStack, buffer, packedLight, packedOverlay) && ci.isCancellable())
            ci.cancel();
    }

}
