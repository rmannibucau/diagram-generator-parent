package com.github.rmannibucau.loader.test;

import com.github.rmannibucau.loader.spi.FileType;
import com.github.rmannibucau.loader.spi.Loader;
import com.github.rmannibucau.loader.spi.graph.Diagram;

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
