package rbasamoyai.betsyross.mixin.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.conczin.immersive_paintings.ClientPaintingManager;
import net.conczin.immersive_paintings.Painting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.betsyross.flags.AbstractFlagScreen;

@Mixin(ClientPaintingManager.class)
public class ClientPaintingManagerMixin {

    @WrapOperation(method = "registerImageType", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ExecutorService;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"), remap = false)
    private static Future<?> betsyross$registerImageType(ExecutorService instance, Runnable runnable, Operation<Future<?>> original,
                                                         @Local(argsOnly = true) ResourceLocation identifier,
                                                         @Local(argsOnly = true, ordinal = 0) Painting.Size size) {
        Runnable wrapped = () -> {
            runnable.run();
            if (size == Painting.Size.THUMBNAIL && Minecraft.getInstance().screen instanceof AbstractFlagScreen screen)
                screen.updateWidget(identifier);
        };
        return instance.submit(wrapped);
    }

}
