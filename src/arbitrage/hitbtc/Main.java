package hitbtc;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import org.json.simple.parser.ParseException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.hitbtc.v2.HitbtcExchange;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcTicker;
import org.knowm.xchange.hitbtc.v2.service.HitbtcMarketDataServiceRaw;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.marketdata.params.Params;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import binance.Edge;
import binance.Graph;
import binance.Main;
import binance.Vertex;


public class Main {
	static boolean debug = true;
	static boolean trade = false;
	static int buffer = 3;
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
	
	private Main() {
		sigDigs = new LinkedHashMap<String,Integer>();
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
	}

	public static void getExchangeRates(HitbtcMarketDataServiceRaw marketDataServiceRaw, MarketDataService marketDataService, ExchangeMetaData metaData, List<CurrencyPair> symbols) throws UnirestException, InterruptedException, IOException {
		Map<CurrencyPair, CurrencyPairMetaData> pairsData = metaData.getCurrencyPairs();
//		for(Entry<CurrencyPair, CurrencyPairMetaData> data:pairsData.entrySet()){
//			System.out.println(data);
//		}
//		System.out.println(symbols);
		for(CurrencyPair symbol:symbols) {
			String[] symbolArray = symbol.toString().split("/");
			Vertex v1 = new Vertex(symbolArray[0]);
			Vertex v2 = new Vertex(symbolArray[1]);
			setOfVertices.add(v1);
			setOfVertices.add(v2);
		}
		vertices = new ArrayList<Vertex>(setOfVertices);
		System.out.println(vertices);
		System.out.println(vertices.size());
		Map<String, HitbtcTicker> tickers = marketDataServiceRaw.getHitbtcTickers();
//		System.out.println(tickers);
		for(Entry<String,HitbtcTicker> ticker: tickers.entrySet()) {
//			System.out.println(ticker);
			String symbol = ticker.getKey();
			String symbol1 = symbol.substring(0, 3);
			String symbol2 = symbol.substring(3);
			String pairReversed;
			Vertex v1;
			Vertex v2;
			HitbtcTicker tick = ticker.getValue();
			System.out.println(tick);
			Double ask;
			Double bid;
			if(!vertices.contains(findVertex(symbol1))){
				symbol1 = symbol.substring(0,4);
				System.out.println(symbol1);
				if(!symbol1.equalsIgnoreCase("ABTC")) {
					if(tick.getBid()!=null) {
						bid = tick.getBid().doubleValue();
					}else {
						bid = tick.getLow().doubleValue();
					}
					if(tick.getAsk()!=null) {
						ask = tick.getAsk().doubleValue();
					} else {
						ask = tick.getHigh().doubleValue();
					}
					symbol2 = symbol.substring(4);
					v1 = findVertex(symbol1);
					v2 = findVertex(symbol2);
					pairReversed = symbol2+symbol1;
					edgeMap.put(symbol, new Edge(v1, v2, -Math.log(bid) , bid));
					edgeMap.put(pairReversed, new Edge(v2,v1, Math.log(ask), 1/ask));
					exchangeRates.put(symbol.toUpperCase(), bid);
					exchangeRates.put(pairReversed.toUpperCase(), 1/ask);
				}
			} else {
				if(tick.getBid()!=null) {
					bid = tick.getBid().doubleValue();
				}else {
					bid = tick.getLow().doubleValue();
				}
				if(tick.getAsk()!=null) {
					ask = tick.getAsk().doubleValue();
				} else {
					ask = tick.getHigh().doubleValue();
				}
				v1 = findVertex(symbol1);
				v2 = findVertex(symbol2);
				pairReversed = symbol2+symbol1;
				edgeMap.put(symbol, new Edge(v1, v2, -Math.log(bid) , bid));
				edgeMap.put(pairReversed, new Edge(v2,v1, Math.log(ask), 1/ask));
				exchangeRates.put(symbol.toUpperCase(), bid);
				exchangeRates.put(pairReversed.toUpperCase(), 1/ask);
			}
		}	
		System.out.println(edgeMap);
		for(Edge e: edgeMap.values()) {
			setOfEdges.add(e);
		}
		edges = new ArrayList<Edge>(setOfEdges);
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
		Properties prop = new Properties();
		InputStream input = null;
		String apiKey = "", apiSecret = "", email ="";
		try {
			input = new FileInputStream("/Users/suejanehan/workspace/hitbtcConfig.properties");
			prop.load(input);
			email = prop.getProperty("email");
			apiKey = prop.getProperty("apiKey");
			apiSecret = prop.getProperty("apiSecret");
		} catch (IOException ex) { ex.printStackTrace(); } finally {
			if (input != null) {
				try { input.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
		ExchangeSpecification exSpec = new HitbtcExchange().getDefaultExchangeSpecification();
		exSpec.setUserName(email);
		exSpec.setApiKey(apiKey);
		exSpec.setSecretKey(apiSecret);
		Exchange hitbtc = ExchangeFactory.INSTANCE.createExchange(exSpec);
		MarketDataService marketDataService = hitbtc.getMarketDataService();
		HitbtcMarketDataServiceRaw marketDataServiceRaw = new HitbtcMarketDataServiceRaw(hitbtc); 
		ExchangeMetaData metaData = hitbtc.getExchangeMetaData();
		List<CurrencyPair> symbols = hitbtc.getExchangeSymbols();
		getExchangeRates(marketDataServiceRaw, marketDataService, metaData, symbols);
		Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
		Vertex src = g.v0;
	    g.BellmanFord(g, src);
	    ArrayList<Vertex> sequence = g.bestCycle;
	    System.out.println(sequence);
	} // DELETE THIS BRACKET AND UNCOMMENT ALL BELOW TO RESTORE TO BEFORE
//		AccountService service = hitbtc.getAccountService();
//		AccountInfo info = service.getAccountInfo();
//		Trader t = new Trader(hitbtc);
//		Scanner reader = new Scanner(System.in);
//		System.out.println("Are you withdrawing or trading? (Enter ANY NUMBER for trading, 999 for withdrawing)");
//		int choice = Integer.valueOf(reader.next());
//		if(choice==999) {
//			getExchangeRates(marketDataServiceRaw, marketDataService, metaData, symbols);
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
//				getExchangeRates(marketDataServiceRaw, marketDataService, metaData, symbols);
//				System.out.println("Enter base amount(BTC) to convert to Cryptocurrencies:");
//				double amountBTCToConvert = Double.valueOf(reader.next());
//				t.convertBTCToCoins(amountBTCToConvert);
//			}
//			System.out.println("Would you like to equalize currencies for trading? Enter ANY NUMBER for no, 666 for yes");
//			int choiceToEqualize = Integer.valueOf(reader.next());
//			if(choiceToEqualize==666) {
//				getExchangeRates(marketDataServiceRaw, marketDataService, metaData, symbols);
//				Main.vertices.remove(findVertex("VEN"));
//				Main.vertices.remove(findVertex("HSR"));
//				t.getBalancesAndEqualize(0.002, 0.005);
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
//				getExchangeRates(marketDataServiceRaw, marketDataService, metaData, symbols);
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
//				Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
//			    if (debug) {System.out.println(Main.symbols);}
//				// Just grabbing first vertex in vertices because we don't care about what source is.
//				Vertex src = g.v0;
//			    g.BellmanFord(g, src);
//			    ArrayList<Vertex> sequence = g.bestCycle;
//			    double tradingFee = (g.bestCycle.size()+buffer) * 0.00075; 
//			    //0.03168059 BTC
//			    //0.03164910 BTC
//			    //0.03160409 BTC
//			    //0.03156073 BTC
//			    //0.03149407 BTC
//			    //0.03147615 BTC
//			    //0.03146342 BTC
//			    //0.03145775 BTC
//			    //0.03144331 BTC
//			    //0.03142991 BTC
//			    //0.03141825 BTC
//			    //0.03140468 BTC
//			    //0.03136730 BTC
//			    //0.03134911 BTC next starting value
//			    //0.03130317 BTC starting value
//	    		t.calculateCurrentMarketValueOfOldBalances();
////				t.getHighestBalance();
////				t.printCurrentMarketValueOfOldBalances(); // UNCOMMENT TO SEE VALUES OF OLD SNAPSHOT AT CURRENT EXCHANGE RATES
////				t.getAccountSnapshot(); // UNCOMMENT THIS TO TAKE SNAPSHOT OF COIN BALANCES
//			    if((1+tradingFee)<g.maxRatio) {
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
//					Thread.sleep((long) .001);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}
}