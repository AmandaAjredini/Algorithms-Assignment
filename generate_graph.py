import osmnx as ox
import networkx as nx

# 1. Load road network for NYC (you can change this to any city or bounding box)
print("Downloading road network for New York City...")
G = ox.graph_from_place("New York City, New York, USA", network_type='drive')

# 2. Convert to undirected graph (simplified)
G = nx.Graph(G)

# 3. Relabel nodes with integers (for easier indexing in Java)
print("Relabeling nodes...")
node_mapping = {node: idx + 1 for idx, node in enumerate(G.nodes())}
G = nx.relabel_nodes(G, node_mapping)

# 4. Prepare output for Java program
filename = "nyc_road_network.txt"
print(f"Writing to {filename}...")

with open(filename, 'w') as f:
    V = G.number_of_nodes()
    E = G.number_of_edges()
    f.write(f"{V} {E}\n")
    
    for u, v, data in G.edges(data=True):
        weight = int(data.get("length", 1))  # default weight is 1 if missing
        f.write(f"{u} {v} {weight}\n")

print("Done! File saved as nyc_road_network.txt")