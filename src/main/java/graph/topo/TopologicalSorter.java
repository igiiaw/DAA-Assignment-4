package graph.topo;
import graph.util.Edge;
import metrics.Metrics;
import java.util.*;

public class TopologicalSorter {
    public static int[] kahnOrder(List<Edge>[] adj, Metrics metrics) {
        int n = adj.length;
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (var e : adj[u]) indeg[e.to]++;
        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) {
            q.add(i);
            if (metrics != null) metrics.queuePushes++;
        }
        int[] order = new int[n];
        int idx = 0;
        while (!q.isEmpty()) {
            int u = q.remove();
            if (metrics != null) metrics.queuePops++;
            order[idx++] = u;
            for (var e : adj[u]) {
                indeg[e.to]--;
                if (indeg[e.to] == 0) {
                    q.add(e.to);
                    if (metrics != null) metrics.queuePushes++;
                }
            }
        }
        if (idx != n) return null;
        return order;
    }
}