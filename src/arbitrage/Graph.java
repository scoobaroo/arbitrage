package arbitrage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Graph {
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    double inf = Double.POSITIVE_INFINITY;
    public Graph(ArrayList<Vertex> vertices, ArrayList<Edge> edges) {
        super();
        this.vertices = vertices;
        this.edges = edges;
    }

    // The main function that finds shortest distances from src
    // to all other vertices using Bellman-Ford algorithm.  The
    // function also detects negative weight cycle
    void BellmanFord(Graph graph, Vertex source)
    {

        Vertex src = source;
        int i,j;
        vertices = graph.vertices;
        edges = graph.edges;
        HashMap<Vertex, Double> dist = new HashMap<Vertex, Double>(vertices.size());
        // Step 1: Initialize distances from src to all other
        // vertices as INFINITE
        for(i=0;i<vertices.size();i++){
            dist.put(vertices.get(i),inf);
        }

        dist.put(src,0.0);
        // Step 2: Relax all edges |V| - 1 times. A simple
        // shortest path from src to any other vertex can
        // have at-most |V| - 1 edges
        for (i = 0; i < vertices.size() -1 ; i++) {
            for (j = 0; j < edges.size(); j++) { //here i am calculating the shortest path
                Vertex u = edges.get(j).src;
                Vertex v = edges.get(j).dest;
                Edge e = edges.get(j);
                if (dist.get(u) + e.weight < dist.get(v)) {
                    dist.put(v, dist.get(u) + e.weight);
                    v.predecessor = u;
                }
             }
         }
        // Step 3: check for negative-weight cycles.  The above
        // step guarantees shortest distances if graph doesn't
        // contain negative weight cycle. If we get a shorter
        //  path, then there is a cycle.
        int totalCycles = 0;
        for (int k=0; k<edges.size(); k++)
        {
            Vertex u = edges.get(k).src;
            Vertex v = edges.get(k).dest;
            Edge e = edges.get(k);
            if (dist.get(u)+e.weight<dist.get(v)){
              totalCycles ++;
              System.out.println("\n=================================================================================");
              System.out.println("Graph contains negative weight cycle");
              System.out.println("Cycle starts with " + v.name+ " connected to " + u.name);
              path(u,v);
              LinkedHashSet<Vertex> cycle = new LinkedHashSet<Vertex>();
              while(cycle.add(v)){
                  v=v.predecessor;
              }
              printCycle(cycle);
            }
        }
        System.out.println("\nThe number of negative cycles, or arbitrage opportunities detected were :"+totalCycles);
        printDistanceHashMap(dist, vertices);
    }

    void path(Vertex u, Vertex v){
        System.out.println("INSIDE PATH FUNCTION");
        LinkedHashSet<Vertex> cycle = new LinkedHashSet<Vertex>();
        ArrayList<Vertex> cycleArrayList = new ArrayList<Vertex>();
        while(cycle.add(v)){
            cycleArrayList.add(v);
            v = v.predecessor;
        }
        Double maxWeight = 0.0;
        Double begin = 1.0;
        Double cycleWeight = 0.0;
        for(int k=0; k<cycleArrayList.size(); k++){
            Vertex v1 = cycleArrayList.get(k);
            System.out.print(v1.name+"--->");
            if(k<cycleArrayList.size()-1){
                Vertex v2 = cycleArrayList.get(k+1);
                Edge edge = findEdge(v1,v2);
                cycleWeight += edge.weight;
                begin *= Math.exp(edge.weight);
            }
        }
        Edge lastEdge = findEdge(cycleArrayList.get(cycleArrayList.size()-1),cycleArrayList.get(0));
        if(lastEdge!=null){
            cycleWeight += lastEdge.weight;
            if(cycleWeight> maxWeight){
            	maxWeight = cycleWeight;
            }
            maxWeight = cycleWeight;
            begin *= Math.exp(lastEdge.weight);
            System.out.println(Math.exp(cycleWeight));
            System.out.println("Starting with 1 " +v.name+ " we can end up with " + begin +" "+v.name +" by utilizing the negative cycle");
    
        } else {
            System.out.println("\nCouldn't find final edge");
        }
        System.out.println("Maximum negative weight cycle is:" + Math.exp(maxWeight));
    }

    void printCycle(LinkedHashSet<Vertex> c){
        System.out.println("we are printing the contents of the LinkedHashSet<Vertex> cycle");

        // switch to for loop for readability.
        for(Vertex v : c) {
        	System.out.print(v.name + "--->");
        }

    }

    Edge findEdge(Vertex src, Vertex dest){
        for ( int i = 0; i < edges.size(); i++){
            Edge e = edges.get(i);
            if(e.src == src && e.dest== dest){
                return e;
            }
        }
        return null;
    }

    Vertex findSource(String name, ArrayList<Vertex> vertices){
        for( int i = 0; i<vertices.size();i++){
            Vertex v= vertices.get(i);
            if (v.name.equalsIgnoreCase(name))
                return v;
        }
        return null;
    }

    // A utility function used to print the solution
    void printDistanceHashMap(HashMap<Vertex, Double> distance, ArrayList<Vertex> V)
    {   System.out.println("\n****************************");
        System.out.println("Vertex Distance from Source");
        for (int i=0; i<V.size(); ++i)
            System.out.println(V.get(i).name+"\t\t"+distance.get(V.get(i)));
    }
}