package graph.topo;
import graph.util.Edge;
import graph.util.Graph;
import graph.scc.SCCResult;
import java.util.*;

public class CondensationGraph {
    public final int compN;
    public final List<Edge>[] adj;
    public final double[] compDuration;
    @SuppressWarnings("unchecked")
    public CondensationGraph(Graph g, SCCResult scc) {
        compN = scc.compCount();
        adj = new ArrayList[compN];
        for (int i = 0; i < compN; i++) adj[i] = new ArrayList<>();
        compDuration = new double[compN];
        for (int i = 0; i < compN; i++) compDuration[i] = 0.0;
        for (int v = 0; v < g.n; v++) {
            int cid = scc.compId[v];
            compDuration[cid] += g.nodeDuration[v];
        }
        Set<Long> seen = new HashSet<>();
        for (int u = 0; u < g.n; u++) {
            int cu = scc.compId[u];
            for (var e : g.adj[u]) {
                int v = e.to;
                int cv = scc.compId[v];
                if (cu == cv) continue;
                long key = ((long)cu << 32) | (cv & 0xffffffffL);
                if (seen.contains(key)) continue;
                seen.add(key);
                adj[cu].add(new Edge(cu, cv, e.weight));
            }
        }
    }
}