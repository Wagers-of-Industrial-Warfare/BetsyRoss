package rbasamoyai.betsyross.fabric;

import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvExecuteImpl {

	public static void executeOnClient(Supplier<Runnable> sup) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            sup.get().run();
    }

}
