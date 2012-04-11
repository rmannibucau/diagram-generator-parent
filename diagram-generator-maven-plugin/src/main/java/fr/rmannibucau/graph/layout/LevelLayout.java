package fr.rmannibucau.graph.layout;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import fr.rmannibucau.graph.layout.comparator.NodeComparator;
import fr.rmannibucau.loader.spi.graph.Diagram;
import fr.rmannibucau.loader.spi.graph.Edge;
import fr.rmannibucau.loader.spi.graph.Node;

import java.awt.Dimension;

import org.apache.commons.collections15.Transformer;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Romain Manni-Bucau
 */
public class LevelLayout extends AbstractLayout<Node, Edge> {
    private static final int X_MARGIN = 4;

    private Transformer<Node, Shape> vertexShapeTransformer;
    private boolean adjust;

    public LevelLayout(Diagram nodeEdgeGraph) {
        super(nodeEdgeGraph);
    }

    @Override public void initialize() {
        Map<Node, Integer> level = levels();
        List<List<Node>> nodes = sortNodeByLevel(level);
        int ySpace = maxHeight(nodes);
        int nLevels = nodes.size();
        int yLevel = Math.max(0, getSize().height - nLevels * ySpace) / Math.max(1, nLevels - 1);

        int y = ySpace / 2;
        int maxWidth = getSize().width;
        for (List<Node> currentNodes : nodes) {
            if (currentNodes.size() == 1) { // only 1 => centering manually
                setLocation(currentNodes.iterator().next(), new Point(getSize().width / 2, y));
            } else {
                int x = 0;
                int xLevel = Math.max(0, getSize().width - width(currentNodes) - X_MARGIN) / (currentNodes.size() - 1);
                Collections.sort(currentNodes, new NodeComparator((Diagram) graph, locations));

                for (Node node : currentNodes) {
                    Rectangle b = getBound(node, vertexShapeTransformer);
                    int step = b.getBounds().width / 2;
                    x += step;
                    setLocation(node, new Point(x, y));
                    x += xLevel + step;
                }

                maxWidth = Math.max(maxWidth, x - xLevel);
            }
            y += yLevel + ySpace;
        }

        if (adjust) {
            setIgnoreSize(false);
            setSize(new Dimension(maxWidth, y + ySpace));
            initialize();
            setIgnoreSize(true);
        }
    }

    @Override public void reset() {
        initialize();
    }

    private int width(List<Node> nodes) {
        int sum = 0;
        for (Node node : nodes) {
            sum += getBound(node, vertexShapeTransformer).width;
        }
        return sum;
    }

    private int maxHeight(List<List<Node>> nodes) {
        int max = 0;
        for (List<Node> list : nodes) {
            for (Node n : list) {
                max = Math.max(max, getBound(n, vertexShapeTransformer).height);
            }
        }
        return max;
    }

    private Rectangle getBound(Node n, Transformer<Node, Shape> vst) {
        if (vst == null) {
            return new Rectangle(0, 0);
        }
        return vst.transform(n).getBounds();
    }

    private List<List<Node>> sortNodeByLevel(Map<Node, Integer> level) {
        int levels = max(level);

        List<List<Node>> sorted = new ArrayList<List<Node>>();
        for (int i = 0; i < levels; i++) {
            sorted.add(new ArrayList<Node>());
        }

        for (Map.Entry<Node, Integer> entry : level.entrySet()) {
            sorted.get(entry.getValue()).add(entry.getKey());
        }
        return sorted;
    }

    private int max(Map<Node, Integer> level) {
        int i = 0;
        for (Map.Entry<Node, Integer> l : level.entrySet()) {
            if (l.getValue() >= i) {
                i = l.getValue() + 1;
            }
        }
        return i;
    }

    private Map<Node, Integer> levels() {
        Map<Node, Integer> out = new HashMap<Node, Integer>();
        for (Node node : graph.getVertices()) { // init
            out.put(node, 0);
        }

        Map<Node, Collection<Node>> successors = new HashMap<Node, Collection<Node>>();
        Map<Node, Collection<Node>> predecessors = new HashMap<Node, Collection<Node>>();
        for (Node node : graph.getVertices()) {
            successors.put(node, graph.getSuccessors(node));
            predecessors.put(node, graph.getPredecessors(node));
        }

        boolean done;
        do {
            done = true;
            for (Node node : graph.getVertices()) {
                int nodeLevel = out.get(node);
                for (Node successor : successors.get(node)) {
                    if (out.get(successor) <= nodeLevel
                            && successor != node
                            && !predecessors.get(node).contains(successor)) {
                        done = false;
                        out.put(successor, nodeLevel + 1);
                    }
                }
            }
        } while (!done);

        int min = Collections.min(out.values());
        for (Map.Entry<Node, Integer> entry : out.entrySet()) {
            out.put(entry.getKey(), entry.getValue() - min);
        }

        return out;
    }

    public void setVertexShapeTransformer(Transformer<Node, Shape> vertexShapeTransformer) {
        this.vertexShapeTransformer = vertexShapeTransformer;
    }

    public void setIgnoreSize(boolean adjust) {
        this.adjust = adjust;
    }
}
