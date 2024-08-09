package rbasamoyai.betsyross.fabric;

import net.fabricmc.api.ModInitializer;
import rbasamoyai.betsyross.BetsyRoss;

public class BetsyRossFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BetsyRoss.init();
    }

}