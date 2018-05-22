package testing;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.knowm.xchange.binance.dto.trade.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import binance.Main;

public class LimitOrderTest {
	List<LimitOrder> orders;
	@Before
	public void setUp() throws Exception {
		final Type TOKEN_TYPE = new TypeToken<List<LimitOrder>>() {}.getType();
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader("limitOrders.json"));
		orders = gson.fromJson(reader, TOKEN_TYPE); // contains the whole reviews list
		for(LimitOrder order:orders) {
			System.out.println(order);
		}
	}

	@Test
	public void test() {
		double startingAmt;
		double endingAmt;
		org.knowm.xchange.dto.Order.OrderType type = orders.get(0).getType();
		if(type==org.knowm.xchange.dto.Order.OrderType.ASK) {
			startingAmt = orders.get(0).getOriginalAmount().doubleValue();
		} else {
			startingAmt = orders.get(0).getOriginalAmount().doubleValue() * orders.get(0).getLimitPrice().doubleValue();
		}
		System.out.println("startingAmt: "+startingAmt);
		org.knowm.xchange.dto.Order.OrderType endingType = orders.get(orders.size()-1).getType();
		if(endingType==org.knowm.xchange.dto.Order.OrderType.BID) {
			endingAmt = orders.get(orders.size()-1).getOriginalAmount().doubleValue();
		} else {
			endingAmt = orders.get(orders.size()-1).getOriginalAmount().doubleValue() * orders.get(orders.size()-1).getLimitPrice().doubleValue();
		}
		System.out.println("endingAmt: " + endingAmt);
		assertTrue("Final !> starting" , endingAmt>startingAmt);
	}

}
