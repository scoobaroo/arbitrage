package binance;
import java.math.BigDecimal;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.MarketOrder;

public class Trade {
	
	protected double amount;
	protected String key1;
	protected String key2;
	protected String buyOrSell;
	
	public Trade(double amount, String key1, String key2, String buyOrSell) {
		String pair = key1+key2;
		if(!Main.symbols.contains(pair)) {
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
		}
		this.amount = amount;
		this.key1 = key1;
		this.key2 = key2;
		this.buyOrSell = buyOrSell;
	}
	
	public MarketOrder createMarketOrder(){
		CurrencyPair pair = new CurrencyPair(key1, key2);
		String symbol = key1+key2;
		if(buyOrSell.toUpperCase().equals("BUY")) {
			System.out.println("creating a BUY order");
			BigDecimal amt = CurrencyConverter.toPrecision(amount,Main.sigDigs.get(symbol));
		    MarketOrder marketOrder = new MarketOrder.Builder(OrderType.BID, pair).originalAmount(amt).build();
		    System.out.println(marketOrder);
		    return marketOrder;
		} else {
			System.out.println("creating a SELL order");
			BigDecimal adjustedAmount = new BigDecimal(0);
//			if(key1.equals("BTC")||key1.equals("ETH")||key1.equals("LTC")||key1.equals("BCC")) { // this is strictly for BTC/USDT, ETH/USDT, LTC/USDT, BCC/USDT pairs
//				if(key2.equals("USDT")) {
//					adjustedAmount = CurrencyConverter.toPrecisionForBtcAndEth(amount,Main.sigDigs.get(symbol));
//				}
//			} else {
				adjustedAmount = CurrencyConverter.toPrecision(amount,Main.sigDigs.get(symbol));
//			}
			MarketOrder marketOrder = new MarketOrder.Builder(OrderType.ASK, pair).originalAmount(adjustedAmount).build();
			System.out.println(marketOrder);
		    return marketOrder;
		}
	}
	
	public MarketOrder createMarketOrderForExchanging(){
		CurrencyPair pair = new CurrencyPair(key1, key2);
		String symbol = key1+key2;
		if(buyOrSell.toUpperCase().equals("BUY")) {
			System.out.println("creating a BUY order");
			BigDecimal adjustedAmount = CurrencyConverter.toPrecision(amount,Main.sigDigs.get(symbol));
			if(adjustedAmount.compareTo(BigDecimal.ZERO)!=0) {
			    MarketOrder marketOrder = new MarketOrder.Builder(OrderType.BID, pair).originalAmount(adjustedAmount).build();
			    System.out.println(marketOrder);
			    return marketOrder;
			}
		} else {
			System.out.println("creating a SELL order");
			BigDecimal adjustedAmount = CurrencyConverter.toPrecision(amount,Main.sigDigs.get(symbol));
			if(adjustedAmount.compareTo(BigDecimal.ZERO)!=0) {
				MarketOrder marketOrder = new MarketOrder.Builder(OrderType.ASK, pair).originalAmount(adjustedAmount).build();
				System.out.println(marketOrder);
			    return marketOrder;
			}
		}
		return null;
	}
}
