package arbitrage;
import java.io.IOException;
import java.math.BigDecimal;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.bitfinex.v1.BitfinexOrderType;
import org.knowm.xchange.bitfinex.v1.service.BitfinexTradeServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.MarketOrder;

public class Trade {
	
	protected BigDecimal amount;
	protected String pair;
	protected String buyOrSell;
	protected Exchange exchange;
	
	public Trade(Exchange exchange, BigDecimal amount, String pair, String buyOrSell) {
		this.amount = amount;
		this.pair = pair;
		this.buyOrSell = buyOrSell;
		this.exchange = exchange;
	}
	
	public MarketOrder createMarketOrder(){
		String key1 = pair.substring(0, 3);
		String key2 = pair.substring(3, 6);
		CurrencyPair pair = new CurrencyPair(key1, key2);
		if(buyOrSell.toUpperCase().equals("BUY")) {
		    MarketOrder marketOrder = new MarketOrder.Builder(OrderType.BID, pair).originalAmount(amount).build();
//		    tradeService.placeBitfinexMarketOrder(marketOrder, BitfinexOrderType.MARKET);
		    System.out.println("creating a BUY order");
		    System.out.println(marketOrder.toString());
		    return marketOrder;
		    
		} else {
			MarketOrder marketOrder = new MarketOrder.Builder(OrderType.ASK, pair).originalAmount(amount).build();
//		    tradeService.placeBitfinexMarketOrder(marketOrder, BitfinexOrderType.MARKET);
			System.out.println("creating a SELL order");
		    System.out.println(marketOrder.toString());
		    return marketOrder;
		}
	}
}
