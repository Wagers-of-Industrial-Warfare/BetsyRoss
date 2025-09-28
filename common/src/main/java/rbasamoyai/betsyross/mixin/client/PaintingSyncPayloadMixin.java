package rbasamoyai.betsyross.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.conczin.immersive_paintings.network.payload.ImmersivePayload;
import net.conczin.immersive_paintings.network.payload.s2c.PaintingSyncPayload;
import net.minecraft.client.Minecraft;
import rbasamoyai.betsyross.flags.BetsyRossFlagScreen;

@Mixin(PaintingSyncPayload.class)
public class PaintingSyncPayloadMixin {

    @WrapOperation(method = "handle", at = @At(value = "INVOKE", target = "Lnet/conczin/immersive_paintings/network/payload/ImmersivePayload$Runner;run(Ljava/lang/Runnable;)V"), remap = false)
    private void betsyross$run(ImmersivePayload.Runner instance, Runnable runnable, Operation<Void> original) {
        Runnable wrapped = () -> {
            runnable.run();
            if (Minecraft.getInstance().screen instanceof BetsyRossFlagScreen flagScreen)
                flagScreen.refreshPage();
        };
        original.call(instance, wrapped);
    }

}
