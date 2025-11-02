package graph.dagsp;
import graph.util.Edge;
import graph.topo.TopologicalSorter;
import metrics.Metrics;
import java.util.*;

public class DAGLongestPath {
    public static class Result {
        public final double[] dist;
        public final int[] parent;
        public Result(int n) { dist = new double[n]; parent = new int[n]; Arrays.fill(dist, Double.NEGATIVE_INFINITY); Arrays.fill(parent, -1); }
    }
    public static Result longest(List<Edge>[] adj, double[] nodeWeight, boolean edgeWeightModel, int source, Metrics metrics) {
        int n = adj.length;
        Result r = new Result(n);
        int[] order = TopologicalSorter.kahnOrder(adj, metrics);
        if (order == null) return r;
        r.dist[source] = edgeWeightModel ? 0.0 : nodeWeight[source];
        for (int u : order) {
            if (r.dist[u] == Double.NEGATIVE_INFINITY) continue;
            for (var e : adj[u]) {
                int v = e.to;
                double w = edgeWeightModel ? e.weight : nodeWeight[v];
                double cand = r.dist[u] + w;
                if (cand > r.dist[v]) {
                    r.dist[v] = cand;
                    r.parent[v] = u;
                    if (metrics != null) metrics.relaxations++;
                }
            }
        }
        return r;
    }
    public static List<Integer> reconstruct(Result r, int target) {
        if (r.dist[target] == Double.NEGATIVE_INFINITY) return Collections.emptyList();
        LinkedList<Integer> path = new LinkedList<>();
        int cur = target;
        while (cur != -1) {
            path.addFirst(cur);
            cur = r.parent[cur];
        }
        return path;
    }
}