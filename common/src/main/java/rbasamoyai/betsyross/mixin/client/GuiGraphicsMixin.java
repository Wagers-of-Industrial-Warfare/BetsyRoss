package rbasamoyai.betsyross.mixin.client;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rbasamoyai.betsyross.remix.ItemModelRemix;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @WrapOperation(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;usesBlockLight()Z"))
    private boolean betsyross$renderItem(BakedModel instance, Operation<Boolean> original, @Nullable LivingEntity entity,
                                         @Nullable Level level, ItemStack itemStack) {
        return !ItemModelRemix.isSpecialItem(itemStack) && original.call(instance);
    }

}
