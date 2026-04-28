package rbasamoyai.betsyross.fabric;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import net.fabricmc.loader.api.FabricLoader;

public enum BetsyRossModsFabric {
    SABLE,
	TRINKETS;

	private final String id = this.name().toLowerCase(Locale.ROOT);

	public boolean isLoaded() { return FabricLoader.getInstance().isModLoaded(this.id); }

	public void executeIfLoaded(Supplier<Runnable> toExecute) {
		if (this.isLoaded())
            toExecute.get().run();
	}

    public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
        return this.isLoaded() ? Optional.of(toRun.get().get()) : Optional.empty();
    }

}
