package binance;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	static boolean trade = true;
	protected static LinkedHashMap<String,Integer> sigDigs;
	protected static LinkedHashMap<String,Integer> sigDigsForPricing;	
	protected ArrayList<Vertex> vertices;
	protected ArrayList<Edge> edges;
	protected static ArrayList<String> symbols;
	protected HashSet<Edge> setOfEdges;
	protected LinkedHashMap<String,Double> exchangeRates;
	protected static LinkedHashMap<String,Double> exchangePrices;
	protected static LinkedHashMap<String,Double> transactionAmounts;
	protected Map<String, Edge> edgeMap;
	protected HashMap<String, Vertex> vertexMap;
	protected HashSet<Vertex> setOfVertices;
	protected boolean firstTime;
	protected double baseAmountBTC;
	protected Vertex ETP, SAN, QTM, EDO, RRT, XRP, DSH, BT1, BT2, BCC, EUR, BCH, USD, QSH, EOS, OMG, IOT, BTC, BTG, ETC, BCU, DAT, YYW, ETH, ZEC, NEO, LTC, XMR, AVT;
	protected org.json.JSONArray tickerArray;
	
	private Main() {
		sigDigs = new LinkedHashMap<String,Integer> ();
		sigDigsForPricing = new LinkedHashMap<String,Integer>();
		transactionAmounts = new LinkedHashMap<String,Double>();
		exchangeRates = new LinkedHashMap<String,Double>();
		exchangePrices = new LinkedHashMap<String,Double>();
		symbols = new ArrayList<String>();
		vertices = new ArrayList<Vertex>();
		setOfVertices = new HashSet<Vertex>();
		edges = new ArrayList<Edge>();
		firstTime = true;
		setOfEdges = new HashSet<Edge>();
		edgeMap = new HashMap<String, Edge>();
		vertexMap = new HashMap<String, Vertex>();
		tickerArray = new org.json.JSONArray();
        String csvFile = "BinanceTradingRule-Master.csv";
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
        String csvFile2 = "BinanceTradingRule-MinPrice.csv";
        BufferedReader br2 = null;
        String line2 = "";
        try {
            br2 = new BufferedReader(new FileReader(csvFile2));
            while ((line2 = br2.readLine()) != null) {
                String[] elements = line2.split(",");
                String symbol = elements[0].replace("/","");
                Integer sigDigit = Integer.valueOf(elements[2]);
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
			if(!pair.equals("123456")) {
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
					if(pair.substring(5,8).equals("BTC") || pair.substring(5,8).equals("ETH") || pair.substring(5,8).equals("BNB")){
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
	}
	
	public void getExchangeRates() throws UnirestException, InterruptedException {
		HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.binance.com/api/v3/ticker/bookTicker").asJson();
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
			if (!pair.equals("123456")) {
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
					if(pair.substring(5,8).equals("BTC") || pair.substring(5,8).equals("ETH") || pair.substring(5,8).equals("BNB")){
						key1 = pair.substring(0,5);
						key2 = pair.substring(5,8);
					} else {
						key1 = pair.substring(0,4);
						key2 = pair.substring(4,8);
					}
				}
				Vertex v1 = findVertex(key1);
				Vertex v2 = findVertex(key2);
				String pairReversed = key2 + key1;
				if(debug) System.out.println("key1: "+key1 +" key2: "+key2);
				double bid = Double.valueOf((String) obj.get("bidPrice"));
				double ask = Double.valueOf((String) obj.get("askPrice"));
				double bidSize = Double.valueOf((String) obj.get("bidQty"));
				double askSize = Double.valueOf((String) obj.get("askQty"));
				edgeMap.put(pair, new Edge(v1,v2,-Math.log(bid), bid));
				edgeMap.put(pairReversed, new Edge(v2,v1, Math.log(ask), 1/ask));
				exchangeRates.put(pair.toUpperCase(), bid);
				exchangeRates.put(pairReversed.toUpperCase(), 1/ask);
				exchangePrices.put(pair.toUpperCase(), bid);
				exchangePrices.put(pairReversed.toUpperCase(), ask);
				transactionAmounts.put(pair, bidSize);
				transactionAmounts.put(pairReversed, askSize);
			}
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
	public static void main(String[] args) throws UnirestException, ParseException, IOException{
		Main m = new Main();
		Exchange binanceExchange = new BinanceExchange();
		Trader t = new Trader(binanceExchange);
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		//uncomment below lines to have dialogs
		System.out.println("Are you withdrawing or trading? (Enter ANY NUMBER for trading, 999 for withdrawing)");
		int choice = Integer.valueOf(reader.next());
		if(choice==999) {
			try {
				m.getExchangeRates();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Graph g = new Graph(m.vertices, m.edges, Main.debug);
			Vertex src = g.v0;
			g.BellmanFord(g, src);
			CurrencyConverter.setExchangeRates(m.exchangeRates);
			t.setExchangeRates(m.exchangeRates);
			t.setVertices(m.vertices);
			System.out.println("Enter percentage (0.xxx) of each currency you would like to sell:");
			double ratio = Double.valueOf(reader.next());
			t.convertCoinsToBTC(ratio);
			return;
		} else {
			System.out.println("Since you're trading, do you want to convert BTC to all available cryptocurrencies? Enter ANY NUMBER for no, 888 for yes");
			int choiceToConvertBTCToCoins = Integer.valueOf(reader.next());
			if(choiceToConvertBTCToCoins==888) {
				try {
					m.getExchangeRates();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CurrencyConverter.setExchangeRates(m.exchangeRates);
				t.setExchangeRates(m.exchangeRates);
				t.setVertices(m.vertices);
				System.out.println(t.vertices);
				System.out.println("Enter base amount(BTC) to convert to Cryptocurrencies:");
				double amountBTCToConvert = Double.valueOf(reader.next());
				t.convertBTCToCoins(amountBTCToConvert);
			}
			System.out.println("Would you like to equalize currencies for trading? Enter ANY NUMBER for no, 666 for yes");
			int choiceToEqualize = Integer.valueOf(reader.next());
			if(choiceToEqualize==666) {
				try {
					m.getExchangeRates();
					CurrencyConverter.setExchangeRates(m.exchangeRates);
					t.setExchangeRates(m.exchangeRates);
					t.setVertices(m.vertices);
					t.getBalancesAndEqualize(0.002, 0.004);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Enter base amount(BTC) to execute in trade sequences: ");	
			String baseAmountBTCString = reader.next();
			m.baseAmountBTC = Double.valueOf(baseAmountBTCString);
			reader.close();		    
			m.vertices.clear();
		    m.edges.clear();
		    m.setOfEdges.clear();
		    m.edgeMap.clear();
		    m.exchangeRates.clear();
		    Main.symbols.clear();
			//comment back to here for dialogs
			int count = 0;
			double maxRatios = 0;
			double profits = 0;
			while(true) {
				try {
					m.getExchangeRates();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				t.setVertices(m.vertices);
				t.setExchangeRates(m.exchangeRates);
				CurrencyConverter.setExchangeRates(m.exchangeRates);
				t.setTransactionAmounts(Main.transactionAmounts);
				Graph g = new Graph(m.vertices, m.edges, Main.debug);
			    if (debug) System.out.println(Main.symbols);
				// Just grabbing first vertex in vertices because we don't care about what source is.
				Vertex src = g.v0;
			    g.BellmanFord(g, src);
			    ArrayList<Vertex> sequence = g.bestCycle;
			    double tradingFee = g.bestCycle.size() * 0.0005;
			    System.out.println(sequence);
			    System.out.println("BNB AVAILABLE BALANCE: "+  t.getBnbBalance());
			    System.out.println("ETH AVAILABLE BALANCE: "+ t.getETHBalance());
			    boolean tradeBool;
			    if(1+tradingFee<g.maxRatio) {
//			    	try {
//						t.getBalancesAndEqualize(0.002, 0.004);
//					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printSta1ckTrace();
//					}
		    		maxRatios += g.maxRatio;
		    		System.out.println("Executing trade sequence");
		    		tradeBool= t.executeTradeSequenceWithList(sequence, m.baseAmountBTC);
		    		if(tradeBool && Main.trade) count++;
//			    		t.executeTradeSequenceSequentially(sequence, m.baseAmountUSD);
		    		double ratioAvg = maxRatios/count;
		    		System.out.println("Average ratio so far: " + ratioAvg);
		    		System.out.println("Number of trades executed so far: " + count);
		    		double profit = (g.maxRatio-(1+tradingFee));
		    		System.out.println("Profit made from this sequence: "+ profit);
		    		profits += profit;
		    		double profitAvg = profits/count;
		    		System.out.println("Average profit so far: " + profitAvg);
		    		if(t.getBnbBalance().doubleValue()<2) {
		    			System.out.println("REFILLING BNB!!!!!!");
		    			t.refillBnb();
		    		}
		    		if(t.getETHBalance().doubleValue()<0.1) {
		    			System.out.println("REFILLING ETH!!!!!!");
		    			t.refillETH();
		    		}
			    }
			    if(debug) {
				    System.out.println(m.exchangeRates);
				    System.out.println("Size of exchange rates:" + m.exchangeRates.size());
				    System.out.println("Testing currency Converter:");
				    System.out.println("Converting 1 BTC to ETH: " + CurrencyConverter.convertBTCToCoin("ETH", 1));
				    System.out.println("Converting 1 ETH to BTC: " + CurrencyConverter.convertCoinToBTC("ETH", 1));
				    System.out.println("Converting 1 USD to BTC: " + CurrencyConverter.convertCoinToCoin("USDT", "BTC", 1));
			    }
			    // Resetting parameters for new api query
			    m.vertices.clear();
			    m.edges.clear();
			    m.setOfEdges.clear();
			    m.edgeMap.clear();
			    Main.symbols.clear();
			    m.exchangeRates.clear();
			    System.out.println("Number of trades executed so far: " + count);
				try {
					Thread.sleep((long) .0001);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}