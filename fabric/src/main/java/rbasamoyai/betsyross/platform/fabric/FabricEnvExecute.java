package rbasamoyai.betsyross.platform.fabric;

import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import rbasamoyai.betsyross.platform.IEnvExecute;

public class FabricEnvExecute implements IEnvExecute {
    @Override
    public void executeOnClient(Supplier<Runnable> sup) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            sup.get().run();
    }
}
