package graph.dagsp;
import graph.util.Edge;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DAGSPTest {
    @Test
    public void testShortestEdgeWeight() {
        @SuppressWarnings("unchecked")
        ArrayList<Edge>[] adj = new ArrayList[4];
        for (int i = 0; i < 4; i++) adj[i] = new ArrayList<>();
        adj[0].add(new Edge(0,1,1));
        adj[0].add(new Edge(0,2,4));
        adj[1].add(new Edge(1,2,2));
        adj[2].add(new Edge(2,3,1));
        DAGShortestPaths.Result r = DAGShortestPaths.shortest(adj, new double[4], true, 0);
        assertEquals(0.0, r.dist[0]);
        assertEquals(1.0, r.dist[1]);
        assertEquals(3.0, r.dist[2]);
        assertEquals(4.0, r.dist[3]);
        List<Integer> p = DAGShortestPaths.reconstruct(r, 3);
        assertEquals(List.of(0,1,2,3), p);
    }
    @Test
    public void testLongestNodeDurationModel() {
        @SuppressWarnings("unchecked")
        ArrayList<Edge>[] adj = new ArrayList[4];
        double[] nodeDur = new double[]{5,2,3,1};
        for (int i = 0; i < 4; i++) adj[i] = new ArrayList<>();
        adj[0].add(new Edge(0,1,0));
        adj[1].add(new Edge(1,2,0));
        adj[0].add(new Edge(0,2,0));
        adj[2].add(new Edge(2,3,0));
        DAGLongestPath.Result r = DAGLongestPath.longest(adj, nodeDur, false, 0);
        assertEquals(11.0, r.dist[3]);
        List<Integer> p = DAGLongestPath.reconstruct(r, 3);
        assertEquals(List.of(0,1,2,3), p);
    }
}