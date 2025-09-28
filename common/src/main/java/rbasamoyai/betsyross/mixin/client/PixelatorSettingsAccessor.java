package rbasamoyai.betsyross.mixin.client;

import java.awt.image.BufferedImage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.conczin.immersive_paintings.client.gui.ImmersivePaintingScreen;

@Mixin(ImmersivePaintingScreen.PixelatorSettings.class)
public interface PixelatorSettingsAccessor {

    @Invoker("<init>")
    static ImmersivePaintingScreen.PixelatorSettings callInit(BufferedImage image, int minResolution, int maxResolution) {
        throw new AssertionError();
    }

}
