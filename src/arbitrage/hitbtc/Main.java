package hitbtc;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
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
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.marketdata.params.Params;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Main {
	static boolean debug = false;
	static boolean trade = true;
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
        } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) { e.printStackTrace(); }
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
        } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } finally {
            if (br2 != null) {
                try {
                    br2.close();
                } catch (IOException e) { e.printStackTrace(); }
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

	public static void getExchangeRates(MarketDataService marketDataService, ExchangeMetaData metaData, List<CurrencyPair> symbols) throws UnirestException, InterruptedException, IOException {
		Map<CurrencyPair, CurrencyPairMetaData> pairs = metaData.getCurrencyPairs();
		System.out.println(symbols);
		List<Ticker> tickers = marketDataService.getTickers((Params) symbols);
		System.out.println(tickers);
//		for(CurrencyPair sym:s) {
//			Ticker ticker = marketDataService.getTicker(sym);
//			System.out.println(ticker);
//		}

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
//			System.out.println(prop.getProperty("apiKey"));
//			System.out.println(prop.getProperty("apiSecret"));
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
		ExchangeMetaData metaData = hitbtc.getExchangeMetaData();
		List<CurrencyPair> symbols = hitbtc.getExchangeSymbols();
		getExchangeRates(marketDataService, metaData, symbols);
		AccountService service = hitbtc.getAccountService();
		AccountInfo info = service.getAccountInfo();
		Trader t = new Trader(hitbtc);
		Scanner reader = new Scanner(System.in);
		System.out.println("Are you withdrawing or trading? (Enter ANY NUMBER for trading, 999 for withdrawing)");
		int choice = Integer.valueOf(reader.next());
		if(choice==999) {
			getExchangeRates(marketDataService, metaData, symbols);
			Graph g = new Graph(Main.vertices, Main.edges, Main.debug);
			Vertex src = g.v0;
			g.BellmanFord(g, src);
			System.out.println("Enter percentage (0.xxx) of each currency you would like to sell:");
			double ratio = Double.valueOf(reader.next());
			t.convertCoinsToBTC(ratio);
		} else {
			System.out.println("Since you're trading, do you want to convert BTC to all available cryptocurrencies? Enter ANY NUMBER for no, 888 for yes");
			int choiceToConvertBTCToCoins = Integer.valueOf(reader.next());
			if(choiceToConvertBTCToCoins==888) {
				getExchangeRates(marketDataService, metaData, symbols);
				System.out.println("Enter base amount(BTC) to convert to Cryptocurrencies:");
				double amountBTCToConvert = Double.valueOf(reader.next());
				t.convertBTCToCoins(amountBTCToConvert);
			}
			System.out.println("Would you like to equalize currencies for trading? Enter ANY NUMBER for no, 666 for yes");
			int choiceToEqualize = Integer.valueOf(reader.next());
			if(choiceToEqualize==666) {
				getExchangeRates(marketDataService, metaData, symbols);
				Main.vertices.remove(findVertex("VEN"));
				Main.vertices.remove(findVertex("HSR"));
				t.getBalancesAndEqualize(0.002, 0.005);
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
				getExchangeRates(marketDataService, metaData, symbols);
				Wallet w = t.info.getWallet();
			    Balance bnbBalanceAll = w.getBalance(new Currency("bnb"));
				BigDecimal bnbBalanceAvailable = bnbBalanceAll.getAvailable();
				Balance ethBalanceAll = w.getBalance(new Currency("eth"));
				BigDecimal ethBalanceAvailable = ethBalanceAll.getAvailable();
				Balance btcBalanceAll = w.getBalance(new Currency("btc"));
				BigDecimal btcBalanceAvailable = btcBalanceAll.getAvailable();
			    System.out.println("BNB AVAILABLE BALANCE: "+  bnbBalanceAvailable);
			    System.out.println("ETH AVAILABLE BALANCE: "+ ethBalanceAvailable);
			    System.out.println("BTC AVAILABLE BALANCE: "+ btcBalanceAvailable);
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
			    double tradingFee = (g.bestCycle.size()+buffer) * 0.00075; 
			    //0.03168059 BTC
			    //0.03164910 BTC
			    //0.03160409 BTC
			    //0.03156073 BTC
			    //0.03149407 BTC
			    //0.03147615 BTC
			    //0.03146342 BTC
			    //0.03145775 BTC
			    //0.03144331 BTC
			    //0.03142991 BTC
			    //0.03141825 BTC
			    //0.03140468 BTC
			    //0.03136730 BTC
			    //0.03134911 BTC next starting value
			    //0.03130317 BTC starting value
	    		t.calculateCurrentMarketValueOfOldBalances();
//				t.getHighestBalance();
//				t.printCurrentMarketValueOfOldBalances(); // UNCOMMENT TO SEE VALUES OF OLD SNAPSHOT AT CURRENT EXCHANGE RATES
//				t.getAccountSnapshot(); // UNCOMMENT THIS TO TAKE SNAPSHOT OF COIN BALANCES
			    if((1+tradingFee)<g.maxRatio) {
		    		maxRatios += g.maxRatio;
		    		boolean tradeBool = t.executeTradeSequenceWithList(sequence, m.baseAmountBTC);
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
					Thread.sleep((long) .001);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}