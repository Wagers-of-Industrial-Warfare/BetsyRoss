package rbasamoyai.betsyross.platform.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import rbasamoyai.betsyross.platform.IIndexPlatform;

public class FabricIndexPlatform implements IIndexPlatform {

    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return FabricItemGroup.builder();
    }

}
