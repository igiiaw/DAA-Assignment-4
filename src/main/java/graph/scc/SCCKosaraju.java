package graph.scc;
import graph.util.Graph;
import java.util.*;

public class SCCKosaraju {
    private final Graph g;
    public SCCKosaraju(Graph g) {
        this.g = g;
    }
    public SCCResult run() {
        int n = g.n;
        boolean[] visited = new boolean[n];
        ArrayList<Integer> order = new ArrayList<>(n);
        for (int v = 0; v < n; v++) {
            if (!visited[v]) dfs1(v, visited, order);
        }
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] radj = new ArrayList[n];
        for (int i = 0; i < n; i++) radj[i] = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            for (var e : g.adj[u]) {
                radj[e.to].add(u);
            }
        }
        Arrays.fill(visited, false);
        SCCResult res = new SCCResult(n);
        for (int i = order.size() - 1; i >= 0; i--) {
            int v = order.get(i);
            if (!visited[v]) {
                List<Integer> comp = new ArrayList<>();
                dfs2(v, visited, radj, comp, res);
                res.components.add(comp);
            }
        }
        return res;
    }
    private void dfs1(int v, boolean[] visited, List<Integer> order) {
        visited[v] = true;
        for (var e : g.adj[v]) {
            int to = e.to;
            if (!visited[to]) dfs1(to, visited, order);
        }
        order.add(v);
    }
    private void dfs2(int v, boolean[] visited, List<Integer>[] radj, List<Integer> comp, SCCResult res) {
        visited[v] = true;
        comp.add(v);
        res.compId[v] = res.components.size();
        for (int to : radj[v]) {
            if (!visited[to]) dfs2(to, visited, radj, comp, res);
        }
    }
}