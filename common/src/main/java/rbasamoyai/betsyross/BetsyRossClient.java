package rbasamoyai.betsyross;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import rbasamoyai.betsyross.content.BetsyRossBlockEntities;
import rbasamoyai.betsyross.content.BetsyRossItems;
import rbasamoyai.betsyross.content.BetsyRossMenus;
import rbasamoyai.betsyross.crafting.EmbroideryTableScreen;
import rbasamoyai.betsyross.flags.ArmorBannerRenderer;
import rbasamoyai.betsyross.flags.BannerStandardRenderer;
import rbasamoyai.betsyross.flags.FlagBlockEntityRenderer;
import rbasamoyai.betsyross.flags.FlagItemRenderer;
import rbasamoyai.betsyross.flags.FlagStandardRenderer;
import rbasamoyai.betsyross.network.BetsyRossNetwork;
import rbasamoyai.betsyross.network.CommonPacket;

public class BetsyRossClient {

    private static BlockEntityWithoutLevelRenderer FLAG_ITEM_RENDERER;
    public static BlockEntityWithoutLevelRenderer getFlagItemRenderer() {
        if (FLAG_ITEM_RENDERER == null) {
            Minecraft mc = Minecraft.getInstance();
            FLAG_ITEM_RENDERER = new FlagItemRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
        }
        return FLAG_ITEM_RENDERER;
    }

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

    public static void init() {
        MenuScreens.register(BetsyRossMenus.EMBROIDERY_TABLE_MENU.get(), EmbroideryTableScreen::new);

        registerItemProperty(BetsyRossItems.BANNER_STANDARD.get(), BetsyRoss.path("raised"), (stack, level, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0;
        });
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
        if (stack.is(BetsyRossItems.FLAG_ITEM.get()) && original != getFlagItemRenderer()) {
            getFlagItemRenderer().renderByItem(stack, transform, poseStack, buffers, light, overlay);
            return true;
        }
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

}
