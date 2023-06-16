package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

class Pair<K, V> {
    K key;
    V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}

public class getSymbolNameAndPrice {
 ArrayList<String>symbol = new ArrayList<>();
 ArrayList<String>shortName = new ArrayList<>();
//  double stockPrice;
 String userKeyIn;

    // Get the price of a particular stock in json format
    private String getPrice(String symbol){
        String url = "https://wall-street-warriors-api-um.vercel.app/price?apikey=UM-7c700383fa9d2afff0cb740d8aa832d75b3fe72ff9000cbeb691c38b2b6fced7&symbol=" + symbol + "&days=1";
        String rapidApiKey = "UM-7c700383fa9d2afff0cb740d8aa832d75b3fe72ff9000cbeb691c38b2b6fced7";
        String jsonData=null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            jsonData = response.body();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return jsonData;
    }

    //Extract the stock from json format into currentPrice variable
    public double loadPrice(String symbol) throws ParseException {
        Double stockPrice = 0.0;
        JSONParser parser = new JSONParser();
        Object object = parser.parse(getPrice(symbol));
        JSONObject mainJsonObj = (JSONObject) object;
        JSONObject jsonFile = ((JSONObject) ( (JSONObject)(mainJsonObj.get(symbol))).get("Close"));
        //return (double)((JSONObject) ( (JSONObject)(mainJsonObj.get(symbol))).get("Close")).get("1685696340000");

        ArrayList<Pair<String, Double>> list = new ArrayList<>();
        for (Object key : jsonFile.keySet()) {
            String k = (String) key;
            double v = (double) jsonFile.get(k);
            list.add(new Pair<>(k, v));
        }
        list.sort(Comparator.comparing(p -> p.key));

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile.toString());

            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
            String lastFieldName = null;
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> fieldEntry = fieldsIterator.next();
                lastFieldName = fieldEntry.getKey();
            }

            stockPrice = rootNode.get(lastFieldName).asDouble();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockPrice;
    }

    //Get the all the symbols and names in json format
    private String symbolAndName(){
        String url = "https://wall-street-warriors-api-um.vercel.app/mylist?apikey=UM-7c700383fa9d2afff0cb740d8aa832d75b3fe72ff9000cbeb691c38b2b6fced7";
        String rapidApiKey = "UM-7c700383fa9d2afff0cb740d8aa832d75b3fe72ff9000cbeb691c38b2b6fced7";
        String jsonData=null;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            jsonData = response.body();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return jsonData;
    }

    //Extract the data from json and store all the symbol and company's names
    public void loadSymbolAndName() throws ParseException {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(symbolAndName());
        JSONArray mainJsonArr = (JSONArray) object;
        //System.out.println(mainJsonArr);
        for (Object obj : mainJsonArr) {
            JSONObject tempt = (JSONObject)obj;
            String checkSymbol = (String) tempt.get("symbol");
            String checkName = (String) tempt.get("name");
            if(checkSymbol.charAt(4)=='.'){
                symbol.add(checkSymbol);
                shortName.add(checkName);
                //System.out.println(checkName + "  " + checkSymbol);
            }
        }
    }

    //User key in symbol or company's name
    public String userkeyInSymbolAndName() throws ParseException {
        //Load all the stock symbol into the symbol and shortName ArrayList
        loadSymbolAndName();

        Scanner input = new Scanner(System.in);
        System.out.print("Enter stock's symbol : ");
        userKeyIn = input.next();
        userKeyIn = userKeyIn.toUpperCase();

        while(!symbol.contains(userKeyIn) && !shortName.contains(userKeyIn)){
            System.out.println();
            System.out.println("The symbol and name of the company could not be found");
            System.out.print("Enter stock's symbol : ");
            userKeyIn = input.next();
            userKeyIn = userKeyIn.toUpperCase();
        }

        return userKeyIn;
    }

    // public static void main(String[] args) throws ParseException {
    //     String inputSymbol = userkeyInSymbolAndName();
    //     double price = loadPrice(inputSymbol);
    //     int index = symbol.indexOf(inputSymbol);
    //     String companyName = shortName.get(index);

    //     System.out.printf("%-20s(%s) : %-10.2f",companyName,inputSymbol,price);

    // }
}