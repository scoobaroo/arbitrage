package arbitrage;
import java.io.IOException;
import java.math.BigDecimal;

import org.knowm.xchange.bitfinex.v1.BitfinexOrderType;
import org.knowm.xchange.bitfinex.v1.service.BitfinexTradeServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.MarketOrder;

public class Trade {
	
	protected BigDecimal amount;
	protected String pair;
	protected String buyOrSell;
	
	public Trade(BigDecimal amount, String pair, String buyOrSell) {
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
		    MarketOrder marketOrder = new MarketOrder.Builder(OrderType.BID, pair).originalAmount(amount).build();
//		    tradeService.placeBitfinexMarketOrder(marketOrder, BitfinexOrderType.MARKET);
		    System.out.println(marketOrder);
		    return marketOrder;	    
		} else {
			System.out.println("creating a SELL order");
			MarketOrder marketOrder = new MarketOrder.Builder(OrderType.ASK, pair).originalAmount(amount).build();
//		    tradeService.placeBitfinexMarketOrder(marketOrder, BitfinexOrderType.MARKET);
			System.out.println(marketOrder);
		    return marketOrder;
		}
	}
}
