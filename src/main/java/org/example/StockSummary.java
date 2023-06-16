package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

public class StockSummary extends YahooFinanceAPI{
    String shortName;
    String fullName;
    String currentPrice;
    String sector;
    String open;
    String close;
    String changePercentage;
    String dayHigh;
    String dayLow;
    String fiftyTwoLow;
    String fiftyTwoHigh;
    String regularVolume;
    String tenDayVolume;
    String threeMonthVolume;
    String code;
    long currentVolume;
    long tenAvgVolume;
    long threeMonthAvgVolume;
    Date dates = new Date();

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public StockSummary(){}

    public String getStockName() {
        return getSymbol();
    }
    
    public String getData(String stockName){
        String jsonData = null;
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-summary?symbol=" + stockName;
        String rapidApiKey = "fe222b7decmsh397d675353ffbcfp136e2ajsn2b5b23f8fc6f";
        String rapidApiHost = "apidojo-yahoo-finance-v1.p.rapidapi.com";
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-RapidAPI-Key", rapidApiKey)
                    .header("X-RapidAPI-Host", rapidApiHost)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            jsonData = response.body();

//            JSONParser parser = new JSONParser();
//            Object object = parser.parse(jsonData);
//            JSONObject mainJsonObj = (JSONObject) object;
//
//            Gson gson = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .create();
//
//            System.out.println(gson.toJson(object));
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return jsonData;
    }

    public String substring(String content,int index){
        return content.substring(0,content.length()-index);
    }

    public void getCurrentStockPrice(String stock) throws ParseException {
        //setSymbol();
        JSONParser parser = new JSONParser();
        Object object = parser.parse(getData(stock));
        JSONObject mainJsonObj = (JSONObject) object;

        shortName = (String)((JSONObject)mainJsonObj.get("price")).get("shortName");
        fullName = (String)((JSONObject)mainJsonObj.get("price")).get("longName");
        currentPrice = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("regularMarketPrice")).get("fmt");
        sector = (String)((JSONObject)mainJsonObj.get("esgScores")).get("peerGroup");

        open = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("regularMarketOpen")).get("fmt");
        close = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("regularMarketPreviousClose")).get("fmt");
        changePercentage = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("regularMarketChangePercent")).get("fmt");
        dayHigh = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("regularMarketDayHigh")).get("fmt");
        dayLow = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("regularMarketDayLow")).get("fmt");
        fiftyTwoLow = (String)((JSONObject)((JSONObject)mainJsonObj.get("summaryDetail")).get("fiftyTwoWeekLow")).get("fmt");
        fiftyTwoHigh = (String)((JSONObject)((JSONObject)mainJsonObj.get("summaryDetail")).get("fiftyTwoWeekHigh")).get("fmt");

        regularVolume = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("regularMarketVolume")).get("fmt");
        tenDayVolume = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("averageDailyVolume10Day")).get("fmt");
        threeMonthVolume = (String)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("averageDailyVolume3Month")).get("fmt");
        currentVolume = (long)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("regularMarketVolume")).get("raw");
        tenAvgVolume = (long)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("averageDailyVolume10Day")).get("raw");
        threeMonthAvgVolume = (long)((JSONObject)((JSONObject)mainJsonObj.get("price")).get("averageDailyVolume3Month")).get("raw");
    }

    public void display(){
        System.out.println("\nTime : " + dates);
        System.out.println("Code : " + getSymbol());
        System.out.println("Company's sector    : " + sector);
        System.out.println("Company's shortName : " + shortName);
        System.out.println("Company's name      : " + fullName);

        System.out.println();

        System.out.println("Current Price       : " + checkCurrentPrice(doubleConvertion(currentPrice),doubleConvertion(open)));
        System.out.println("Open                : RM " + doubleConvertion(open));
        System.out.println("Previous Close      : RM " + doubleConvertion(close));
        System.out.println("Range               : " + doubleConvertion(dayLow) + " - " + doubleConvertion(dayHigh) + " (" + changePercentage +")");

        System.out.println();

        double high52 = doubleConvertion(fiftyTwoHigh);
        double low52 = doubleConvertion(fiftyTwoLow);
        System.out.println("52 weeks high : RM " + high52);
        System.out.println("52 weeks low  : RM " + low52);
        double fiftyTwoPriceChange = (double)Math.round(((high52/low52) - 1)*10000)/100;
        System.out.println("52 weeks price range : " + low52 + " - " + high52 + "( " + fiftyTwoPriceChange + "% )");

        System.out.println();

        System.out.println("Current volume     : " + regularVolume);
        System.out.println("Avg 10 day volume  : " + tenDayVolume);
        System.out.println("Avg 3 months volume: " + threeMonthVolume);
    }
    public double doubleConvertion(String content){
        return Double.parseDouble(content);
    }

    public String checkCurrentPrice(double price,double open){
        if(price>open)
            return (ANSI_GREEN) + "RM " + price + (ANSI_RESET);
        else if(price < open)
            return (ANSI_RED) +  "RM " + price + (ANSI_RESET);
        return  ("RM " + String.valueOf(price));
    }
}
