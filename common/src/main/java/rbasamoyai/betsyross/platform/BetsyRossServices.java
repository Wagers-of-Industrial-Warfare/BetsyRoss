package rbasamoyai.betsyross.platform;

import java.util.ServiceLoader;

import rbasamoyai.betsyross.BetsyRoss;

public class BetsyRossServices {

    public static final IIndexPlatform INDEX_PLATFORM = load(IIndexPlatform.class);

    /**
     * Copied from Immersive Paintings {@link net.conczin.immersive_paintings.platform.Services#load(Class)} with Betsy Ross logger
     * Needs definition in META-INF/services to work
     */
    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        BetsyRoss.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

    private BetsyRossServices() {}

}
