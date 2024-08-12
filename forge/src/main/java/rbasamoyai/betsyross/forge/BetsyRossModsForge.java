package rbasamoyai.betsyross.forge;

import java.util.Locale;
import java.util.function.Supplier;

import net.minecraftforge.fml.ModList;

public enum BetsyRossModsForge {
	CURIOS;

	private final String id = this.name().toLowerCase(Locale.ROOT);

	public boolean isLoaded() { return ModList.get().isLoaded(this.id); }

	public void executeIfLoaded(Supplier<Runnable> toExecute) {
		if (this.isLoaded())
            toExecute.get().run();
	}

}
