package arbitragebinance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.trade.OrderSide;
import org.knowm.xchange.binance.dto.trade.OrderType;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsSorted.Order;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public class Trader {
	protected AccountInfo info;
	protected AccountService service;
	protected MarketDataService marketDataService;
	protected Exchange exchange;
	protected TradeService tradeService;
	
	public Trader(Exchange exchange) throws IOException {
		this.exchange = exchange;
		Properties prop = new Properties();
		InputStream input = null;
		String apiKey = "", apiSecret = "";
		try {
			input = new FileInputStream("/Users/suejanehan/workspace/binanceConfig.properties");
			prop.load(input);
//			System.out.println(prop.getProperty("apiKey"));
//			System.out.println(prop.getProperty("apiSecret"));
			apiKey = prop.getProperty("apiKey");
			apiSecret = prop.getProperty("apiSecret");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("mild.7.eric@gmail.com");
		exSpec.setApiKey(apiKey);
		exSpec.setSecretKey(apiSecret);
		Exchange binance = ExchangeFactory.INSTANCE.createExchange(exSpec);
		tradeService = binance.getTradeService();
		marketDataService = binance.getMarketDataService();
		service = binance.getAccountService();
		info = service.getAccountInfo();
	}
	
	public void printCurrentMarketValueOfOldBalances() throws FileNotFoundException {
		calculateCurrentMarketValueOfOldBalances();
		System.exit(0);
	}

	public void calculateCurrentMarketValueOfOldBalances() throws FileNotFoundException {
		final Type TOKEN_TYPE = new TypeToken<HashMap<String,Double>>() {}.getType();
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader("balancesNewNewNew.json"));
		HashMap<String,Double> balances = gson.fromJson(reader, TOKEN_TYPE); // contains the whole reviews list
//		System.out.println(balances);
		double BTCValue = 0;
		for(Entry<String, Double> entry: balances.entrySet()) {
			String coinString = entry.getKey();
			double balance = entry.getValue();
			double btcValueForCoin = 0;
			if(!coinString.equals("BTC")) {
				double exchangeRate = Main.exchangeRatesMid.get(coinString.toUpperCase()+"BTC");
				btcValueForCoin = balance * exchangeRate;
			} else {
				btcValueForCoin = balance;
			}
			BTCValue+=btcValueForCoin;
		}
		double USDValue = BTCValue * Main.exchangeRates.get("BTCUSDT");
		System.out.println("Total Bitcoin Value (without using bot): " + BTCValue + ", USD Value Equivalent: "+ USDValue);
	}
	
	public void getTradesSnapshot(List<LimitOrder> orders) throws IOException, InterruptedException {
		Gson gson = new Gson();
		String tradeList = gson.toJson(orders);
		FileWriter file = new FileWriter("limitOrders.json");
		try {
			file.write(tradeList);
		}catch (Exception e) {
			System.out.println(e);
		}
		finally {
			file.close();
		}
		System.out.println("Successfully Copied tradesList to File...");
		System.out.println("JSON Object: " + tradeList);
		System.exit(0);
	}
	
	public void getExchangeRatesSnapshot(HashMap<String,Double> rates) throws IOException, InterruptedException {
		Gson gson = new Gson();
		String ratesJSON = gson.toJson(rates);
		FileWriter file = new FileWriter("exchangeRatesJSON.json");
		try {
			file.write(ratesJSON);
		}catch (Exception e) {
			System.out.println(e);
		}
		finally {
			file.close();
		}
		System.out.println("Successfully Copied rates to File...");
		System.out.println("JSON Object: " + ratesJSON);
	}
	
	
	public void getAccountSnapshot() throws IOException, InterruptedException {
		Wallet wallet = info.getWallet();
		HashMap<String,Double> currenciesAndCoinBalances = new HashMap<String,Double>();
		for (Vertex v : Main.vertices) {
			double coinBalance = wallet.getBalance(new Currency(v.name)).getTotal().doubleValue();
			currenciesAndCoinBalances.put(v.toString(), coinBalance);
		}
		Gson gson = new Gson();
		String balances = gson.toJson(currenciesAndCoinBalances);
		FileWriter file = new FileWriter("balancesNewNewNew.json");
		try {
			file.write(balances);
		}catch (Exception e) {
			System.out.println(e);
		}
		finally {
			file.close();
		}
		System.out.println("Successfully Copied currenciesAndCoinBalances Object to File...");
		System.out.println("\nJSON Object: " + balances);
		System.out.println("COIN BALANCES");
		System.out.println(currenciesAndCoinBalances);
		//UNCOMMENT BELOW TO TAKE SNAPSHOT OF EXCHANGE RATES
//		String exchangeRatesforWritingToFile = gson.toJson(Main.exchangeRates);
//		FileWriter file2 = new FileWriter("exchangeRatesNewNew.json");
//		try {
//			file2.write(exchangeRatesforWritingToFile);
//		}catch (Exception e) {
//			System.out.println(e);
//		}
//		finally {
//			file2.close();
//		}
//		System.out.println("Successfully Copied Exchange Rate Object to File...");
//		System.out.println("JSON Object: " + exchangeRatesforWritingToFile);
		System.exit(0);
	}
	
	public void getHighestBalance() {
		Wallet wallet = info.getWallet();
		HashMap<Vertex, Double> currenciesAndBalances = new LinkedHashMap<Vertex,Double>();
		for (Vertex v : Main.vertices) {
			double coinBalance = wallet.getBalance(new Currency(v.name)).getAvailable().doubleValue();
			double btcValue = CurrencyConverter.convertCoinToBTC(v.toString(), coinBalance);
			currenciesAndBalances.put(v, btcValue);
			System.out.println(v.name + ": " + coinBalance + " ("+ CurrencyConverter.toPrecision(btcValue, 8)+ " BTC)");
		}
		Vertex maxV = currenciesAndBalances.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
		System.out.println("MAX CURRENCY: " + maxV + ", BALANCE: " + currenciesAndBalances.get(maxV) + "BTC");
		System.exit(0);
	}
	
	public void getBalancesAndEqualize(double belowThreshold, double aboveThreshold) throws IOException, InterruptedException {
		Wallet wallet = info.getWallet();
		HashMap<Vertex, Double> currenciesAndBalances = new LinkedHashMap<Vertex,Double>();
		ArrayList<Vertex> coinsToConvertToBTC = new ArrayList<Vertex>();
		ArrayList<Vertex> coinsNeeded = new ArrayList<Vertex>();
		List<LimitOrder> limitOrderList1 = new ArrayList<LimitOrder>();
		List<LimitOrder> limitOrderList2 = new ArrayList<LimitOrder>();
		for (Vertex v : Main.vertices) {
			double coinBalance = wallet.getBalance(new Currency(v.name)).getAvailable().doubleValue();
			double btcValue = CurrencyConverter.convertCoinToBTC(v.toString(), coinBalance);
			currenciesAndBalances.put(v, btcValue);
			System.out.println(v.name + ": " + coinBalance + " ("+ CurrencyConverter.toPrecision(btcValue, 8)+ " BTC)");
			if (btcValue>aboveThreshold) {
				if(!v.toString().equals("BTC")) {
					coinsToConvertToBTC.add(v);
				}
			}
			if (btcValue < belowThreshold) {
				coinsNeeded.add(v);
			}
		}
		Vertex maxV = currenciesAndBalances.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
		System.out.println("MAX CURRENCY: " + maxV + ", BALANCE: " + currenciesAndBalances.get(maxV) + "BTC");
		//uncomment below  to convert high balance currencies back to BTC
//		for(Vertex v: coinsToConvertToBTC) {
//			if(!v.toString().equals("BTC")) {
//				double availableBTCBalance = currenciesAndBalances.get(v); 
//				System.out.println("Coin " +v.toString() + " is in coinsToConvertToBTC list.");			
//				if(v.toString().equals("USDT")) {
//					double amountInBTCToSell = availableBTCBalance - 0.002;
//					Trade trade = new Trade(amountInBTCToSell, "BTC", v.toString(), "buy");
//					limitOrderList1.add(trade.createLimitOrder());
//				} else {
//					if(!v.toString().equals("BNB") && !v.toString().equals("ETH")) {
//						double amountInBTCToSell = availableBTCBalance - 0.002;
//						double amountCoins = CurrencyConverter.convertBTCToCoin(v.toString(), amountInBTCToSell);
//						Trade trade = new Trade(amountCoins, v.toString(), "BTC", "sell");
//						limitOrderList1.add(trade.createLimitOrder());
//					}
//				}
//			}
//		}
		//end uncomment
		for(Vertex v: coinsNeeded) {
			System.out.println(v.toString());
		}
		System.out.println("HELLO");
		for(Vertex v: coinsNeeded) {
			double availableBTCBalance = currenciesAndBalances.get(v); 
			System.out.println("Coin " +v.toString() + " is in coinsNeeded list.");
			if(!v.toString().equals("BTC")) {
				double amount = 0.002;
				if(v.toString().equals("USDT")) {
					Trade trade = new Trade(amount, "BTC", v.toString(), "sell");
					limitOrderList2.add(trade.createLimitOrder());
				} else {
					double amountCoin = CurrencyConverter.convertBTCToCoin(v.toString(), amount);
					Trade trade = new Trade(amountCoin, v.toString(), "BTC", "buy");
					limitOrderList2.add(trade.createLimitOrder());
				}
			}
		}
		if(Main.trade) {
//	    	for(LimitOrder order: limitOrderList1) {
//	    		if(order!=null) {
//	    			String orderReturnVal = tradeService.placeLimitOrder(order);
//	    			System.out.println("coinsToConvertToBTC Order Return Value: " + orderReturnVal);
//	    		}
//	    	}
	    	for(LimitOrder order: limitOrderList2) {
	    		if(order!=null) {
	    			String orderReturnVal = tradeService.placeLimitOrder(order);
	    			System.out.println("coinsNeeded Order Return Value: " + orderReturnVal);
	    		}
	    	}
		}
		System.out.println("EQUALIZING!!!");
		System.out.println("EQUALIZING!!!");
		System.out.println("EQUALIZING!!!");
		currenciesAndBalances.clear();
		Thread.sleep(1000);
	}

	public void refillETH() throws IOException {
		Trade trade = new Trade(0.05, "ETH","BTC", "buy");
		LimitOrder order = trade.createLimitOrder();
		String orderReturnVal = tradeService.placeLimitOrder(order);
		System.out.println("refillBnb ReturnVal" + orderReturnVal);
	}
	
	public void refillBnb() throws IOException {
		Trade trade = new Trade(1, "BNB","BTC", "buy");
		LimitOrder order = trade.createLimitOrder();
		String orderReturnVal = tradeService.placeLimitOrder(order);
		System.out.println("refillBnb ReturnVal" + orderReturnVal);
	}
	
	public ShouldTrade filterSequence(List<Vertex> sequence, double amountBTC) {
		sequence.add(sequence.get(0));
		System.out.println(sequence);
		ArrayList<String> zerosList = new ArrayList<String>();
		for(int i = 0; i< sequence.size()-1 ; i++) {
    		String key1;
    		String key2;
    		String symbol;
			key1 = sequence.get(i).toString().toUpperCase();
    		key2 = sequence.get(i+1).toString().toUpperCase();
    		symbol = key1+key2;
    		int sigDig = 999;
    		if(Main.sigDigs.containsKey(symbol)) {
    			sigDig = Main.sigDigs.get(symbol);
    			System.out.println(symbol + " got sigDig of " + sigDig);
    		} else{
    			symbol = key2 + key1;
    			sigDig = Main.sigDigs.get(symbol);
    			System.out.println(symbol + " got sigDig of " + sigDig);
    		};
    		if(sigDig==0) {
        		symbol = key1+key2;
    			zerosList.add(symbol);
    			System.out.println("we are filtering out " +symbol + " because its sigDig==0");
    		}
		}
		//////----NEW FEATURE IMPLEMENTATION-----/////////
		if(zerosList.size()==1) {
			System.out.println("WE ARE IN NEW FEATURE");
			System.out.println("WE ARE IN NEW FEATURE");
			System.out.println("WE ARE IN NEW FEATURE");
			HashMap<String,LimitOrder> symbolsAndOrders = new HashMap<String,LimitOrder>();
			HashMap<String,Double> symbolsAndAmounts = new HashMap<String,Double>();
			double amt = 0;
			double startingAmt = 0;
			double oldAmt = 0;
			double newAmt = 0;
			System.out.println("Original Order List:");
		    for(int i = 0; i< sequence.size()-1 ; i++) {
	    		String symbol;
	    		String orderType;
				String key1 = sequence.get(i).toString().toUpperCase();
	    		String key2 = sequence.get(i+1).toString().toUpperCase();
	    		symbol = key1+key2;
				double rate = Main.exchangeRates.get(symbol);
	    		if(i==0) {
		    		if(!Main.symbols.contains(symbol)) {
						orderType = "buy";
						startingAmt = CurrencyConverter.convertBTCToCoin(key1, amountBTC); //getting amount of coin with respect to amountBTC
						amt = startingAmt * rate;
						Trade trade = new Trade(amt, key2, key1, orderType);
						LimitOrder limitOrder = trade.createLimitOrder();
						symbolsAndOrders.put(symbol, limitOrder);
						symbolsAndAmounts.put(symbol, amt);
					} else {
						orderType = "sell";
						startingAmt = CurrencyConverter.convertBTCToCoin(key1, amountBTC); //getting amount of coin with respect to amountBTC
						amt = startingAmt * rate;
						Trade trade = new Trade(startingAmt, key1, key2, orderType);
						LimitOrder limitOrder = trade.createLimitOrder();
						symbolsAndOrders.put(symbol, limitOrder);
						symbolsAndAmounts.put(symbol, startingAmt);
					}
	    		} else {
		    		if(!Main.symbols.contains(symbol)) {
						orderType = "buy";
						oldAmt = amt;
						amt = oldAmt * rate;
						Trade trade = new Trade(amt, key2, key1, orderType);
						LimitOrder limitOrder = trade.createLimitOrder();
						symbolsAndOrders.put(symbol, limitOrder);
						symbolsAndAmounts.put(symbol, amt);
					} else {
						orderType = "sell";
						oldAmt = amt;
						amt = oldAmt * rate;
						Trade trade = new Trade(oldAmt, key1, key2, orderType);
						LimitOrder limitOrder = trade.createLimitOrder();
						symbolsAndOrders.put(symbol, limitOrder);
						symbolsAndAmounts.put(symbol, oldAmt);
					}
	    		}
		    }
		    String symbol = zerosList.get(0);
    		LimitOrder zeroOrder = symbolsAndOrders.get(symbol);
//    		double zeroAmt = symbolsAndAmounts.get(symbol);
    		double zeroAmt = zeroOrder.getOriginalAmount().doubleValue();
    		CurrencyPair cp = zeroOrder.getCurrencyPair();
    		org.knowm.xchange.dto.Order.OrderType type = zeroOrder.getType();
    		int intAmt = (int) Math.ceil(zeroAmt);
    		String[] pairArr = cp.toString().split("/");
    		String key = "";
    		if(type==org.knowm.xchange.dto.Order.OrderType.ASK) {
    			key = pairArr[0];
    		} else { //It is a buy order and hence the second element in the array(Currency Pair) is the starting value
    			key = pairArr[1];
    		}
    	    ArrayList<String> sequenceString = new ArrayList<String>();
    	    for(Vertex v: sequence) {
    	    	sequenceString.add(v.toString());
    	    }
    	    int index = sequenceString.indexOf(key);
    	    System.out.println("index: " + index);
    	    if(index!=0 && index!=-1) {
	    	    for (int k = index; k > 0; k--) {
	    	    	String plusKey = sequenceString.get(k+1);
	    	    	String keyText = sequenceString.get(k);
	    	    	String downKey = sequenceString.get(k-1);
	    	    	double conversionRate = Main.exchangeRates.get(downKey+keyText);
	    	    	double conversionRate2 = Main.exchangeRates.get(keyText+plusKey);
	    	    	if(k==index) {
	    	    		newAmt = intAmt / conversionRate;
	    	    		System.out.println(newAmt + " = " + intAmt + " / " + conversionRate);
	    	    	} else {
	    	    		newAmt = newAmt / conversionRate; 
	    	    		System.out.println(newAmt + " = " + newAmt + " / " + conversionRate);
	    	    	}
	    	    }
	    	    System.out.println("newAmt: " + newAmt);
			    return new ShouldTrade(true, true , newAmt); // uncomment this to execute on more trades...
    	    } else if(index==0 && sequence.get(0)!=sequence.get(sequence.size()-1)){
    	    	//get second index
    	    	System.out.println("INDEX == 0!!!!!!");
    	    	System.out.println("INDEX == 0!!!!!!");
    	    	System.out.println("INDEX == 0!!!!!!");
    	    	String keyIndex0 = sequenceString.get(0);
    	    	String keyIndex1 = sequenceString.get(1);
    	    	String keyIndex2 = sequenceString.get(2);
    	    	LimitOrder nextOrder = symbolsAndOrders.get(keyIndex1 + keyIndex2);
    	    	newAmt = Math.ceil(nextOrder.getOriginalAmount().doubleValue())/Main.exchangeRates.get(keyIndex0+keyIndex1);
    	    	return new ShouldTrade(true, true , newAmt);
//    	    } else if(index==sequenceString.size()-1){
//    	    	System.out.println("LASTINDEX == 0!!!!!!");
//    	    	System.out.println("LASTINDEX == 0!!!!!!");
//    	    	System.out.println("LASTINDEX == 0!!!!!!");
//    	    	String keyIndex0 = sequenceString.get(0);
//    	    	String keyIndex1 = sequenceString.get(1);
//    	    	String symbol = keyIndex0 + keyIndex1;
//    	    	LimitOrder firstOrder = symbolsAndOrders.get(symbol);
//    	    	newAmt = Math.ceil(firstOrder.getOriginalAmount().doubleValue());
//    	    	return new ShouldTrade(true, true , newAmt);
    	    } else {
    	    	return new ShouldTrade(false, false);
    	    }
		} else if (zerosList.size()>1) {
			return new ShouldTrade(false, false);
			//////--- END FEATURE IMPLEMENTATION -----//////
		} else return new ShouldTrade(true, false);
	}
	
	public boolean executeTradeSequenceWithList(ArrayList<Vertex> sequence, double amountBTC) throws IOException, InterruptedException{
		ShouldTrade shouldTrade = filterSequence(sequence, amountBTC);
		boolean shouldWeTrade = shouldTrade.isShouldTrade();
		if(!shouldWeTrade) {
			return shouldWeTrade;
		}
		boolean isNewTrade = shouldTrade.isNewTrade();
		List<LimitOrder> limitOrderList = new ArrayList<LimitOrder>();
		if(!isNewTrade) {
			double amt = 0;
			double startingAmt = 0;
			double oldAmt = 0;
			System.out.println(sequence);
		    for(int i = 0; i< sequence.size()-1 ; i++) {
	    		String key1;
	    		String key2;
	    		String symbol;
	    		String orderType;
				key1 = sequence.get(i).toString().toUpperCase();
	    		key2 = sequence.get(i+1).toString().toUpperCase();
	    		symbol = key1+key2;
				double rate = Main.exchangeRates.get(symbol);
				System.out.println("We got " + rate + " for " +symbol);
	    		if(i==0) {
		    		if(!Main.symbols.contains(symbol)) {
						orderType = "buy";
						startingAmt = CurrencyConverter.convertBTCToCoin(key1, amountBTC); //getting amount of coin with respect to amountBTC
						System.out.println(amountBTC + " BTC = " + startingAmt + " " + key1);
						amt = startingAmt * rate;
						System.out.println(startingAmt + " " + key1 + " * " + rate + " = " +amt + " " + key2);
						Trade trade = new Trade(amt, key2, key1, orderType);
			    		limitOrderList.add(trade.createLimitOrder());
					} else {
						orderType = "sell";
						startingAmt = CurrencyConverter.convertBTCToCoin(key1, amountBTC); //getting amount of coin with respect to amountBTC
						System.out.println(amountBTC + " BTC = " + startingAmt + " " + key1);
						amt = startingAmt * rate;
						System.out.println(startingAmt + " " + key1 + " * " + rate + " = " +amt + " " + key2);
						Trade trade = new Trade(startingAmt, key1, key2, orderType);
						limitOrderList.add(trade.createLimitOrder());
					}
	    		} else {
		    		if(!Main.symbols.contains(symbol)) {
						orderType = "buy";
						oldAmt = amt;
						amt = oldAmt * rate;
						System.out.println(oldAmt+ " " + key1 + " * " + rate + " = " + amt + " " + key2);
						Trade trade = new Trade(amt, key2, key1, orderType);
						limitOrderList.add(trade.createLimitOrder());
					} else {
						orderType = "sell";
						oldAmt = amt;
						amt = oldAmt * rate;
						System.out.println(oldAmt + " " + key1 + " * " + rate + " = " + amt + " " + key2);
						Trade trade = new Trade(oldAmt, key1, key2, orderType);
						limitOrderList.add(trade.createLimitOrder());
					}
	    		}
		    }
		} else { // it is a new Trade with adjusted amounts
			System.out.println("New Order List:");
			double amt = 0;
			double startingAmt = shouldTrade.getAmount();
			double oldAmt = 0;
			System.out.println(sequence);
		    for(int i = 0; i< sequence.size()-1 ; i++) {
	    		String key1;
	    		String key2;
	    		String symbol;
	    		String orderType;
				key1 = sequence.get(i).toString().toUpperCase();
	    		key2 = sequence.get(i+1).toString().toUpperCase();
	    		symbol = key1+key2;
				double rate = Main.exchangeRates.get(symbol);
				int sigDig;
				System.out.println("We got " + rate + " for " +symbol);
	    		if(i==0) {
		    		if(!Main.symbols.contains(symbol)) {
						orderType = "buy";
						amt = startingAmt * rate;
						System.out.println(startingAmt + " " + key1 + " * " + rate + " = " +amt + " " + key2);
			    		if(Main.sigDigs.containsKey(symbol)) {
			    			sigDig = Main.sigDigs.get(symbol);
			    		} else{
			    			symbol = key2 + key1;
			    			sigDig = Main.sigDigs.get(symbol);
			    		};
			    		if(sigDig==0) {
			        		amt = Math.ceil(amt);
			    		}
						Trade trade = new Trade(amt, key2, key1, orderType);
			    		limitOrderList.add(trade.createLimitOrder());
					} else {
						orderType = "sell";
						amt = startingAmt * rate;
						System.out.println(startingAmt + " " + key1 + " * " + rate + " = " +amt + " " + key2);
			    		if(Main.sigDigs.containsKey(symbol)) {
			    			sigDig = Main.sigDigs.get(symbol);
			    		} else{
			    			symbol = key2 + key1;
			    			sigDig = Main.sigDigs.get(symbol);
			    		};
			    		if(sigDig==0) {
			        		startingAmt = Math.ceil(startingAmt);
			    		}
						Trade trade = new Trade(startingAmt, key1, key2, orderType);
						limitOrderList.add(trade.createLimitOrder());
					}
	    		} else {
		    		if(!Main.symbols.contains(symbol)) {
						orderType = "buy";
						oldAmt = amt;
						amt = oldAmt * rate;
						System.out.println(oldAmt+ " " + key1 + " * " + rate + " = " + amt + " " + key2);
			    		if(Main.sigDigs.containsKey(symbol)) {
			    			sigDig = Main.sigDigs.get(symbol);
			    		} else{
			    			symbol = key2 + key1;
			    			sigDig = Main.sigDigs.get(symbol);
			    		};
			    		if(sigDig==0) {
			        		amt = Math.ceil(amt);
			    		}
						Trade trade = new Trade(amt, key2, key1, orderType);
						limitOrderList.add(trade.createLimitOrder());
					} else {
						orderType = "sell";
						oldAmt = amt;
						amt = oldAmt * rate;
						System.out.println(oldAmt + " " + key1 + " * " + rate + " = " + amt + " " + key2);
			    		if(Main.sigDigs.containsKey(symbol)) {
			    			sigDig = Main.sigDigs.get(symbol);
			    		} else{
			    			symbol = key2 + key1;
			    			sigDig = Main.sigDigs.get(symbol);
			    		};
			    		if(sigDig==0) {
			        		oldAmt = Math.ceil(oldAmt);
			    		}
						Trade trade = new Trade(oldAmt, key1, key2, orderType);
						limitOrderList.add(trade.createLimitOrder());
					}
	    		}
		    }
		    System.out.println("::::::::::Order List::::::::::");
		    for(LimitOrder order: limitOrderList) {
		    	System.out.println(order);
		    }
		    getTradesSnapshot(limitOrderList);
		}
	    if(Main.trade && shouldWeTrade) {
	    	for(LimitOrder order: limitOrderList) {
	    		if(order!=null) {
		    		String orderReturnVal;
					try {
						orderReturnVal = tradeService.placeLimitOrder(order);
						System.out.println("Limit Order Return Value: " + orderReturnVal);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
	    	}
	    };
	    return shouldWeTrade;
	}
	
	public void convertCoinsToBTC(double ratio) throws IOException {
		Wallet wallet = info.getWallet();
		ArrayList<LimitOrder> convertCoinsToBTCList = new ArrayList<LimitOrder>();
		for (Vertex v : Main.vertices) {
			if(!v.name.toUpperCase().equals("BTC")) {
				if(v.name.equals("USDT")) {
					String key1 = "BTC";
					String key2 = v.name;
					double amount = CurrencyConverter.convertCoinToBTC(v.name, wallet.getBalance(new Currency(v.name)).getAvailable().doubleValue()*ratio);
					// first, we get the name of the currency with v.name. then we convert it to a new Currency so we can grab it from wallet. Then we convert the amount coin available		
					// in the wallet to equivalent amount of BTC using CurrencyConverter's convertCoinToBTC method. We then create a trade by creating a new trade with "buy" and pair.
					if(amount>0.0017) {
						Trade trade = new Trade(amount, key1, key2, "buy");
						// adding the trade to convertCoinToBTCList for execution by tradeService. We need to execute a list of market orders hence trade.createMarketOrder()
						convertCoinsToBTCList.add(trade.createLimitOrder());
					}
				} else {
					String key1 = v.name;
					String key2 = "BTC";
					double amount = wallet.getBalance(new Currency(v.name)).getAvailable().doubleValue()*ratio;
					double amountBTC = CurrencyConverter.convertCoinToBTC(v.name, amount);
					System.out.println("Equivalent BTC balance for " + v.name+ " :" + amountBTC);
					if(amountBTC>0.0017) {
						Trade trade = new Trade(amount, key1, key2, "sell");
						convertCoinsToBTCList.add(trade.createLimitOrder());
					}
				}
			}
		}
		System.out.println("convertCoinsToBTCList size: " +convertCoinsToBTCList.size());
		System.out.println(convertCoinsToBTCList);
	    if(Main.trade) {
	    	for(LimitOrder order: convertCoinsToBTCList) {
	    		if(order != null ) {
	    			String orderReturnVal = tradeService.placeLimitOrder(order);
	    			System.out.println("convertCoinsToBTCList Return Value: " + orderReturnVal);
	    		}
	    	}
	    };
	}
	
	public void convertBTCToCoins(double amountBTC) throws IOException {
		System.out.println("Converting " + amountBTC + " BTC to all availabe cryptocurrencies");
		double btcPerCoin = amountBTC/(Main.vertices.size()-1);
		ArrayList<MarketOrder> convertBTCToCoinsList = new ArrayList<MarketOrder>();
		for (Vertex v : Main.vertices) {
			if(!v.name.toUpperCase().equals("BTC")) {
				if(v.name.equals("USDT")) {
					String key1 = "BTC";
					String key2 = v.name;
					// first, we get the name of the currency with v.name. then we convert it to a new Currency so we can grab it from wallet. Then we convert the amount coin available		
					// in the wallet to equivalent amount of BTC using CurrencyConverter's convertCoinToBTC method. We then create a trade by creating a new trade with "buy" and pair.
					Trade trade = new Trade(btcPerCoin, key1, key2, "sell");
					// adding the trade to convertCoinToBTCList for execution by tradeService. We need to execute a list of market orders hence trade.createMarketOrder()
					convertBTCToCoinsList.add(trade.createMarketOrderForExchanging());
				} else {
					String key1 = v.name;
					String key2 = "BTC";
					double amount = CurrencyConverter.convertBTCToCoin(v.name, btcPerCoin);
					Trade trade = new Trade(amount, key1, key2, "buy");
					convertBTCToCoinsList.add(trade.createMarketOrderForExchanging());
				}
			}
		}
		System.out.println("Converting to " + convertBTCToCoinsList.size() + " coins");
		System.out.println(convertBTCToCoinsList);
	    if(Main.trade) {
	    	for(MarketOrder order: convertBTCToCoinsList) {
	    		String orderReturnVal = tradeService.placeMarketOrder(order);
	    		System.out.println("Market Order Return Value: " + orderReturnVal);
	    	}
	    } 
	}
}
