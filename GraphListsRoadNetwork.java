import java.io.IOException;
import java.util.Scanner;

// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

class Graph2 extends Graph{
    public Graph2(String file) throws IOException {
        super(file);
    }
    
    // Instead of converting vertices into chars for pretty printing, display them as numbers as there are more vertices than characters in the large road network
    @Override
    public String toChar(int u) {  
        return Integer.toString(u);
    }
}

public class GraphListsRoadNetwork {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for the graph file name
        System.out.print("Enter the name of the graph file: ");
        String file = scanner.nextLine();

        // Prompt user for the starting vertex
        System.out.print("Enter the starting vertex (as a number): ");
        int s = scanner.nextInt();

        // Create a graph instance using the given file name
        Graph2 g = new Graph2(file);
        
        // Measure start time and memory before Dijkstra
        long startTime = System.nanoTime();
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Run garbage collector to get more accurate memory
        long startMemory = runtime.totalMemory() - runtime.freeMemory();

        // Run Dijkstra
        g.SPT_Dijkstra(s);

        // Measure end time and memory after Dijkstra
        long endTime = System.nanoTime();
        long endMemory = runtime.totalMemory() - runtime.freeMemory();

        // Calculate execution time and memory used
        long runtimeMs = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        long usedMemoryKB = (endMemory - startMemory) / 1024; // Convert to KB

        // Display performance report
        System.out.println("\n--- Performance Report ---");
        System.out.println("Execution time: " + runtimeMs + " ms");
        System.out.println("Memory used: " + usedMemoryKB + " KB");
        
        scanner.close();
    }
}