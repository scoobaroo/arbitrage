package testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.knowm.xchange.currency.CurrencyPair;

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVWriter;

import binance.Vertex;

public class Main {
	static LinkedHashMap<String,Integer> sigDigs;

	public Main() {
		sigDigs = new LinkedHashMap<String,Integer>();
        String csvFile = "BinanceTradingRule-Master.csv";
        BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] elements = line.split(",");
                String symbol = elements[0].replace("/","");
                sigDigs.put(symbol, Integer.valueOf(elements[1]));
            }
            System.out.println(sigDigs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            return new BigDecimal(String.valueOf(x)).setScale(precision, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(precision, BigDecimal.ROUND_CEILING);
        }
    }
    
	public boolean filterSequence(List<Vertex> sequence) {
		boolean shouldTrade = true;
		sequence.add(sequence.get(0));
		System.out.println(sequence);
		for(int i = 0; i< sequence.size()-1 ; i++) {
    		String key1;
    		String key2;
    		String symbol;
			key1 = sequence.get(i).toString().toUpperCase();
    		key2 = sequence.get(i+1).toString().toUpperCase();
    		symbol = key1+"/"+key2;
    		int sigDig = 999;
    		if(Main.sigDigs.containsKey(symbol)) {
    			sigDig = Main.sigDigs.get(symbol);
    			System.out.println(symbol + " got sigDig of " + sigDig);
    		} else{
    			symbol = key2 + "/" + key1;
    			sigDig = Main.sigDigs.get(symbol);
    			System.out.println(symbol + " got sigDig of " + sigDig);
    		};
    		if(sigDig==0) {
    			System.out.println("we are filtering out " +symbol + " because its sigDig==0");
    			shouldTrade = false;
    		}
		}
		return shouldTrade;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		addDataToCSV("result.csv");
//		final Type TOKEN_TYPE = new TypeToken<HashMap<String,Double>>() {}.getType();
//		Gson gson = new Gson();
//		JsonReader reader = new JsonReader(new FileReader("balances.json"));
//		HashMap<String,Double> balances = gson.fromJson(reader, TOKEN_TYPE); // contains the whole reviews list
//		System.out.println(balances);
//		reader = new JsonReader(new FileReader("exchangeRates.json"));
//		HashMap<String,Double> exchangeRates = gson.fromJson(reader, TOKEN_TYPE); // contains the whole reviews list
//		System.out.println(exchangeRates);
//		double BTCValue = 0;
//		for(Entry<String, Double> entry: balances.entrySet()) {
//			String coinString = entry.getKey();
//			double balance = entry.getValue();
//			double btcValueForCoin = 0;
//			if(!coinString.equals("BTC")) {
//				double exchangeRate = exchangeRates.get(coinString.toUpperCase()+"BTC");
//				btcValueForCoin = balance * exchangeRate;
//			} else {
//				btcValueForCoin = balance;
//			}
//			BTCValue+=btcValueForCoin;
//		}
//		System.out.println("Total Bitcoin Value: " + BTCValue);
//		double num =  0.007373;
//		System.out.println(toPrecision(num,6));
//		System.out.println(toPrecisionForBtcAndEth(num,6));

//		Double d = 0.00001;
//		String text = Double.toString(Math.abs(d));
//		text = text.replace("0","8");
//		int integerPlaces = text.indexOf('.');
//		int decimalPlaces = text.length() - integerPlaces;
//		System.out.println(text);
//		System.out.println(decimalPlaces);
		// TODO Auto-generated method stub
//		sigDigs = new LinkedHashMap<String,Integer>();
//		String s = "ETHBTC, LTCBTC, BNBBTC, NEOBTC, QTUMETH, EOSETH, SNTETH, BNTETH, BCCBTC, GASBTC, BNBETH, BTCUSDT, ETHUSDT, HSRBTC, OAXETH, DNTETH, MCOETH, ICNETH, MCOBTC, WTCBTC, WTCETH, LRCBTC, LRCETH, QTUMBTC, YOYOBTC, OMGBTC, OMGETH, ZRXBTC, ZRXETH, STRATBTC, STRATETH, SNGLSBTC, SNGLSETH, BQXBTC, BQXETH, KNCBTC, KNCETH, FUNBTC, FUNETH, SNMBTC, SNMETH, NEOETH, IOTABTC, IOTAETH, LINKBTC, LINKETH, XVGBTC, XVGETH, SALTBTC, SALTETH, MDABTC, MDAETH, MTLBTC, MTLETH, SUBBTC, SUBETH, EOSBTC, SNTBTC, ETCETH, ETCBTC, MTHBTC, MTHETH, ENGBTC, ENGETH, DNTBTC, ZECBTC, ZECETH, BNTBTC, ASTBTC, ASTETH, DASHBTC, DASHETH, OAXBTC, ICNBTC, BTGBTC, BTGETH, EVXBTC, EVXETH, REQBTC, REQETH, VIBBTC, VIBETH, HSRETH, TRXBTC, TRXETH, POWRBTC, POWRETH, ARKBTC, ARKETH, YOYOETH, XRPBTC, XRPETH, MODBTC, MODETH, ENJBTC, ENJETH, STORJBTC, STORJETH, BNBUSDT, VENBNB, YOYOBNB, POWRBNB, VENBTC, VENETH, KMDBTC, KMDETH, NULSBNB, RCNBTC, RCNETH, RCNBNB, NULSBTC, NULSETH, RDNBTC, RDNETH, RDNBNB, XMRBTC, XMRETH, DLTBNB, WTCBNB, DLTBTC, DLTETH, AMBBTC, AMBETH, AMBBNB, BCCETH, BCCUSDT, BCCBNB, BATBTC, BATETH, BATBNB, BCPTBTC, BCPTETH, BCPTBNB, ARNBTC, ARNETH, GVTBTC, GVTETH, CDTBTC, CDTETH, GXSBTC, GXSETH, NEOUSDT, NEOBNB, POEBTC, POEETH, QSPBTC, QSPETH, QSPBNB, BTSBTC, BTSETH, BTSBNB, XZCBTC, XZCETH, XZCBNB, LSKBTC, LSKETH, LSKBNB, TNTBTC, TNTETH, FUELBTC, FUELETH, MANABTC, MANAETH, BCDBTC, BCDETH, DGDBTC, DGDETH, IOTABNB, ADXBTC, ADXETH, ADXBNB, ADABTC, ADAETH, PPTBTC, PPTETH, CMTBTC, CMTETH, CMTBNB, XLMBTC, XLMETH, XLMBNB, CNDBTC, CNDETH, CNDBNB, LENDBTC, LENDETH, WABIBTC, WABIETH, WABIBNB, LTCETH, LTCUSDT, LTCBNB, TNBBTC, TNBETH, WAVESBTC, WAVESETH, WAVESBNB, GTOBTC, GTOETH, GTOBNB, ICXBTC, ICXETH, ICXBNB, OSTBTC, OSTETH, OSTBNB, ELFBTC, ELFETH, AIONBTC, AIONETH, AIONBNB, NEBLBTC, NEBLETH, NEBLBNB, BRDBTC, BRDETH, BRDBNB, MCOBNB, EDOBTC, EDOETH, WINGSBTC, WINGSETH, NAVBTC, NAVETH, NAVBNB, LUNBTC, LUNETH, TRIGBTC, TRIGETH, TRIGBNB, APPCBTC, APPCETH, APPCBNB, VIBEBTC, VIBEETH, RLCBTC, RLCETH, RLCBNB, INSBTC, INSETH, PIVXBTC, PIVXETH, PIVXBNB, IOSTBTC, IOSTETH, CHATBTC, CHATETH, STEEMBTC, STEEMETH, STEEMBNB, NANOBTC, NANOETH, NANOBNB, VIABTC, VIAETH, VIABNB, BLZBTC, BLZETH, BLZBNB, AEBTC, AEETH, AEBNB, RPXBTC, RPXETH, RPXBNB, NCASHBTC, NCASHETH, NCASHBNB, POABTC, POAETH, POABNB, ZILBTC, ZILETH, ZILBNB, ONTBTC, ONTETH, ONTBNB, STORMBTC, STORMETH, STORMBNB, QTUMBNB, QTUMUSDT, XEMBTC, XEMETH, XEMBNB, WANBTC, WANETH, WANBNB, WPRBTC, WPRETH, QLCBTC, QLCETH, SYSBTC, SYSETH, SYSBNB, QLCBNB, GRSBTC, GRSETH";
//		String integers = "0,2,0,2,2,2,0,3,0,2,0,2,0,2,2,0,2,2,0,2,0,2,0,0,0,2,2,2,2,2,2,2,2,2,2,2,0,0,0,2,2,0,2,0,2,2,0,0,2,0,0,3,3,0,5,0,0,0,0,3,2,2,2,0,0,2,2,2,0,0,2,0,0,5,0,0,0,2,2,0,0,0,2,0,0,0,0,2,2,0,2,2,0,2,3,2,0,0,2,0,0,0,0,2,0,0,2,2,2,0,2,2,0,0,0,2,0,2,0,0,2,0,0,0,0,2,0,0,2,0,0,0,2,2,0,0,0,0,2,9,2,0,2,0,0,0,0,2,0,2,0,0,0,2,0,2,0,2,3,0,0,0,0,0,0,2,2,2,0,0,5,0,2,0,0,0,0,2,0,2,0,0,0,0,3,2,0,0,2,3,3,0,2,0,0,0,2,0,2,0,2,5,0,0,2,0,0,0,2,2,2,2,0,3,2,0,2,2,0,2,2,2,0,3,2,0,2,2,0,0,0,0,2,0,0,3,3,0,3,2,0,2,0,0,3,2,2,0,0,3,0,5,2,2,0,3,0,0,2,2,2,0,3,0,0,2,0,0,0,0,6,0,2,2,0,0,0,2,0,0,0,2,0,0,0,0,0,0,3,2,2,0,2,0,0,0,2,0";
//		String[] symbols = s.replaceAll("\\s+","").split(",");
//		String[] intStrings = integers.split(",");
//		int[] ints = new int[symbols.length];
//		for (int i = 0; i < symbols.length ; i++) {
//			ints[i] = Integer.valueOf(intStrings[i]);
//		}
//		for (int i = 0; i < symbols.length ; i++) {
//			String sym = symbols[i];
//			int significantDigit = ints[i];
//			sigDigs.put(sym, significantDigit);
//		}
//		System.out.println(sigDigs);
//		Scanner reader = new Scanner(System.in);  // Reading from System.in
//		for(String string : symbols) {
//			System.out.println("Enter significant digits for "+ string + ":");	
//			String sigDigitsString = reader.next();
//			int sigDigits = Integer.valueOf(sigDigitsString);
//			sigDigs.put(string, sigDigits);
//			System.out.println(sigDigs);
//		}
//		String intString = "";
//		for (int i: sigDigs.values()) {
//			intString += i+",";
//		}
//		System.out.println(intString);
//		for(String str:symbols) {
//			System.out.print(str+" ");
//		}
	}
}
