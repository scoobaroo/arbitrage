package arbitrage;

public class Edge {
	
	protected Vertex src;
    protected Vertex dest;
    protected double weight;
    
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