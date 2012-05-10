package fr.rmannibucau.loader.test;

import fr.rmannibucau.loader.spi.FileType;
import fr.rmannibucau.loader.spi.Loader;
import fr.rmannibucau.loader.spi.graph.Diagram;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Loader used by tests.
 *
 * @author Romain Manni-Bucau
 */
public class LoaderImpl implements Loader {
    @Override public List<Diagram> load(String input, FileType type) {
        return Arrays.asList(new Diagram());
    }

    @Override public String key() {
        return "test";
    }
}
