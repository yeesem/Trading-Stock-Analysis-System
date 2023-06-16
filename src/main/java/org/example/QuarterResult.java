package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
public class QuarterResult extends YahooFinanceAPI{
    public QuarterResult(){}
    String period1 = "1642713600";
    String period2 = "1672483200";
    ArrayList<String>date = new ArrayList<>();
    ArrayList<Double>quarterRevenue = new ArrayList<>();
    ArrayList<Double>quarterCost = new ArrayList<>();
    ArrayList<Double>quarterGross = new ArrayList<>();
    ArrayList<Double>quarterEBITDA = new ArrayList<>();
    ArrayList<Double>quarterAdministrationExpenses = new ArrayList<>();
    ArrayList<Double>quarterFinanceCost = new ArrayList<>();
    ArrayList<Double>quarterDilutedEPS = new ArrayList<>();
    ArrayList<Double>quarterDilutedNumShares = new ArrayList<>();
    ArrayList<Double>quarterTaxation = new ArrayList<>();
    ArrayList<Double>quarterPretax = new ArrayList<>();
    ArrayList<Double>quarterNetIncome = new ArrayList<>();
    ArrayList<Double>quarterBasicNumShares = new ArrayList<>();
    ArrayList<Double>quarterBasicEPS = new ArrayList<>();
    ArrayList<Double>otherIncome = new ArrayList<>();
    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String ANSI_RESET = "\u001B[0m";

    int temptSize = 0;

    public void clearAll() {
        date = new ArrayList<>();
        quarterRevenue = new ArrayList<>();
        quarterCost = new ArrayList<>();
        quarterGross = new ArrayList<>();
        quarterEBITDA = new ArrayList<>();
        quarterAdministrationExpenses = new ArrayList<>();
        quarterFinanceCost = new ArrayList<>();
        quarterDilutedEPS = new ArrayList<>();
        quarterDilutedNumShares = new ArrayList<>();
        quarterTaxation = new ArrayList<>();
        quarterPretax = new ArrayList<>();
        quarterNetIncome = new ArrayList<>();
        quarterBasicNumShares = new ArrayList<>();
        quarterBasicEPS = new ArrayList<>();
        otherIncome = new ArrayList<>();

    }

