package fr.rmannibucau.loader.spi;

import fr.rmannibucau.loader.spi.graph.Diagram;

import java.io.File;
import java.util.List;

/**
 * @author Romain Manni-Bucau
 */
public interface Loader {
    List<Diagram> load(String input, FileType type);
    String key();
}
