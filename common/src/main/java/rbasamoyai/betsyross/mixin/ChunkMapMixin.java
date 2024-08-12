package rbasamoyai.betsyross.mixin;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import immersive_paintings.ServerDataManager;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import rbasamoyai.betsyross.flags.flag_block.FlagBlockEntity;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @Inject(method = "playerLoadedChunk", at = @At("TAIL"))
    private void betsyross$playerLoadedChunk(ServerPlayer player, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache,
                                             LevelChunk chunk, CallbackInfo ci) {
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (!(be instanceof FlagBlockEntity))
                continue;
            ServerDataManager.playerRequestedImages(player);
            return;
        }
    }

}
