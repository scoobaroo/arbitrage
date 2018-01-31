package arbitrage;

public class Vertex {
    public CryptoCurrency cc;
    public String name;
    public Vertex predecessor=null;
    public Vertex(){
 
    }
    public Vertex(CryptoCurrency cc, String name) {
        super();
        this.cc = cc;
        this.name=name;
        this.predecessor = null;
    }
    public String toString() {
    		return name;
    }
}