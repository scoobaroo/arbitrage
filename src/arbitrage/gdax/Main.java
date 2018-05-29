package gdax;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.gdax.GDAXExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.knowm.xchange.currency.CurrencyPair;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.gdax.GDAXStreamingExchange;


public class Main {
	static int count = 0;
	static int unexecutedCount = 0;
	static boolean debug = false;
	static boolean trade = true;
	protected static LinkedHashMap<String,Integer> sigDigs;
	protected static LinkedHashMap<String,Integer> sigDigsForPricing;	
	protected static ArrayList<Vertex> vertices;
	protected static HashSet<Edge> setOfEdges;
	protected static ArrayList<Edge> edges;
	protected static ArrayList<String> symbols;
	protected static LinkedHashMap<String,Double> exchangeRates;
	protected static LinkedHashMap<String,Double> exchangeRatesMid;
	protected static LinkedHashMap<String,Double> exchangePrices;
	protected static LinkedHashMap<String,Double> transactionAmounts;
	protected static Map<String, Edge> edgeMap;
	protected static HashMap<String, Vertex> vertexMap;
	protected static HashSet<Vertex> setOfVertices;
	protected double baseAmountBTC;
	protected Vertex ETP, SAN, QTM, EDO, RRT, XRP, DSH, BT1, BT2, BCC, EUR, BCH, USD, QSH, EOS, OMG, IOT, BTC, BTG, ETC, BCU, DAT, YYW, ETH, ZEC, NEO, LTC, XMR, AVT;
	protected org.json.JSONArray tickerArray;
	
