package gdax;
import java.math.BigDecimal;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.LimitOrder;

public class Trade {
	
	protected double amount;
	protected String key1;
	protected String key2;
	protected String buyOrSell;
	
	public Trade(double amount, String key1, String key2, String buyOrSell) {
		String pair = key1+key2;
		System.out.println("PAIRXYZ:::" + pair);
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
	
	public LimitOrder createLimitOrder() {
		CurrencyPair pair = new CurrencyPair(key1, key2);
		String symbol = "";
		if(buyOrSell.toUpperCase().equals("BUY")) {
			BigDecimal amt = CurrencyConverter.toPrecision(amount,Main.sigDigs.get(key1+key2));
			symbol = key2 + key1;
			double exchangePrice = Main.exchangePrices.get(symbol)*.9999;
			BigDecimal price = CurrencyConverter.toPrecision(exchangePrice,Main.sigDigsForPricing.get(key1+key2));
		    LimitOrder limitOrder = new LimitOrder.Builder(OrderType.BID, pair).originalAmount(amt).limitPrice(price).build();
		    System.out.println(limitOrder);
		    return limitOrder;
		} else {
			symbol = key1 + key2;
			BigDecimal amt = CurrencyConverter.toPrecision(amount,Main.sigDigs.get(symbol));
			double exchangePrice = Main.exchangePrices.get(symbol)*1.0001;
			BigDecimal price = CurrencyConverter.toPrecision(exchangePrice,Main.sigDigsForPricing.get(symbol));
			LimitOrder limitOrder = new LimitOrder.Builder(OrderType.ASK, pair).originalAmount(amt).limitPrice(price).build();
			System.out.println(limitOrder);
		    return limitOrder;
		}
	}
	
}
