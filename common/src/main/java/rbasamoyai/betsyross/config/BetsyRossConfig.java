package rbasamoyai.betsyross.config;

import java.util.function.Supplier;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import rbasamoyai.betsyross.BetsyRoss;

public class BetsyRossConfig {

    public static final String CONFIG_ID = "config." + BetsyRoss.MOD_ID + ".";

    public static final CfgClient CLIENT = register(CfgClient::new, RegisterType.CLIENT);
    public static final CfgServer SERVER = register(CfgServer::new, RegisterType.SERVER);

    public static void init() {}

    private static <T extends Config> T register(Supplier<T> sup, RegisterType type) {
        return ConfigApiJava.registerAndLoadConfig(sup, type);
    }

}
