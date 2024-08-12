package rbasamoyai.betsyross.fabric;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import rbasamoyai.betsyross.BetsyRossClient;

public class BetsyRossModelLoading implements ModelLoadingPlugin {

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        BetsyRossClient.registerModels(pluginContext::addModels);
    }

}
