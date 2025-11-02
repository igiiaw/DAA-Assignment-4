package graph.edgecases;
import graph.util.Graph;
import graph.scc.SCCTarjan;
import graph.scc.SCCResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EdgeCasesTest {
    @Test
    public void testEmptyGraph() {
        Graph g = new Graph(0, true);
        SCCTarjan t = new SCCTarjan(g);
        SCCResult r = t.run();
        assertEquals(0, r.compCount());
    }
    @Test
    public void testSingleNodeNoEdges() {
        Graph g = new Graph(1, true);
        SCCTarjan t = new SCCTarjan(g);
        SCCResult r = t.run();
        assertEquals(1, r.compCount());
        assertEquals(0, r.components.get(0).get(0));
    }
    @Test
    public void testSelfLoopCreatesSingleComponent() {
        Graph g = new Graph(1, true);
        g.addEdge(0,0,1);
        SCCTarjan t = new SCCTarjan(g);
        SCCResult r = t.run();
        assertEquals(1, r.compCount());
        assertEquals(1, r.components.get(0).size());
    }
    @Test
    public void testParallelEdgesCondensationNoDuplicates() {
        Graph g = new Graph(3, true);
        g.addEdge(0,1,1);
        g.addEdge(0,1,2);
        g.addEdge(1,2,1);
        SCCTarjan t = new SCCTarjan(g);
        SCCResult r = t.run();
        assertEquals(3, r.compCount());
    }
}