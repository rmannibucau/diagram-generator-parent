package fr.rmannibucau.loader.spi;

import fr.rmannibucau.loader.spi.graph.Diagram;

import java.io.File;

/**
 * @author Romain Manni-Bucau
 */
public interface Loader {
    Diagram load(String input, FileType type);
    String key();
}
