package rbasamoyai.betsyross.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.betsyross.content.BetsyRossItems;

// Adapted from GogglesCurioRenderer
public class ArmorBannerTrinketRenderer implements TrinketRenderer {

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> model,
                       PoseStack matrices, MultiBufferSource multiBufferSource, int light, LivingEntity entity,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                       float headYaw, float headPitch) {
        if (!stack.is(BetsyRossItems.ARMOR_BANNER.get()) || !(model instanceof HumanoidModel humanoidModel))
            return;
        matrices.pushPose();
		// Prepare values for transformation
        TrinketRenderer.followBodyRotations(entity, humanoidModel);

		// Render
        matrices.translate(0, -0.25, 0);
        matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
        matrices.scale(0.625f, 0.625f, 0.625f);

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getItemRenderer().renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY,
			matrices, multiBufferSource, minecraft.level, 0);
		matrices.popPose();
	}

}
