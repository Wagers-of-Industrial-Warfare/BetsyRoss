package rbasamoyai.betsyross.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import rbasamoyai.betsyross.BetsyRossClient;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;
import rbasamoyai.betsyross.remix.CulledRenderBBBlockEntity;

@Mixin(FlagBlockEntity.class)
public abstract class FlagBlockEntityMixin extends BlockEntity implements CulledRenderBBBlockEntity {

    @Unique private BlockState oldState = this.getBlockState();

    @Unique private AABB renderBoundingBox = null;

    FlagBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) { super(type, pos, blockState); }

    @Override
    public AABB getRenderBox() {
        if (this.renderBoundingBox == null || !this.oldState.equals(this.getBlockState())) {
            this.renderBoundingBox = BetsyRossClient.getFlagBlockEntityBox((FlagBlockEntity) (Object) this);
            this.oldState = this.getBlockState();
        }
        return this.renderBoundingBox;
    }

    @Inject(method = "setFlag", at = @At("TAIL"))
    private void betsyross$setFlag(ResourceLocation par1, CallbackInfo ci) {
        this.renderBoundingBox = null;
    }

}
