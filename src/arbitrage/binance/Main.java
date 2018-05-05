package binance;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import org.json.simple.parser.ParseException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Main {
	static boolean debug = false;
	static boolean trade = true;
	protected static LinkedHashMap<String,Integer> sigDigs;
	protected static LinkedHashMap<String,Integer> sigDigsForPricing;	
	protected static ArrayList<Vertex> vertices;
	protected static HashSet<Edge> setOfEdges;
	protected static ArrayList<Edge> edges;
	protected static ArrayList<String> symbols;
	protected static LinkedHashMap<String,Double> exchangeRates;
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
		exchangePrices = new LinkedHashMap<String,Double>();
		symbols = new ArrayList<String>();
		vertices = new ArrayList<Vertex>();
		setOfVertices = new HashSet<Vertex>();
		edges = new ArrayList<Edge>();
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

	public static void populateVertices(org.json.JSONArray list) {
		Set<Vertex> vertexSet = new HashSet<Vertex>();
		vertexMap = new HashMap<String,Vertex>();
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
	
	public static void getExchangeRates() throws UnirestException, InterruptedException {
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
    
	@SuppressWarnings("resource")
	public static void main(String[] args) throws UnirestException, ParseException, IOException, InterruptedException{
		Main m = new Main();
		Exchange binanceExchange = new BinanceExchange();
		ConservativeTrader t = new ConservativeTrader(binanceExchange);
//		Trader t = new Trader(binanceExchange);
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		//uncomment below lines to have dialogs
		System.out.println("Are you withdrawing or trading? (Enter ANY NUMBER for trading, 999 for withdrawing)");
		int choice = Integer.valueOf(reader.next());
		if(choice==999) {
			getExchangeRates();
			Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
			Vertex src = g.v0;
			g.BellmanFord(g, src);
			System.out.println("Enter percentage (0.xxx) of each currency you would like to sell:");
			double ratio = Double.valueOf(reader.next());
			t.convertCoinsToBTC(ratio);
			return;
		} else {
			System.out.println("Since you're trading, do you want to convert BTC to all available cryptocurrencies? Enter ANY NUMBER for no, 888 for yes");
			int choiceToConvertBTCToCoins = Integer.valueOf(reader.next());
			if(choiceToConvertBTCToCoins==888) {
				getExchangeRates();
				System.out.println("Enter base amount(BTC) to convert to Cryptocurrencies:");
				double amountBTCToConvert = Double.valueOf(reader.next());
				t.convertBTCToCoins(amountBTCToConvert);
			}
			System.out.println("Would you like to equalize currencies for trading? Enter ANY NUMBER for no, 666 for yes");
			int choiceToEqualize = Integer.valueOf(reader.next());
			if(choiceToEqualize==666) {
				try {
					getExchangeRates();
					int bcdIndex = Main.vertices.indexOf(findVertex("BCD"));
					Main.vertices.remove(bcdIndex);
					t.getBalancesAndEqualize(0.0018, 0.005);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Enter base amount(BTC) to execute in trade sequences: ");	
			String baseAmountBTCString = reader.next();
			m.baseAmountBTC = Double.valueOf(baseAmountBTCString);
			reader.close();		    
			clearAll();
			//comment back to here for dialogs
			int count = 0;
			int unexecutedCount = 0;
			double maxRatios = 0;
			while(true) {
				try {
					getExchangeRates();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    t.calculateCurrentMarketValueOfOldBalances();
			    Wallet w = t.info.getWallet();
			    Balance bnbBalanceAll = w.getBalance(new Currency("bnb"));
				BigDecimal bnbBalanceAvailable = bnbBalanceAll.getAvailable();
				Balance ethBalanceAll = w.getBalance(new Currency("eth"));
				BigDecimal ethBalanceAvailable = ethBalanceAll.getAvailable();
			    System.out.println("BNB AVAILABLE BALANCE: "+  bnbBalanceAvailable);
			    System.out.println("ETH AVAILABLE BALANCE: "+ ethBalanceAvailable);
	    		if(bnbBalanceAvailable.doubleValue()<1) {
	    			System.out.println("REFILLING BNB!!!!!!");
//	    			t.refillBnb();
	    		}
	    		if(ethBalanceAvailable.doubleValue()<0.05) {
	    			System.out.println("REFILLING ETH!!!!!!");
//	    			t.refillETH();
	    		}
				Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
			    if (debug) {System.out.println(Main.symbols);}
				// Just grabbing first vertex in vertices because we don't care about what source is.
				Vertex src = g.v0;
			    g.BellmanFord(g, src);
			    ArrayList<Vertex> sequence = g.bestCycle;
			    double tradingFee = (g.bestCycle.size()+3) * 0.0005; //adding 3 for buffer
			    boolean tradeBool;
//				t.getHighestBalance();
//				t.printCurrentMarketValueOfOldBalances(); // UNCOMMENT TO SEE VALUES OF OLD SNAPSHOT AT CURRENT EXCHANGE RATES
//				t.getAccountSnapshot(); // UNCOMMENT THIS TO TAKE SNAPSHOT OF COIN BALANCES
			    if(1+tradingFee<g.maxRatio) {
//			    	try {
//						t.getBalancesAndEqualize(0.002, 0.005);
//					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printSta1ckTrace();
//					}
		    		maxRatios += g.maxRatio;
		    		tradeBool = t.executeTradeSequenceWithList(sequence, m.baseAmountBTC);
		    		if(Main.trade) {
		    			if(tradeBool) count++;
		    			else unexecutedCount++;
		    		}
		    		double ratioAvg = maxRatios/count;
		    		System.out.println("Average ratio so far: " + ratioAvg);
		    		double profit = (g.maxRatio-(1+tradingFee)) * m.baseAmountBTC;
		    		System.out.println("Profit made from this sequence: "+ profit + " BTC");
			    }
			    if(debug) {
				    System.out.println(Main.exchangeRates);
				    System.out.println("Size of exchange rates:" + Main.exchangeRates.size());
				    System.out.println("Converting 1 BTC to ETH: " + CurrencyConverter.convertBTCToCoin("ETH", 1));
				    System.out.println("Converting 1 ETH to BTC: " + CurrencyConverter.convertCoinToBTC("ETH", 1));
				    System.out.println("Converting 1 USD to BTC: " + CurrencyConverter.convertCoinToCoin("USDT", "BTC", 1));
			    }
	    		System.out.println("Number of trades executed so far: " + count);
	    		System.out.println("Number of unexecuted trades executed so far: " + unexecutedCount);
			    // Resetting parameters for new api query
			    clearAll();
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