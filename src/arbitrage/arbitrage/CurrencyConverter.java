package arbitrage;

import java.math.BigDecimal;
import java.util.HashMap;

public class CurrencyConverter {
	
	protected static HashMap<String,Double> exchangeRates;
	
	public static void setExchangeRates(HashMap<String,Double> er) {
		exchangeRates = er;
	}
	
    public static double convertUSDToCoin(String currency, double amountUSD) {
		if(currency.toLowerCase().equals("usd")) {
			return amountUSD;
		} else {
			String symbol = "usd"+currency.toLowerCase();
	    	double rate = exchangeRates.get(symbol);
	    	double amount = rate * amountUSD;
			return amount;
		}
    }
    
    public static double convertCoinToUSD(String currency, double amountCurrency) {
		if(currency.toLowerCase().equals("usd")) {
			return amountCurrency;
		} else {
			String symbol = currency.toLowerCase() + "usd"; 
			double rate = exchangeRates.get(symbol);
	    	double amount = rate * amountCurrency;
			return amount;
		}
    }
    
    public static double convertBTCToCoin(String currency, double amountBTC) {
    	if(currency.toLowerCase().equals("btc")){
    		return amountBTC;
    	}
		String symbol = "btc"+currency.toLowerCase(); 
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountBTC;
		return amount;
    }
    
    public static double convertCoinToBTC(String currency, double amountCurrency) {
    	if(currency.toLowerCase().equals("btc")){
    		return amountCurrency;
    	}
		String symbol = currency.toLowerCase() + "btc"; 
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountCurrency;
		return amount;
    }
    
    public static double convertCoinToCoin(String currency1, String currency2, double amountCoin) {
    	if(currency1.equals(currency2)) {
    		System.out.println("Can't convert between same currency");
    		return amountCoin;
    	}
		String symbol = currency1.toLowerCase() + currency2.toLowerCase();
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountCoin;
		return amount;
    }
}
