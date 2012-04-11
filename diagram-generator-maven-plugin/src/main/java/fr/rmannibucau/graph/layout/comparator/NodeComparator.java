package fr.rmannibucau.graph.layout.comparator;

import fr.rmannibucau.loader.spi.graph.Diagram;
import fr.rmannibucau.loader.spi.graph.Node;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * @author Romain Manni-Bucau
 */
public class NodeComparator implements Comparator<Node> { // sort by predecessor location
    private Diagram graph;
    private Map<Node, Point2D> locations;

    public NodeComparator(Diagram diagram, Map<Node, Point2D> points) {
        graph = diagram;
        locations = points;
    }

    @Override public int compare(Node o1, Node o2) {
        Collection<Node> p1 = graph.getPredecessors(o1);
        Collection<Node> p2 = graph.getPredecessors(o2);

        // mean value is used but almost always there is only one predecessor
        int m1 = mean(p1);
        int m2 = mean(p2);
        return m1 - m2;
    }

    private int mean(Collection<Node> p) {
        if (p.size() == 0) {
            return 0;
        }
        int mean = 0;
        for (Node n : p) {
            mean += locations.get(n).getX();
        }
        return mean / p.size();
    }
}
