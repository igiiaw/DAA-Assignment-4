package graph.util;

public class Edge {
    public final int from;
    public final int to;
    public final double weight;
    public Edge(int from, int to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}