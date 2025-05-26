// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


// Heap class to manage a priority queue for Dijkstra's algorithm and Prim's MST
class Heap
{
    private int[] a;	   // heap array
    private int[] hPos;	   // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v

    private int N;         // heap size
   
    // The heap constructor gets passed from the Graph:
    //    1. maximum heap size
    //    2. reference to the dist[] array
    //    3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) {
        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;
    }

    // Method to check if the heap is empty
    public boolean isEmpty() {
        return N == 0;
    }

    // Sift-up method to maintain heap properties
    public void siftUp(int k) {
        int v = a[k];
        int priority = dist[v];

        a[0] = 0;    // consider 0 as a kind of dummy heap value
        dist[0] = Integer.MAX_VALUE; // set initial priority to maximum

        // Moving the vertex upwards in the heap until heap property is satisfied
        while(k > 1 && priority < dist[a[k / 2]]) {
            int parent = a[k / 2]; // parent of the current vertex
            a[k] = parent; // move parent down
            hPos[parent] = k; // update heap position of parent
            k = k / 2; // move up the heap
        }

        a[k] = v; // place the vertex at the correct position
        hPos[v] = k; // update heap position
    }

    // Sift-down method to maintain heap properties
    public void siftDown(int k) {   
        int v = a[k];
        int j;
        int priority = dist[v];
       

        // Keep moving the vertex down until heap property is satisfied
        while (k <= N / 2) {
            j = 2 * k; // left child index
            
            // Compare with right child if exists
            if (j < N && dist[a[j]] > dist[a[j + 1]]) {
                j++;
            }

            // Stop if current vertex priority is smaller than both children
            if (priority <= dist[a[j]]) {
                break;
            }

            a[k] = a[j]; // move child up
            hPos[a[j]] = k; // update heap position of child
            k = j; // move down the heap
        }

        a[k] = v; // place the vertex at the correct position
        hPos[v] = k; // update heap position
    }

    // Method to insert a new vertex into the heap
    public void insert(int x) {
        a[++N] = x; // increment heap size and add vertex at the end
        siftUp(N); // maintain heap property
    }

    // Method to remove the vertex with the smallest priority from the heap
    public int remove() {   
        int v = a[1]; // get the root (min vertex)
        hPos[v] = 0; // v is no longer in heap
        a[N + 1] = 0;  // put null node into empty spot
        
        a[1] = a[N--]; // move last vertex to root and reduce heap size
        siftDown(1); // restore heap property
        
        return v; // removed vertex
    }
}


// Graph class representing the graph structure
class Graph {
    class Node {
        public int vert;
        public int wgt;
        public Node next;
    }
    
    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;
    
    // used for traversing graph
    private static final int WHITE = 0, GREY = 1, BLACK = 2;
    private int[] colour; // node colours for DFS/BFS
    private int[] parent; // parent nodes for DFS/BFS
    private int[] d; // discovery time for DFS
    private int[] f; // finish time (DFS)
    private int time; // global time counter for DFS
    
    
    // default constructor that initalises the graph from a file
    public Graph(String graphFile)  throws IOException {
        // Vertices
        int u, v;

        // Edges and Weight
        int e, wgt;

        // Node use for assignment to adjacency lists
        Node t;

        FileReader fr = new FileReader(graphFile);
		BufferedReader reader = new BufferedReader(fr);
	           
        String splits = " +";  // split by one or more whitespace
		String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);

        
        V = Integer.parseInt(parts[0]); // number of vertices
        E = Integer.parseInt(parts[1]); // number of edges
        
        // create sentinel node
        z = new Node(); 
        z.next = z;
        
        // create adjacency lists, initialised to sentinel node z       
        adj = new Node[V + 1];
        mst = new int[V + 1];

        for(v = 1; v <= V; ++v) {
            adj[v] = z;
        }               
        
        // read the edges
        System.out.println("\nReading edges from text file");

