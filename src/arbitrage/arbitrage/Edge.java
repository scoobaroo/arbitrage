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
    @Override
    public String toString() {
    		String s = "Edge " + this.src + this.dest + " with weight " + this.weight;
    		return s;
    }
}