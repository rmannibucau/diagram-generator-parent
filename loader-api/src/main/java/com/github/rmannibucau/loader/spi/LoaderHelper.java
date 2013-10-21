package com.github.rmannibucau.loader.spi;

import java.util.ServiceLoader;

/**
 * @author Romain Manni-Bucau
 */
public final class LoaderHelper {
    private LoaderHelper() {
        // no-op
    }

    public static Loader getLoader(final String key) {
        for (final Loader loader : ServiceLoader.load(Loader.class)) {
            if (key.equals(loader.key())) {
                return loader;
            }
        }
        throw new DiagramGeneratorRuntimeException("no loader found for key " + key, null);
    }
}
