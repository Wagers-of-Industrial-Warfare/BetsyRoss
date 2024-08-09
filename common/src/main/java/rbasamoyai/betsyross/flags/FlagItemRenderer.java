package rbasamoyai.betsyross.flags;

import static rbasamoyai.betsyross.flags.FlagBlockEntityRenderer.renderFullTexture;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.betsyross.content.BetsyRossBlocks;

public class FlagItemRenderer extends BlockEntityWithoutLevelRenderer {

	public FlagItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models) {
		super(dispatcher, models);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transform, PoseStack posestack, MultiBufferSource buffers, int light, int overlay) {
		CompoundTag flagData = stack.getOrCreateTag().getCompound("BlockEntityTag");
		BlockState state = BetsyRossBlocks.FLAG_BLOCK.get().defaultBlockState();
		String url = flagData.getString("FlagUrl");

		posestack.pushPose();

		float dir = transform == ItemDisplayContext.GUI ? 90 : 0;

		posestack.translate(0, 1, 0.5);

		if (transform != ItemDisplayContext.GUI) {
			posestack.translate(0.5, 0, 0);
		}

		// TODO: hand transform

		renderFullTexture(state, url, 1, 1, 1, dir, posestack, buffers, light, overlay, false, FlagAnimationDetail.NO_WAVE, true);
		renderFullTexture(state, url, 1, 1, 1, dir, posestack, buffers, light, overlay, true, FlagAnimationDetail.NO_WAVE, true);

		posestack.popPose();
	}

}
