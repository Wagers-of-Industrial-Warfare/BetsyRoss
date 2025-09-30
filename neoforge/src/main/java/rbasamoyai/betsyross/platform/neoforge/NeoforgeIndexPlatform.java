package rbasamoyai.betsyross.platform.neoforge;

import net.minecraft.world.item.CreativeModeTab;
import rbasamoyai.betsyross.platform.IIndexPlatform;

public class NeoforgeIndexPlatform implements IIndexPlatform {

    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return CreativeModeTab.builder();
    }

}
