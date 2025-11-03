# Assignment 4: Graph Algorithms for Smart City Scheduling

## Overview

This project implements graph algorithms for task scheduling in smart city networks. I implemented Tarjan's algorithm for finding strongly connected components, Kahn's algorithm for topological sorting, and path algorithms for DAGs.

The main idea is to take a graph with task dependencies, find cycles using SCC, compress them into single nodes, then use topological sort to find valid execution order, and finally compute shortest/longest paths for optimization.

## How to Build and Run

### What you need
- Java 11 or higher
- Maven 3.6+

### Compiling
```
mvn clean compile
```

### Running tests
```
mvn test
```

### Running the program

To process all graphs in data/ folder:
```
mvn exec:java -Dexec.mainClass="app.Main" -Dexec.args="all"
```

To process just one file:
```
mvn exec:java -Dexec.mainClass="app.Main" -Dexec.args="data/small-1.json"
```

You can also specify which node to use as source:
```
mvn exec:java -Dexec.mainClass="app.Main" -Dexec.args="data/small-1.json 0"
```

Results get saved to `results/results.csv` with timing and operation counts.

## Project Structure

```
project/
├── src/main/java/
│   ├── app/Main.java (main entry point)
│   ├── graph/
│   │   ├── scc/ (SCC algorithms)
│   │   │   ├── SCCTarjan.java
│   │   │   ├── SCCKosaraju.java
│   │   │   └── SCCResult.java
│   │   ├── topo/ (topological sort)
│   │   │   ├── TopologicalSorter.java
│   │   │   └── CondensationGraph.java
│   │   ├── dagsp/ (shortest/longest paths)
│   │   │   ├── DAGShortestPaths.java
│   │   │   └── DAGLongestPath.java
│   │   └── util/
│   │       ├── Graph.java
│   │       └── Edge.java
│   ├── io/ (reading JSON, writing CSV)
│   │   ├── GraphIO.java
│   │   └── ResultCSVWriter.java
│   └── metrics/Metrics.java
├── src/test/java/graph/ (JUnit tests)
├── data/ (input JSON files)
├── results/results.csv (output)
└── pom.xml
```

## Dataset Information

I created 9 test graphs following the assignment requirements: 3 small, 3 medium, 3 large. All of them use edge weights to represent task durations.

### Summary Table

| Dataset | Vertices | Edges | Has Cycles? | Source Node |
|---------|----------|-------|-------------|-------------|
| small-1.json | 6 | 6 | false          | 0 |
| small-2.json | 7 | 7 | true        | 3 |
| small-3.json | 8 | 8 | true        | 5 |
| medium-1.json | 12 | 13 | true        | 9 |
| medium-2.json | 15 | 17 | false          | 0 |
| medium-3.json | 18 | 20 | true        | 6 |
| large-1.json | 25 | 25 | false          | 0 |
| large-2.json | 30 | 31 | true        | 6 |
| large-3.json | 40 | 44 | false       | 0 |

### Details about each graph

**Small graphs:**

**small-1.json** - Simple DAG
- 6 vertices, 6 edges
- Basically a chain: 0 -> 1 -> 2 -> 3 -> 4 -> 5
- Has one shortcut edge 0 -> 2
- No cycles, straightforward

**small-2.json** - One cycle
- 7 vertices, 7 edges
- Has a cycle with 3 nodes: 0 -> 1 -> 2 -> 0
- Rest is just a path 3 -> 4 -> 5 -> 6
- Starts from node 3

**small-3.json** - Two cycles
- 8 vertices, 8 edges
- First cycle: 0 and 1 pointing to each other
- Second cycle: 2 -> 3 -> 4 -> 2
- Plus a separate path 5 -> 6 -> 7

**Medium graphs:**

**medium-1.json**
- 12 vertices, 13 edges, has cycles
- Two separate cycles: (0 -> 1 -> 2 -> 0) and (6 -> 7 -> 8 -> 6)
- They're connected by a path 2 -> 3 -> 4 -> 5 -> 6
- Also has path 9 -> 10 -> 11 that connects via 1 -> 9

**medium-2.json**
- 15 vertices, 17 edges, pure DAG
- Has diamond-like structures where paths split and merge
- Starts at 0, splits to 1 and 2, then merges at 3
- Similar pattern happens later in the graph
- Some shortcut edges like 1 -> 7 and 4 -> 9

**medium-3.json**
- 18 vertices, 20 edges, has cycles
- Two cycles again: (0 -> 1 -> 2 -> 0) and (3 -> 4 -> 5 -> 3)
- Diamond pattern with vertices 6,7,8,9
- Long chain at the end: 10 -> 11 -> 12 -> 13 -> 14 -> 15 -> 16 -> 17
- Has a feedback edge 10 -> 6 which makes it interesting

**Large graphs:**

