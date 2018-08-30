package binance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class Utilities {

	public static void addDataToCSV(String filePath, String[] data)
	{
	    File file = new File(filePath);
	    try {
	        FileWriter outputfile = new FileWriter(file, true);
	        CSVWriter writer = new CSVWriter(outputfile);
	        String[] header = { "Date and Time", "Trade Sequence", "Amount of BTC after Arbitrage", "Account value w/o Arbitrage", "Exchange" };
//	        writer.writeNext(header);
	        writer.writeNext(data);
	        writer.close();
	        System.out.println("Wrote to file");
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
