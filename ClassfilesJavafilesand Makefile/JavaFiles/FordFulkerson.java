import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**x
 *
 * An implementation of the Ford-Fulkerson algorithm  steps
 * Here we construct the residual graphCode
 * Find for an augmenting s-t path in the graphCode which is done using breadth first search in O(m+n) with maximmun of F iterations where overall run time = O(mF).
 * If path is  found,
 * then push maximum flow it can hold and repeat it.
 * else, final flow is returned.
 *
 *
 * author: Petluri Navyasree
 */
public class FordFulkerson {


    public static void main(String[] args) {
        // Load the Graph.
        SimpleGraph G = new SimpleGraph();
        Hashtable graphTable = GraphInput.LoadSimpleGraph(G, args[0]);
        System.out.println("Number of vertices: " + G.numVertices());
        System.out.println("Number of Edges: " + G.numEdges());

        FordFulkerson m = new FordFulkerson();
        long startTime = System.currentTimeMillis();
        Double maxFlow = m.fordFulkersonInit(G);
        long endTime = System.currentTimeMillis();


        System.out.println("maxFlow: " + maxFlow);
        System.out.println("Time taken for ford fulkerson:" + (endTime - startTime));
    }

    /**
     * Assigns an index to each vertex for easy access of vertices in the path.
     * @param G Loaded graphCode.
     * @return A Hashmap of vertex names to their assigned indices.
     */
    public HashMap<String, Integer> createVertexIndexMap(SimpleGraph G) {
        // Create a Map of VertexName to index.
        HashMap<String, Integer> vertexIndexMap = new HashMap<String, Integer>();
        int count = 0;
        Iterator i = G.vertices();

        // Iterate through each vertex in the graphCode and assign an index to it.
        while (i.hasNext()) {
            Vertex v = (Vertex) i.next();
            vertexIndexMap.put((String) v.getName(), count++);
        }
        return vertexIndexMap;
    }

    /**
     * Build a residual graphCode as an adjacency matrix.
     * Though it O(V^2) space, this makes it easy to access edges and edge capacities in O(1).
     * @param G Loaded graphCode.
     * @param vertexIndexMap A Hashmap of vertex names to their assigned indices.
     * @return A residual graphCode as an adjaceny matrix of the given graphCode.
     */
    public Double[][] createResidualGraph(SimpleGraph G, HashMap<String, Integer> vertexIndexMap, int numVertices) {

        // Create an adjacency matrix and fill all the elements with 0.0
        Double residualGraph[][] = new Double[numVertices][numVertices];
        for (int u = 0; u < numVertices; u++) {
            for (int v = 0; v < numVertices; v++) {
                residualGraph[u][v] = 0.0;
            }
        }

        // Iterate through each vertex in the given graphCode.
        // Pick each incident edge of the graphCode and add the edge capacity to the adjaceny matrix at appropriate place.
        // For each vertex u and its incident edge e, pick opposite vertex of e i.e v.
        // Add the edge data to residualGraph[u][v]
        Iterator vertices = G.vertices();
        while (vertices.hasNext()) {
            Vertex curVertex = (Vertex) vertices.next();
            int curVertexIndex = vertexIndexMap.get(curVertex.getName());
            Iterator edges = curVertex.incidentEdgeList.iterator();
            while (edges.hasNext()) {
                Edge e = (Edge) edges.next();
                if (e.getFirstEndpoint().getName().equals(curVertex.getName())) {
                    Vertex opposite = G.opposite(curVertex, e);
                    int oppVertexIndex = vertexIndexMap.get(opposite.getName());
                    residualGraph[curVertexIndex][oppVertexIndex] = (Double) e.getData();
                }
            }
        }

        return residualGraph;
    }

    /**
     * Implementation of ford fulkerson algorithm
     * @param G
     * @return
     */
    public Double fordFulkersonInit(SimpleGraph G) {
        int numVertices = G.numVertices();
        HashMap<String, Integer> vertexIndexMap = createVertexIndexMap(G);

        //Create a residual graphCode.
        Double[][] residualGraph = createResidualGraph(G, vertexIndexMap, numVertices);

        // Get source and sink indices from vertexIndexMap.
        int source = vertexIndexMap.get("s");
        int sink = vertexIndexMap.get("t");

        // Call Ford fulkerson algorithm and get max Flow.
        return fordFulkersonCore(residualGraph, source, sink, numVertices);

    }

    /**
     * Implement the ford fulkerson algorithm on the given residual graphCode (adjacency matrix)
     * and return max flow.
     * @param residualGraph An adjancency matrix with residual capacities.
     * @param source the source vertex index.
     * @param sink the sink vertex index.
     * @return maxFlow.
     */
    public Double fordFulkersonCore(Double[][] residualGraph, int source, int sink, int numVertices) {
        Double maxFlow = 0.0;
        // Each field stores the index of the parent vertex.
        int path[] = new int[numVertices];

        // Augment the flow while there is path from source to sink.
        // Do Bfs to determine if there is a path frm source to sink.
        while (bfs(residualGraph, source, sink, path, numVertices))
        {
            //Find min of the path.
            // Iterate the path from sink to source.
            // If the edge data is smaller than the pathFlow, update pathFlow to edge data.
            Double pathFlow = Double.valueOf(Integer.MAX_VALUE);
            for (int v=sink; v!=source; v=path[v]) {
                int u = path[v];
                if (pathFlow > residualGraph[u][v]) {
                    pathFlow = residualGraph[u][v];
                }
            }

            // update capacities of the edges in the path.
            // Iterate the path from sink to source.
            // Subtract pathFlow from each forward edge.
            // Add pathFlow to each forward edge.
            for (int v=sink; v != source; v=path[v]) {
                int u = path[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            // Add path flow to overall flow
            maxFlow += pathFlow;
        }

        return maxFlow;
    }


    /**
     * Breadth first search ,checking vertices if they are visited or not.
     * @param residualGraph
     * @param s
     * @param sink
     * @param parent
     * @return
     */
    public boolean bfs(Double[][] residualGraph, int s, int sink, int parent[], int numVertices)
    {
        boolean visited[] = new boolean[numVertices];
        for(int i=0; i<numVertices; ++i)
            visited[i]=false;

        // Create a queue, enqueue source vertex and mark
        // source vertex as visited
        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.add(s);
        visited[s] = true;
        parent[s]=-1;

        // Standard BFS Loop
        while (queue.size()!=0)
        {
            int u = queue.poll();

            for (int v=0; v<numVertices; v++)
            {
                if (visited[v]==false && residualGraph[u][v] > 0)
                {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }

        // If we reached sink in BFS starting from source, then
        // return true, else false
        return (visited[sink] == true);
    }


}
