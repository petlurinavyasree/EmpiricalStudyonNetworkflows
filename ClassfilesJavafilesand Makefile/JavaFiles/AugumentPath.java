/*
 * Project : Empirical Study Project TCSS543 2017
 * Program for calculating the augmenting path with a residual graphCode
 * @author Richa Jain
 * 
 */
import java.util.Iterator;

public class AugumentPath {
	  static int vertexInfo; 
	  static boolean isPath = false;
	  public AugumentPath() {}
	  /*
	   * Function to calculate the augment path 
	   * G : Simple Graph input graphCode
	   * s : Source vertex s
	   * t : Sink vertex t
	   * path : empty simple graphCode to store the path
	   * d : scaling parameter
	   * 
	   */
	  
      public static SimpleGraph AugmentPath(SimpleGraph G, Vertex s, Vertex t, SimpleGraph path, int d) {
        Vertex vertex;
        Edge edge;
        Iterator itr;
        SimpleGraph newPath;
        isPath = false;

        VertexInfo v_data = new VertexInfo(true);
        s.setData(v_data);	
        path.vertexList.add(s);
        itr = G.incidentEdges(s);
        
        while (itr.hasNext()) {
            edge = (Edge) itr.next();
            vertex = G.opposite(s, edge);

            if (!( (VertexInfo) vertex.getData()).isVisited() &&
                    (vertex == edge.getSecondEndpoint() &&
                    ((EdgeInfo) edge.getData()).getAvailableCapacity() > d ||
                    vertex == edge.getFirstEndpoint() &&
                    ((EdgeInfo) edge.getData()).getFlow() > d)) {

                if (vertex == t){
                    path.edgeList.add(edge);
                    path.vertexList.add(vertex);
                    return path;
                }

                path.edgeList.add(edge);
                newPath = AugmentPath(G, vertex, t, path, d);
                if (newPath.vertexList.getLast() == t) {
                    maxFlow((EdgeInfo) ((Edge) newPath.edgeList.getFirst()).getData());
                    setPath();
                    return newPath;
                }
                path.edgeList.removeLast();
            }
            ((VertexInfo)s.getData()).setVisited(true);
        }

        path.vertexList.removeLast();
        return path;
    }
    /*
     * Returns the max flow for some augmenting path
     * 
     */
    private static int maxFlow(EdgeInfo v_data) {
        vertexInfo = vertexInfo + v_data.getFlow();
        return vertexInfo;
    }
    
    private static Boolean setPath() {

        return isPath = true;
    }

}
