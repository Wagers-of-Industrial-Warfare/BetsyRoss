package rbasamoyai.betsyross.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import immersive_paintings.network.ClientNetworkManager;
import immersive_paintings.network.s2c.PaintingListMessage;
import immersive_paintings.network.s2c.RegisterPaintingResponse;
import net.minecraft.client.Minecraft;
import rbasamoyai.betsyross.flags.BetsyRossFlagScreen;

@Mixin(ClientNetworkManager.class)
public class ClientNetworkManagerMixin {

    @Inject(method = "handlePaintingListResponse", at = @At("TAIL"), cancellable = true, remap = false)
    private void betsyross$handlePaintingListResponse(PaintingListMessage response, CallbackInfo ci) {
        if (!(Minecraft.getInstance().screen instanceof BetsyRossFlagScreen flagScreen))
            return;
        flagScreen.refreshPage();
        if (ci.isCancellable())
            ci.cancel();
    }

    @Inject(method = "handleRegisterPaintingResponse", at = @At("TAIL"), cancellable = true, remap = false)
    private void betsyross$handleRegisterPaintingResponse(RegisterPaintingResponse response, CallbackInfo ci) {
        if (!(Minecraft.getInstance().screen instanceof BetsyRossFlagScreen flagScreen))
            return;
        flagScreen.onReceivePaintingResponse(response);
        if (ci.isCancellable())
            ci.cancel();
    }

}
