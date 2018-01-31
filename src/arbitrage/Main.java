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
	public ArrayList<Vertex> currencies;
	public ArrayList<Edge> edges;
	public static HashMap<String,Double> exchangeRates;
	public static HashMap<String,Double> negExchangeRates;
	public Map<String, Edge> edgeMap;
	
	private Main() {
		currencies = new ArrayList<Vertex>();
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
		for(Vertex c: currencies) {
			if(c.name==name) {
				return c;
			}
		}
		return null; 
    }
    
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws UnirestException, JsonParseException, IOException, ParseException{
		Main m = new Main();
		getExchangeRates();
        //creating set of vertices with CryptoCurrencies as their value
        Vertex BTC = new Vertex(CryptoCurrency.BTC, "BTC");
        Vertex USD = new Vertex(CryptoCurrency.USD, "USD");
        Vertex LTC = new Vertex(CryptoCurrency.LTC, "LTC");
        Vertex EUR = new Vertex(CryptoCurrency.EUR, "EUR");
        Vertex DSH = new Vertex(CryptoCurrency.DSH, "DSH");
        Vertex ETH = new Vertex(CryptoCurrency.ETH, "ETH");
        Vertex ETP = new Vertex(CryptoCurrency.ETP, "ETP");
        Vertex SAN = new Vertex(CryptoCurrency.SAN, "SAN");
        Vertex OTM = new Vertex(CryptoCurrency.QTM, "QTM");
        Vertex EDO = new Vertex(CryptoCurrency.EDO, "EDO");
        Vertex RRT = new Vertex(CryptoCurrency.RRT, "RRT");
        Vertex XRP = new Vertex(CryptoCurrency.XRP, "XRP");
        Vertex BT1 = new Vertex(CryptoCurrency.BT1, "BT1");
        Vertex BT2 = new Vertex(CryptoCurrency.BT2, "BT2");
        Vertex BCC = new Vertex(CryptoCurrency.EDO, "BCC");
        Vertex BCH = new Vertex(CryptoCurrency.BCH, "BCH");
        Vertex QSH = new Vertex(CryptoCurrency.QSH, "QSH");
        Vertex EOS = new Vertex(CryptoCurrency.EOS, "EOS");
        Vertex OMG = new Vertex(CryptoCurrency.OMG, "OMG");
        Vertex IOT = new Vertex(CryptoCurrency.IOT, "IOT");
        Vertex BTG = new Vertex(CryptoCurrency.BTG, "BCH");
        Vertex ETC = new Vertex(CryptoCurrency.ETC, "QSH");
        Vertex BCU = new Vertex(CryptoCurrency.BCU, "EOS");
        Vertex DAT = new Vertex(CryptoCurrency.DAT, "DAT");
        Vertex YYW = new Vertex(CryptoCurrency.YYW, "YYW");   
        Vertex ZEC = new Vertex(CryptoCurrency.ZEC, "ZEC");
        Vertex NEO = new Vertex(CryptoCurrency.NEO, "NEO");
        Vertex XMR = new Vertex(CryptoCurrency.XMR, "XMR");
        Vertex AVT = new Vertex(CryptoCurrency.AVT, "AVT");
     
        //adding vertices to ArrayList of vertices to use in bellman ford
        m.currencies.add(BTC);
        m.currencies.add(USD);
        m.currencies.add(LTC); 
        m.currencies.add(EUR);
        m.currencies.add(DSH);
        m.currencies.add(ETH);
        m.currencies.add(ETP);
        m.currencies.add(SAN);
        m.currencies.add(OTM);
        m.currencies.add(EDO);
        m.currencies.add(RRT);
        m.currencies.add(XRP);
        m.currencies.add(BT1);
        m.currencies.add(BT2);
        m.currencies.add(BCC);
        m.currencies.add(BCH);
        m.currencies.add(QSH);
        m.currencies.add(EOS);
        m.currencies.add(OMG);
        m.currencies.add(IOT);
        m.currencies.add(BTG);
        m.currencies.add(ETC);
        m.currencies.add(BCU);
        m.currencies.add(DAT);
        m.currencies.add(YYW);
        m.currencies.add(ZEC);
        m.currencies.add(NEO);
        m.currencies.add(XMR);
        m.currencies.add(AVT);

        System.out.println("size of currencies" + m.currencies.size());
        System.out.println(CryptoCurrency.values().length);
        
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
        JSONObject a = (JSONObject) parser.parse(new FileReader("/Users/erichan/desktop/cs297/data.json"));
        JSONArray list = new JSONArray();
        list = (JSONArray) a.get("Tickers");
        int listSize = list.size();
        System.out.println(list);
        for (int i = 0; i < listSize; i++){
        		JSONObject obj = (JSONObject) list.get(i);
            String timestamp = obj.keySet().toString(); 
            Collection values = obj.values();
            System.out.println(values);
            Object[] array = values.toArray();
            System.out.println("=============================");
            Collection vals = (Collection) array[0];
            Iterator iter = vals.iterator();
            while(iter.hasNext()){
            		JSONObject o = (JSONObject) iter.next();
            		String key = o.keySet().toString();
            		key = key.replace("[", "");
            		key = key.replace("]", "");
            		String key1 = key.substring(0, 3).toUpperCase();
            		String key2 = key.substring(3, 6).toUpperCase();
            		System.out.println(key1 + " " +key2);
            		Collection tickerVals = o.values();
            		JSONObject individualTicker = (JSONObject) parser.parse((String) tickerVals.iterator().next());
            		System.out.println(individualTicker);
            		System.out.println(individualTicker.get("mid"));
            		m.edgeMap.put(key.toUpperCase(), new Edge(m.findVertex(key1),m.findVertex(key2),-Math.log(Double.valueOf((String) individualTicker.get("mid")))));
            		//creating new edges for reversing directions of edges with new weights, sources, and destinations
            		String newKey = key2+key1;
            		m.edgeMap.put(newKey, new Edge(m.findVertex(key2),m.findVertex(key1), Math.log(Double.valueOf((String) individualTicker.get("mid")))));
            }
            System.out.println(m.edgeMap.size());
            //creating list of edges to use for our graph  
            for(Edge e: m.edgeMap.values()) {
            		m.edges.add(e);
//            		System.out.println(e.toString());
            }
		    System.out.println(m.edges.size());
		    System.out.println(CryptoCurrency.values().length);
		    System.out.println(m.currencies.size());
//            Graph g = new Graph(m.currencies, m.edges);
//		    @SuppressWarnings("resource")
//			Scanner reader = new Scanner(System.in);  // Reading from System.in
//		    System.out.println("Enter starting currency: ");
//		    String input = reader.next();
//		    Vertex src = g.findSource(input);
//            g.BellmanFord(g, src);
        }
    }
}