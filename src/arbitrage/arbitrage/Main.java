package arbitrage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.core.JsonParseException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Main {
	public ArrayList<Vertex> vertices;
	public ArrayList<Edge> edges;
	public static ArrayList<String> symbols;
	public HashSet<Edge> setOfEdges;
	public HashMap<String,Double> exchangeRates;
	public HashMap<String,Double> pseudoExchangeRates;
	public Map<String, Edge> edgeMap;
	HashMap<String, Vertex> vertexMap;
	boolean firstTime;
	double baseAmountUSD;
	Vertex ETP, SAN, QTM, EDO, RRT, XRP, DSH, BT1, BT2, BCC, EUR, BCH, USD, QSH, EOS, OMG, IOT, BTC, BTG, ETC, BCU, DAT, YYW, ETH, ZEC, NEO, LTC, XMR, AVT;
	org.json.JSONArray tickerArray;
	
	private Main() {
		pseudoExchangeRates = new HashMap<String,Double>();
		exchangeRates = new HashMap<String,Double>();
		symbols = new ArrayList<String>();
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		firstTime = true;
		setOfEdges = new HashSet<Edge>();
		edgeMap = new HashMap<String, Edge>();
		vertexMap = new HashMap<String, Vertex>();
		tickerArray = new org.json.JSONArray();
	}
	
//	private static String [] pairings = {"btcusd","ltcusd","ltcbtc","ethusd","ethbtc","etcbtc","etcusd","rrtusd","rrtbtc","zecusd","zecbtc","xmrusd",
//	                                     "xmrbtc","dshusd","dshbtc","btceur","xrpusd","xrpbtc","iotusd","iotbtc","ioteth","eosusd","eosbtc","eoseth",
//	                                     "sanusd","sanbtc","saneth","omgusd","omgbtc","omgeth","bchusd","bchbtc","bcheth","neousd","neobtc","neoeth",
//	                                     "etpusd","etpbtc","etpeth","qtmusd","qtmbtc","qtmeth","avtusd","avtbtc","avteth","edousd","edobtc","edoeth",
//	                                     "btgusd","btgbtc","datusd","datbtc","dateth","qshusd","qshbtc","qsheth","yywusd","yywbtc","yyweth","gntusd",
//	                                     "gntbtc","gnteth","sntusd","sntbtc","snteth","ioteur","batusd","batbtc","bateth","mnausd","mnabtc","mnaeth",
//	                                     "funusd","funbtc","funeth","zrxusd","zrxbtc","zrxeth","tnbusd","tnbbtc","tnbeth","spkusd","spkbtc","spketh",
//	                                     "trxusd","trxbtc","trxeth","rcnusd","rcnbtc","rcneth","rlcusd","rlcbtc","rlceth","aidusd","aidbtc","aideth",
//	                                     "sngusd","sngbtc","sngeth","repusd","repbtc","repeth","elfusd","elfbtc","elfeth"};
//

	@SuppressWarnings("rawtypes")
	public void getSymbols() throws UnirestException {
		System.out.println("URL: " + "https://api.bitfinex.com/v1/symbols");
		HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.bitfinex.com/v1/symbols").asJson();
		System.out.println(jsonResponse.getBody());
		JsonNode body = jsonResponse.getBody();
		tickerArray = body.getArray();
		System.out.println("Number of tickers received: " + tickerArray.length());
		Set<String> vertexSet = new LinkedHashSet<String>();
		Map<String,Vertex> vertexMap = new HashMap<String,Vertex>();
		for (Object pair : tickerArray) {
			String p = pair.toString();
			symbols.add(p);
			String symbol1 = p.substring(0,3);
			String symbol2 = p.substring(3,6);
			vertexSet.add(symbol1.toUpperCase());
			vertexSet.add(symbol2.toUpperCase());
		}
		// vertices before bitfinex changed symbols available
		String[] oldVertices = { "BTC", "USD", "LTC", "EUR", "DSH", "ETH", "ETP", "SAN", "QTM", "EDO", "RRT", "XRP",
				"BT1", "BT2", "BCC", "BCH", "QSH", "EOS", "OMG", "IOT", "BCH", "QSH", "EOS", "DAT", "YYW", "ZEC", "NEO",
				"XMR", "AVT" };
		for(String v : oldVertices) {
			vertexSet.add(v);
		}
		System.out.println();
		System.out.println("vertexSet size: " + vertexSet.size());
		// Creating unique set of vertices
		for(String v:vertexSet) {
			vertexMap.put(v, new Vertex(CryptoCurrency.get(v),v));
		}
		System.out.println("vertexMap size: " + vertexMap.size());
		Set<Vertex> setOfVertices = new HashSet<Vertex>();
		for(Vertex v : vertexMap.values()) {
			setOfVertices.add(v);
		}
		vertices = new ArrayList<Vertex>(setOfVertices);
		firstTime = false;
	}
	
	public void getExchangeRatesV2() throws UnirestException, InterruptedException {
		double rate = 0.0;
		String queryString = "";
		for(Object pair : tickerArray) {
			queryString += "t"+pair.toString().toUpperCase() + ",";
		}
		System.out.println("Current url:" + "https://api.bitfinex.com/v2/tickers?symbols=" + queryString);
		HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.bitfinex.com/v2/tickers?symbols=" + queryString).asJson();
		org.json.JSONArray resp = jsonResponse.getBody().getArray();
		for(Object o :resp) {
			String str = o.toString();
			System.out.println(str);
			Object[] array = str.split(",");
			String symbol = (String) array[0];
			String pair = symbol.substring(3, 9);
			String key1 = pair.substring(0,3);
			String key2 = pair.substring(3,6);
			double bid = Double.valueOf((String) array[1]);
			double ask = Double.valueOf((String) array[3]);
			System.out.println("key1: " + key1 + " key2: " + key2 + " bid: " + bid +" ask: " +ask);
			Vertex v1 = findVertex(key1);
			Vertex v2 = findVertex(key2);
			String pairReversed = key2 + key1;
			edgeMap.put(pair, new Edge(v1,v2,-Math.log(bid)));
			edgeMap.put(pairReversed, new Edge(v2,v1, Math.log(ask)));
			exchangeRates.put(pair.toLowerCase(), bid);
			exchangeRates.put(pairReversed.toLowerCase(), ask);
		}
		for(Edge e: edgeMap.values()) {
    			setOfEdges.add(e);
		}
		edges = new ArrayList<Edge>(setOfEdges);
		System.out.println(resp.length());
	}
	
	public void getExchangeRatesV1() throws UnirestException, InterruptedException {
		double rate = 0.0;
		ArrayList<String> tickerStringArray = new ArrayList<String>();
		for(Object pair : tickerArray) {
			tickerStringArray.add((String) pair);
		}
		Collections.shuffle(tickerStringArray);
		for(String pair : tickerStringArray) {
			System.out.println("Current url:" + "https://api.bitfinex.com/v1/pubticker/" + pair);
			HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.bitfinex.com/v1/pubticker/" + pair).asJson();
			System.out.println(jsonResponse.getBody().getObject());
			if (jsonResponse.getBody().getObject().has("error") == true) {
				for(Edge e: edgeMap.values()) {
		    			setOfEdges.add(e);
				}
				edges = new ArrayList<Edge>(setOfEdges);
				return;
			} else {
				org.json.JSONObject obj = jsonResponse.getBody().getObject();
				rate = Double.valueOf((String) obj.get("mid"));
				System.out.println(pair + ": " + rate);
				String symbol1 = pair.substring(0,3);
				String symbol2 = pair.substring(3,6);
				Vertex v1 = findVertex(symbol1);
				Vertex v2 = findVertex(symbol2);
				String pairReversed = symbol2 + symbol1;
				edgeMap.put(pair, new Edge(v1,v2,-Math.log(rate)));
				edgeMap.put(pairReversed, new Edge(v2,v1, Math.log(rate)));
			}
			Thread.sleep(200);
		}
		for(Edge e: edgeMap.values()) {
	    		setOfEdges.add(e);
		}
		edges = new ArrayList<Edge>(setOfEdges);
	}
	
    public Vertex findVertex(String name) {
		for(Vertex v: vertices) {
			if(v.name.equalsIgnoreCase(name)) {
				return v;
			}
		}
		return null; 
    }
    
    public void makePseudoExchangeRates() {
    		pseudoExchangeRates = exchangeRates;
    		
    }
    
    
    public double convertToUSD(String edge) {
    		double rate = exchangeRates.get(edge);
    		return rate;
    }
    
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws UnirestException, JsonParseException, IOException, ParseException, InterruptedException{
		Main m = new Main();
		System.out.println("Enter base amount(USD) to execute in trade sequences: ");
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		String baseAmountUSDString = reader.next();
		m.baseAmountUSD = Double.valueOf(baseAmountUSDString);
		System.out.println(m.baseAmountUSD);
//		Trader t = new Trader();
//		t.getAccountInfo();

		while(true) {
			m.getSymbols();
			m.getExchangeRatesV2();
			Graph g = new Graph(m.vertices, m.edges);
			
			// Just grabbing first vertex in vertices because we don't care about what source is.
			Vertex src = g.vertices.get(1);
		    g.BellmanFord(g, src);
		    System.out.println(m.symbols);
		    ArrayList<Vertex> sequence = g.bestCycle;
		    System.out.println(m.exchangeRates);
		    // Resetting parameters for new api query
		    m.edges.clear();
		    m.setOfEdges.clear();
		    m.edgeMap.clear();
			Thread.sleep(5000);
		}
	}
}
			// don't need below code because we are populating vertices in getSymbols
	        //creating set of vertices with CryptoCurrencies as their value
	//        m.BTC = new Vertex(CryptoCurrency.BTC, "BTC");
	//        m.USD = new Vertex(CryptoCurrency.USD, "USD");
	//        m.LTC = new Vertex(CryptoCurrency.LTC, "LTC");
	//        m.EUR = new Vertex(CryptoCurrency.EUR, "EUR");
	//        m.DSH = new Vertex(CryptoCurrency.DSH, "DSH");
	//        m.ETH = new Vertex(CryptoCurrency.ETH, "ETH");
	//        m.ETP = new Vertex(CryptoCurrency.ETP, "ETP");
	//        m.SAN = new Vertex(CryptoCurrency.SAN, "SAN");
	//        m.QTM = new Vertex(CryptoCurrency.QTM, "QTM");
	//        m.EDO = new Vertex(CryptoCurrency.EDO, "EDO");
	//        m.RRT = new Vertex(CryptoCurrency.RRT, "RRT");
	//        m.XRP = new Vertex(CryptoCurrency.XRP, "XRP");
	//        m.BT1 = new Vertex(CryptoCurrency.BT1, "BT1");
	//        m.BT2 = new Vertex(CryptoCurrency.BT2, "BT2");
	//        m.BCC = new Vertex(CryptoCurrency.EDO, "BCC");
	//        m.BCH = new Vertex(CryptoCurrency.BCH, "BCH");
	//        m.QSH = new Vertex(CryptoCurrency.QSH, "QSH");
	//        m.EOS = new Vertex(CryptoCurrency.EOS, "EOS");
	//        m.OMG = new Vertex(CryptoCurrency.OMG, "OMG");
	//        m.IOT = new Vertex(CryptoCurrency.IOT, "IOT");
	//        m.BTG = new Vertex(CryptoCurrency.BTG, "BCH");
	//        m.ETC = new Vertex(CryptoCurrency.ETC, "QSH");
	//        m.BCU = new Vertex(CryptoCurrency.BCU, "EOS");
	//        m.DAT = new Vertex(CryptoCurrency.DAT, "DAT");
	//        m.YYW = new Vertex(CryptoCurrency.YYW, "YYW");   
	//        m.ZEC = new Vertex(CryptoCurrency.ZEC, "ZEC");
	//        m.NEO = new Vertex(CryptoCurrency.NEO, "NEO");
	//        m.XMR = new Vertex(CryptoCurrency.XMR, "XMR");
	//        m.AVT = new Vertex(CryptoCurrency.AVT, "AVT");
	//     
	//        // don't need this code because we are populating vertices from getSymbols
	//        m.vertices.add(m.BTC);
	//        m.vertices.add(m.USD);
	//        m.vertices.add(m.LTC); 
	//        m.vertices.add(m.EUR);
	//        m.vertices.add(m.DSH);
	//        m.vertices.add(m.ETH);
	//        m.vertices.add(m.ETP);
	//        m.vertices.add(m.SAN);
	//        m.vertices.add(m.QTM);
	//        m.vertices.add(m.EDO);
	//        m.vertices.add(m.RRT);
	//        m.vertices.add(m.XRP);
	//        m.vertices.add(m.BT1);
	//        m.vertices.add(m.BT2);
	//        m.vertices.add(m.BCC);
	//        m.vertices.add(m.BCH);
	//        m.vertices.add(m.QSH);
	//        m.vertices.add(m.EOS);
	//        m.vertices.add(m.OMG);
	//        m.vertices.add(m.IOT);
	//        m.vertices.add(m.BTG);
	//        m.vertices.add(m.ETC);
	//        m.vertices.add(m.BCU);
	//        m.vertices.add(m.DAT);
	//        m.vertices.add(m.YYW);
	//        m.vertices.add(m.ZEC);
	//        m.vertices.add(m.NEO);
	//        m.vertices.add(m.XMR);
	//        m.vertices.add(m.AVT);
	
	//        JSONParser parser = new JSONParser();
	//        JSONObject a = (JSONObject) parser.parse(new FileReader("/Users/erichan/desktop/cs297/goodData48MB.json"));
	//        JSONArray list = new JSONArray();
	//        list = (JSONArray) a.get("Tickers");
	//        int listSize = list.size();
	//        System.out.println("There are " + listSize + " JSON objects in the data file");
	//        for (int i = 0; i < listSize; i++){
	//        		m.edgeMap = new HashMap<String,Edge>();
	//        		JSONObject tickersAtParticularTime = (JSONObject) list.get(i);
	////            String timestamp = tickersAtParticularTime.keySet().toString(); 
	//            Collection tickers = tickersAtParticularTime.values();
	//            Object[] array = tickers.toArray();
	//            System.out.println("=============================");
	//            Collection vals = (Collection) array[0];
	//            for(int z = 0; z < vals.size(); z++){
	//            		Object ticker = ((ArrayList) vals).get(z);
	//            		String key = ((HashMap) ticker).keySet().toString();
	//            		key = key.replace("[", "");
	//            		key = key.replace("]", "");
	//            		String key1 = key.substring(0, 3).toUpperCase();
	//            		String key2 = key.substring(3, 6).toUpperCase();
	//            		Collection tickerVals = ((HashMap) ticker).values();
	//            		Iterator tickerValsIter = tickerVals.iterator();
	//            		while(tickerValsIter.hasNext()) {
	//	            		JSONObject individualTicker = (JSONObject) parser.parse((String) tickerValsIter.next());
	//	            		Vertex v1 = m.findVertex(key1);
	//	            		Vertex v2 = m.findVertex(key2);
	//	            		m.edgeMap.put(key.toUpperCase(), new Edge(v1,v2,-Math.log(Double.valueOf((String) individualTicker.get("mid")))));
	//	            		//creating new edges for reversing directions of edges with new weights, sources, and destinations
	//	            		String newKey = key2+key1;
	//	            		m.edgeMap.put(newKey, new Edge(v2,v1, Math.log(Double.valueOf((String) individualTicker.get("mid")))));
	//            		}
	//                //adding to list of edges for our graph  
	//            		for(Edge e: m.edgeMap.values()) {
	//                		m.setOfEdges.add(e);
	//                }
	//        		    m.edges = new ArrayList<Edge>(m.setOfEdges);
	//            }
	//        }