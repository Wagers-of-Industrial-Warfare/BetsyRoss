package rbasamoyai.betsyross.flags;

import static rbasamoyai.betsyross.flags.FlagBlockEntityRenderer.renderFullTexture;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.betsyross.BetsyRossClient;
import rbasamoyai.betsyross.content.BetsyRossBlocks;

public class ArmorBannerRenderer extends BlockEntityWithoutLevelRenderer {
	public static final Material STANDARD_FLAGPOLE = FlagStandardRenderer.STANDARD_FLAGPOLE;

	private final ModelPart flagpole;

	public ArmorBannerRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models) {
		super(dispatcher, models);
		this.flagpole = models.bakeLayer(BetsyRossClient.ARMOR_FLAGPOLE);
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext transform, PoseStack posestack, MultiBufferSource buffers, int light, int overlay) {
		Minecraft mc = Minecraft.getInstance();

		CompoundTag flagData = stack.getOrCreateTag();
		BlockState state = BetsyRossBlocks.FLAG_BLOCK.get().defaultBlockState();
		String url = flagData.getString("FlagUrl");

		posestack.pushPose();

		int width = flagData.getByte("Width");
		int height = flagData.getByte("Height");

		float pt = mc.getFrameTime();

		if (transform == ItemDisplayContext.GUI) {
			width = 1;
			height = 1;
			pt = 1.0f;
		} else {
			this.flagpole.render(posestack, STANDARD_FLAGPOLE.buffer(buffers, RenderType::entitySolid), light, overlay);
			posestack.translate(.5, 2, 0);
		}

		float dir = transform == ItemDisplayContext.GUI ? 90 : 0;

		posestack.translate(0, 1, 0.5);

		renderFullTexture(state, url, width, height, pt, dir, posestack, buffers, light, overlay, false, FlagAnimationDetail.NO_WAVE, true);
		renderFullTexture(state, url, width, height, pt, dir, posestack, buffers, light, overlay, true, FlagAnimationDetail.NO_WAVE, true);

		posestack.popPose();
	}

	public static LayerDefinition defineArmorFlagpole() {
		MeshDefinition mesh = new MeshDefinition();
		mesh.getRoot().addOrReplaceChild("flagpole", CubeListBuilder.create()
				.texOffs(0, 0).addBox(7, 0, 7, 2, 16, 2)
				.texOffs(0, 0).addBox(7, 16, 7, 2, 16, 2)
				.texOffs(0, 0).addBox(7, 32, 7, 2, 16, 2), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 64);
	}

}