package fr.rmannibucau.loader.spi.graph;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * @author Romain Manni-Bucau
 */
public class Diagram extends DirectedSparseGraph<Node, Edge> {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
