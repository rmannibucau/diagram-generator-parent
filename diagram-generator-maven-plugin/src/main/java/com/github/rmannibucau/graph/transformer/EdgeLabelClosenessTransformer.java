package com.github.rmannibucau.graph.transformer;

import com.github.rmannibucau.loader.spi.graph.Edge;
import com.github.rmannibucau.loader.spi.graph.Node;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import org.apache.commons.collections15.Transformer;

/**
 * @author Romain Manni-Bucau
 */
public class EdgeLabelClosenessTransformer implements Transformer<Context<Graph<Node, Edge>, Edge>, Number> {
    @Override public Number transform(Context<Graph<Node, Edge>, Edge> context) {
        return 0.5;
    }
}
