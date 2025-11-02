package app;
import io.GraphIO;
import io.ResultCSVWriter;
import graph.dagsp.DAGLongestPath;
import graph.dagsp.DAGShortestPaths;
import graph.scc.SCCTarjan;
import graph.scc.SCCResult;
import graph.topo.CondensationGraph;
import graph.topo.TopologicalSorter;
import graph.util.Graph;
import metrics.Metrics;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        Path dataDir = Paths.get("data");
        if (args.length >= 1 && !"all".equalsIgnoreCase(args[0])) {
            Path p = Paths.get(args[0]);
            runSingle(p, args.length >= 2 ? args[1] : null);
            return;
        }
        if (!Files.exists(dataDir) || !Files.isDirectory(dataDir)) {
            System.err.println("data/ directory not found");
            return;
        }
        ResultCSVWriter csv = new ResultCSVWriter("results/results.csv");
        csv.ensureHeader();
        List<Path> files;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dataDir, "*.json")) {
            files = new ArrayList<>();
            for (Path p : ds) files.add(p);
        }
        files.sort(Comparator.comparing(Path::getFileName));
        int gid = 1;
        for (Path p : files) {
            try {
                ProcessResult r = analyze(p, null);
                csv.appendRow(p.getFileName().toString(), gid, r.n, r.edges, r.cyclic, "SCC", r.sccTimeMs, r.sccOpCount);
                csv.appendRow(p.getFileName().toString(), gid, r.n, r.edges, r.cyclic, "Topo", r.topoTimeMs, r.topoOpCount);
                csv.appendRow(p.getFileName().toString(), gid, r.n, r.edges, r.cyclic, "DAGSP", r.dagTimeMs, r.dagOpCount);
                System.out.println("Processed " + p.getFileName());
            } catch (Exception ex) {
                System.err.println("Error processing " + p.getFileName() + ": " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
            gid++;
        }
        System.out.println("Results -> results/results.csv");
    }
    private static void runSingle(Path p, String sourceArg) throws Exception {
        ProcessResult r = analyze(p, sourceArg);
        System.out.println("File: " + p.getFileName());
        System.out.println("Vertices: " + r.n + " Edges: " + r.edges + " Cyclic: " + r.cyclic);
        System.out.printf("SCC: time=%.3fms opCount=%d%n", r.sccTimeMs, r.sccOpCount);
        System.out.printf("Topo: time=%.3fms opCount=%d%n", r.topoTimeMs, r.topoOpCount);
        System.out.printf("DAGSP: time=%.3fms opCount=%d%n", r.dagTimeMs, r.dagOpCount);
    }
    private static ProcessResult analyze(Path p, String sourceArg) throws Exception {
        long totalStart = System.nanoTime();
        GraphIO.ParseResult pr = GraphIO.readFromJson(p);
        Graph g = pr.graph;
        int n = g.n;
        int edgeCount = 0;
        for (int i = 0; i < n; i++) edgeCount += g.adj[i].size();
        int sourceNode = 0;
        if (sourceArg != null) {
            try {
                int v = Integer.parseInt(sourceArg);
                if (v >= 0 && v < g.n) sourceNode = v;
                else if (g.idToIndex.containsKey(v)) sourceNode = g.idToIndex.get(v);
            } catch (Exception ignored) {}
        } else if (pr.sourceIndex != null) {
            sourceNode = pr.sourceIndex;
        }
        Metrics mScc = new Metrics();
        long sStart = System.nanoTime();
        SCCTarjan tarjan = new SCCTarjan(g, mScc);
        SCCResult scc = tarjan.run();
        long sEnd = System.nanoTime();
        double sccTimeMs = (sEnd - sStart) / 1e6;
        long sccOpCount = mScc.dfsVisits + mScc.edgesVisited;
        CondensationGraph cg = new CondensationGraph(g, scc);
        boolean cyclic = scc.components.stream().anyMatch(c -> c.size() > 1);
        Metrics mTopo = new Metrics();
        long tStart = System.nanoTime();
        int[] compOrder = TopologicalSorter.kahnOrder(cg.adj, mTopo);
        long tEnd = System.nanoTime();
        double topoTimeMs = (tEnd - tStart) / 1e6;
        long topoOpCount = mTopo.queuePushes + mTopo.queuePops;
        int sourceComp = scc.compId[sourceNode];
        Metrics mDag = new Metrics();
        long dStart = System.nanoTime();
        DAGShortestPaths.Result sp = DAGShortestPaths.shortest(cg.adj, cg.compDuration, g.edgeWeightModel, sourceComp, mDag);
        DAGLongestPath.Result lp = DAGLongestPath.longest(cg.adj, cg.compDuration, g.edgeWeightModel, sourceComp, mDag);
        long dEnd = System.nanoTime();
        double dagTimeMs = (dEnd - dStart) / 1e6;
        long dagOpCount = mDag.relaxations;
        double bestVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < cg.compN; i++) {
            if (lp.dist[i] != Double.NEGATIVE_INFINITY && lp.dist[i] > bestVal) bestVal = lp.dist[i];
        }
        long totalEnd = System.nanoTime();
        double totalMs = (totalEnd - totalStart) / 1e6;
        ProcessResult res = new ProcessResult();
        res.dataset = p.getFileName().toString();
        res.n = n;
        res.edges = edgeCount;
        res.sccCount = scc.compCount();
        res.cyclic = cyclic;
        res.sourceComp = sourceComp;
        res.sccTimeMs = sccTimeMs;
        res.sccOpCount = sccOpCount;
        res.topoTimeMs = topoTimeMs;
        res.topoOpCount = topoOpCount;
        res.dagTimeMs = dagTimeMs;
        res.dagOpCount = dagOpCount;
        res.totalTimeMs = totalMs;
        res.criticalLength = bestVal == Double.NEGATIVE_INFINITY ? null : bestVal;
        return res;
    }
    private static class ProcessResult {
        String dataset;
        int n;
        int edges;
        int sccCount;
        boolean cyclic;
        int sourceComp;
        double sccTimeMs;
        long sccOpCount;
        double topoTimeMs;
        long topoOpCount;
        double dagTimeMs;
        long dagOpCount;
        double totalTimeMs;
        Double criticalLength;
    }
}