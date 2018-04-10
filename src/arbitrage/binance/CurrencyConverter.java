package binance;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

public class CurrencyConverter {
	
	protected static HashMap<String,Double> exchangeRates;
	
	public static void setExchangeRates(HashMap<String,Double> er) {
		exchangeRates = er;
	}
	
    public static double convertUSDToCoin(String currency, double amountUSD) {
		if(currency.toUpperCase().equals("USDT")) {
			return amountUSD;
		} else {
			String symbol = "USDT"+currency.toUpperCase();
	    	double rate = exchangeRates.get(symbol);
	    	double amount = rate * amountUSD;
			return amount;
		}
    }
    
    public static double convertCoinToUSD(String currency, double amountCurrency) {
		if(currency.toUpperCase().equals("USDT")) {
			return amountCurrency;
		} else {
			String symbol = currency.toUpperCase() + "USDT"; 
			double rate = exchangeRates.get(symbol);
	    	double amount = rate * amountCurrency;
			return amount;
		}
    }
    
    public static double convertBTCToCoin(String currency, double amountBTC) {
    	System.out.println(exchangeRates);
    	if(currency.toUpperCase().equals("BTC")){
    		return amountBTC;
    	}
		String symbol = "BTC"+currency.toUpperCase(); 
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountBTC;
		return amount;
    }
    
    public static double convertCoinToBTC(String currency, double amountCurrency) {
    	System.out.println(exchangeRates);
    	if(currency.toUpperCase().equals("BTC")){
    		return amountCurrency;
    	}
		String symbol = currency.toUpperCase() + "BTC"; 
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountCurrency;
		return amount;
    }
    
    public static double convertCoinToCoin(String currency1, String currency2, double amountCoin) {
    	if(currency1.equals(currency2)) {
    		System.out.println("Can't convert between same currency");
    		return amountCoin;
    	}
		String symbol = currency1.toUpperCase() + currency2.toUpperCase();
		double rate = exchangeRates.get(symbol);
		double amount = rate * amountCoin;
		return amount;
    }
    
    public static double round(double d, int sigDigits) {
	    BigDecimal bd = new BigDecimal(d);
	    bd = bd.round(new MathContext(sigDigits));
	    double rounded = bd.doubleValue();
	    return rounded;
    }
}
