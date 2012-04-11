package fr.rmannibucau.graph.transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import fr.rmannibucau.loader.spi.graph.Edge;
import fr.rmannibucau.loader.spi.graph.Node;
import org.apache.commons.collections15.Transformer;

/**
 * @author Romain Manni-Bucau
 */
public class EdgeLabelClosenessTransformer implements Transformer<Context<Graph<Node, Edge>, Edge>, Number> {
    @Override public Number transform(Context<Graph<Node, Edge>, Edge> context) {
        return 0.5;
    }
}
