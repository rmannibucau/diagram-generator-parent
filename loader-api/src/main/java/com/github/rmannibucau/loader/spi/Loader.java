package com.github.rmannibucau.loader.spi;

import com.github.rmannibucau.loader.spi.graph.Diagram;

import java.util.List;

/**
 * @author Romain Manni-Bucau
 */
public interface Loader {
    List<Diagram> load(String input, FileType type);
    String key();
}
