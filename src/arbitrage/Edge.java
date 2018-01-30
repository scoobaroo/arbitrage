package arbitrage;

public class Edge {
    public Vertex src;
    public Vertex dest;
    public double weight;

    public Edge(Vertex src, Vertex dest, double weight) {
        super();
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}