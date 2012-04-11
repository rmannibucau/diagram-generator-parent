package fr.rmannibucau.graph.transformer;

import fr.rmannibucau.loader.spi.graph.Node;
import org.apache.commons.collections15.Transformer;

import javax.swing.Icon;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * @author Romain Manni-Bucau
 */
public class VertexShapeTransformer implements Transformer<Node, Shape> {
    private static final int X_MARGIN = 4;
    private static final int Y_MARGIN = 2;

    private FontMetrics metrics;

    public VertexShapeTransformer(FontMetrics f) {
        metrics = f;
    }

    @Override public Shape transform(Node i) {
        int w;
        int h;
        Icon icon = i.getIcon();
        if (icon == null) {
            w = metrics.stringWidth(i.getText());
            h = metrics.getHeight();
        } else {
            w = icon.getIconWidth();
            h = icon.getIconHeight();
        }

        h += Y_MARGIN;
        w += X_MARGIN;

        // centering
        AffineTransform transform = AffineTransform.getTranslateInstance(-w / 2.0, -h / 2.0);
        return transform.createTransformedShape(new Rectangle(0, 0, w, h));
    }
}
