package rbasamoyai.betsyross.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import immersive_paintings.client.gui.ImmersivePaintingScreen;
import immersive_paintings.resources.ByteImage;

@Mixin(ImmersivePaintingScreen.PixelatorSettings.class)
public interface PixelatorSettingsAccessor {

    @Invoker("<init>")
    static ImmersivePaintingScreen.PixelatorSettings callInit(ByteImage byteImage) {
        throw new AssertionError();
    }

}
