package rbasamoyai.betsyross.flags.flag_block;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import immersive_paintings.Config;
import immersive_paintings.resources.ClientPaintingManager;
import immersive_paintings.resources.Painting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.betsyross.BetsyRossClient;
import rbasamoyai.betsyross.config.BetsyRossConfig;
import rbasamoyai.betsyross.content.BetsyRossBlocks;
import rbasamoyai.betsyross.remix.CulledRenderBBBlockEntity;
import rbasamoyai.betsyross.remix.FrustumCache;

public class FlagBlockEntityRenderer implements BlockEntityRenderer<FlagBlockEntity> {

	private final BlockRenderDispatcher brd;

	public FlagBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		this.brd = ctx.getBlockRenderDispatcher();
	}

	@Override
	public void render(FlagBlockEntity flag, float partialTicks, PoseStack stack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (flag instanceof CulledRenderBBBlockEntity culled && !FrustumCache.isVisible(culled.getRenderBox()))
            return;

		ResourceLocation flagId = flag.getFlagId();
        BetsyRossClient.FlagRenderInfo renderInfo = BetsyRossClient.getFlagRenderInfo(flagId);
        int w = renderInfo.width();
        int h = renderInfo.height();

		BlockState state = flag.getBlockState();

        Minecraft mc = Minecraft.getInstance();
        double distance = mc.player == null ? 0 : Math.sqrt(mc.player.distanceToSqr(Vec3.atCenterOf(flag.getBlockPos())));

		stack.pushPose();

		if (state.is(BetsyRossBlocks.FLAG_BLOCK.get())) {
			float dir = RotationSegment.convertToDegrees(state.getValue(FlagBlock.ROTATION));
			FlagAnimationDetail detail = BetsyRossConfig.CLIENT.animationDetail.get();

			stack.pushPose();
			stack.translate(0.5, h, 0.5);
			renderFullTexture(renderInfo.location(), distance, w, h, partialTicks, dir, stack, buffers, packedLight, packedOverlay, false, detail, false);
			renderFullTexture(renderInfo.location(), distance, w, h, partialTicks, dir, stack, buffers, packedLight, packedOverlay, true, detail, false);
			stack.popPose();

			BlockState flagpole = flag.getFlagPole();
			if (flagpole != null)
				this.brd.renderSingleBlock(flagpole, stack, buffers, packedLight, packedOverlay);
		} else if (state.is(BetsyRossBlocks.DRAPED_FLAG_BLOCK.get())) {
			Direction dir = state.getValue(DrapedFlagBlock.FACING);
			Direction dir1 = dir.getAxis() == Direction.Axis.X ? dir.getCounterClockWise() : dir.getClockWise();
			float f = dir1.toYRot();
			stack.translate(0, 1, 0);

			if (dir == Direction.WEST || dir == Direction.NORTH) stack.translate(1, 0, 0);
			if (dir == Direction.NORTH || dir == Direction.EAST) stack.translate(0, 0, 1);

			renderFullTexture(renderInfo.location(), distance, w, h, partialTicks, f, stack, buffers, packedLight,
                packedOverlay, false, FlagAnimationDetail.NO_WAVE, false);
			renderFullTexture(renderInfo.location(), distance, w, h, partialTicks, f, stack, buffers, packedLight,
                packedOverlay, true, FlagAnimationDetail.NO_WAVE, false);
		}

		stack.popPose();
	}

	public static void renderFullTexture(ResourceLocation flagId, double distance, int width, int height, float partialTicks,
                                         float dir, PoseStack stack, MultiBufferSource buffers, int packedLight, int packedOverlay,
                                         boolean flip, FlagAnimationDetail detail, boolean isItem) {
		if (width <= 0 || height <= 0) return;
		stack.pushPose();

		Vector3f v3f = new Vector3f(0, 0, 0);
		v3f.mulTransposePosition(stack.last().pose());
		stack.translate(-v3f.x(), -v3f.y(), -v3f.z());

		stack.mulPose(Axis.YP.rotationDegrees(dir - 90));
		stack.mulPose(Axis.XP.rotationDegrees(180));

		stack.translate(v3f.x(), v3f.y(), v3f.z());

		stack.translate(0, 0, flip ? -0.01 : 0.01);

		VertexConsumer vcons = buffers.getBuffer(getFlagBuffer(flagId, distance));
		switch (detail) {
			case NO_WAVE -> renderSimple(vcons, stack, width, height, packedLight, packedOverlay, flip, isItem);
			case WAVE -> renderWaveSimple(vcons, stack, partialTicks, width, height, packedLight, packedOverlay, flip);
		}

		stack.popPose();
	}

	private static void renderSimple(VertexConsumer vcons, PoseStack stack, int w, int h, int light, int overlay, boolean flip, boolean isItem) {
		float nx = 0;
		float ny = isItem ? 1 : 0;
		float nz = isItem ? 0 : flip ? 1 : -1;

		if (!flip) {
			Vector3f v3f1 = new Vector3f(0, 0, 0);
			v3f1.mulTransposePosition(stack.last().pose());
			stack.translate(-v3f1.x(), -v3f1.y(), -v3f1.z());
			stack.mulPose(Axis.YP.rotationDegrees(180));
			stack.translate(v3f1.x() - w, v3f1.y(), v3f1.z());
		}

		Matrix4f pose = stack.last().pose();
        Matrix3f normal = isItem ? new Matrix3f() : stack.last().normal();

		vcons.vertex(pose, 0, 0, 0)
				.color(255, 255, 255, 255)
				.uv(flip ? 0 : 1, 0)
				.overlayCoords(overlay)
				.uv2(light)
				.normal(normal, nx, ny, nz)
				.endVertex();

		vcons.vertex(pose, 0, h, 0)
				.color(255, 255, 255, 255)
				.uv(flip ? 0 : 1, 1)
				.overlayCoords(overlay)
				.uv2(light)
				.normal(normal, nx, ny, nz)
				.endVertex();

		vcons.vertex(pose, w, h, 0)
				.color(255, 255, 255, 255)
				.uv(flip ? 1 : 0, 1)
				.overlayCoords(overlay)
				.uv2(light)
				.normal(normal, nx, ny, nz)
				.endVertex();

		vcons.vertex(pose, w, 0, 0)
				.color(255, 255, 255, 255)
				.uv(flip ? 1 : 0, 0)
				.overlayCoords(overlay)
				.uv2(light)
				.normal(normal, nx, ny, nz)
				.endVertex();
	}

	private static void renderWaveSimple(VertexConsumer vcons, PoseStack stack, float partialTicks, float w, float h,
                                         int light, int overlay, boolean flip) {
		Minecraft mc = Minecraft.getInstance();

		float sample = 1;
		float freq = Mth.PI / 8f;
		float coAmp = 1 / 2f / (w <= 1e-2f ? 1 : w);
		int sz = Mth.ceil(w * sample) + 1;
		float phaseOffs = mc.level == null ? 0 : (float)(mc.level.getGameTime() % 16) + partialTicks;

		float[] horizDisp = new float[sz];
		float sampleRec = 1 / sample;
		for (int i = 0; i < sz; ++i)
			horizDisp[i] = Mth.sin(freq * (i * sampleRec - phaseOffs)) * coAmp * i * sampleRec;

		Vector3f[] normals = new Vector3f[sz];
		for (int i = 0 ; i < sz; ++i) {
			if (i == 0) {
                normals[i] = new Vector3f(sampleRec, 0, i + 1 == sz ? 0 : -horizDisp[i]).normalize(); // 90 deg rotation ccw
            } else if (i + 1 == sz) {
                normals[i] = new Vector3f(sampleRec, 0, horizDisp[i]).normalize(); // 90 deg rotation cw
            } else { // https://math.stackexchange.com/a/2285989 for algorithm
				Vector3f vec1 = new Vector3f(horizDisp[i - 1], 0, -sampleRec);
				float len1 = vec1.length();
				Vector3f vec2 = new Vector3f(horizDisp[i + 1], 0, sampleRec);
				float len2 = vec2.length();
				Vector3f vec3 = vec1.mul(len2).add(vec2.mul(len1));
				if (vec3.lengthSquared() <= 1e-4d)
                    vec3 = new Vector3f(sampleRec, 0, -horizDisp[i]);
				if (!flip)
                    vec3.mul(-1);
				normals[i] = vec3.normalize();
			}
		}

		if (!flip) {
			Vector3f v3f1 = new Vector3f(0, 0, 0);
			v3f1.mulTransposePosition(stack.last().pose());
			stack.translate(-v3f1.x(), -v3f1.y(), -v3f1.z());
			stack.translate(v3f1.x() + w, v3f1.y(), v3f1.z());
		}

		Matrix4f pose = stack.last().pose();
        Matrix3f normal = stack.last().normal();

		float f = sz <= 2 ? 1 : 1f / (sz - 1);
		float ulen = w * f;
		for (int i = 0; i < sz - 1; ++i) {
            int index = flip ? i : sz - i - 1;
            int nextIndex = flip ? i + 1 : sz - i - 2;
            Vector3f n1 = normals[index];
            Vector3f n2 = normals[nextIndex];

			float u1 = flip ? 0 + i * f : 1 - i * f;
			float u2 = flip ? u1 + f : u1 - f;
			float w1 = flip ? ulen * i : ulen * -i;
			float w2 = flip ? w1 + ulen : w1 - ulen;
			float z1 = horizDisp[index];
			float z2 = horizDisp[nextIndex];

			vcons.vertex(pose, w1, 0, z1)
					.color(255, 255, 255, 255)
					.uv(u1, 0)
					.overlayCoords(overlay)
					.uv2(light)
					.normal(normal, n1.x, n1.y, n1.z)
					.endVertex();

			vcons.vertex(pose, w1, h, z1)
					.color(255, 255, 255, 255)
					.uv(u1, 1)
					.overlayCoords(overlay)
					.uv2(light)
					.normal(normal, n1.x, n1.y, n1.z)
					.endVertex();

			vcons.vertex(pose, w2, h, z2)
					.color(255, 255, 255, 255)
					.uv(u2, 1)
					.overlayCoords(overlay)
					.uv2(light)
					.normal(normal, n2.x, n2.y, n2.z)
					.endVertex();

			vcons.vertex(pose, w2, 0, z2)
					.color(255, 255, 255, 255)
					.uv(u2, 0)
					.overlayCoords(overlay)
					.uv2(light)
					.normal(normal, n2.x, n2.y, n2.z)
					.endVertex();
		}
	}

	public static RenderType getFlagBuffer(ResourceLocation flagId, double distance) {
        // Adapted from ImmersivePaintingEntityRenderer#getTexture.
        Minecraft mc = Minecraft.getInstance();
        Config config = Config.getInstance();

        double blocksVisible = Math.tan(mc.options.fov().get() / 180.0 * Math.PI / 2.0) * 2.0 * distance;
        int resolution = ClientPaintingManager.getPainting(flagId).resolution;
        double pixelDensity = blocksVisible * resolution / mc.getWindow().getHeight();

        Painting.Type type = pixelDensity > config.eighthResolutionThreshold ? Painting.Type.EIGHTH
            : pixelDensity > config.quarterResolutionThreshold ? Painting.Type.QUARTER
            : pixelDensity > config.halfResolutionThreshold ? Painting.Type.HALF
            : Painting.Type.FULL;

		return RenderType.entityTranslucentCull(ClientPaintingManager.getPaintingTexture(flagId, type).textureIdentifier);
	}

}
