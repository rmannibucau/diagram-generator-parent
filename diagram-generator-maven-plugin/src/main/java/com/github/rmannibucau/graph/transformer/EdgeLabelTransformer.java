package com.github.rmannibucau.graph.transformer;

import com.github.rmannibucau.loader.spi.graph.Edge;
import org.apache.commons.collections15.Transformer;

/**
 * @author Romain Manni-Bucau
 */
public class EdgeLabelTransformer implements Transformer<Edge, String> {
    @Override public String transform(Edge i) {
        return i.getText();
    }
}
