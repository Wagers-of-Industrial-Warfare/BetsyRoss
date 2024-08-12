package rbasamoyai.betsyross.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import immersive_paintings.ServerDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;

@Mixin(ChunkHolder.class)
public class ChunkHolderMixin {

    @Inject(method = "broadcastBlockEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkHolder;broadcast(Ljava/util/List;Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER))
    private void betsyross$broadcastBlockEntity$broadcast(List<ServerPlayer> players, Level level, BlockPos pos, CallbackInfo ci,
                                                          @Local BlockEntity blockEntity) {
        if (!(blockEntity instanceof FlagBlockEntity))
            return;
        for (ServerPlayer player : players)
            ServerDataManager.playerRequestedImages(player);
    }

}
