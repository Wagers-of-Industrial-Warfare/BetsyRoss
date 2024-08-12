package rbasamoyai.betsyross.flags.flag_block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.betsyross.BetsyRossClient;
import rbasamoyai.betsyross.config.BetsyRossConfig;
import rbasamoyai.betsyross.flags.AbstractFlagScreen;
import rbasamoyai.betsyross.network.ServerboundModifyFlagBlockPacket;

public class FlagBlockScreen extends AbstractFlagScreen {

    private final BlockPos blockPos;
    private final FlagBlockEntity flagBlockEntity;

    public FlagBlockScreen(BlockPos blockPos, FlagBlockEntity flag, int minResolution, int maxResolution, boolean showOtherPlayersPaintings, int uploadPermissionLevel) {
        super(minResolution, maxResolution, showOtherPlayersPaintings, uploadPermissionLevel);
        this.blockPos = blockPos;
        this.flagBlockEntity = flag;
    }

    protected void updateFlag(ResourceLocation loc) {
        this.flagBlockEntity.setFlag(loc);
        BetsyRossClient.sendToServer(new ServerboundModifyFlagBlockPacket(this.blockPos, loc));
    }

    @Override protected int getConfigWidth() { return BetsyRossConfig.SERVER.flagBlockMaxWidth.get(); }
    @Override protected int getConfigHeight() { return BetsyRossConfig.SERVER.flagBlockMaxHeight.get(); }

    @Override protected boolean canUpdateFlag() { return this.flagBlockEntity != null; }

}
