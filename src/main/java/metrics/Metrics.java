package metrics;

public class Metrics {
    private long start;
    private long elapsed;
    public long dfsVisits;
    public long edgesVisited;
    public long queuePushes;
    public long queuePops;
    public long relaxations;
    public void start() { start = System.nanoTime(); }
    public void stop() { elapsed = System.nanoTime() - start; }
    public long nanos() { return elapsed; }
}