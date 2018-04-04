/*
 * Project : Empirical Study Project TCSS543 2017
 * Program that implements scaling Max Flow algorithm
 * @author Richa Jain
 * 
 */
import java.util.Iterator;
import java.util.LinkedList;

public class ScalingMaxFlow {

	/*
	 *Function to calculate the max flow in a graphCode using scaling max flow
	 * algorithm
	 * Parameters: Graph, source vertex and sink vertex
	 * Returns the maximum flow
	 */
	public static int scalingMaxFlow(SimpleGraph G, Vertex s, Vertex t ) throws Exception {
		int flow =0;
		int maxCapacity=0;
		int currentCapacity;
		Edge edge;
		Vertex vertex;
		Vertex previousVertex;
		EdgeInfo edgeInfo;
		VertexInfo vertexinfo =new VertexInfo();
		Iterator iterator;
		SimpleGraph residualGraph;
		
		//loop for finding the maximum capacity
		iterator = G.edges();
		while(iterator.hasNext()) {
			edge = (Edge) iterator.next();
			int edgeCapacity = ((Double)edge.getData()).intValue();
			//int edgeCapacity = (int) edge.getData();
			edgeInfo = new EdgeInfo(edgeCapacity,0);
			edge.setData(edgeInfo);
			maxCapacity = Math.max(maxCapacity, edgeInfo.getCapacity());	
		}
		//loop that marks the nodes unvisited
		iterator = G.vertices();
		while(iterator.hasNext()) {
		vertex = (Vertex) iterator.next();
		vertexinfo.setVisited(false);
		vertex.setData(vertexinfo);
		}
		//getting delta value which is power of 2 and is less than or equal to maximum capacity out of s
		int delta = calculateDelta(s);
		
		//the main loop for finding the augmenting path
		while(delta >=1) {
			//calculate the residual graphCode using the augmenting graphcode
			residualGraph = AugumentPath.AugmentPath(G,s,t,new SimpleGraph(),delta);
			while(residualGraph.numEdges() > 0){
				currentCapacity = maxCapacity;
				previousVertex = s;
				iterator = residualGraph.edges();
				//for the current set of edges the loop gets the edge data
				while(iterator.hasNext()) {
					edge = (Edge) iterator.next();
					edgeInfo = (EdgeInfo) edge.getData();
					if( edge.getFirstEndpoint() == previousVertex) {
						currentCapacity = Math.min(currentCapacity, edgeInfo.getAvailableCapacity());
						previousVertex = edge.getSecondEndpoint();
					}
					else {
						currentCapacity = Math.min(currentCapacity, edgeInfo.getFlow());
						previousVertex = edge.getFirstEndpoint();						
					}
				}
				previousVertex = s;
                iterator = residualGraph.edges();
				//for a path in the graphCode the loop calculates the flow that passes through it
                while (iterator.hasNext()) {
                    edge = (Edge) iterator.next();
                    edgeInfo = (EdgeInfo) edge.getData();
                    if (edge.getFirstEndpoint() == previousVertex){
                        edgeInfo.setFlow(edgeInfo.getFlow()+ currentCapacity);
                        previousVertex = edge.getSecondEndpoint();
                    } else {
                        edgeInfo.setFlow(edgeInfo.getFlow() - currentCapacity);
                        previousVertex = edge.getFirstEndpoint();
                    }
                }
                flow = flow + currentCapacity;
                iterator = G.vertices();
                
             //sets all the unvisited vertices to false
                while (iterator.hasNext()) {
                    vertex = (Vertex) iterator.next();
                    vertexinfo = (VertexInfo) vertex.getData();
                    vertexinfo.setVisited(false);
                }
                
				residualGraph = AugumentPath.AugmentPath(G, s, t, new SimpleGraph(),0);
				
			}
			delta = delta/2;
		}
		
		return flow;
	}
	/*
	 * Calculates the delta value for each cycle such that it is power of 2 and 
	 * is less than or equal to maximum capacity out of source
	 */
	private static int calculateDelta(Vertex s) throws Exception {
		int capacity  =2;
		EdgeInfo eInfo;
		LinkedList edgeList = s.incidentEdgeList;
		Edge e;
		for (int i =0; i<edgeList.size();i++) {
			e = (Edge) edgeList.get(i);
			eInfo = (EdgeInfo) e.getData();
			while(capacity < eInfo.getCapacity()) {
				capacity = capacity *2;
			}
			capacity = capacity /2;
			
		}
		return capacity;
	}
	public void scalingStarter(SimpleGraph G) throws Exception {
		Iterator iterator;
		Vertex s = null;
		Vertex t = null;
	
		iterator = G.vertices();
		//from the list of vertices, if any of the vertex is source or sink we assign them 
		while(iterator.hasNext()) {
			Vertex vertex = (Vertex) iterator.next();
			if(vertex.getName().equals("s")) {
				
				s=vertex;
				
			}else if(vertex.getName().equals("t")) {
				t=vertex;

			}
		
		}
		//to calculate the run time for each graphCode
		//long startTime = System.currentTimeMillis();
		//call to function for calculating max flow using scaling max flow algorithm
		int flow = scalingMaxFlow(G,s,t);
		//long endTime = System.currentTimeMillis();
		
		System.out.println("The max flow for scaling is : " + flow);
		//System.out.println("The run time in miliseconds for scaling is : " + (endTime-startTime));

	}
	

}
