package rbasamoyai.betsyross.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import rbasamoyai.betsyross.network.BetsyRossNetwork;
import rbasamoyai.betsyross.network.CommonPacket;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin implements ClientGamePacketListener {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void betsyross$handleCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        CommonPacket cpacket = BetsyRossNetwork.constructCommonPacket(packet.getIdentifier(), packet.getData());
        if (cpacket == null)
            return;
        cpacket.handle(Minecraft.getInstance(), this, null);
        if (ci.isCancellable())
            ci.cancel();
    }

}
