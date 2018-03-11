package arbitrage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;
import org.knowm.xchange.bitfinex.v1.BitfinexOrderType;
import org.knowm.xchange.bitfinex.v1.service.BitfinexTradeServiceRaw;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.account.AccountService;

public class Trader {
	protected Exchange exchange;
	protected BitfinexTradeServiceRaw tradeService;
	protected HashMap<String,Double> exchangeRates;
	protected ArrayList<Vertex> vertices;
	
	public Trader(Exchange ex) {
		this.exchange = ex;
		tradeService = (BitfinexTradeServiceRaw) exchange.getTradeService();
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
//		ExchangeSpecification exSpec = new BitfinexExchange().getDefaultExchangeSpecification();
//		exSpec.setUserName("");
//		exSpec.setApiKey("");
//		exSpec.setSecretKey("");
//		bitfinex = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}
	
	public AccountInfo getAccountInfo() throws IOException {
		AccountService accountService = exchange.getAccountService();
		AccountInfo accountInfo = accountService.getAccountInfo();
		System.out.println(accountInfo.toString());
		return accountInfo;
	}
	
	public void executeTradeSequence(ArrayList<Vertex> sequence, double amountUSD) throws IOException {
		ArrayList<MarketOrder> marketOrderList = new ArrayList<MarketOrder>();
	    for(int i = 0; i< sequence.size(); i++) {
	    		String key1;
	    		String key2;
	    		String symbol;
	    		String orderType;
	    		BigDecimal amt;
	    		if(i==sequence.size()-1) {
	    			//linking up last and first
	    			key1 = sequence.get(sequence.size()-1).toString().toLowerCase(); 
	    			key2 = sequence.get(0).toString().toLowerCase();;
	    			symbol = key1+key2;
	    		} else {
	    			key1 = sequence.get(i).toString().toLowerCase();
		    		key2 = sequence.get(i+1).toString().toLowerCase();
		    		symbol = key1+key2;
	    		}
	    		if(!Main.symbols.contains(symbol)) {
				orderType = "buy";
				symbol = key2+key1;
				amt = CurrencyConverter.convertUSDToCoin(key2, amountUSD);
			} else {
				orderType = "sell";
				amt = CurrencyConverter.convertUSDToCoin(key1, amountUSD);
			}
	    		Trade trade = new Trade(exchange, amt, symbol, orderType);
	    		marketOrderList.add(trade.createMarketOrder());
	    }
//	    tradeService.placeBitfinexOrderMulti(marketOrderList, BitfinexOrderType.MARKET);
	}
	
	public void setExchangeRates(HashMap<String,Double> er) {
		exchangeRates = er;
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
				String pair = "btc"+v.name;
				Trade trade = new Trade(exchange, wallet.getBalance(new Currency(v.name)).getAvailable(), pair, "buy");
				convertCoinToBTCList.add(trade.createMarketOrder());
			}
		}
		System.out.println(convertCoinToBTCList.size());
		System.out.println(convertCoinToBTCList);
//		tradeService.placeBitfinexOrderMulti(convertCoinToBTCList, BitfinexOrderType.MARKET);
	}
	public void convertBTCToCoins(double amountBTC) throws IOException {
		System.out.println("Converting " + amountBTC + " BTC to all availabe cryptocurrencies");
		double btcPerCoin = amountBTC/(vertices.size()-1);
		ArrayList<MarketOrder> convertBTCToCoinList = new ArrayList<MarketOrder>();
		for (Vertex v : vertices) {
			if(!v.name.toUpperCase().equals("BTC")) {
				String pair = "btc"+v.name;
				Trade trade = new Trade(exchange, new BigDecimal(btcPerCoin), pair, "sell");
				convertBTCToCoinList.add(trade.createMarketOrder());
			}
		}
		System.out.println(convertBTCToCoinList.size());
		System.out.println(convertBTCToCoinList);
//		tradeService.placeBitfinexOrderMulti(convertBTCToCoinList, BitfinexOrderType.MARKET);
	}
}
