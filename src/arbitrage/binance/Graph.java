package binance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class Graph {
	protected boolean debug;
	protected ArrayList<Vertex> vertices;
	protected ArrayList<Edge> edges;
    protected ArrayList<Double> ratioList;
    protected ArrayList<Vertex> bestCycle;
    protected int cycleSize;
    protected double maxRatio;
    protected HashMap<Vertex, Double> dist;
    protected double inf = Double.POSITIVE_INFINITY;
    protected Vertex v0;
    
    public Graph(ArrayList<Vertex> vertices, ArrayList<Edge> edges, boolean debug) {
        super();
        this.debug = debug;
        this.vertices = vertices;
        this.edges = edges;
        v0 = new Vertex("v0");
        for (Vertex v : vertices) {
        	Edge e = new Edge(v0,v,0,0);
        	edges.add(e);
        }
        ratioList = new ArrayList<Double>();
        bestCycle = new ArrayList<Vertex>();
        dist = new HashMap<Vertex, Double>(vertices.size());
        System.out.println("Graph vertices");
        System.out.println(vertices.size());
        if(debug) {
	        System.out.println("Vertices size: " + vertices.size());
	        System.out.println("Graph edges");
	        System.out.println("Edges size: " + edges.size());
	        System.out.println(edges);
        }
    }

    // The main function that finds shortest distances from src
    // to all other vertices using Bellman-Ford algorithm.  The
    // function also detects negative weight cycles
    public void BellmanFord(Graph graph, Vertex source)
    {
        Vertex src = source;
        int i,j;
        vertices = graph.vertices;
        edges = graph.edges;
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
                if(u!=null && v!=null) {
	                if (dist.get(u) + e.weight < dist.get(v)) {
	                    dist.put(v, dist.get(u) + e.weight);
	                    v.predecessor = u;
	                }
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
            if(u!=null && v!=null) {
	            if (dist.get(u)+e.weight<dist.get(v)){
	              totalCycles++;
	              if(debug == true) {
		              System.out.println("\n=================================================================================");
		              System.out.println("Graph contains negative weight cycle");
		              System.out.println("Cycle starts with " + v.name+ " connected to " + u.name);
	              }
	              path(u,v);
	              LinkedHashSet<Vertex> cycle = new LinkedHashSet<Vertex>();
	              while(cycle.add(v)){
	                  v=v.predecessor;
	              }
	            }
            }
        }
        if( debug == true) {
	        System.out.println("\n==============================BELLMAN FORD ENDED=================================");
	        System.out.println("\n=================================================================================");
	        System.out.println("\nThe number of negative cycles, or arbitrage opportunities detected were :"+totalCycles);
        }
        if(ratioList.size()>0) maxRatio = Collections.max(ratioList);
        System.out.println("Maximum Profit Ratio found: "+ maxRatio);
    	cycleSize = bestCycle.size();
	    if(debug==true) {
	        System.out.println("Number of trades in sequence to execute:" + cycleSize);
	        System.out.println(bestCycle);
        }
        //uncomment below to see Vertex Distance from Source
//        printDistanceHashMap(dist, vertices);
    }

    public void path(Vertex u, Vertex v){
        LinkedHashSet<Vertex> cycle = new LinkedHashSet<Vertex>();
        ArrayList<Vertex> cycleArrayList = new ArrayList<Vertex>();
        while(cycle.add(v)){
            cycleArrayList.add(v);
            v = v.predecessor;
        }
        Collections.reverse(cycleArrayList);
        double begin = 1.0;
        for(int k=0; k<cycleArrayList.size(); k++){
            Vertex v1 = cycleArrayList.get(k);
            if(debug) System.out.print(v1.name+"--->");
            if(k<cycleArrayList.size()-1){
                Vertex v2 = cycleArrayList.get(k+1);
                Edge edge = findEdge(v1,v2);
                begin *= edge.rate;
            }
        }
        Edge lastEdge = findEdge(cycleArrayList.get(cycleArrayList.size()-1),cycleArrayList.get(0));
        if(lastEdge!=null){
            begin *= lastEdge.rate;
            if(debug) {
	            System.out.println();
	            System.out.println("Starting with 1 " +v.name+ " we can end up with " + begin +" "+v.name +" by utilizing the negative cycle");
            }
            ratioList.add(begin);
            Double maxRatio = Collections.max(ratioList);
            if(begin > maxRatio) {
            		maxRatio = begin;
            }
            if(begin == maxRatio) {
        		bestCycle = cycleArrayList;
//        		cycleArrayList.add(cycleArrayList.get(0));
//                for(int j=0; j<cycleArrayList.size(); j++){
//                    Vertex v1 = cycleArrayList.get(j);
//                    if(debug) System.out.print(v1.name+"--->");
//                    if(j<cycleArrayList.size()-1){
//                        Vertex v2 = cycleArrayList.get(j+1);
//                        Edge edge = findEdge(v1,v2);
//                        cycleWeight += edge.weight;
//                        begin *= Math.exp(edge.weight);
//                        System.out.println(Math.exp(edge.weight)+" * ");
//                    }
//                }
            }
        } else {
            if (debug) System.out.println("\nCouldn't find final edge");
        } 
    }
    
    public void printCycle(LinkedHashSet<Vertex> set){
		if(debug == true) {
	        System.out.println("we are printing the contents of the LinkedHashSet<Vertex> cycle");
	        // switch to for loop for readability.
	        for(Vertex v : set) {
        		System.out.print(v.name + "--->");
	        }
		}
    }

    public Edge findEdge(Vertex src, Vertex dest){
        for ( int i = 0; i < edges.size(); i++){
            Edge e = edges.get(i);
            if(e.src==src && e.dest== dest){
                return e;
            }
        }
        return null;
    }
    
    public Vertex findVertex(String name) {
		for(Vertex v: vertices) {
			if(v.name.equalsIgnoreCase(name)) {
				return v;
			}
		}
		return null; 
    }

    // A utility function used to print the solution
    public void printDistanceHashMap(HashMap<Vertex, Double> distance, ArrayList<Vertex> V){   
    		if(debug == true) {
	    		System.out.println("\n**********************************************************************");
	    		System.out.println("\n**********************************************************************");
		        System.out.println("Vertex Distance from Source");
		        for (int i=0; i<V.size(); ++i) {
	        	System.out.println(V.get(i).name+"\t\t"+distance.get(V.get(i)));
	        }
		}
    }
}