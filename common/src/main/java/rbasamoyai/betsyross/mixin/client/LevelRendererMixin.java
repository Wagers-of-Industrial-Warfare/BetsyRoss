package rbasamoyai.betsyross.mixin.client;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import rbasamoyai.betsyross.remix.FrustumCache;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 3))
    private void betsyross$renderLevel(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline,
                                       Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix,
                                       CallbackInfo ci, @Local Frustum frustum) {
        FrustumCache.cacheFrustum(frustum);
    }

}
