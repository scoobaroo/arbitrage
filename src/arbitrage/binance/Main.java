package binance;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;

import com.fasterxml.jackson.core.JsonParseException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Main {
	static boolean debug = false;
	static boolean trade = false;
	protected ArrayList<Vertex> vertices;
	protected ArrayList<Edge> edges;
	protected static ArrayList<String> symbols;
	protected HashSet<Edge> setOfEdges;
	protected LinkedHashMap<String,Double> exchangeRates;
	protected LinkedHashMap<String,Double> transactionAmounts;
	protected Map<String, Edge> edgeMap;
	protected HashMap<String, Vertex> vertexMap;
	protected HashSet<Vertex> setOfVertices;
	protected boolean firstTime;
	protected double baseAmountBTC;
	protected Vertex ETP, SAN, QTM, EDO, RRT, XRP, DSH, BT1, BT2, BCC, EUR, BCH, USD, QSH, EOS, OMG, IOT, BTC, BTG, ETC, BCU, DAT, YYW, ETH, ZEC, NEO, LTC, XMR, AVT;
	protected org.json.JSONArray tickerArray;
	
	private Main() {
		transactionAmounts = new LinkedHashMap<String,Double>();
		exchangeRates = new LinkedHashMap<String,Double>();
		symbols = new ArrayList<String>();
		vertices = new ArrayList<Vertex>();
		setOfVertices = new HashSet<Vertex>();
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

	public void populateVertices(org.json.JSONArray list) {
		Set<Vertex> vertexSet = new HashSet<Vertex>();
		Map<String,Vertex> vertexMap = new HashMap<String,Vertex>();
		for(Object o : list) {
			String key1= "", key2 = "";
			org.json.JSONObject obj = (org.json.JSONObject) o;
			String pair = (String) obj.get("symbol");
			if (pair.length() == 5) {
				key1 = pair.substring(0,2);
				key2 = pair.substring(2,5);
			} else if(pair.length() == 6) {
				key1 = pair.substring(0,3);
				key2 = pair.substring(3,6);
			} else if (pair.length() == 7) {
				if(pair.substring(3,7).equals("USDT")) {
					key1 = pair.substring(0,3);
					key2 = pair.substring(3,7);
				} else {
					key1 = pair.substring(0,4);
					key2 = pair.substring(4,7);
				}
			} else if (pair.length() == 8) {
				if(pair.substring(5,8).equals("BTC") || pair.substring(5,8).equals("ETH")){
					key1 = pair.substring(0,5);
					key2 = pair.substring(5,8);
				} else {
					key1 = pair.substring(0,4);
					key2 = pair.substring(4,8);
				}
			}
			Vertex v1 = new Vertex(key1.toUpperCase());
			Vertex v2 = new Vertex(key2.toUpperCase());
			vertexSet.add(v1);
			vertexSet.add(v2);
			if(debug) System.out.println("vertexSet size: " + vertexSet.size());
			vertices = new ArrayList<Vertex>(vertexSet);
		}
	}
	
	public void getExchangeRates() throws UnirestException, InterruptedException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.binance.com//api/v3/ticker/bookTicker").asJson();
		org.json.JSONArray list = jsonResponse.getBody().getArray();
		if(debug==true) {
			System.out.println(list);
			System.out.println(list.length());
		}
		populateVertices(list);
		for(Object o : list) {
			String key1= "", key2 = "";
			org.json.JSONObject obj = (org.json.JSONObject) o;
			String pair = (String) obj.get("symbol");
			symbols.add(pair);
			if (pair.length() == 5) {
				key1 = pair.substring(0,2);
				key2 = pair.substring(2,5);
			} else if(pair.length() == 6) {
				key1 = pair.substring(0,3);
				key2 = pair.substring(3,6);
			} else if (pair.length() == 7) {
				if(pair.substring(3,7).equals("USDT")) {
					key1 = pair.substring(0,3);
					key2 = pair.substring(3,7);
				} else {
					key1 = pair.substring(0,4);
					key2 = pair.substring(4,7);
				}
			} else if (pair.length() == 8) {
				if(pair.substring(5,8).equals("BTC") || pair.substring(5,8).equals("ETH")){
					key1 = pair.substring(0,5);
					key2 = pair.substring(5,8);
				} else {
					key1 = pair.substring(0,4);
					key2 = pair.substring(4,8);
				}
			}
			double bid = Double.valueOf((String) obj.get("bidPrice"));
			double ask = Double.valueOf((String) obj.get("askPrice"));
			double bidSize = Double.valueOf((String) obj.get("bidQty"));
			double askSize = Double.valueOf((String) obj.get("askQty"));
			if(!setOfVertices.contains(new Vertex(key1))) {
				Vertex vertex1 = new Vertex(key1);
				setOfVertices.add(vertex1);
			}
			if(!setOfVertices.contains(new Vertex(key1))) {
				Vertex vertex2 = new Vertex(key2);
				setOfVertices.add(vertex2);
			}
			Vertex v1 = findVertex(key1);
			Vertex v2 = findVertex(key2);
			String pairReversed = key2 + key1;
			edgeMap.put(pair, new Edge(v1,v2,-Math.log(bid), bid));
			edgeMap.put(pairReversed, new Edge(v2,v1, Math.log(ask), 1/ask));
			exchangeRates.put(pair.toUpperCase(), bid);
			exchangeRates.put(pairReversed.toUpperCase(), 1/ask);
			transactionAmounts.put(pair, bidSize);
			transactionAmounts.put(pairReversed, askSize);
		}
		for(Edge e: edgeMap.values()) {
    			setOfEdges.add(e);
		}
		edges = new ArrayList<Edge>(setOfEdges);
		if (debug) System.out.println("list length: " + list.length());
	}
	
    public Vertex findVertex(String name) {
		for(Vertex v: vertices) {
			if(v.name.equalsIgnoreCase(name)) {
				return v;
			}
		}
		return null; 
    }
     
    //find out if all vertices are connected to BTC/USD
    //make graph with graphstream
    
	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnirestException, JsonParseException, IOException, ParseException, InterruptedException{
		Main m = new Main();
		Exchange binanceExchange = new BinanceExchange();
		Trader t = new Trader(binanceExchange);
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		//uncomment below lines to have dialogs
//		System.out.println("Are you withdrawing or trading? (Enter ANY NUMBER for trading, 999 for withdrawing)");
//		int choice = Integer.valueOf(reader.next());
//		if(choice==999) {
//			t.convertCoinsToBTC();
//			return;
//		} else {
//			System.out.println("Since you're trading, do you want to convert BTC to all available cryptocurrencies? Enter ANY NUMBER for no, 888 for yes");
//			int choiceToConvertBTCToCoins = Integer.valueOf(reader.next());
//			if(choiceToConvertBTCToCoins==888) {
//				m.getExchangeRates();
//				CurrencyConverter.setExchangeRates(m.exchangeRates);
//				t.setExchangeRates(m.exchangeRates);
//				t.setVertices(m.vertices);
//				System.out.println(t.vertices);
//				System.out.println("Enter base amount(BTC) to convert to Cryptocurrencies: ");
//				double amountBTCToConvert = Double.valueOf(reader.next());
//				t.convertBTCToCoins(amountBTCToConvert);
//			}
			System.out.println("Enter base amount(BTC) to execute in trade sequences: ");	
			String baseAmountBTCString = reader.next();
			m.baseAmountBTC = Double.valueOf(baseAmountBTCString);
//			System.out.println(m.baseAmountUSD);
//	//		t.getAccountInfo();
//			reader.close();
//		    
//			m.vertices.clear();
//		    m.edges.clear();
//		    m.setOfEdges.clear();
//		    m.edgeMap.clear();
//		    m.exchangeRates.clear();
//		    Main.symbols.clear();
			//comment back to here for dialogs
			int count = 0;
			double maxRatios = 0;
			while(true) {
				m.getExchangeRates();
				t.setExchangeRates(m.exchangeRates);
				t.setTransactionAmounts(m.transactionAmounts);
				CurrencyConverter.setExchangeRates(m.exchangeRates);
				Graph g = new Graph(m.vertices, m.edges, Main.debug);
			    if (debug) System.out.println(Main.symbols);
				// Just grabbing first vertex in vertices because we don't care about what source is.
				Vertex src = g.v0;
			    g.BellmanFord(g, src);
			    ArrayList<Vertex> sequence = g.bestCycle;
			    double tradingFee = g.bestCycle.size() * 0.0005;
			    System.out.println(sequence);
			    if(1+tradingFee<g.maxRatio) {
			    		count++;
			    		maxRatios += g.maxRatio;
			    		System.out.println("Executing trade sequence");
			    		t.executeTradeSequenceWithList(sequence, m.baseAmountBTC);
//			    		t.executeTradeSequenceSequentially(sequence, m.baseAmountUSD);
			    		double ratio = maxRatios/count;
			    		System.out.println("Average ratio so far: " + ratio);
			    		System.out.println("Number of trades executed so far: " + count);
			    }
			    if(debug) {
				    System.out.println(m.exchangeRates);
				    System.out.println("Size of exchange rates:" + m.exchangeRates.size());
				    System.out.println("Testing currency Converter:");
				    System.out.println("Converting 1 USD to ETH: " + CurrencyConverter.convertUSDToCoin("ETH", 1));
				    System.out.println("Converting 1 ETH to USD: " + CurrencyConverter.convertCoinToUSD("ETH", 1));
				    System.out.println("Converting 1 USD to BTC: " + CurrencyConverter.convertUSDToCoin("BTC", 1));
				    System.out.println("Converting 1 BTC to USD: " + CurrencyConverter.convertCoinToUSD("BTC", 1));
			    }
			    // Resetting parameters for new api query
			    m.vertices.clear();
			    m.edges.clear();
			    m.setOfEdges.clear();
			    m.edgeMap.clear();
			    Main.symbols.clear();
			    m.exchangeRates.clear();
			    System.out.println("Number of trades executed so far: " + count);
				Thread.sleep((long) 0.000001);
			}
		}
	}