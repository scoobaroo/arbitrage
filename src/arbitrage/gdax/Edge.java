package gdax;

public class Edge {
	
	protected Vertex src;
    protected Vertex dest;
    protected double weight;
    protected double rate;
    
    public Edge(Vertex src, Vertex dest, double weight, double rate) {
		this.src = src;
        this.dest = dest;
        this.weight = weight;
        this.rate = rate;
    }
    
    @Override
    public String toString() {
    		String s = "Edge " + this.src + this.dest + " with weight " + this.weight;
    		return s;
    }
    
}