    public String getData(String stockName){
        String jsonData = null;
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-timeseries?symbol=" + stockName
                      + "&period2=" + period2 + "&period1=" + period1;
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

            JSONParser parser = new JSONParser();
            Object object = parser.parse(jsonData);
            JSONObject mainJsonObj = (JSONObject) object;

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

    public void getQuarterResult(String stockName) throws ParseException {
        clearAll();
        JSONParser parser = new JSONParser();
        Object object = parser.parse(getData(stockName));
        JSONObject mainJsonObj = (JSONObject) object;

        JSONObject timeSeries = (JSONObject) mainJsonObj.get("timeseries");
        JSONArray result = (JSONArray) timeSeries.get("result");

        for(Object obj : result){
            loadData(obj,"quarterlyTotalRevenue",quarterRevenue);
            loadDataDate(obj,"quarterlyTotalRevenue",date);
            loadDataNega(obj,"quarterlyCostOfRevenue",quarterCost);
            loadData(obj,"quarterlyGrossProfit",quarterGross);
            loadData(obj,"quarterlyEbitda",quarterEBITDA);
            loadDataNega(obj,"quarterlySellingGeneralAndAdministration",quarterAdministrationExpenses);
            loadDataNega(obj,"quarterlyInterestExpense",quarterFinanceCost);
            loadData(obj,"quarterlyDilutedEPS",quarterDilutedEPS);
            loadData(obj,"quarterlyDilutedAverageShares",quarterDilutedNumShares);
            loadData(obj,"quarterlyPretaxIncome",quarterPretax);
            loadData(obj,"quarterlyNetIncome",quarterNetIncome);
            loadData(obj,"quarterlyBasicAverageShares",quarterBasicNumShares);
            loadData(obj,"quarterlyBasicEPS",quarterBasicEPS);
            loadDataTax(obj,"quarterlyTaxProvision",quarterTaxation);

        }
        for (int i=0;i<date.size();i++){
            if(quarterAdministrationExpenses.isEmpty())
                fillEmptyList(quarterAdministrationExpenses);
            if(quarterFinanceCost.isEmpty())
                fillEmptyList(quarterFinanceCost);
            if(quarterGross.isEmpty())
                fillEmptyList(quarterGross);
            if(quarterPretax.isEmpty())
                break;
            
            otherIncome.add(quarterPretax.get(i)-(quarterGross.get(i) + quarterAdministrationExpenses.get(i) + quarterFinanceCost.get(i)));
        }
    }
    public void fillEmptyList(ArrayList<Double>list){
        for (int i=0;i<date.size();i++){
            list.add((double)0);
        }
    }
    public void loadData(Object obj,String financialTerm,ArrayList<Double>list){
        JSONObject jsonObject = (JSONObject)obj;
        JSONArray object = (JSONArray)jsonObject.get(financialTerm);
        if(object!=null) {
            for (Object obj2 : object) {
                if(obj2==null){list.add((double)0);continue;}
                JSONObject jsonObject2 = (JSONObject) obj2;
                list.add((Double)((JSONObject) jsonObject2.get("reportedValue")).get("raw"));
            }
        }
    }

    public void loadDataTax(Object obj,String financialTerm,ArrayList<Double>list){
        int index = 0;
        JSONObject jsonObject = (JSONObject)obj;
        JSONArray object = (JSONArray)jsonObject.get(financialTerm);
        if(object!=null) {
            for (Object obj2 : object) {
                if(obj2==null){list.add((double)0);continue;}
                JSONObject jsonObject2 = (JSONObject) obj2;
                //if(quarterNetIncome.get(index)<quarterPretax.get(index))
                //   list.add(-1 * (Double)((JSONObject) jsonObject2.get("reportedValue")).get("raw"));
                //else
                    list.add(-1 * (Double)((JSONObject) jsonObject2.get("reportedValue")).get("raw"));
                index++;
            }
        }
    }

    public void loadDataNega(Object obj,String financialTerm,ArrayList<Double>list){
        JSONObject jsonObject = (JSONObject)obj;
        JSONArray object = (JSONArray)jsonObject.get(financialTerm);
        if(object!=null) {
            for (Object obj2 : object) {
                if(obj2==null){list.add((double)0);continue;}
                JSONObject jsonObject2 = (JSONObject) obj2;
                list.add(-1 * (Double)((JSONObject) jsonObject2.get("reportedValue")).get("raw"));
            }
        }
    }

    public void loadDataDate(Object obj,String financialTerm,ArrayList<String>list){
        JSONObject jsonObject = (JSONObject)obj;
        JSONArray object = (JSONArray)jsonObject.get(financialTerm);
        if(object!=null) {
            for (Object obj2 : object) {
                if(obj2==null){list.add((String)"null");continue;}
                JSONObject jsonObject2 = (JSONObject) obj2;
                list.add((String)(jsonObject2.get("asOfDate")));
            }
        }
    }

    public void displayDot(int size){
        int dot = 32 + (20*size);
        for(int i=0;i<dot;i++){
            System.out.print("-");
        }
        System.out.println();
    }

    public void display(){
        temptSize = date.size();

        System.out.println(BLACK_BOLD + "\n\nQuarterly Result" + ANSI_RESET);

        displayDot(temptSize);
        System.out.printf("|%-30s|","Date (RM '000'000)");
        for (int i=0;i<date.size();i++){
            System.out.printf("%-20s",date.get(i));
        }
        System.out.print("|");
        System.out.println();
        displayDot(temptSize);
        display("Revenue",quarterRevenue);
        displayDot(temptSize);
        display("Cost of Sales",quarterCost);
        displayDot(temptSize);
        display("Gross Profit",quarterGross);
        displayDot(temptSize);
        display("Other Income",otherIncome);
        displayDot(temptSize);
        display("Administrative Expenses",quarterAdministrationExpenses);
        displayDot(temptSize);
        display("Finance Cost",quarterFinanceCost);
        displayDot(temptSize);
        display("Profit Before Tax",quarterPretax);
        displayDot(temptSize);
        display("Taxation",quarterTaxation);
        displayDot(temptSize);
        display("Net Profit",quarterNetIncome);
        displayDot(temptSize);

        System.out.println();

        displayDot(temptSize);
        display("EBITDA",quarterEBITDA);
        displayDot(temptSize);

        System.out.println();

        displayDot(temptSize);
        displayEPS("Basic EPS",quarterBasicEPS);
        displayDot(temptSize);
        displayEPS("Diluted EPS",quarterDilutedEPS);
        displayDot(temptSize);

        System.out.println();

        displayDot(temptSize);
        displayNumShare("Basic Num of Shares",quarterBasicNumShares);
        displayDot(temptSize);
        displayNumShare("Diluted Num of Shares",quarterDilutedNumShares);
        displayDot(temptSize);

    }

    public void display(String content,ArrayList<Double>list){

        System.out.printf("|%-30s|",content);
        for (int j=0;j<list.size();j++){
            System.out.printf("%-20s",(double)list.get(j)/1000000);
        }
        System.out.print("|");
        System.out.println();
    }
    public void displayNumShare(String content,ArrayList<Double>list){

        System.out.printf("|%-30s|",content);
        for (int j=0;j<list.size();j++){
            if(list.get(j)==0)
                System.out.printf("%-20s","null");
            else
                System.out.printf("%-20s", (double)list.get(j)/1000000);
        }
        System.out.print("|");
        System.out.println();
    }
    public void displayEPS(String content,ArrayList<Double>list){

        System.out.printf("|%-30s|",content);
        for (int j=0;j<list.size();j++){
            if(list.get(j)==0)
                System.out.printf("%-20s","null");
            else
                System.out.printf("%-20s", BigDecimal.valueOf(list.get(j)).setScale(4, RoundingMode.HALF_UP));
        }
        System.out.print("|");
        System.out.println();
    }

}
