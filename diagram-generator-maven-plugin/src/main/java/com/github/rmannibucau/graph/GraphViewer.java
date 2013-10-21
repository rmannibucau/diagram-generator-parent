package com.github.rmannibucau.graph;

import com.github.rmannibucau.graph.renderer.EdgeLabelRenderer;
import com.github.rmannibucau.graph.transformer.EdgeLabelClosenessTransformer;
import com.github.rmannibucau.graph.transformer.EdgeLabelTransformer;
import com.github.rmannibucau.graph.transformer.VertexFillPaintTransformer;
import com.github.rmannibucau.graph.transformer.VertexIconTransformer;
import com.github.rmannibucau.graph.transformer.VertexLabelTransformer;
import com.github.rmannibucau.graph.transformer.VertexShapeTransformer;
import com.github.rmannibucau.loader.spi.graph.Edge;
import com.github.rmannibucau.loader.spi.graph.Node;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import java.awt.Color;

/**
 * @author Romain Manni-Bucau
 */
public class GraphViewer extends VisualizationViewer<Node, Edge> {
    public GraphViewer(Layout<Node, Edge> nodeEdgeLayout) {
        super(nodeEdgeLayout);
        init();
    }

    private void init() {
        setOpaque(true);
        setBackground(new Color(255, 255, 255, 0));

        getRenderContext().setVertexFillPaintTransformer(new VertexFillPaintTransformer());
        getRenderContext().setVertexShapeTransformer(new VertexShapeTransformer(getFontMetrics(getFont())));
        getRenderContext().setVertexIconTransformer(new VertexIconTransformer());
        getRenderContext().setVertexLabelTransformer(new VertexLabelTransformer());
        getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        getRenderContext().setEdgeLabelTransformer(new EdgeLabelTransformer());
        getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Node, Edge>());
        getRenderContext().setEdgeLabelClosenessTransformer(new EdgeLabelClosenessTransformer());
        getRenderContext().getEdgeLabelRenderer().setRotateEdgeLabels(false);
        getRenderer().setEdgeLabelRenderer(new EdgeLabelRenderer());
    }
}
