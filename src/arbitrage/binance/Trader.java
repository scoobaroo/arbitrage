package binance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.service.BinanceMarketDataServiceRaw;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.trade.TradeService;

public class Trader {
	protected AccountInfo info;
	protected AccountService service;
	protected MarketDataService marketDataService;
	protected Exchange exchange;
	protected TradeService tradeService;
	protected HashMap<String,Double> exchangeRates;
	protected HashMap<String,Double> transactionAmounts;
	protected ArrayList<Vertex> vertices;
	
	public Trader(Exchange exchange) throws IOException {
		this.exchange = exchange;
		Properties prop = new Properties();
		InputStream input = null;
		String apiKey = "", apiSecret = "";
		try {
			input = new FileInputStream("/Users/suejanehan/workspace/config.properties");
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
		getBnbBalance();
	}
	public void getMarketData () {
		 
	}
	
	public BigDecimal getBnbBalance() {
		Wallet wallet = info.getWallet();
		Balance bnbBalanceAll = wallet.getBalance(new Currency("bnb"));
		BigDecimal bnbBalanceAvailable = bnbBalanceAll.getAvailable();
		System.out.println(bnbBalanceAvailable);
		return bnbBalanceAvailable;
	}
	
	public void refillBnb() throws IOException {
		Trade trade = new Trade(100, "BNB","BTC", "buy");
		MarketOrder order = trade.createMarketOrder();
		String orderReturnVal = tradeService.placeMarketOrder(order);
		System.out.println("refillBnb ReturnVal" + orderReturnVal);
	}
	
	public void executeTradeSequenceWithList(ArrayList<Vertex> sequence, double amountBTC) throws IOException {
		System.out.println("Inside trader's executeTradeSequenceWithList");
		System.out.println("Main.symbols:");
		System.out.println(Main.symbols);
		System.out.println("Main.symbols.size():" + Main.symbols.size());
		List<MarketOrder> marketOrderList = new ArrayList<MarketOrder>();
		double amt = 0;
		double startingAmt = 0;
		double oldAmt = 0;
		sequence.add(sequence.get(0));
//		Collections.reverse(sequence);
//		System.out.println("reversed sequence:");
		System.out.println(sequence);
	    for(int i = 0; i< sequence.size()-1 ; i++) {
	    		String key1;
	    		String key2;
	    		String symbol;
	    		String orderType;
    			key1 = sequence.get(i).toString().toUpperCase();
	    		key2 = sequence.get(i+1).toString().toUpperCase();
	    		symbol = key1+key2;
				double rate = exchangeRates.get(symbol);
				System.out.println("We got " + rate + " for " +symbol);
	    		if(i==0) {
		    		if(!Main.symbols.contains(symbol)) {
						orderType = "buy";
						symbol = key2+key1;
						startingAmt = CurrencyConverter.convertBTCToCoin(key1, amountBTC); //getting amount of coin with respect to amountBTC
						System.out.println(amountBTC + " BTC = " + startingAmt + " " + key1);
						amt = startingAmt * rate;
						System.out.println(startingAmt + " " + key1 + " * " + rate + " = " +amt + " " + key2);
						Trade trade = new Trade(amt, key2, key1, orderType);
			    		marketOrderList.add(trade.createMarketOrder());
					} else {
						orderType = "sell";
						startingAmt = CurrencyConverter.convertBTCToCoin(key1, amountBTC); //getting amount of coin with respect to amountBTC
						System.out.println(amountBTC + " BTC = " + startingAmt + " " + key1);
						amt = startingAmt * rate;
						System.out.println(startingAmt + " " + key1 + " * " + rate + " = " +amt + " " + key2);
						Trade trade = new Trade(startingAmt, key1, key2, orderType);
			    		marketOrderList.add(trade.createMarketOrder());
					}
		    		System.out.println();
	    		} else {
		    		if(!Main.symbols.contains(symbol)) {
						orderType = "buy";
						symbol = key2+key1;
						oldAmt = amt;
						amt = rate * oldAmt;
						System.out.println(oldAmt+ " " + key1 + " * " + rate + " = " + amt + " " + key2);
						Trade trade = new Trade(amt, key2, key1, orderType);
			    		marketOrderList.add(trade.createMarketOrder());
					} else {
						orderType = "sell";
						oldAmt = amt;
						amt = rate * oldAmt;
						System.out.println(oldAmt + " " + key1 + " * " + rate + " = " + amt + " " + key2);
						Trade trade = new Trade(oldAmt, key1, key2, orderType);
			    		marketOrderList.add(trade.createMarketOrder());
					}
		    		System.out.println();
	    		}
	    }
	    if(Main.trade) {
	    	for(MarketOrder order: marketOrderList) {
	    		String orderReturnVal = tradeService.placeMarketOrder(order);
	    		System.out.println("Market Order Return Value: " + orderReturnVal);
	    	}
	    };
	}
	
	public void setExchangeRates(HashMap<String,Double> er) {
		exchangeRates = er;
//		System.out.println(exchangeRates);
	}
	
	public void setTransactionAmounts(HashMap<String,Double> ta) {
		transactionAmounts = ta;
//		System.out.println(exchangeRates);
	}
	
	public void setVertices(ArrayList<Vertex> v) {
		vertices = v;
	}
	
	public void convertCoinsToBTC(double ratio) throws IOException {
		Wallet wallet = info.getWallet();
		ArrayList<MarketOrder> convertCoinToBTCList = new ArrayList<MarketOrder>();
		for (Vertex v : vertices) {
			if(!v.name.toUpperCase().equals("BTC")) {
				if(v.name.equals("USDT")) {
					String key1 = "BTC";
					String key2 = v.name;
					double amount = CurrencyConverter.convertCoinToBTC(v.name, wallet.getBalance(new Currency(v.name)).getAvailable().doubleValue()*ratio);
					// first, we get the name of the currency with v.name. then we convert it to a new Currency so we can grab it from wallet. Then we convert the amount coin available		
					// in the wallet to equivalent amount of BTC using CurrencyConverter's convertCoinToBTC method. We then create a trade by creating a new trade with "buy" and pair.
					Trade trade = new Trade(amount, key1, key2, "buy");
					// adding the trade to convertCoinToBTCList for execution by tradeService. We need to execute a list of market orders hence trade.createMarketOrder()
					convertCoinToBTCList.add(trade.createMarketOrder());
				} else {
					String key1 = v.name;
					String key2 = "BTC";
					double amount = wallet.getBalance(new Currency(v.name)).getAvailable().doubleValue()*ratio;
					// first, we get the name of the currency with v.name. then we convert it to a new Currency so we can grab it from wallet. Then we convert the amount coin available		
					// in the wallet to equivalent amount of BTC using CurrencyConverter's convertCoinToBTC method. We then create a trade by creating a new trade with "buy" and pair.
					Trade trade = new Trade(amount, key1, key2, "sell");
					// adding the trade to convertCoinToBTCList for execution by tradeService. We need to execute a list of market orders hence trade.createMarketOrder()
					convertCoinToBTCList.add(trade.createMarketOrder());
				}
			}
		}
		System.out.println("convertCoinsToBTCList size: " +convertCoinToBTCList.size());
		System.out.println(convertCoinToBTCList);
	    if(Main.trade) {
	    	for(MarketOrder order: convertCoinToBTCList) {
	    		String orderReturnVal = tradeService.placeMarketOrder(order);
	    		System.out.println("Market Order Return Value: " + orderReturnVal);
	    	}
	    };
	}
	
	public void convertBTCToCoins(double amountBTC) throws IOException {
		System.out.println("Converting " + amountBTC + " BTC to all availabe cryptocurrencies");
		double btcPerCoin = amountBTC/(vertices.size()-1);
		ArrayList<MarketOrder> convertBTCToCoinsList = new ArrayList<MarketOrder>();
		for (Vertex v : vertices) {
			if(!v.name.toUpperCase().equals("BTC")) {
				if(v.name.equals("USDT")) {
					String key1 = "BTC";
					String key2 = v.name;
					// first, we get the name of the currency with v.name. then we convert it to a new Currency so we can grab it from wallet. Then we convert the amount coin available		
					// in the wallet to equivalent amount of BTC using CurrencyConverter's convertCoinToBTC method. We then create a trade by creating a new trade with "buy" and pair.
					Trade trade = new Trade(btcPerCoin, key1, key2, "sell");
					// adding the trade to convertCoinToBTCList for execution by tradeService. We need to execute a list of market orders hence trade.createMarketOrder()
					convertBTCToCoinsList.add(trade.createMarketOrder());
				} else {
					String key1 = v.name;
					String key2 = "BTC";
					double amount = CurrencyConverter.convertBTCToCoin(v.name, btcPerCoin);
					Trade trade = new Trade(amount, key1, key2, "buy");
					convertBTCToCoinsList.add(trade.createMarketOrder());
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
	    };
	}
}
