package io;
import com.google.gson.*;
import graph.util.Graph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GraphIO {
    public static class ParseResult {
        public final Graph graph;
        public final Integer sourceIndex;
        public ParseResult(Graph graph, Integer sourceIndex) {
            this.graph = graph;
            this.sourceIndex = sourceIndex;
        }
    }
    public static ParseResult readFromJson(Path path) throws IOException {
        String s = Files.readString(path);
        JsonElement je = JsonParser.parseString(s);
        if (!je.isJsonObject()) throw new IOException("Invalid JSON root");
        JsonObject root = je.getAsJsonObject();
        Gson gson = new Gson();
        if (root.has("n") && root.has("edges") && root.get("edges").isJsonArray()) {
            int n = root.get("n").getAsInt();
            String weightModel = "edge";
            if (root.has("weight_model")) weightModel = root.get("weight_model").getAsString();
            boolean edgeModel = "edge".equalsIgnoreCase(weightModel);
            Graph g = new Graph(n, edgeModel);
            if (root.has("nodes") && root.get("nodes").isJsonArray()) {
                JsonArray nodes = root.getAsJsonArray("nodes");
                for (JsonElement ne : nodes) {
                    if (!ne.isJsonObject()) continue;
                    JsonObject no = ne.getAsJsonObject();
                    if (no.has("id") && no.has("duration")) {
                        int id = no.get("id").getAsInt();
                        double dur = no.get("duration").getAsDouble();
                        if (id >= 0 && id < n) g.setNodeDuration(id, dur);
                    }
                }
            }
            JsonArray edges = root.getAsJsonArray("edges");
            for (JsonElement ee : edges) {
                if (!ee.isJsonObject()) continue;
                JsonObject ej = ee.getAsJsonObject();
                int u = ej.get("u").getAsInt();
                int v = ej.get("v").getAsInt();
                double w = ej.has("w") ? ej.get("w").getAsDouble() : 0.0;
                if (u < 0 || u >= n || v < 0 || v >= n) continue;
                g.addEdge(u, v, w);
            }
            Integer sourceIdx = null;
            if (root.has("source")) {
                JsonElement se = root.get("source");
                if (se.isJsonPrimitive() && se.getAsJsonPrimitive().isNumber()) {
                    int src = se.getAsInt();
                    if (src >= 0 && src < n) sourceIdx = src;
                }
            }
            return new ParseResult(g, sourceIdx);
        }
        class NodeJson { public int id; public double duration; }
        class EdgeJson { public int from; public int to; public double weight; }
        class Root { public List<NodeJson> nodes; public List<EdgeJson> edges; public Map<String,String> meta; }
        Root r = gson.fromJson(root, Root.class);
        if (r == null) throw new IOException("Invalid/empty graph JSON");
        Set<Integer> ids = new LinkedHashSet<>();
        if (r.nodes != null) for (NodeJson nn : r.nodes) ids.add(nn.id);
        if (r.edges != null) for (EdgeJson ee : r.edges) { ids.add(ee.from); ids.add(ee.to); }
        Map<Integer,Integer> idMap = new LinkedHashMap<>();
        int idx = 0;
        for (int id : ids) idMap.put(id, idx++);
        boolean edgeModel = true;
        if (r.meta != null && r.meta.containsKey("weightModel"))
            edgeModel = "edge".equalsIgnoreCase(r.meta.get("weightModel"));
        Graph g = Graph.fromMappings(idMap, edgeModel);
        if (r.nodes != null) for (NodeJson nn : r.nodes) {
            int u = g.idToIndex.get(nn.id);
            g.setNodeDuration(u, nn.duration);
        }
        if (r.edges != null) for (EdgeJson ee : r.edges) {
            int u = g.idToIndex.get(ee.from);
            int v = g.idToIndex.get(ee.to);
            g.addEdge(u, v, ee.weight);
        }
        return new ParseResult(g, null);
    }
}