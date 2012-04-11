package fr.rmannibucau.graph.renderer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import fr.rmannibucau.loader.spi.graph.Edge;
import fr.rmannibucau.loader.spi.graph.Node;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * simplifying the edge label renderer to have something more simple but more readable.
 *
 * @author Romain Manni-Bucau
 */
public class EdgeLabelRenderer extends BasicEdgeLabelRenderer<Node, Edge> {
    public void labelEdge(RenderContext<Node, Edge> rc, Layout<Node, Edge> layout, Edge e, String label) {
        if (label == null || label.length() == 0) {
            return;
        }

        Graph<Node, Edge> graph = layout.getGraph();
        // don't draw edge if either incident vertex is not drawn
        Pair<Node> endpoints = graph.getEndpoints(e);
        Node v1 = endpoints.getFirst();
        Node v2 = endpoints.getSecond();
        if (!rc.getEdgeIncludePredicate().evaluate(Context.<Graph<Node, Edge>, Edge>getInstance(graph, e))) {
            return;
        }

        if (!rc.getVertexIncludePredicate().evaluate(Context.<Graph<Node, Edge>, Node>getInstance(graph, v1)) ||
                !rc.getVertexIncludePredicate().evaluate(Context.<Graph<Node, Edge>, Node>getInstance(graph, v2))) {
            return;
        }

        Point2D p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, layout.transform(v1));
        Point2D p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, layout.transform(v2));

        GraphicsDecorator g = rc.getGraphicsContext();
        Component component = prepareRenderer(rc, rc.getEdgeLabelRenderer(), label, rc.getPickedEdgeState().isPicked(e), e);
        Dimension d = component.getPreferredSize();

        AffineTransform old = g.getTransform();
        AffineTransform xform = new AffineTransform(old);
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(e.getText());
        double p =  Math.max(0, p1.getX() +  p2.getX() - w);
        xform.translate(Math.min(layout.getSize().width - w, p / 2), (p1.getY() + p2.getY() - fm .getHeight()) / 2);
        g.setTransform(xform);
        g.draw(component, rc.getRendererPane(), 0, 0, d.width, d.height, true);

        g.setTransform(old);
    }
}
