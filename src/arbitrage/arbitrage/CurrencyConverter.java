package arbitrage;

import java.math.BigDecimal;
import java.util.HashMap;

public class CurrencyConverter {
	public static HashMap<String,Double> exchangeRates;
	
	public static void setExchangeRates(HashMap<String,Double> er) {
		exchangeRates = er;
	}
	
    public static BigDecimal convertUSDToCoin(String currency, double amountUSD) {
		String symbol = "usd"+currency.toLowerCase();
    		double rate = exchangeRates.get(symbol);
		return new BigDecimal(amountUSD * rate);
    }
    
    public static BigDecimal convertCoinToUSD(String currency, double amountCoin) {
    		String symbol = currency.toLowerCase() + "usd"; 
    		double rate = exchangeRates.get(symbol);
    		return new BigDecimal(rate*amountCoin);
    }
    
    public static BigDecimal convertBTCToCoin(String currency, double amountBTC) {
    		String symbol = "btc"+currency.toLowerCase(); 
		double rate = exchangeRates.get(symbol);
		return new BigDecimal(rate * amountBTC );
    }
    
    public static BigDecimal convertCoinToBTC(String currency, double amountCoin) {
    		String symbol = currency.toLowerCase() + "btc"; 
    		double rate = exchangeRates.get(symbol);
    		return new BigDecimal(rate * amountCoin);
    }
}
