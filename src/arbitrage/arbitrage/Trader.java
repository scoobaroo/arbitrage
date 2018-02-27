package arbitrage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.service.account.AccountService;

public class Trader {
	public Exchange bitfinex;
	
	public Trader() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("/Users/suejanehan/workspace/config.properties");
			prop.load(input);
			System.out.println(prop.getProperty("apiKey"));
			System.out.println(prop.getProperty("apiSecret"));
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
		ExchangeSpecification exSpec = new BitfinexExchange().getDefaultExchangeSpecification();
		exSpec.setUserName("");
		exSpec.setApiKey("");
		exSpec.setSecretKey("");
		bitfinex = ExchangeFactory.INSTANCE.createExchange(exSpec);
	}
	
	public void getAccountInfo() throws IOException {
		AccountService accountService = bitfinex.getAccountService();
		AccountInfo accountInfo = accountService.getAccountInfo();
		System.out.println(accountInfo.toString());
	}
	
	public void executeTradeSequence(ArrayList<Vertex> sequence) throws IOException {
	    for(int i = 0; i< sequence.size(); i++) {
	    		String key1;
	    		String key2;
	    		String symbol;
	    		String orderType;
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
				orderType = "bid";
			} else {
				orderType = "ask";
			}
	    		Trade trade = new Trade(bitfinex, new BigDecimal(1), symbol, orderType);
	    		trade.execute();
	    }
	}
}
