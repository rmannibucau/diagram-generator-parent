package fr.rmannibucau.graph.transformer;

import fr.rmannibucau.loader.spi.graph.Node;
import org.apache.commons.collections15.Transformer;

import javax.swing.Icon;

/**
 * @author Romain Manni-Bucau
 */
public class VertexIconTransformer implements Transformer<Node, Icon> {
    @Override public Icon transform(Node v) {
        if (v.getIcon() != null) {
            return v.getIcon();
        }
        return null;
    }
}
