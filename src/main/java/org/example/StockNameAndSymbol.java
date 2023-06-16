package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
public class StockNameAndSymbol {
    ArrayList<String>symbol = new ArrayList<>();
    ArrayList<String>shortName = new ArrayList<>();

    public StockNameAndSymbol(){}

    public String symbolAndName(){
        String url = "https://wall-street-warriors-api-um.vercel.app/mylist?apikey=UM-7c700383fa9d2afff0cb740d8aa832d75b3fe72ff9000cbeb691c38b2b6fced7";
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

    public void loadSymbolAndName() throws ParseException {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(symbolAndName());
        JSONArray mainJsonArr = (JSONArray) object;

        for (Object obj : mainJsonArr) {
            JSONObject tempt = (JSONObject)obj;
            String checkSymbol = (String) tempt.get("symbol");
            String checkName = (String) tempt.get("name");
            if(checkSymbol.charAt(4)=='.'){
                symbol.add(checkSymbol);
                shortName.add(checkName);
            }
        }
    }

}

