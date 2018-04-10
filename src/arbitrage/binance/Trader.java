package binance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.bitfinex.v1.BitfinexOrderType;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.account.AccountService;
import org.knowm.xchange.service.trade.TradeService;

public class Trader {
	protected Exchange exchange;
	protected TradeService tradeService;
	protected HashMap<String,Double> exchangeRates;
	protected HashMap<String,Double> transactionAmounts;
	protected ArrayList<Vertex> vertices;
	
	public Trader(Exchange ex) {
		this.exchange = ex;
//		Properties prop = new Properties();
//		InputStream input = null;
//		try {
//			input = new FileInputStream("/Users/suejanehan/workspace/config.properties");
//			prop.load(input);
//			System.out.println(prop.getProperty("apiKey"));
//			System.out.println(prop.getProperty("apiSecret"));
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		} finally {
//			if (input != null) {
//				try {
//					input.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		ExchangeSpecification exSpec = new BinanceExchange().getDefaultExchangeSpecification();
//		exSpec.setUserName("");
//		exSpec.setApiKey("");
//		exSpec.setSecretKey("");
//		Exchange binance = ExchangeFactory.INSTANCE.createExchange(exSpec);
//		tradeService = binance.getTradeService();
	}
	
	public AccountInfo getAccountInfo() throws IOException {
		AccountService accountService = exchange.getAccountService();
		AccountInfo accountInfo = accountService.getAccountInfo();
		System.out.println(accountInfo.toString());
		return accountInfo;
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
						startingAmt = CurrencyConverter.convertBTCToCoin(key1, amountBTC);
						System.out.println(amountBTC + " BTC = " + startingAmt + " " + key1);
						amt = startingAmt * rate;
						System.out.println(startingAmt + " " + key1 + " * " + rate + " = " +amt + " " + key2);
						Trade trade = new Trade(amt, key2, key1, orderType);
			    		marketOrderList.add(trade.createMarketOrder());
					} else {
						orderType = "sell";
						startingAmt = CurrencyConverter.convertBTCToCoin(key1, amountBTC);
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
//	    if(Main.trade) tradeService.placeBitfinexOrderMulti(marketOrderList, BitfinexOrderType.MARKET);
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
	
	public void convertCoinsToBTC() throws IOException {
		AccountInfo accountInfo = getAccountInfo();
		Wallet wallet = accountInfo.getWallet();
		System.out.println(wallet.toString());
		ArrayList<MarketOrder> convertCoinToBTCList = new ArrayList<MarketOrder>();
		for (Vertex v : vertices) {
			if(!v.name.toUpperCase().equals("BTC")) {
				String key1 = "BTC";
				String key2 = v.name;
				// first, we get the name of the currency with v.name. then we convert it to a new Currency so we can grab it from wallet. Then we convert the amount coin available		
				// in the wallet to equivalent amount of BTC using CurrencyConverter's convertCoinToBTC method. We then create a trade by creating a new trade with "buy" and pair.
				Trade trade = new Trade(CurrencyConverter.convertCoinToBTC(v.name, wallet.getBalance(new Currency(v.name)).getAvailable().doubleValue()), key1, key2, "buy");
				// adding the trade to convertCoinToBTCList for execution by tradeService. We need to execute a list of market orders hence trade.createMarketOrder()
				convertCoinToBTCList.add(trade.createMarketOrder());
			}
		}
		System.out.println(convertCoinToBTCList.size());
		System.out.println(convertCoinToBTCList);
//		if(Main.trade) tradeService.placeBitfinexOrderMulti(convertCoinToBTCList, BitfinexOrderType.MARKET);
	}
	
	public void convertBTCToCoins(double amountBTC) throws IOException {
		System.out.println("Converting " + amountBTC + " BTC to all availabe cryptocurrencies");
		double btcPerCoin = amountBTC/(vertices.size()-1);
		ArrayList<MarketOrder> convertBTCToCoinList = new ArrayList<MarketOrder>();
		for (Vertex v : vertices) {
			if(!v.name.toUpperCase().equals("BTC")) {
				String key1 = "BTC";
				String key2 = v.name;
				Trade trade = new Trade(btcPerCoin, key1, key2, "sell");
				convertBTCToCoinList.add(trade.createMarketOrder());
			}
		}
		System.out.println("Converting to " + convertBTCToCoinList.size() + " coins");
		System.out.println(convertBTCToCoinList);
//		if(Main.trade) tradeService.placeBitfinexOrderMulti(convertBTCToCoinList, BitfinexOrderType.MARKET);
	}
}
