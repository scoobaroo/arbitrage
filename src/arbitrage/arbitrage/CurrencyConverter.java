package arbitrage;

import java.math.BigDecimal;
import java.util.HashMap;

public class CurrencyConverter {
	
	protected static HashMap<String,Double> exchangeRates;
	
	public static void setExchangeRates(HashMap<String,Double> er) {
		exchangeRates = er;
	}
	
    public static double convertUSDToCoin(String currency, double amountUSD) {
		String symbol = "usd"+currency.toLowerCase();
    	double rate = exchangeRates.get(symbol);
    	double amount = rate * amountUSD;
		return amount;
    }
    
    public static double convertCoinToUSD(String currency, double amountCoin) {
		String symbol = currency.toLowerCase() + "usd"; 
		double rate = exchangeRates.get(symbol);
    	double amount = rate * amountCoin;
		return amount;
    }
    
    public static double convertBTCToCoin(String currency, double amountBTC) {
		String symbol = "btc"+currency.toLowerCase(); 
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountBTC;
		return amount;
    }
    
    public static double convertCoinToBTC(String currency, double amountCoin) {
		String symbol = currency.toLowerCase() + "btc"; 
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountCoin;
		return amount;
    }
    
    public static double convertCoinToCoin(String currency1, String currency2, double amountCoin) {
		String symbol = currency1.toLowerCase() + currency2.toLowerCase();
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountCoin;
		return amount;
    }
}
