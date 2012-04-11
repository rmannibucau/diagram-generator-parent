package fr.rmannibucau.graph.transformer;

import fr.rmannibucau.loader.spi.graph.Node;
import org.apache.commons.collections15.Transformer;

import java.awt.Color;
import java.awt.Paint;

/**
 * @author Romain Manni-Bucau
 */
public class VertexFillPaintTransformer implements Transformer<Node, Paint> {
    @Override public Paint transform(Node node) {
        return Color.WHITE;
    }
}
