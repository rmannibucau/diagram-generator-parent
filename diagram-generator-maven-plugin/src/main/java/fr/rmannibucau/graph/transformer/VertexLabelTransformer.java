package fr.rmannibucau.graph.transformer;

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import fr.rmannibucau.loader.spi.graph.Node;

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
