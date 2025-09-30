package rbasamoyai.betsyross.neoforge;

import java.util.Locale;
import java.util.function.Supplier;

import net.neoforged.fml.ModList;

public enum BetsyRossModsNeoforge {
	CURIOS;

	private final String id = this.name().toLowerCase(Locale.ROOT);

	public boolean isLoaded() { return ModList.get().isLoaded(this.id); }

	public void executeIfLoaded(Supplier<Runnable> toExecute) {
		if (this.isLoaded())
            toExecute.get().run();
	}

}
