import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class tcss543 {
    public static void main(String args[]) throws ExecutionException, InterruptedException {
        String filePath = args[0];
        CompletableFuture<Double> fordFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Started ford fulkerson algorithm");
            SimpleGraph fordFulkersonGraph = new SimpleGraph();
            GraphInput.LoadSimpleGraph(fordFulkersonGraph, filePath);
            return new FordFulkerson().fordFulkersonInit(fordFulkersonGraph);
        });
        CompletableFuture<Double> scalingFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Started scaling max flow algorithm");
            SimpleGraph scalingGraph = new SimpleGraph();
            GraphInput.LoadSimpleGraph(scalingGraph, filePath);
            Vertex source = (Vertex) scalingGraph.vertexList.stream().filter(vertex -> ((Vertex) vertex).getName().equals("s")).findFirst().get();
            Vertex sink = (Vertex) scalingGraph.vertexList.stream().filter(vertex -> ((Vertex) vertex).getName().equals("t")).findFirst().get();
            try {
                return (double) ScalingMaxFlow.scalingMaxFlow(scalingGraph, source, sink);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        CompletableFuture<Double> preflowFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Started pre flow push relabel algorithm");
            SimpleGraph preflowGraph = new SimpleGraph();
            GraphInput.LoadSimpleGraph(preflowGraph, filePath);
            return new PreflowPushRelabel(preflowGraph).preflowPush();
        });
        System.out.println("Flow value for ford fulkerson algorithm is: " + fordFuture.get());
        System.out.println("Flow value for scaling max flow algorithm is: " + scalingFuture.get());
        System.out.println("Flow value for pre flow push relabel algorithm is: " + preflowFuture.get());
    }
}
