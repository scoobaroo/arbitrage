package arbitrage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Main {
	public ArrayList<Vertex> vertices;
	public ArrayList<Edge> edges;
	public static HashMap<String,Double> exchangeRates;
	public static HashMap<String,Double> negExchangeRates;
	public Map<String, Edge> edgeMap;
	Vertex ETP, SAN, QTM, EDO, RRT, XRP, DSH, BT1, BT2, BCC, EUR, BCH, USD, QSH, EOS, OMG, IOT, BTC, BTG, ETC, BCU, DAT, YYW, ETH, ZEC, NEO, LTC, XMR, AVT;
	
	private Main() {
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		edgeMap = new HashMap<String, Edge>();
	}
	
	private static String [] pairings = {
	    "btcusd","ltcusd","ltcbtc","ethusd","ethbtc","etcbtc","etcusd","rrtusd","rrtbtc","zecusd","zecbtc","xmrusd","xmrbtc",
	    "dshusd","dshbtc","bccbtc","bcubtc","bccusd","bcuusd","btceur","xrpusd","xrpbtc","iotusd","iotbtc","ioteth","eosusd",
	    "eosbtc","eoseth","sanusd","sanbtc","saneth","omgusd","omgbtc","omgeth","bchusd","bchbtc","bcheth","neousd","neobtc",
	    "neoeth","etpusd","etpbtc","etpeth","qtmusd","qtmbtc","qtmeth","bt1usd","bt2usd","bt1btc","bt2btc","avtusd","avtbtc",
	    "avteth","edousd","edobtc","edoeth","btgusd","btgbtc","datusd","datbtc","dateth","qshusd","qshbtc","qsheth","yywusd",
	    "yywbtc","yyweth" 
	};

	public static void getExchangeRates() throws UnirestException {
		exchangeRates = new HashMap<String, Double>();
		negExchangeRates = new HashMap<String, Double>();
		for(String pair : pairings) {
			//System.out.println("Current url:" + "https://btc-e.com/api/3/ticker/" + pair);
//				HttpResponse<JsonNode> jsonResponse = Unirest.get("https://btc-e.com/api/3/ticker/" + pair).asJson();
//				exchangeRates.put(pair, rate);
//				negExchangeRates.put(pair, -Math.log(rate));
			//System.out.println(pair + ":" + -Math.log(rate));
		}
	}

    public Vertex findVertex(String name) {
		for(Vertex v: vertices) {
			if(v.name.equalsIgnoreCase(name)) {
				return v;
			}
		}
		return null; 
    }
    
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws UnirestException, JsonParseException, IOException, ParseException{
		Main m = new Main();
		getExchangeRates();
        //creating set of vertices with CryptoCurrencies as their value
        m.BTC = new Vertex(CryptoCurrency.BTC, "BTC");
        m.USD = new Vertex(CryptoCurrency.USD, "USD");
        m.LTC = new Vertex(CryptoCurrency.LTC, "LTC");
        m.EUR = new Vertex(CryptoCurrency.EUR, "EUR");
        m.DSH = new Vertex(CryptoCurrency.DSH, "DSH");
        m.ETH = new Vertex(CryptoCurrency.ETH, "ETH");
        m.ETP = new Vertex(CryptoCurrency.ETP, "ETP");
        m.SAN = new Vertex(CryptoCurrency.SAN, "SAN");
        m.QTM = new Vertex(CryptoCurrency.QTM, "QTM");
        m.EDO = new Vertex(CryptoCurrency.EDO, "EDO");
        m.RRT = new Vertex(CryptoCurrency.RRT, "RRT");
        m.XRP = new Vertex(CryptoCurrency.XRP, "XRP");
        m.BT1 = new Vertex(CryptoCurrency.BT1, "BT1");
        m.BT2 = new Vertex(CryptoCurrency.BT2, "BT2");
        m.BCC = new Vertex(CryptoCurrency.EDO, "BCC");
        m.BCH = new Vertex(CryptoCurrency.BCH, "BCH");
        m.QSH = new Vertex(CryptoCurrency.QSH, "QSH");
        m.EOS = new Vertex(CryptoCurrency.EOS, "EOS");
        m.OMG = new Vertex(CryptoCurrency.OMG, "OMG");
        m.IOT = new Vertex(CryptoCurrency.IOT, "IOT");
        m.BTG = new Vertex(CryptoCurrency.BTG, "BCH");
        m.ETC = new Vertex(CryptoCurrency.ETC, "QSH");
        m.BCU = new Vertex(CryptoCurrency.BCU, "EOS");
        m.DAT = new Vertex(CryptoCurrency.DAT, "DAT");
        m.YYW = new Vertex(CryptoCurrency.YYW, "YYW");   
        m.ZEC = new Vertex(CryptoCurrency.ZEC, "ZEC");
        m.NEO = new Vertex(CryptoCurrency.NEO, "NEO");
        m.XMR = new Vertex(CryptoCurrency.XMR, "XMR");
        m.AVT = new Vertex(CryptoCurrency.AVT, "AVT");
     
        //adding vertices to ArrayList of vertices to use in bellman ford
        m.vertices.add(m.BTC);
        m.vertices.add(m.USD);
        m.vertices.add(m.LTC); 
        m.vertices.add(m.EUR);
        m.vertices.add(m.DSH);
        m.vertices.add(m.ETH);
        m.vertices.add(m.ETP);
        m.vertices.add(m.SAN);
        m.vertices.add(m.QTM);
        m.vertices.add(m.EDO);
        m.vertices.add(m.RRT);
        m.vertices.add(m.XRP);
        m.vertices.add(m.BT1);
        m.vertices.add(m.BT2);
        m.vertices.add(m.BCC);
        m.vertices.add(m.BCH);
        m.vertices.add(m.QSH);
        m.vertices.add(m.EOS);
        m.vertices.add(m.OMG);
        m.vertices.add(m.IOT);
        m.vertices.add(m.BTG);
        m.vertices.add(m.ETC);
        m.vertices.add(m.BCU);
        m.vertices.add(m.DAT);
        m.vertices.add(m.YYW);
        m.vertices.add(m.ZEC);
        m.vertices.add(m.NEO);
        m.vertices.add(m.XMR);
        m.vertices.add(m.AVT);

        System.out.println("size of vertices " + m.vertices.size());
        System.out.println("size of CryptoCurrency enum " + CryptoCurrency.values().length);
        
        Set<String> cryptocurrencies = new HashSet<>();
        for(String pair : pairings) {
        		String c = pair.substring(0,3);
        		String c2 = pair.substring(3,6);
        		String currency = c.toUpperCase();
        		String currency2 = c2.toUpperCase();
        		cryptocurrencies.add(currency);
        		cryptocurrencies.add(currency2);
        }
        
        System.out.println(cryptocurrencies);
        JSONParser parser = new JSONParser();
        JSONObject a = (JSONObject) parser.parse(new FileReader("/Users/erichan/desktop/cs297/goodData36MB.json"));
        JSONArray list = new JSONArray();
        list = (JSONArray) a.get("Tickers");
        int listSize = list.size();
        System.out.println(listSize);
        for (int i = 0; i < listSize; i++){
        		m.edges = new ArrayList<Edge>();
        		m.edgeMap = new HashMap<String,Edge>();
        		JSONObject tickersAtParticularTime = (JSONObject) list.get(i);
//            String timestamp = tickersAtParticularTime.keySet().toString(); 
            Collection tickers = tickersAtParticularTime.values();
            System.out.println(tickers);
            Object[] array = tickers.toArray();
            System.out.println("=============================");
            Collection vals = (Collection) array[0];
            System.out.println(vals.getClass().getTypeName());
            for(int z = 0; z < vals.size(); z++){
            		Object ticker = ((ArrayList) vals).get(z);
            		String key = ((HashMap) ticker).keySet().toString();
            		key = key.replace("[", "");
            		key = key.replace("]", "");
            		String key1 = key.substring(0, 3).toUpperCase();
            		String key2 = key.substring(3, 6).toUpperCase();
            		System.out.println(key1 + " " +key2);
            		Collection tickerVals = ((HashMap) ticker).values();
            		Iterator tickerValsIter = tickerVals.iterator();
            		while(tickerValsIter.hasNext()) {
	            		JSONObject individualTicker = (JSONObject) parser.parse((String) tickerValsIter.next());
	            		System.out.println(individualTicker.get("mid") + "\n");
	            		Vertex v1 = m.findVertex(key1);
	            		Vertex v2 = m.findVertex(key2);
	            		m.edgeMap.put(key.toUpperCase(), new Edge(v1,v2,-Math.log(Double.valueOf((String) individualTicker.get("mid")))));
	            		//creating new edges for reversing directions of edges with new weights, sources, and destinations
	            		String newKey = key2+key1;
	            		m.edgeMap.put(newKey, new Edge(v2,v1, Math.log(Double.valueOf((String) individualTicker.get("mid")))));
            		}
                //adding to list of edges for our graph  
            		for(Edge e: m.edgeMap.values()) {
                		m.edges.add(e);
                }
                System.out.println(m.edgeMap.size());
        		    System.out.println(m.edges.size());
            }
            Edge e = new Edge(m.findVertex("LTC"),m.findVertex("BTC"), 1.00);
            System.out.println(e);
            System.out.println(m.vertices.size());
            System.out.println(m.edges.size());
            m.edges.removeAll(Collections.singleton(null));
    	    		Graph g = new Graph(m.vertices, m.edges);
    	    		@SuppressWarnings("resource")
    	    		Scanner reader = new Scanner(System.in);  // Reading from System.in
    	    	    System.out.println("Enter starting vertex: ");
    	    	    String input = reader.next();
    	    	    Vertex src = g.findSource(input);
    	        g.BellmanFord(g, src);
        }
    }
}