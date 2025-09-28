package rbasamoyai.betsyross.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import rbasamoyai.betsyross.remix.FrustumCache;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 3))
    private void betsyross$renderLevel(ProfilerFiller instance, String s, Operation<Void> original, @Local Frustum frustum) {
        original.call(instance, s);
        FrustumCache.cacheFrustum(frustum);
    }

}
