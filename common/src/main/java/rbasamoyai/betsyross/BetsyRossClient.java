package rbasamoyai.betsyross;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.architectury.injectables.annotations.ExpectPlatform;
import immersive_paintings.resources.ClientPaintingManager;
import immersive_paintings.resources.Painting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.AABB;
import rbasamoyai.betsyross.content.BetsyRossBlockEntities;
import rbasamoyai.betsyross.content.BetsyRossBlocks;
import rbasamoyai.betsyross.content.BetsyRossItems;
import rbasamoyai.betsyross.flags.flag_block.DrapedFlagBlock;
import rbasamoyai.betsyross.flags.flag_block.FlagBlock;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntityRenderer;
import rbasamoyai.betsyross.flags.standards.ArmorBannerRenderer;
import rbasamoyai.betsyross.flags.standards.BannerStandardRenderer;
import rbasamoyai.betsyross.flags.standards.FlagStandardRenderer;
import rbasamoyai.betsyross.network.BetsyRossNetwork;
import rbasamoyai.betsyross.network.CommonPacket;

public class BetsyRossClient {

    private static BlockEntityWithoutLevelRenderer FLAG_STANDARD_RENDERER;
    public static BlockEntityWithoutLevelRenderer getFlagStandardRenderer() {
        if (FLAG_STANDARD_RENDERER == null) {
            Minecraft mc = Minecraft.getInstance();
            FLAG_STANDARD_RENDERER = new FlagStandardRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
        }
        return FLAG_STANDARD_RENDERER;
    }

    private static BlockEntityWithoutLevelRenderer BANNER_STANDARD_RENDERER;
    public static BlockEntityWithoutLevelRenderer getBannerStandardRenderer() {
        if (BANNER_STANDARD_RENDERER == null) {
            Minecraft mc = Minecraft.getInstance();
            BANNER_STANDARD_RENDERER = new BannerStandardRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
        }
        return BANNER_STANDARD_RENDERER;
    }

    private static BlockEntityWithoutLevelRenderer ARMOR_BANNER_RENDERER;
    public static BlockEntityWithoutLevelRenderer getArmorBannerRenderer() {
        if (ARMOR_BANNER_RENDERER == null) {
            Minecraft mc = Minecraft.getInstance();
            ARMOR_BANNER_RENDERER = new ArmorBannerRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
        }
        return ARMOR_BANNER_RENDERER;
    }

    private static final Set<ModelLayerLocation> ALL_LAYERS = new HashSet<>();
    public static final ModelLayerLocation ITEM_FLAGPOLE = registerLayer("item_flagpole");
    public static final ModelLayerLocation ITEM_BANNER = registerLayer("item_banner");
    public static final ModelLayerLocation ARMOR_FLAGPOLE = registerLayer("armor_flagpole");

    private static ModelLayerLocation registerLayer(String id) {
        ModelLayerLocation loc = new ModelLayerLocation(BetsyRoss.path(id), "main");
        if (!ALL_LAYERS.add(loc))
            throw new IllegalStateException("Duplicate registration for " + loc);
        return loc;
    }

    public static void init(BiConsumer<Block, RenderType> layerRegistration) {
        registerItemProperty(BetsyRossItems.BANNER_STANDARD.get(), BetsyRoss.path("raised"), (stack, level, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0;
        });

        layerRegistration.accept(BetsyRossBlocks.EMBROIDERY_TABLE_BLOCK.get(), RenderType.cutout());
    }

    public static final ResourceLocation SPECIAL_ARMOR_BANNER_MODEL = BetsyRoss.path("item/special_armor_banner");
    public static final ResourceLocation SPECIAL_BANNER_STANDARD_MODEL = BetsyRoss.path("item/special_banner_standard");
    public static final ResourceLocation SPECIAL_BANNER_STANDARD_RAISED_MODEL = BetsyRoss.path("item/special_banner_standard_raised");
    public static final ResourceLocation SPECIAL_FLAG_STANDARD_MODEL = BetsyRoss.path("item/special_flag_standard");

