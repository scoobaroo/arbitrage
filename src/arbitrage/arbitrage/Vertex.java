package arbitrage;

public class Vertex {
	
	protected CryptoCurrency cc;
	protected String name;
	protected Vertex predecessor=null;
	
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