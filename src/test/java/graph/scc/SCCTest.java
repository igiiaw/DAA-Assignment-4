package graph.scc;
import graph.util.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SCCTest {
    @Test
    public void testSimpleCycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0,1,1);
        g.addEdge(1,2,1);
        g.addEdge(2,0,1);
        SCCTarjan t = new SCCTarjan(g);
        SCCResult r = t.run();
        assertEquals(1, r.compCount());
        assertEquals(0, r.compId[0]);
    }
    @Test
    public void testDAG() {
        Graph g = new Graph(3, true);
        g.addEdge(0,1,1);
        g.addEdge(1,2,1);
        SCCTarjan t = new SCCTarjan(g);
        SCCResult r = t.run();
        assertEquals(3, r.compCount());
    }
}