        for(e = 1; e <= E; ++e) {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            wgt = Integer.parseInt(parts[2]);
            
            
            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));   

            // Append nodes to the end of the adjacency lists u and v
            t = new Node();
            t.vert = v;
            t.wgt = wgt;
            t.next = adj[u];
            adj[u] = t;
            
            t = new Node();
            t.vert = u;
            t.wgt = wgt;
            t.next = adj[v];
            adj[v] = t;
        }
    }
   
    // convert vertex into char for pretty printing
    // Had to modify the original toChar method to change the return type so that I can override the method for large road network graphs - still does the same thing
    protected String toChar(int u) {  
        return String.valueOf((char)(u + 64));  
    }
    
    // method to display the graph representation
    public void display() {
        int v;
        Node n;
        
        System.out.println();

        for(v = 1; v <= V; ++v) {
            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != z; n = n.next) {
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->"); 
            }
            System.out.print(" Z"); // mark the end of the adjacency list
        }
        System.out.println("\n");
    }

    // method to initialise Depth First Traversal of Graph
    public void DF(int s) {
        colour = new int[V + 1];
        parent = new int[V + 1];
        d = new int[V + 1];
        f = new int[V + 1];
        time = 0;

        // Initialize the vertices
        for (int u = 1; u <= V; u++) {
            colour[u] = WHITE;
            parent[u] = 0;
        }

        System.out.println("\nStarting Depth-First Search (Cormen) from " + toChar(s));

        // Perform DFS for all unvisited vertices
        for (int u = s; u <= V; u++) {
            if (colour[u] == WHITE) {
                dfVisit(u);
            }
        }
    }

    // Recursive Depth First Traversal
    private void dfVisit(int u) {
        colour[u] = GREY; // mark u as discovered
        d[u] = ++time; // set discovery time
        System.out.println("Discovered " + toChar(u) + " at time " + d[u]);

        // Visit all adjacent vertices of u
        for (Node n = adj[u]; n != z; n = n.next) {
            int v = n.vert;
            // If v is unvisited, visit it
            if (colour[v] == WHITE) {
                parent[v] = u;
                dfVisit(v); // recursively visit v
            }
        }

        colour[u] = BLACK; // mark u as fully explored
        f[u] = ++time; // set finish time
        System.out.println("Finished " + toChar(u) + " at time " + f[u]);
    }

    // Method for Breadth First Search
    public void BF(int s) {
        colour = new int[V + 1];
        d = new int[V + 1];
        parent = new int[V + 1];

        // Initialize the vertices
        for (int u = 1; u <= V; ++u) {
            colour[u] = WHITE;
            d[u] = Integer.MAX_VALUE;
            parent[u] = 0;
        }

        colour[s] = GREY; // mark source vertex as discovered
        d[s] = 0; // set source vertex distance to 0
        parent[s] = 0;

        Queue<Integer> queue = new LinkedList<>();
        queue.add(s); // enqueue the source vertex

        System.out.println("\n\nStarting Breadth-First Search (Cormen) from " + toChar(s));

        // Perform BFS as long as the queue is not empty
        while (!queue.isEmpty()) {
            int u = queue.poll(); // dequeue the next vertex
            System.out.println("Visited " + toChar(u) + " with a distance of " + d[u] + " edge(s) away from " + toChar(s));

            // Visit all adjacent vertices of u
            for (Node n = adj[u]; n != z; n = n.next) {
                int v = n.vert;
                if (colour[v] == WHITE) {
                    colour[v] = GREY; // mark v as discovered
                    d[v] = d[u] + 1; // set distance of v
                    parent[v] = u; // set parent of v
                    queue.add(v); // enqueue v
                }
            }

            colour[u] = BLACK; // mark u as fully explored
        }
    }

    // Method for Prim's Minimum Spanning Tree
	public void MST_Prim(int s) {
        int v, u;
        int wgt, wgt_sum = 0;
        int[]  dist, parent, hPos;
        Node t;

        // Initialize arrays for distance, parent, and heap position
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];

        // Initialize all distances to infinity, parents to null (0), and heap positions to 0
        for (v = 1; v <= V; ++v) {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = 0;
            hPos[v] = 0;
            mst[v] = 0;
        }
        
        // Start from source vertex
        dist[s] = 0;
        
        Heap h =  new Heap(V, dist, hPos);

        // Insert the source vertex into the heap with a distance of 0
        h.insert(s);

        System.out.println("\n\nStarting MST Prim from vertex " + toChar(s));
        
        // Loop until all vertices are added to the MST
        while (!h.isEmpty()) {
            // Remove the vertex with the smallest distance
            v = h.remove();

            // Add the weight of selected vertex to total weight
            wgt_sum += dist[v];

            // Mark as visited by negating the distance
            dist[v] = -dist[v];

            // Store the parent vertex of v in the MST
            mst[v] = parent[v];

            // Traverse all adjacent vertices of v
            for (t = adj[v]; t != z; t = t.next) {
                u = t.vert;
                wgt = t.wgt;

                // If smaller weight edge found
                if (wgt < dist[u]) { 
                    dist[u] = wgt; // Update distance to new smaller weight
                    parent[u] = v; // Update the parent of u to v

                    // If u not in the heap, insert it
                    if (hPos[u] == 0) { 
                        h.insert(u);
                    
                    // If already in heap, update position
                    } else {
                        h.siftUp(hPos[u]);
                    }
                }
            }
        }

        showMST();
        System.out.println("Total Weight of MST = " + wgt_sum + "\n");
	}
    
    // Method to display the edges of the MST 
    public void showMST() {
        System.out.println("Minimum Spanning Tree Edges:");
        System.out.println("Vertex\tParent\tWeight");

        for(int v = 1; v <= V; ++v) {
            // Skip root node (no parent)
            if (mst[v] != 0) {
                int u = mst[v];
                int weight = -1;

                // Find the weight of edge u-v in adjacency list
                for (Node n = adj[u]; n != z; n = n.next) {
                    if (n.vert == v) {
                        weight = n.wgt;
                        break;
                    }
                }

                System.out.println(toChar(v) + "\t" + toChar(u) + "\t" + weight);

            }
        }
        System.out.println("");
    }

    // Method for Dijkstra's Shortest Path Tree
    public void SPT_Dijkstra(int s) {
        int v, u;
        int wgt;
        Node t;
    
        // Initialize arrays for distance, parent, and heap position
        int[] dist = new int[V + 1];
        int[] parent = new int[V + 1];
        int[] hPos = new int[V + 1];
    
        // Initialize all distances to infinity, parents to null (0), and heap positions to 0
        for (v = 1; v <= V; ++v) {
            dist[v] = Integer.MAX_VALUE;
            parent[v] = 0;
            hPos[v] = 0;
        }
    
        // Set the source vertex distance to 0
        dist[s] = 0;
    
        // Create a new heap 
        Heap pq = new Heap(V, dist, hPos);
        pq.insert(s);

        System.out.println("\nStarting Djikstra's SPT from vertex " + toChar(s));
    
        // Main Dijkstra algorithm loop; continue until all vertices are processed
        while (!pq.isEmpty()) {
            // Remove vertex with smallest distance
            v = pq.remove();
    
            // Process all adjacent vertices of v
            for (t = adj[v]; t != z; t = t.next) {
                u = t.vert;
                wgt = t.wgt;
    
                // If distance to u through v is shorter
                if (dist[v] != Integer.MAX_VALUE && (dist[v] + wgt) < dist[u]) {
                    dist[u] = dist[v] + wgt; // Update the distance to u
                    parent[u] = v; // Update the parent of u to be v

                    // If u is not in the heap, insert it
                    if (hPos[u] == 0) {
                        pq.insert(u);
                    }
                    // If u is already in the heap, sift it up - update its position
                    else {
                        pq.siftUp(hPos[u]);
                    }
                }
            }
        }
        
        // Display the shortest path tree
        System.out.println("Vertex\tParent\tDistance from " + toChar(s));

        for (v = 1; v <= V; ++v) {
            if (v != s) { // Skip the source vertex
                System.out.println(toChar(v) + "\t" + toChar(parent[v]) + "\t" + dist[v]);
            }
        }
    }
}

public class GraphLists {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for the graph file name
        System.out.print("Enter the name of the graph file: ");
        String file = scanner.nextLine();

        // Prompt user for the starting vertex
        System.out.print("Enter the starting vertex (as a number): ");
        int s = scanner.nextInt();

        // Create a graph instance using the given file name
        Graph g = new Graph(file);
        
        // Display the Adjacency lists created 
        g.display();

        // Perform DFS on graph
        g.DF(s);

        // Perform BFS on graph
        g.BF(s);

        // Perform Prim's MST on graph
        g.MST_Prim(s);
        
        // Perform Dijkstra's SPT on graph
        g.SPT_Dijkstra(s);
        
        scanner.close();
    }
}