	private Main() {
		sigDigs = new LinkedHashMap<String,Integer> ();
		sigDigsForPricing = new LinkedHashMap<String,Integer>();
		transactionAmounts = new LinkedHashMap<String,Double>();
		exchangeRates = new LinkedHashMap<String,Double>();
		exchangeRatesMid = new LinkedHashMap<String,Double>();
		exchangePrices = new LinkedHashMap<String,Double>();
		symbols = new ArrayList<String>();
		vertices = new ArrayList<Vertex>();
		setOfVertices = new HashSet<Vertex>();
		edges = new ArrayList<Edge>();
		setOfEdges = new HashSet<Edge>();
		edgeMap = new HashMap<String, Edge>();
		vertexMap = new HashMap<String, Vertex>();
		tickerArray = new org.json.JSONArray();
		String csvFile = "GDAXAmounts.csv";
        BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] elements = line.split(",");
                String symbol = elements[0].replace("/","");
                sigDigs.put(symbol, Integer.valueOf(elements[1]));
            }
            System.out.println(sigDigs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String csvFile2 = "GDAXPricing.csv";
        BufferedReader br2 = null;
        String line2 = "";
        try {
            br2 = new BufferedReader(new FileReader(csvFile2));
            while ((line2 = br2.readLine()) != null) {
                String[] elements = line2.split(",");
                String symbol = elements[0].replace("/","");
                Integer sigDigit = Integer.valueOf(elements[1]);
                sigDigsForPricing.put(symbol,sigDigit);
            }
            System.out.println(sigDigsForPricing);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br2 != null) {
                try {
                    br2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public static void getExchangeRates(String productId) throws UnirestException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.gdax.com/products/"+productId+"/ticker").asJson();
		org.json.JSONArray list = jsonResponse.getBody().getArray();
		System.out.println(productId);
		System.out.println(list);
		JSONObject obj = list.getJSONObject(0);
		String [] currencies = productId.split("-");
		String key1 = currencies[0];
		String key2 = currencies[1];
		Vertex v1 = findVertex(key1);
		Vertex v2 = findVertex(key2);
		String pair = key1 + key2;
		String pairReversed = key2 + key1;
		if(debug) System.out.println("key1: "+key1 +" key2: "+key2);
		double bid = Double.valueOf((String) obj.get("bid"));
		double ask = Double.valueOf((String) obj.get("ask"));
		edgeMap.put(pair, new Edge(v1,v2,-Math.log(bid), bid));
		edgeMap.put(pairReversed, new Edge(v2,v1, Math.log(ask), 1/ask));
		exchangeRates.put(pair.toUpperCase(), bid);
		exchangeRates.put(pairReversed.toUpperCase(), 1/ask);
		double price = Double.valueOf((String) obj.get("price"));
		double mid = (bid + ask) / 2;
		exchangeRatesMid.put(pair, mid);
		exchangeRatesMid.put(pairReversed, 1/mid);
		exchangePrices.put(pair.toUpperCase(), bid);
		exchangePrices.put(pairReversed.toUpperCase(), ask);
		for(Edge e: edgeMap.values()) {
			setOfEdges.add(e);
		}
		edges = new ArrayList<Edge>(setOfEdges);
		if (debug) System.out.println("list length: " + list.length());
	}
	
	public static void getProducts() throws UnirestException, InterruptedException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.gdax.com/products").asJson();
		org.json.JSONArray list = jsonResponse.getBody().getArray();
		System.out.println(list);
		if(debug==true) {
			System.out.println(list);
			System.out.println(list.length());
		}
		for(Object o : list) {
			org.json.JSONObject obj = (org.json.JSONObject) o;
			String pair = (String) obj.get("id");
			symbols.add(pair.replace("-", ""));
			String[] symbol = pair.split("-");
			Vertex v1 = new Vertex(symbol[0]);
			Vertex v2 = new Vertex(symbol[1]);
			Main.setOfVertices.add(v1);
			Main.setOfVertices.add(v2);
		}
		Main.vertices = new ArrayList<Vertex>(setOfVertices);
		for(Object o : list) {
			org.json.JSONObject obj = (org.json.JSONObject) o;
			String pair = (String) obj.get("id");
			getExchangeRates(pair);
		}
	}
	
    public static Vertex findVertex(String name) {
		for(Vertex v: vertices) {
			if(v.name.equalsIgnoreCase(name)) {
				return v;
			}
		}
		return null; 
    }
     
    public static void clearAll() {
	    Main.edges.clear();
	    Main.setOfEdges.clear();
	    Main.edgeMap.clear();
	    Main.exchangeRates.clear();
	    Main.symbols.clear();
	    Main.vertices.clear();
    }
    
    public static void populateExchangeRate(Ticker ticker) {
        CurrencyPair pair = ticker.getCurrencyPair();
        String pairString = pair.toString();
        BigDecimal ask = ticker.getAsk();
        BigDecimal bid = ticker.getBid();
        System.out.println("Pair: " + pair);
        System.out.println("Ask: " + ask);
        System.out.println("Bid: " + bid);
        updateEdgesForPair(pairString, bid, ask);
    }
    
    public static void updateEdgesForPair(String name, BigDecimal bid, BigDecimal ask) {
    	System.out.println("We are updating edges for:" + name);
    	String[] array = name.split("/");
    	String key1 = array[0];
    	String key2 = array[1];
    	Vertex v1 = findVertex(key1);
    	Vertex v2 = findVertex(key2);
    	String forwardPair = key1 + key2;
    	String reversedPair = key2 + key1;
//		Edge forwardEdge = edgeMap.get(forwardPair);
//		Edge reversedEdge = edgeMap.get(reversedPair);
		edgeMap.put(forwardPair, new Edge(v1,v2,-Math.log(bid.doubleValue()), bid.doubleValue()));
		edgeMap.put(reversedPair, new Edge(v2,v1, Math.log(ask.doubleValue()), 1/ask.doubleValue()));
		exchangeRates.put(forwardPair.toUpperCase(), bid.doubleValue());
		exchangeRates.put(reversedPair.toUpperCase(), 1/ask.doubleValue());
		exchangePrices.put(forwardPair.toUpperCase(), bid.doubleValue());
		exchangePrices.put(reversedPair.toUpperCase(), ask.doubleValue());
		for(Edge e: edgeMap.values()) {
			setOfEdges.add(e);
		}
		edges = new ArrayList<Edge>(setOfEdges);
    }
    
    public static void doBellmanFord(Trader t) throws IOException, InterruptedException {
    	Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
	    g.BellmanFord(g, g.v0);
	    ArrayList<Vertex> sequenceNew = g.bestCycle;
	    System.out.println(sequenceNew);
	    if((1.001)<g.maxRatio) {
		boolean tradeBool = t.executeTradeSequenceWithList(sequenceNew, 0.002);
		if(Main.trade) {
			if(tradeBool) count++;
			else unexecutedCount++;
		} 
		}
    }
    
	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnirestException, ParseException, IOException, InterruptedException, URISyntaxException{
		Main m = new Main();
		Logger LOG = LoggerFactory.getLogger(Main.class);
		Exchange gdaxExchange = new GDAXExchange();
		Trader t = new Trader(gdaxExchange);
		getProducts();
		Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
		Vertex src = g.v0;
	    g.BellmanFord(g, src);
	    ArrayList<Vertex> sequence = g.bestCycle;
	    System.out.println(sequence);
	    System.out.println("Main.symbols:");
	    System.out.println(Main.symbols);
	    System.out.println("Main.vertices:");
	    System.out.println(Main.vertices);
	    System.out.println("Main.edges:");
	    System.out.println(Main.edges);
        ProductSubscription productSubscription = ProductSubscription.create().addTicker(CurrencyPair.BTC_USD)
                .addTicker(CurrencyPair.BTC_EUR).addTicker(CurrencyPair.ETH_USD).addTicker(CurrencyPair.BCH_BTC)
                .addTicker(CurrencyPair.BCH_USD).addTicker(CurrencyPair.BTC_GBP).addTicker(CurrencyPair.ETH_BTC)
                .addTicker(CurrencyPair.ETH_EUR).addTicker(CurrencyPair.LTC_BTC).addTicker(CurrencyPair.LTC_EUR)
                .addTicker(CurrencyPair.LTC_USD).addTicker(CurrencyPair.BCH_EUR).build();
        StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(GDAXStreamingExchange.class.getName());
        exchange.connect(productSubscription).blockingAwait();
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_USD).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
    	    doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_GBP).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BTC_EUR).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BCH_BTC).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BCH_USD).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.BCH_EUR).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.ETH_USD).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.ETH_BTC).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.ETH_EUR).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.LTC_BTC).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.LTC_EUR).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
        exchange.getStreamingMarketDataService().getTicker(CurrencyPair.LTC_USD).subscribe(ticker -> {
            System.out.println(ticker);
            populateExchangeRate(ticker);
            doBellmanFord(t);
        }, throwable -> LOG.error("ERROR in getting ticker: ", throwable));
	}
}
		// Disconnect from exchange (non-blocking)
