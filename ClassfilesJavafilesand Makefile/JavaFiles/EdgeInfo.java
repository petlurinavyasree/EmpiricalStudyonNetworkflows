/*
 * Program to keep track of capacity and flow for the edges
 * Initial capacity is set to 1 and initial flow is set to 0
 */
public class EdgeInfo {
	private int flow;
	private int capacity;
	public EdgeInfo(){
		this.flow=0;
		this.capacity=1;
	}
	/*
	 * Function to keep track of flow and capacity for each edge.
	 * Throws IndexOutOfBound Exception when flow or capacity is less than 0,
	 * or if capacity is less than flow
	 */
	public EdgeInfo(int capacity, int flow) throws Exception {
		if(capacity < flow || flow <0 || capacity<0) {
			throw new IndexOutOfBoundsException();
		}
		this.capacity=capacity;
		this.flow=flow;
		}
	//get the current capacity
	public int getCapacity() {
		return this.capacity;
	}
	//get the available capacity
	public int getAvailableCapacity() {
		return this.capacity-flow;
		}
	//returns the current flow
	public int getFlow() {
		return this.flow;
	}
	
	/*
	 * Function sets the flow for the current edge.
	 * Throws IndexOutOfBound Exception when flow less than 0
	 * or if capacity is less than flow
	 */
	public void setFlow(int flow) throws Exception{
		if(this.capacity < flow || flow <0){
			throw new IndexOutOfBoundsException();
		}
		this.flow=flow;
	}

}
