package gdax;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

public class CurrencyConverter {
		
    public static double convertBTCToCoin(String currency, double amountBTC) {
    	if(currency.toUpperCase().equals("BTC")){
    		return amountBTC;
    	}
		String symbol = "BTC"+currency.toUpperCase(); 
		double rate = Main.exchangeRates.get(symbol);
		double amount = rate * amountBTC;
		return amount;
    }
    
    public static double convertCoinToBTC(String currency, double amountCurrency) {
    	System.out.println("Inside CurrencyConverter's convertCoinToBTC method, currency to be converted = "+currency);
    	if(currency.toUpperCase().equals("BTC")){
    		return amountCurrency;
    	}
		String symbol = currency.toUpperCase() + "BTC";
		double rate = Main.exchangeRates.get(symbol);
		double amount = rate * amountCurrency;
		return amount;
    }
    
    public static double convertCoinToCoin(String currency1, String currency2, double amountCoin) {
    	if(currency1.equals(currency2)) {
    		System.out.println("Can't convert between same currency");
    		return amountCoin;
    	}
		String symbol = currency1.toUpperCase() + currency2.toUpperCase();
		double rate = Main.exchangeRates.get(symbol);
		double amount = rate * amountCoin;
		return amount;
    }
    
    public static BigDecimal toPrecisionForBtcAndEth(double number, int precision) {
    	if(precision==0) {
    		return new BigDecimal(Math.ceil(number));
    	}
    	if(number>1) {
    		return toPrecision(number,precision);
    	} else {
    		return new BigDecimal(number, new MathContext(precision));
        }
    }
        
    public static BigDecimal toPrecision(double x, int precision) {
        if ( x > 0) {
//            return new BigDecimal(String.valueOf(x)).setScale(precision, BigDecimal.ROUND_HALF_UP);
//            return new BigDecimal(String.valueOf(x)).setScale(precision, BigDecimal.ROUND_HALF_EVEN);
            return new BigDecimal(String.valueOf(x)).setScale(precision, BigDecimal.ROUND_FLOOR);
        } else {
//            return new BigDecimal(String.valueOf(x)).setScale(precision, BigDecimal.ROUND_HALF_UP);
        	return new BigDecimal(String.valueOf(x)).setScale(precision, BigDecimal.ROUND_HALF_EVEN);
        }
    }   
}