**large-1.json**
- 25 vertices, 25 edges, no cycles
- Mostly just a long chain 0 -> 1 -> 2 -> ... -> 24
- One shortcut: 5 -> 12 (skips a bunch of nodes)
- Edge weights vary from 1 to 5

**large-2.json**
- 30 vertices, 31 edges, has cycles
- Two cycles at the beginning: (0 -> 1 -> 2 -> 0) and (3 -> 4 -> 5 -> 3)
- Then a long path from 6 to 29
- Two shortcuts: 2 -> 14 and 10 -> 20
- Starts at node 6

**large-3.json**
- 40 vertices, 44 edges, pure DAG
- Main path goes 0 -> 1 -> 2 -> ... -> 39
- Several shortcut edges:
    - 2 -> 10 (skip 8 nodes)
    - 10 -> 20 (skip 10 nodes)
    - 20 -> 30 (skip 10 nodes)
    - 5 -> 15 and 15 -> 25
- These shortcuts create multiple ways to reach the end

All graphs use "edge" weight model where the weights are on the edges, not the nodes.

## Implementation

### SCC - Tarjan's Algorithm

I used Tarjan's algorithm to find strongly connected components. It does DFS and keeps track of discovery time and low-link values for each vertex.

The basic idea is: when you're doing DFS, you keep a stack. If a node's low-link value equals its discovery time, then that node is the root of an SCC, and you pop everything above it from the stack.

Time complexity: O(V + E)
Space: O(V) for the arrays and stack

I track how many DFS visits happen and how many edges get visited.

Code is in SCCTarjan.java. There's also SCCKosaraju.java but I mainly use Tarjan since it's one pass instead of two.

### Condensation Graph

After finding SCCs, I build a condensation graph where each SCC becomes one big node. This is in CondensationGraph.java.

The condensation removes edges inside components and only keeps edges between different components. It also adds up the node durations inside each component.

This is important because it turns a cyclic graph into a DAG, which lets us do topological sort and path algorithms.

### Topological Sort - Kahn's Algorithm

For topological sorting I used Kahn's algorithm which is queue-based.

How it works:
1. Count in-degrees for all nodes
2. Put all nodes with in-degree 0 into a queue
3. Keep taking nodes from queue, and reduce in-degrees of their neighbors
4. If a neighbor's in-degree becomes 0, add it to queue
5. If we process all V nodes, we have a valid order. Otherwise there's a cycle.

Time: O(V + E)

I count queue pushes and pops as operations.

Implementation is in TopologicalSorter.java. It returns null if there's a cycle.

### Shortest Paths in DAG

For shortest paths I use the standard DP approach on topological order.

Algorithm:
1. Do topological sort first
2. Set distance to source as 0, everything else as infinity
3. Go through nodes in topological order
4. For each node, relax all its outgoing edges

Time: O(V + E)

This only works on DAGs because we need topological order. I track relaxations (edge updates) as operations.

Code is in DAGShortestPaths.java. It also has a reconstruct() method to get the actual path using parent pointers.

### Longest Paths in DAG

Longest path is basically the same as shortest path but you maximize instead of minimize.

I initialize distances to negative infinity and use max during relaxation instead of min. This finds the critical path which is useful for project scheduling.

Time: O(V + E)

Implementation in DAGLongestPath.java.

## Results

Here's what I got when running on all 9 graphs:

| Dataset | Vertices | Edges | Cyclic | Algorithm | Time (ms) | Operations |
|---------|----------|-------|--------|-----------|-----------|------------|
| large-1 | 25 | 25 | false  | SCC | 0.704 | 50 |
| large-1 | 25 | 25 | false  | Topo | 0.380 | 50 |
| large-1 | 25 | 25 | false  | DAGSP | 1.363 | 49 |
| large-2 | 30 | 31 | true    | SCC | 0.029 | 61 |
| large-2 | 30 | 31 | true    | Topo | 0.026 | 52 |
| large-2 | 30 | 31 | true    | DAGSP | 0.036 | 47 |
| large-3 | 40 | 44 | false  | SCC | 0.042 | 84 |
| large-3 | 40 | 44 | false  | Topo | 0.021 | 80 |
| large-3 | 40 | 44 | false  | DAGSP | 0.056 | 83 |
| medium-1 | 12 | 13 | true    | SCC | 0.014 | 25 |
| medium-1 | 12 | 13 | true    | Topo | 0.006 | 16 |
| medium-1 | 12 | 13 | true    | DAGSP | 0.014 | 4 |
| medium-2 | 15 | 17 | false  | SCC | 0.015 | 32 |
| medium-2 | 15 | 17 | false  | Topo | 0.009 | 30 |
| medium-2 | 15 | 17 | false  | DAGSP | 0.023 | 30 |
| medium-3 | 18 | 20 | true    | SCC | 0.053 | 38 |
| medium-3 | 18 | 20 | true    | Topo | 0.009 | 20 |
| medium-3 | 18 | 20 | true    | DAGSP | 0.023 | 14 |
| small-1 | 6 | 6 | false  | SCC | 0.021 | 12 |
| small-1 | 6 | 6 | false  | Topo | 0.006 | 12 |
| small-1 | 6 | 6 | false  | DAGSP | 0.031 | 10 |
| small-2 | 7 | 7 | true   | SCC | 0.007 | 14 |
| small-2 | 7 | 7 | true    | Topo | 0.012 | 10 |
| small-2 | 7 | 7 | true    | DAGSP | 0.009 | 6 |
| small-3 | 8 | 8 | true    | SCC | 0.009 | 16 |
| small-3 | 8 | 8 | true    | Topo | 0.006 | 10 |
| small-3 | 8 | 8 | true    | DAGSP | 0.010 | 4 |

