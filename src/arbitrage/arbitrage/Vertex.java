package arbitrage;

public class Vertex {
    public CryptoCurrency cc;
    public String name;
    public Vertex predecessor=null;
    public Vertex(CryptoCurrency cc, String name) {
        super();
        this.cc = cc;
        this.name=name;
        this.predecessor = null;
    }
    @Override
    public String toString() {
    		return name;
    }
}