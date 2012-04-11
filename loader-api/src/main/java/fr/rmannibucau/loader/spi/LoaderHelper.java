package fr.rmannibucau.loader.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Romain Manni-Bucau
 */
public final class LoaderHelper {
    private LoaderHelper() {
        // no-op
    }

    public static Loader getLoader(String key) {
        ServiceLoader<Loader> serviceLoader = ServiceLoader.load(Loader.class);
        Iterator<Loader> it = serviceLoader.iterator();
        while (it.hasNext()) {
            Loader loader = it.next();
            if (key.equals(loader.key())) {
                return loader;
            }
        }
        throw new DiagramGeneratorRuntimeException("no loader found for key " + key, null);
    }
}