    public static void registerModels(Consumer<ResourceLocation> cons) {
        cons.accept(SPECIAL_ARMOR_BANNER_MODEL);
        cons.accept(SPECIAL_BANNER_STANDARD_MODEL);
        cons.accept(SPECIAL_BANNER_STANDARD_RAISED_MODEL);
        cons.accept(SPECIAL_FLAG_STANDARD_MODEL);
    }

    @ExpectPlatform
    public static void registerItemProperty(Item item, ResourceLocation location, ItemPropertyFunction func) {
        throw new AssertionError();
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(BetsyRossBlockEntities.FLAG_BLOCK_ENTITY.get(), FlagBlockEntityRenderer::new);
    }

    public static void registerLayerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> cons) {
        cons.accept(ITEM_FLAGPOLE, FlagStandardRenderer::defineFlagpole);
        cons.accept(ITEM_BANNER, BannerStandardRenderer::defineBannerBar);
        cons.accept(ARMOR_FLAGPOLE, ArmorBannerRenderer::defineArmorFlagpole);
    }

    public static void sendToServer(CommonPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() == null)
            return;
        BetsyRossNetwork.sendToServer(mc.getConnection()::send, packet);
    }

    public static boolean renderCustomItem(BlockEntityWithoutLevelRenderer original, ItemStack stack, ItemDisplayContext transform,
                                           PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (stack.is(BetsyRossItems.FLAG_STANDARD.get()) && original != getFlagStandardRenderer()) {
            getFlagStandardRenderer().renderByItem(stack, transform, poseStack, buffers, light, overlay);
            return true;
        }
        if (stack.is(BetsyRossItems.BANNER_STANDARD.get()) && original != getBannerStandardRenderer()) {
            getBannerStandardRenderer().renderByItem(stack, transform, poseStack, buffers, light, overlay);
            return true;
        }
        if (stack.is(BetsyRossItems.ARMOR_BANNER.get()) && original != getArmorBannerRenderer()) {
            getArmorBannerRenderer().renderByItem(stack, transform, poseStack, buffers, light, overlay);
            return true;
        }
        return false;
    }

    public static FlagRenderInfo getFlagRenderInfo(ResourceLocation location) {
        Painting painting = ClientPaintingManager.getPaintings().get(location);
        if (painting == null) {
            painting = ClientPaintingManager.getPainting(BetsyRoss.DEFAULT_FLAG);
            location = BetsyRoss.DEFAULT_FLAG;
        }
        return new FlagRenderInfo(location, painting.width, painting.height);
    }

    public record FlagRenderInfo(ResourceLocation location, int width, int height) {
    }

    public static AABB getFlagBlockEntityBox(FlagBlockEntity flag) {
        BlockState state = flag.getBlockState();
        BlockPos pos = flag.getBlockPos();
        Painting painting = ClientPaintingManager.getPaintings().get(flag.getFlagId());
        if (painting == null)
            painting = ClientPaintingManager.getPainting(BetsyRoss.DEFAULT_FLAG);
        int flagWidth = painting.width;
        int flagHeight = painting.height;
        if (state.is(BetsyRossBlocks.FLAG_BLOCK.get())) {
            float dir = RotationSegment.convertToDegrees(state.getValue(FlagBlock.ROTATION));
            float f1 = Mth.sin(dir * Mth.DEG_TO_RAD);
            float f2 = Mth.cos(dir * Mth.DEG_TO_RAD);
            return new AABB(pos).expandTowards(f1 * flagWidth, flagHeight, f2 * flagWidth).inflate(1);
        }
        if (state.is(BetsyRossBlocks.DRAPED_FLAG_BLOCK.get())) {
            Direction dir = state.getValue(DrapedFlagBlock.FACING);
            return new AABB(pos.relative(dir.getOpposite()), pos.below(flagHeight).relative(dir.getCounterClockWise(), flagWidth)).inflate(1);
        }
        return new AABB(pos);
    }

}
