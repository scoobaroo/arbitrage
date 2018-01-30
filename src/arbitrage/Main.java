package arbitrage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class Main {
	private static String [] pairings = {
	    "btcusd","ltcusd","ltcbtc","ethusd","ethbtc","etcbtc","etcusd","rrtusd","rrtbtc","zecusd","zecbtc","xmrusd","xmrbtc",
	    "dshusd","dshbtc","bccbtc","bcubtc","bccusd","bcuusd","btceur","xrpusd","xrpbtc","iotusd","iotbtc","ioteth","eosusd",
	    "eosbtc","eoseth","sanusd","sanbtc","saneth","omgusd","omgbtc","omgeth","bchusd","bchbtc","bcheth","neousd","neobtc",
	    "neoeth","etpusd","etpbtc","etpeth","qtmusd","qtmbtc","qtmeth","bt1usd","bt2usd","bt1btc","bt2btc","avtusd","avtbtc",
	    "avteth","edousd","edobtc","edoeth","btgusd","btgbtc","datusd","datbtc","dateth","qshusd","qshbtc","qsheth","yywusd",
	    "yywbtc","yyweth" 
	};
	
	private static HashMap<String,Double> exchangeRates;
	private static HashMap<String,Double> negExchangeRates;

	public static void getExchangeRates() throws UnirestException {
		exchangeRates = new HashMap<String, Double>();
		negExchangeRates = new HashMap<String, Double>();
		for(String pair : pairings) {
			//System.out.println("Current url:" + "https://btc-e.com/api/3/ticker/" + pair);
//				HttpResponse<JsonNode> jsonResponse = Unirest.get("https://btc-e.com/api/3/ticker/" + pair).asJson();
//				exchangeRates.put(pair, rate);
//				negExchangeRates.put(pair, -Math.log(rate));
			//System.out.println(pair + ":" + -Math.log(rate));
		}
	}
	
	public static void main(String[] args) throws UnirestException, JsonParseException, IOException{
		getExchangeRates();
        //creating set of vertices with currencies as their value
        Vertex BTC =new Vertex(CryptoCurrency.BTC, "BTC");
        Vertex USD =new Vertex(CryptoCurrency.USD, "USD");
        Vertex LTC =new Vertex(CryptoCurrency.LTC, "LTC");
        Vertex EUR =new Vertex(CryptoCurrency.EUR, "EUR");
        Vertex DSH =new Vertex(CryptoCurrency.DSH, "DSH");
        Vertex ETH =new Vertex(CryptoCurrency.ETH, "ETH");

        //creating ArrayList of vertexes to use in bellman ford
        ArrayList<Vertex> currencies = new ArrayList<Vertex>();
        currencies.add(BTC); //0
        currencies.add(USD); //1
        currencies.add(LTC); //2 
        currencies.add(EUR); //3
        currencies.add(DSH); //4
        currencies.add(ETH); //5
        //creating edges to use in bellman ford
       
        //creating list of these edges
        ArrayList<Edge> edges = new ArrayList<Edge>();

              
        ArrayList<Edge> allEdges = new ArrayList<Edge>();
        //creating new edges for reversing directions of edges with new weights, sources, and destinations
        for (int i = 0; i< edges.size() ; i++){
            allEdges.add(edges.get(i));
            Edge e = new Edge(edges.get(i).dest,edges.get(i).src, -edges.get(i).weight);
            allEdges.add(e);
            //System.out.println(edges.get(i).weight);
        }

        Graph graph = new Graph(currencies,allEdges);
//      Scanner reader = new Scanner(System.in);  // Reading from System.in
//      System.out.println("Enter starting currency: ");
//      String n = reader.next();
//      Vertex src = graph.findSource(n,graph.vertices);
//      graph.BellmanFord(graph, src);
        Set<String> cryptocurrencies = new HashSet<>();
        for(String pair : pairings) {
        		String c = pair.substring(0,3);
        		String c2 = pair.substring(3,6);
        		String currency = c.toUpperCase();
        		String currency2 = c2.toUpperCase();
        		cryptocurrencies.add(currency);
        		cryptocurrencies.add(currency2);
        }
        System.out.println(cryptocurrencies);
        List<String> jsonData = Files.readAllLines(Paths.get("/Users/erichan/desktop/cs297/data.json"));
        Gson gson = new Gson();
        gson.toJson(jsonData);
        
        System.out.println(gson);
    }
}