package binance;

public class Vertex {
	
	protected CryptoCurrency cc;
	protected String name;
	protected Vertex predecessor=null;
	public Vertex(String name) {
		this.name = name;
	}
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        Vertex v = (Vertex) o;
        return name != v.name;
    }
     
    @Override
    public int hashCode() {
        return name.hashCode();
    }

}