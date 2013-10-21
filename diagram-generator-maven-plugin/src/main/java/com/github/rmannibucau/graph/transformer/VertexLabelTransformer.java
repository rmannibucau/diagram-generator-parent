package com.github.rmannibucau.graph.transformer;

import com.github.rmannibucau.loader.spi.graph.Node;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * @author Romain Manni-Bucau
 */
public class VertexLabelTransformer extends ToStringLabeller<Node> {
    public String transform(Node node) {
        if (node.getIcon() != null) {
            return null;
        }
        return node.getText();
    }
}
