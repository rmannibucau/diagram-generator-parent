package fr.rmannibucau.camel.loader;

import edu.uci.ics.jung.graph.util.EdgeType;
import fr.rmannibucau.loader.spi.graph.Diagram;
import fr.rmannibucau.loader.spi.graph.Edge;
import fr.rmannibucau.loader.spi.graph.Node;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.view.GraphSupport;
import org.apache.camel.view.NodeData;

/**
 * @author Romain Manni-Bucau
 */
public class GraphGenerator extends GraphSupport {
    private final Map<String, Node> nodesCache = new HashMap<String, Node>();
    private Diagram diagram;

    protected GraphGenerator(Diagram diag) {
        diagram = diag;
    }

    public void drawRoutes(final Collection<RouteDefinition> routes) throws IOException {
        Map<String,List<RouteDefinition>> routeGroupMap = createRouteGroupMap(new ArrayList<RouteDefinition>(routes));

        if (routeGroupMap.size() >= 1) {
            Set<Map.Entry<String, List<RouteDefinition>>> entries = routeGroupMap.entrySet();
            for (Map.Entry<String, List<RouteDefinition>> entry : entries) {
                for (RouteDefinition route : entry.getValue()) {
                    addRoute(route);
                }
            }
        }
    }

    private void addRoute(RouteDefinition route) {
        List<FromDefinition> inputs = route.getInputs();
        for (FromDefinition input : inputs) {
            addRoute(route, input);
        }
    }

    private void addRoute(RouteDefinition route, FromDefinition input) {
        NodeData from = getNodeData(input);
        node(from);
        for (ProcessorDefinition<?> output : route.getOutputs()) {
            from = addNode(from, output);
        }
    }

    // took from dot generator
    private NodeData addNode(NodeData fromData, ProcessorDefinition<?> node) {
        if (node instanceof MulticastDefinition) {
            // no need for a multicast or interceptor node
            List<ProcessorDefinition<?>> outputs = node.getOutputs();
            boolean isPipeline = isPipeline(node);
            for (ProcessorDefinition output : outputs) {
                NodeData out = addNode(fromData, output);
                // if in pipeline then we should move the from node to the next in the pipeline
                if (isPipeline) {
                    fromData = out;
                }
            }
            return fromData;
        }

        NodeData toData = getNodeData(node);
        node(toData);

        if (fromData != null) {
            diagram.addEdge(new Edge(fromData.edgeLabel), node(fromData), node(toData), EdgeType.DIRECTED);
        }

        List<ProcessorDefinition<?>> outputs = toData.outputs;
        if (outputs != null) {
            for (ProcessorDefinition output : outputs) {
                NodeData newData = addNode(toData, output);
                if (!isMulticastNode(node)) {
                    toData = newData;
                }
            }
        }
        return toData;
    }

    private Node node(NodeData data) {
        completeNodeData(data);

        if (!nodesCache.containsKey(data.id)) {
            Node node = new Node(getText(data));
            if (data.image != null && !data.image.isEmpty()) {
                String icon = "/icons" + data.image.substring(data.image.lastIndexOf('/'));
                URL url = GraphGenerator.class.getResource(icon);
                if (url != null) {
                    node.setIcon(new ImageIcon(url));
                }
            }
            nodesCache.put(data.id, node);
            diagram.addVertex(node);
        }
        return nodesCache.get(data.id);
    }

    /**
     * This method aims to add missing datas.
     *
     * For example otherwise case doesn't have an icon.
     *
     * @param data the input data
     */
    private void completeNodeData(NodeData data) {
        if ("Otherwise".equals(data.nodeType)) {
            data.image = "/MessageFilterIcon.png";
            data.edgeLabel = "otherwise";
        }
    }

    private String getText(NodeData data) {
        return data.tooltop;
    }
}
