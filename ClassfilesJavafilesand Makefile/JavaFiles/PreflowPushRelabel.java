import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PreflowPushRelabel {

    private static final String SOURCE_VERTEX_NAME = "s";
    private static final String SINK_VERTEX_NAME = "t";

    private SimpleGraph resGraph;

    public PreflowPushRelabel(SimpleGraph graph) {
        initialize(graph);
    }

    // Class to store the height and excess flow for each vertex of the graph
    class VertexData {
        private int height;
        private double excessFlow;
        VertexData(int height, double excessFlow) {
            this.height = height;
            this.excessFlow = excessFlow;
        }
        int getHeight() {
            return height;
        }
        void setHeight(int height) {
            this.height = height;
        }
        double getExcessFlow() {
            return excessFlow;
        }
        void setExcessFlow(double excessFlow) {
            this.excessFlow = excessFlow;
        }
        @Override
        public String toString() {
            return "VertexData{" +
                    "height=" + height +
                    ", excessFlow=" + excessFlow +
                    '}';
        }
    }

    // Class to store the capacity and flow for each edge of the graph
    class EdgeData {
        private double capacity;
        private double flow;
        EdgeData(double capacity, double flow) {
            this.capacity = capacity;
            this.flow = flow;
        }
        double getCapacity() {
            return capacity;
        }
        double getFlow() {
            return flow;
        }
        void setFlow(double flow) {
            this.flow = flow;
        }
        @Override
        public String toString() {
            return "EdgeData{" +
                    "capacity=" + capacity +
                    ", flow=" + flow +
                    '}';
        }
    }

    private void initialize(SimpleGraph graph) {
        this.resGraph = graph;

        // Initialize height for every node to zero and height of source to number of vertices
        Iterator<Vertex> resGraphVertices = resGraph.vertices();
        resGraphVertices.forEachRemaining(resGraphVertex -> {
            resGraphVertex.setData(new VertexData(resGraphVertex.getName().equals(SOURCE_VERTEX_NAME) ?
                    graph.numVertices() : 0, 0));
        });

        // Initialize edge capacity to given capacity and flow to 0 for each edge in the graphCode
        Iterator<Edge> resGraphEdges = resGraph.edges();
        resGraphEdges.forEachRemaining(resGraphEdge -> {
            Double capacity = (Double) resGraphEdge.getData();
            resGraphEdge.setData(new EdgeData(capacity, 0));
        });

        // Initialize excessFlow for nodes adjacent to source node to the capacity of the edge and saturate all the edges
        List<Edge> incidentEdges = new LinkedList<>();
        Iterator<Edge> sourceIncidentEdges = resGraph.incidentEdges((Vertex) resGraph.vertices().next());
        sourceIncidentEdges.forEachRemaining(incidentEdge -> {
            EdgeData incidentEdgeData = (EdgeData) incidentEdge.getData();
            incidentEdge.setData(new EdgeData(incidentEdgeData.getCapacity(), incidentEdgeData.getCapacity()));
            incidentEdge.getSecondEndpoint().setData(new VertexData(0, ((EdgeData) incidentEdge.getData()).getCapacity()));
            incidentEdges.add(incidentEdge);
        });

        // Create reverse edge in residual graphCode after saturating edges from source node
        for (Edge incidentEdge : incidentEdges) {
            EdgeData incidentEdgeData = (EdgeData) incidentEdge.getData();
            resGraph.insertEdge(incidentEdge.getSecondEndpoint(), incidentEdge.getFirstEndpoint(), new EdgeData(0,
                    incidentEdgeData.getCapacity() * -1), null);
        }
    }

    public double preflowPush() {
        // Do the process until there is no vertex with excess flow
        while (getVertexWithExcessFlow() != null) {
            Vertex vertexWithExcessFlow = getVertexWithExcessFlow();
            Edge edgeToPushFlow = getEdgeToPushFlow(vertexWithExcessFlow);
            if (edgeToPushFlow != null) {
                push(edgeToPushFlow);
            } else {
                relabel(vertexWithExcessFlow);
            }
        }

        // The max flow is the excess flow at sink vertex
        Vertex destVertex = null;
        Iterator<Vertex> resGraphVertices = resGraph.vertices();
        while (resGraphVertices.hasNext()) {
            destVertex = resGraphVertices.next();
            if (destVertex.getName().equals(SINK_VERTEX_NAME)) {
                break;
            }
        }
        return ((VertexData) destVertex.getData()).getExcessFlow();
    }

    // Returns a vertex other than source and sink node with excess flow 
    private Vertex getVertexWithExcessFlow() {
        Iterator<Vertex> vertices = resGraph.vertices();
        while (vertices.hasNext()) {
            Vertex vertex = vertices.next();
            if(!vertex.getName().equals(SINK_VERTEX_NAME) && !vertex.getName().equals(SOURCE_VERTEX_NAME)
                    && ((VertexData) vertex.getData()).getExcessFlow() > 0) {
                return vertex;
            }
        }
        return null;
    }

    // Creates reverse edges in residual graph for the forward edges
    private void updateResidualGraph(Edge edge, double delta) {
        Iterator<Edge> edges = resGraph.incidentEdges(edge.getSecondEndpoint());
        boolean hasReverseEdge = false;
        while(edges.hasNext()) {
            Edge graphEdge = edges.next();
            if (graphEdge.getFirstEndpoint().getName().equals(edge.getSecondEndpoint().getName()) &&
                    graphEdge.getSecondEndpoint().getName().equals(edge.getFirstEndpoint().getName())) {
                hasReverseEdge = true;
                EdgeData graphEdgeData = (EdgeData) graphEdge.getData();
                graphEdgeData.setFlow(graphEdgeData.getFlow() - delta);
            }
        }
        if (!hasReverseEdge) {
            resGraph.insertEdge(edge.getSecondEndpoint(), edge.getFirstEndpoint(),
                    new EdgeData(0, -1 * delta), null);
        }
    }

    // Gets the edge of the graph to push flow. Returns the edge that still has the possibility to push flow
    private Edge getEdgeToPushFlow(Vertex sourceVertex) {
        VertexData sourceVertexData = (VertexData) sourceVertex.getData();
        Iterator<Edge> edges = resGraph.incidentEdges(sourceVertex);
        while (edges.hasNext()) {
            Edge edge = edges.next();
            EdgeData edgeData = (EdgeData) edge.getData();
            Vertex oppositeVertex = resGraph.opposite(sourceVertex, edge);
            if ((edge.getFirstEndpoint().getName() == sourceVertex.getName()) && (edgeData.getCapacity() != edgeData.getFlow())
                    && ((VertexData) oppositeVertex.getData()).getHeight() < sourceVertexData.getHeight()) {
                return edge;
            }
        }
        return null;
    }

    // Peforms the push opertion
    private void push(Edge graphEdge) {
        VertexData sourceVertextData = (VertexData) graphEdge.getFirstEndpoint().getData();
        VertexData destVertextData = (VertexData) graphEdge.getSecondEndpoint().getData();
        EdgeData edgeData = (EdgeData) graphEdge.getData();
        double delta = Math.min(sourceVertextData.getExcessFlow(), edgeData.getCapacity() - edgeData.getFlow());
        sourceVertextData.setExcessFlow(sourceVertextData.getExcessFlow() - delta);
        destVertextData.setExcessFlow(destVertextData.getExcessFlow() + delta);
        edgeData.setFlow(edgeData.getFlow() + delta);
        updateResidualGraph(graphEdge, delta);
    }

    // Peforms the relabel opertion
    private void relabel(Vertex sourceVertex) {
        int minHeight = Integer.MAX_VALUE;
        VertexData sourceVertexData = (VertexData) sourceVertex.getData();
        Iterator<Edge> edges = resGraph.incidentEdges(sourceVertex);
        while (edges.hasNext()) {
            Edge edge = edges.next();
            EdgeData edgeData = (EdgeData) edge.getData();
            VertexData oppositeVertexData = (VertexData) resGraph.opposite(sourceVertex, edge).getData();
            if ((edgeData.getCapacity() - edgeData.getFlow() > 0) && oppositeVertexData.getHeight() >= sourceVertexData.getHeight()) {
                minHeight = Math.min(oppositeVertexData.getHeight(), minHeight);
            }
        }
        sourceVertexData.setHeight(minHeight + 1);
    }
}
