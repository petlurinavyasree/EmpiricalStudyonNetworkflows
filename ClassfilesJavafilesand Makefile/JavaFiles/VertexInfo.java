/*
 * Program to keep track of visited vertices
 * If the vertex is visited it is set to True else
 * to false
 * 
 */
public class VertexInfo {
	 private boolean visited;

	    public VertexInfo(boolean visited){

	        this.visited = visited;
	    }	
	    
	    public VertexInfo(){

	        this.visited = false;
	    }

	    public boolean isVisited() {

	        return visited;
	    }

	    public void setVisited(boolean visited) {

	        this.visited = visited;
	    }

}
