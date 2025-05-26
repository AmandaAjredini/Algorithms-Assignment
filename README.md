# Algorithms-Assignment
My assignment for algorithms module detailing the works of Depth First Search, Breadth First Search, Dijkstra's SPT &amp; Prim's MST in Java. It was implemented using Adjacency Lists - a graph representation where each vertex stores a list of its adjacent (connected) vertices.

## Depth First Search
DFS was completed recursively using Cormen's pseudocode and idea of using colours to mark visited vertices and edges.

- WHITE - not discovered yet
- GREY - visited but not fully discovered
- BLACK - fully discovered

## Breadth First Search
BFS was also completed using Cormen's pseudocode with the idea of using colours. It uses a Queue data structure to process vertices in level order.

## Dijkstra's Shortest Path Tree
This was implemented using a min-heap priority queue along with three arrays:

- parent[] – tracks the predecessor of each vertex in the SPT
- distance[] – stores the shortest known distance from the source
- hPos[] – tracks positions of vertices in the heap for efficient updates

The vertex with the smallest known distance is repeatedly selected until all reachable vertices have been processed.

## Prim's Minimum Spanning Tree
Prim’s algorithm was implemented similarly to Dijkstra’s, but instead of calculating shortest paths from a source, it focuses on building the Minimum Spanning Tree. At each step, it chooses the smallest weight edge that connects a vertex in the MST to one outside it. Like Dijkstra's, it uses a min-heap for efficient selection.

## Testing
The algorithms were initially tested on a small graph, wGraph1.txt (see attached), to verify correctness and expected behavior. Once validated, they were tested on a larger, real-world graph.

## Real-World Graph
After confirming correct behavior on the small graph, I extracted a weighted road network of New York City using the OSMNx Python library. I then timed Dijkstra’s algorithm on this graph. Surprisingly, even with over 55,000 vertices, it completed in approximately 2 seconds, demonstrating the efficiency of the implementation.

## Time Complexity
Depth First Search (DFS): Runs in O(V + E) time as each vertex and edge is explored once.

Breadth First Search (BFS): Also runs in O(V + E) time due to level-order traversal using a queue.

Dijkstra’s Algorithm: Runs in O((V + E) log V) time when using a min-heap priority queue.

Prim’s Algorithm: Also runs in O((V + E) log V) time with a min-heap, similar to Dijkstra’s.
