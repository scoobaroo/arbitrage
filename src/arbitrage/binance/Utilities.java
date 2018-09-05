package binance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.opencsv.CSVWriter;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.Trade;

public class Utilities {
	
	public static void addDataToCSV(String filePath, String[] data)
	{
	    File file = new File(filePath);
	    try {
	        FileWriter outputfile = new FileWriter(file, true);
	        CSVWriter writer = new CSVWriter(outputfile);
//	        String[] header = { "Date and Time", "Trade Sequence", "Amount of BTC after Arbitrage", "Account value w/o Arbitrage", "Exchange" };
//	        writer.writeNext(header);
	        writer.writeNext(data);
	        writer.close();
	        System.out.println("Wrote to file");
	    }
	    catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void getAccountBalance(String currency) throws UnirestException {
		Properties prop = new Properties();
		InputStream input = null;
		String apiKey = "", apiSecret = "";
		try {
			input = new FileInputStream("/Users/suejanehan/workspace/binanceConfig.properties");
			prop.load(input);
//			System.out.println(prop.getProperty("apiKey"));
//			System.out.println(prop.getProperty("apiSecret"));
			apiKey = prop.getProperty("apiKey");
			apiSecret = prop.getProperty("apiSecret");
		} catch (IOException ex) { ex.printStackTrace(); } finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) { e.printStackTrace(); }
			}
		}
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, apiSecret);
		BinanceApiRestClient client = factory.newRestClient();
		System.out.println(client.getAccount().getBalances());
		
	}
}
