package graph.util;
import java.util.*;

public class Graph {
    public final int n;
    public final List<Edge>[] adj;
    public final double[] nodeDuration;
    public final boolean edgeWeightModel;
    public final Map<Integer,Integer> idToIndex;
    public final int[] indexToId;
    @SuppressWarnings("unchecked")
    public Graph(int n, boolean edgeWeightModel) {
        this.n = n;
        this.edgeWeightModel = edgeWeightModel;
        adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        nodeDuration = new double[n];
        idToIndex = new HashMap<>();
        indexToId = new int[n];
    }
    public void addEdge(int u, int v, double w) {
        adj[u].add(new Edge(u,v,w));
    }
    public void setNodeDuration(int u, double d) {
        nodeDuration[u] = d;
    }
    public static Graph fromMappings(Map<Integer, Integer> idMap, boolean edgeWeightModel) {
        int n = idMap.size();
        Graph g = new Graph(n, edgeWeightModel);
        for (Map.Entry<Integer,Integer> e : idMap.entrySet()) {
            int id = e.getKey();
            int idx = e.getValue();
            g.idToIndex.put(id, idx);
            g.indexToId[idx] = id;
        }
        return g;
    }
}