//		exchange.disconnect().subscribe(() -> System.out.println("Disconnected from the Exchange"));
		
		
//	    while(true) {
//	    	getProducts();
//			System.out.println(Main.symbols);
//			System.out.println(Main.vertices);
//			System.out.println(Main.edges);
//			Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
//		    if (debug) {System.out.println(Main.symbols);}
//			Vertex src = g.v0;
//		    g.BellmanFord(g, src);
//		    ArrayList<Vertex> sequence = g.bestCycle;
//		    System.out.println(sequence);
//			try {
//				Thread.sleep((long) 10000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//	    }
//	    });
//		Scanner reader = new Scanner(System.in);
//		System.out.println("Are you withdrawing or trading? (Enter ANY NUMBER for trading, 999 for withdrawing)");
//		int choice = Integer.valueOf(reader.next());
//		if(choice==999) {
//			getExchangeRates();
//			Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
//			Vertex src = g.v0;
//			g.BellmanFord(g, src);
//			System.out.println("Enter percentage (0.xxx) of each currency you would like to sell:");
//			double ratio = Double.valueOf(reader.next());
//			t.convertCoinsToBTC(ratio);
//		} else {
//			System.out.println("Since you're trading, do you want to convert BTC to all available cryptocurrencies? Enter ANY NUMBER for no, 888 for yes");
//			int choiceToConvertBTCToCoins = Integer.valueOf(reader.next());
//			if(choiceToConvertBTCToCoins==888) {
//				getExchangeRates();
//				System.out.println("Enter base amount(BTC) to convert to Cryptocurrencies:");
//				double amountBTCToConvert = Double.valueOf(reader.next());
//				t.convertBTCToCoins(amountBTCToConvert);
//			}
//			System.out.println("Would you like to equalize currencies for trading? Enter ANY NUMBER for no, 666 for yes");
//			int choiceToEqualize = Integer.valueOf(reader.next());
//			if(choiceToEqualize==666) {
//				try {
//					getExchangeRates();
//					int bcdIndex = Main.vertices.indexOf(findVertex("BCD"));
//					Main.vertices.remove(bcdIndex);
//					t.getBalancesAndEqualize(0.0018, 0.0036);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			System.out.println("Enter base amount(BTC) to execute in trade sequences: ");	
//			String baseAmountBTCString = reader.next();
//			m.baseAmountBTC = Double.valueOf(baseAmountBTCString);
//			reader.close();		    
//			clearAll();
//			//comment back to here for dialogs
//			int count = 0;
//			int unexecutedCount = 0;
//			double maxRatios = 0;
//			while(true) {
//				getExchangeRates();
//				Wallet w = t.info.getWallet();
//			    Balance bnbBalanceAll = w.getBalance(new Currency("bnb"));
//				BigDecimal bnbBalanceAvailable = bnbBalanceAll.getAvailable();
//				Balance ethBalanceAll = w.getBalance(new Currency("eth"));
//				BigDecimal ethBalanceAvailable = ethBalanceAll.getAvailable();
//				Balance btcBalanceAll = w.getBalance(new Currency("btc"));
//				BigDecimal btcBalanceAvailable = btcBalanceAll.getAvailable();
//			    System.out.println("BNB AVAILABLE BALANCE: "+  bnbBalanceAvailable);
//			    System.out.println("ETH AVAILABLE BALANCE: "+ ethBalanceAvailable);
//			    System.out.println("BTC AVAILABLE BALANCE: "+ btcBalanceAvailable);
//	    		if(bnbBalanceAvailable.doubleValue()<1) {
//	    			System.out.println("REFILLING BNB!!!!!!");
////	    			t.refillBnb();
//	    		}
//	    		if(ethBalanceAvailable.doubleValue()<0.05) {
//	    			System.out.println("REFILLING ETH!!!!!!");
////	    			t.refillETH();
//	    		}
//	    		t.calculateCurrentMarketValueOfOldBalances();
//				Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
//			    if (debug) {System.out.println(Main.symbols);}
//				// Just grabbing first vertex in vertices because we don't care about what source is.
//				Vertex src = g.v0;
//			    g.BellmanFord(g, src);
//			    ArrayList<Vertex> sequence = g.bestCycle;
//			    double tradingFee = (g.bestCycle.size()+4) * 0.0005; //adding 5 for buffer
////			    double tradingFee = (g.bestCycle.size()*2) * 0.0005; //adding *2 for buffer
////				t.getHighestBalance();
////				t.printCurrentMarketValueOfOldBalances(); // UNCOMMENT TO SEE VALUES OF OLD SNAPSHOT AT CURRENT EXCHANGE RATES
////				t.getAccountSnapshot(); // UNCOMMENT THIS TO TAKE SNAPSHOT OF COIN BALANCES
//			    if((1+tradingFee)<g.maxRatio) {
////			    	try {
////						t.getBalancesAndEqualize(0.002, 0.005);
////					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
////						e.printSta1ckTrace();
////					}
//		    		maxRatios += g.maxRatio;
//		    		boolean tradeBool = t.executeTradeSequenceWithList(sequence, m.baseAmountBTC);
//		    		if(Main.trade) {
//		    			if(tradeBool) count++;
//		    			else unexecutedCount++;
//		    		}
//		    		double ratioAvg = maxRatios/count;
//		    		System.out.println("Average ratio so far: " + ratioAvg);
//		    		double profit = (g.maxRatio-(1+tradingFee)) * m.baseAmountBTC;
//		    		System.out.println("Profit made from this sequence: "+ profit + " BTC");
//			    }
//			    if(debug) {
//				    System.out.println(Main.exchangeRates);
//				    System.out.println("Size of exchange rates:" + Main.exchangeRates.size());
//				    System.out.println("Converting 1 BTC to ETH: " + CurrencyConverter.convertBTCToCoin("ETH", 1));
//				    System.out.println("Converting 1 ETH to BTC: " + CurrencyConverter.convertCoinToBTC("ETH", 1));
//				    System.out.println("Converting 1 USD to BTC: " + CurrencyConverter.convertCoinToCoin("USDT", "BTC", 1));
//			    }
//	    		System.out.println("Number of trades executed so far: " + count);
//	    		System.out.println("Number of unexecuted trades executed so far: " + unexecutedCount);
//			    // Resetting parameters for new api query
//			    clearAll();
//				try {
//					Thread.sleep((long) 200);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//}