package rbasamoyai.betsyross.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;
import rbasamoyai.betsyross.compat.sable.SableCompatClient;
import rbasamoyai.betsyross.fabric.BetsyRossModsFabric;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntityRenderer;
import rbasamoyai.betsyross.remix.CulledRenderBBBlockEntity;
import rbasamoyai.betsyross.remix.FrustumCache;

@Mixin(FlagBlockEntityRenderer.class)
public abstract class FlagBlockEntityRendererMixin implements BlockEntityRenderer<FlagBlockEntity> {

    @WrapMethod(method = "render(Lrbasamoyai/betsyross/flags/flag_block/FlagBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V")
    private void render(FlagBlockEntity flag, float partialTicks, PoseStack stack, MultiBufferSource buffers, int packedLight,
                        int packedOverlay, Operation<Void> original) {
        if (flag instanceof CulledRenderBBBlockEntity culled) {
            final AABB renderBox = culled.getRenderBox();
            final AABB renderBox1 = BetsyRossModsFabric.SABLE.runIfInstalled(() -> () -> SableCompatClient.transformRenderBB(renderBox)).orElse(renderBox);
            if (!FrustumCache.isVisible(renderBox1))
                return;
        }
        original.call(flag, partialTicks, stack, buffers, packedLight, packedOverlay);
    }

}
