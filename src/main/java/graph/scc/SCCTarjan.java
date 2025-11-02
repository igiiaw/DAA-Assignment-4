package graph.scc;
import graph.util.Graph;
import metrics.Metrics;
import java.util.*;

public class SCCTarjan {
    private final Graph g;
    private final Metrics metrics;
    private int time;
    private final int[] index;
    private final int[] low;
    private final boolean[] onStack;
    private final Deque<Integer> stack;
    private final SCCResult res;
    public SCCTarjan(Graph g, Metrics metrics) {
        this.g = g;
        this.metrics = metrics;
        int n = g.n;
        index = new int[n];
        Arrays.fill(index, -1);
        low = new int[n];
        onStack = new boolean[n];
        stack = new ArrayDeque<>();
        res = new SCCResult(n);
    }
    public SCCResult run() {
        for (int v = 0; v < g.n; v++) {
            if (index[v] == -1) dfs(v);
        }
        return res;
    }
    private void dfs(int v) {
        if (metrics != null) metrics.dfsVisits++;
        index[v] = time;
        low[v] = time;
        time++;
        stack.push(v);
        onStack[v] = true;
        for (var e : g.adj[v]) {
            if (metrics != null) metrics.edgesVisited++;
            int w = e.to;
            if (index[w] == -1) {
                dfs(w);
                low[v] = Math.min(low[v], low[w]);
            } else if (onStack[w]) {
                low[v] = Math.min(low[v], index[w]);
            }
        }
        if (low[v] == index[v]) {
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int w = stack.pop();
                onStack[w] = false;
                comp.add(w);
                res.compId[w] = res.components.size();
                if (w == v) break;
            }
            res.components.add(comp);
        }
    }
}