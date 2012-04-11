package fr.rmannibucau.loader.test;

import fr.rmannibucau.loader.spi.FileType;
import fr.rmannibucau.loader.spi.Loader;
import fr.rmannibucau.loader.spi.graph.Diagram;
import java.io.File;

/**
 * Loader used by tests.
 *
 * @author Romain Manni-Bucau
 */
public class LoaderImpl implements Loader {
    @Override public Diagram load(String input, FileType type) {
        return new Diagram();
    }

    @Override public String key() {
        return "test";
    }
}
