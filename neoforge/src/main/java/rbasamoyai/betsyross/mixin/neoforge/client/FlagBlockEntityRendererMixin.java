package rbasamoyai.betsyross.mixin.neoforge.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntityRenderer;
import rbasamoyai.betsyross.remix.CulledRenderBBBlockEntity;

@Mixin(FlagBlockEntityRenderer.class)
public abstract class FlagBlockEntityRendererMixin implements BlockEntityRenderer<FlagBlockEntity> {

    @Override
    public AABB getRenderBoundingBox(FlagBlockEntity blockEntity) {
        return ((CulledRenderBBBlockEntity) blockEntity).getRenderBox();
    }

}
