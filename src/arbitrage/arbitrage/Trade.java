package arbitrage;
import java.math.BigDecimal;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.MarketOrder;

public class Trade {
	
	protected double amount;
	protected String pair;
	protected String buyOrSell;
	
	public Trade(double amount, String pair, String buyOrSell) {
		if(!Main.symbols.contains(pair)) {
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
			System.out.println("Invalid symbol for Trade !!!!!!!!!!!!!!!!!!!!!!! WARNING");
		}
		this.amount = amount;
		this.pair = pair;
		this.buyOrSell = buyOrSell;
	}
	
	public MarketOrder createMarketOrder(){
		String key1 = pair.substring(0, 3);
		String key2 = pair.substring(3, 6);
		CurrencyPair pair = new CurrencyPair(key1, key2);
		if(buyOrSell.toUpperCase().equals("BUY")) {
			System.out.println("creating a BUY order");
		    MarketOrder marketOrder = new MarketOrder.Builder(OrderType.BID, pair).originalAmount(BigDecimal.valueOf(amount)).build();
//		    tradeService.placeBitfinexMarketOrder(marketOrder, BitfinexOrderType.MARKET);
		    System.out.println(marketOrder);
		    return marketOrder;	    
		} else {
			System.out.println("creating a SELL order");
			MarketOrder marketOrder = new MarketOrder.Builder(OrderType.ASK, pair).originalAmount(BigDecimal.valueOf(amount)).build();
//		    tradeService.placeBitfinexMarketOrder(marketOrder, BitfinexOrderType.MARKET);
			System.out.println(marketOrder);
		    return marketOrder;
		}
	}
}
