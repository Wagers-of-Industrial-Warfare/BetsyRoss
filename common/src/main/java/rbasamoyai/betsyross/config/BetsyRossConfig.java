package rbasamoyai.betsyross.config;

import java.util.function.BiConsumer;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class BetsyRossConfig {

	public static ForgeConfigSpec CLIENT_SPEC;
	public static CfgClient CLIENT;
	static {
		Pair<CfgClient, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CfgClient::new);
		CLIENT_SPEC = pair.getRight();
		CLIENT = pair.getLeft();
	}

	public static ForgeConfigSpec SERVER_SPEC;
	public static CfgServer SERVER;
	static {
		Pair<CfgServer, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CfgServer::new);
		SERVER_SPEC = pair.getRight();
		SERVER = pair.getLeft();
	}

    public static void init(BiConsumer<ModConfig.Type, ForgeConfigSpec> cons) {
        cons.accept(ModConfig.Type.CLIENT, CLIENT_SPEC);
        cons.accept(ModConfig.Type.SERVER, SERVER_SPEC);
    }

}