## Analysis

### Does it match O(V+E)?

**SCC:** Operations are roughly 2*E which makes sense since each edge gets checked during DFS. For 25 edges I got 50 ops, for 31 edges got 61 ops, for 44 edges got 84 ops. So yeah, it's linear.

**Topological Sort:** Operations are close to V+E. For the 25 vertex graph with 25 edges, I got 50 operations. For 30V/31E got 52 ops. Looks good.

**DAG Paths:** Number of relaxations varies but is always less than E. Depends on how connected the graph is. Sparse graphs have fewer relaxations.

### Performance

Everything runs really fast - under 2ms even for the biggest graph.

The first run (large-1) took longer (0.704ms for SCC) but that's probably just JVM warmup. After that, similar sized graphs run much faster like 0.029-0.042ms.

Small graphs: around 0.01ms
Medium graphs: around 0.02ms  
Large graphs: 0.02-0.7ms (with warmup)

### Cyclic vs Acyclic Graphs

For cyclic graphs, SCC finds the cycles and compresses them. After compression, both cyclic and acyclic graphs work the same way.

Interesting thing: after compression, cyclic graphs sometimes have FEWER operations. Like medium-1 (cyclic) only needed 4 DAGSP relaxations after compression.

### Graph Structure Effects

Sparse graphs (where edges ≈ vertices) are faster. All my test graphs are sparse with E/V ratio around 1.0-1.1.

When there are big SCCs, compression helps more. Small SCCs or pure DAGs don't benefit much from compression but still work fine.

### Bottlenecks

**SCC:** The DFS traversal is the slow part. More edges = more work. Already optimal though.

**Topological Sort:** Super fast, just queue operations. Consistently the fastest algorithm.

**Path Algorithms:** Edge relaxation takes time. More edges or complex structures = more relaxations.

## What I Learned

### When to use each algorithm

**Tarjan SCC:**
- Use when you need to find cycles
- Good for validating dependencies before scheduling
- One pass makes it efficient

**Topological Sort:**
- Best for getting execution order
- Fast and simple
- Use this as the main scheduling engine

**DAG Shortest Path:**
- Find minimum time/cost paths
- Much faster than Dijkstra for DAGs
- Good for optimization problems

**DAG Longest Path:**
- Find critical path in projects
- Identifies bottlenecks
- Useful for project management

### Practical Observations

All the algorithms are O(V+E) in theory and that matched what I saw in practice. Operation counts scaled linearly with graph size.

For small graphs (under 10 nodes), everything is instant so algorithm choice doesn't matter much.

For medium graphs (10-20 nodes), you can see the differences but still very fast.

For large graphs (20-50 nodes), still fast enough for real-time use. Even 50 nodes with 200 edges would probably be fine.

The whole pipeline (SCC + Topo + Shortest + Longest) runs in under 1ms for 40 node graphs, so this could work in a real scheduling system.

### Some Issues I Had

At first I had trouble with the SCC algorithm - wasn't handling the stack correctly and was getting wrong component sizes. Fixed it by carefully following the pseudocode from lecture notes.

Topological sort was pretty straightforward once I understood the in-degree approach.

Path reconstruction took a bit to get right - had to make sure parent pointers were set correctly during relaxation.

Testing helped catch bugs, especially edge cases like empty graphs and self-loops.

## Testing

I wrote JUnit tests for the main functionality:

**SCCTest.java** - Tests simple cycle and pure DAG
**TopoTest.java** - Tests basic DAG, multiple sources, cycle detection  
**DAGSPTest.java** - Tests shortest path with edge weights and longest path
**EdgeCasesTest.java** - Tests empty graph, single node, self-loops, parallel edges

All tests pass. You can run them with:
```
mvn test
```

## Conclusion

The implementation works and meets all the requirements. All algorithms run in O(V+E) time as expected. The results show that this approach is practical for real scheduling systems.

Tarjan's SCC is good for finding cycles, Kahn's topological sort is fast for ordering, and the path algorithms work well for optimization. Together they handle both cyclic and acyclic dependency graphs.

For the datasets I tested (up to 40 vertices), performance was excellent. Everything completed in under 2ms which is way more than fast enough for practical use.