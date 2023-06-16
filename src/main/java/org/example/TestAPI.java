package org.example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class TestAPI {
    public static void main(String[] args) throws IOException,ParseException {
        
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Symbol : ");
        String symbol = input.next();
        System.out.print("Enter interval : ");
        String interval = input.next();
        System.out.print("Enter range : ");
        String range = input.next();
        //String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/get-charts?symbol=1155.KL&interval=1mo&range=5y";
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/get-charts?symbol=" + symbol + "&interval=" + interval + "&range=" + range;
        String rapidApiKey = "bcc6146983msh6a97dea2bd8be87p17192cjsn7e94899e2e2d";
        String rapidApiHost = "apidojo-yahoo-finance-v1.p.rapidapi.com";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-RapidAPI-Key", rapidApiKey)
                    .header("X-RapidAPI-Host", rapidApiHost)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            String jsonData = response.body();
            System.out.println(jsonData);
            System.out.println(jsonData.getClass().getName());

            //Map jsonJavaRootObject = new Gson().fromJson(jsonData, Map.class);
            //System.out.println(jsonJavaRootObject.get("chart"));

            String jsonData2 = jsonData.substring(9,jsonData.length()-1);
            System.out.println(jsonData2);

            /////////////
            JSONParser parser = new JSONParser();
            Object object = parser.parse(jsonData2);
            JSONObject mainJsonObj = (JSONObject) object;

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            System.out.println(gson.toJson(object));

            JSONArray array = (JSONArray) mainJsonObj.get("result");
            System.out.println(array);

            for (int i=0;i<array.size();i++){
                JSONObject current = (JSONObject) array.get(i);
                System.out.println(current.toJSONString());
                //String stockName = ((JSONObject) current.get("meta")).get("symbol").toString();
                //System.out.println(stockName);
                String range2 = ((JSONArray) ((JSONObject) current.get("meta")).get("validRanges")).toString();
                System.out.println(range2);
                JSONArray timestamp = (JSONArray) current.get("timestamp");
                System.out.println(timestamp.toString());
            }

//            JSONObject timeSeries = (JSONObject) mainJsonObj.get("timeSeries");
//            JSONArray annualTotalRevenue = (JSONArray) timeSeries.get("annualTotalRevenue");
//            for (Object obj : annualTotalRevenue) {
//                JSONObject jsonObject = (JSONObject) obj;
//            for (Object key : jsonObject.keySet())
//                System.out.println(key + " " + jsonObject.get(key) + " " + jsonObject.get(key).getClass() + "\n");
//                System.out.println(((JSONObject) jsonObject.get("reportedValue")).get("raw"));
//            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}





