package graph.scc;
import java.util.*;

public class SCCResult {
    public final int[] compId;
    public final List<List<Integer>> components;
    public SCCResult(int n) {
        compId = new int[n];
        Arrays.fill(compId, -1);
        components = new ArrayList<>();
    }
    public int compCount() { return components.size(); }
}