package arbitrage;

public class Edge {
    public Vertex src;
    public Vertex dest;
    public double weight;
    public Edge(Vertex src, Vertex dest, double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
    public String toString() {
    		return "Edge " + src.toString() + dest.toString();
    }
}