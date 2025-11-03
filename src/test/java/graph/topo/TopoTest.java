package graph.topo;
import graph.util.Graph;
import graph.scc.SCCTarjan;
import graph.scc.SCCResult;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TopoTest {
    @Test
    public void testKahnSimpleDAG() {
        @SuppressWarnings("unchecked")
        java.util.ArrayList<graph.util.Edge>[] adj = new java.util.ArrayList[3];
        for (int i = 0; i < 3; i++) adj[i] = new java.util.ArrayList<>();
        adj[0].add(new graph.util.Edge(0,1,1));
        adj[1].add(new graph.util.Edge(1,2,1));
        int[] order = TopologicalSorter.kahnOrder(adj, null);
        assertNotNull(order);
        assertEquals(3, order.length);
        int pos0= -1, pos1=-1, pos2=-1;
        for (int i=0;i<order.length;i++){
            if(order[i]==0) pos0=i;
            if(order[i]==1) pos1=i;
            if(order[i]==2) pos2=i;
        }
        assertTrue(pos0 < pos1 && pos1 < pos2);
    }
    @Test
    public void testKahnMultipleSources() {
        @SuppressWarnings("unchecked")
        java.util.ArrayList<graph.util.Edge>[] adj = new java.util.ArrayList[4];
        for (int i = 0; i < 4; i++) adj[i] = new java.util.ArrayList<>();
        adj[0].add(new graph.util.Edge(0,2,1));
        adj[1].add(new graph.util.Edge(1,2,1));
        adj[2].add(new graph.util.Edge(2,3,1));
        int[] order = TopologicalSorter.kahnOrder(adj, null);
        assertNotNull(order);
        int p0=-1,p1=-1,p2=-1,p3=-1;
        for (int i=0;i<order.length;i++){
            if(order[i]==0) p0=i; if(order[i]==1) p1=i; if(order[i]==2) p2=i; if(order[i]==3) p3=i;
        }
        assertTrue(p0 < p2 && p1 < p2 && p2 < p3);
    }
    @Test
    public void testKahnCycleReturnsNull() {
        @SuppressWarnings("unchecked")
        java.util.ArrayList<graph.util.Edge>[] adj = new java.util.ArrayList[2];
        for (int i = 0; i < 2; i++) adj[i] = new java.util.ArrayList<>();
        adj[0].add(new graph.util.Edge(0,1,1));
        adj[1].add(new graph.util.Edge(1,0,1));
        int[] order = TopologicalSorter.kahnOrder(adj, null);
        assertNull(order);
    }
}