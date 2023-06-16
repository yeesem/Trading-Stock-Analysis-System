package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.security.jgss.GSSUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class IncomeStatement extends YahooFinanceAPI{

    ArrayList<Long>revenue = new ArrayList<>();
    ArrayList<Object>date = new ArrayList<>();
    ArrayList<Double>basicEPS = new ArrayList<>();
    ArrayList<Long>interestExpX = new ArrayList<>();
    ArrayList<Double>dilutedEPS = new ArrayList<>();
    ArrayList<Long>pretaxIncome = new ArrayList<>();
    ArrayList<Long>costOfSales = new ArrayList<>();
    ArrayList<Long>incomeAfterTax = new ArrayList<>();
    ArrayList<Long>netIncome = new ArrayList<>();
    ArrayList<Long>operatingIncome = new ArrayList<>();
    ArrayList<Long>grossProfit = new ArrayList<>();
    ArrayList<Long>operatingExpenses = new ArrayList<>();
    ArrayList<Long>taxation = new ArrayList<>();
    ArrayList<Long>netIncomeStakeholder = new ArrayList<>();
    ArrayList<Long>numberOfShare = new ArrayList<>();
    ArrayList<Long>otherIncome = new ArrayList<>();
    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String YELLOW_BOLD = "\033[1;33m"; 

    ArrayList<ArrayList<Object>>list = new ArrayList<>();
    public IncomeStatement(){}

    public void clearAll(){
        revenue = new ArrayList<>();
        date = new ArrayList<>();
        basicEPS = new ArrayList<>();
        interestExpX = new ArrayList<>();
        dilutedEPS = new ArrayList<>();
        pretaxIncome = new ArrayList<>();
        costOfSales = new ArrayList<>();
        incomeAfterTax = new ArrayList<>();
        netIncome = new ArrayList<>();
        operatingIncome = new ArrayList<>();
        grossProfit = new ArrayList<>();
        operatingExpenses = new ArrayList<>();
        taxation = new ArrayList<>();
        netIncomeStakeholder = new ArrayList<>();
        numberOfShare = new ArrayList<>();
        otherIncome = new ArrayList<>();
    }

    public String getData(String stockName){
        String jsonData = null;
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-financials?symbol=" + stockName;
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

    public void getIncomeStatement(String stockName) throws ParseException {
        clearAll();
        JSONParser parser = new JSONParser();
        Object object = parser.parse(getData(stockName));
        JSONObject mainJsonObj = (JSONObject) object;


        JSONObject timeSeries = (JSONObject) mainJsonObj.get("timeSeries");
        JSONArray annualTotalRevenue = (JSONArray) timeSeries.get("annualTotalRevenue");
        for (Object obj : annualTotalRevenue) {
            if(obj==null) {revenue.add((long)0);date.add(null);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            revenue.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
            date.add((String) jsonObject.get("asOfDate"));
        }

        JSONArray basiceps = (JSONArray) timeSeries.get("annualBasicEPS");
        for(Object obj : basiceps){
            if(obj==null) { basicEPS.add(0.0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            basicEPS.add((double)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray interestexpX = (JSONArray) timeSeries.get("annualInterestExpense");
        for(Object obj : interestexpX){
            if(obj==null) {interestExpX.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            interestExpX.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray dilutedeps = (JSONArray) timeSeries.get("annualDilutedEPS");
        for(Object obj : dilutedeps){
            if(obj==null) {dilutedEPS.add(0.0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            dilutedEPS.add((double)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray pretaxincome = (JSONArray) timeSeries.get("annualPretaxIncome");
        for(Object obj : pretaxincome){
            if(obj==null) {pretaxIncome.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            pretaxIncome.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray incomeaftertax = (JSONArray) timeSeries.get("annualNetIncomeContinuousOperations");
        for(Object obj : incomeaftertax){
            if(obj==null) {incomeAfterTax.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            incomeAfterTax.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray costofsales = (JSONArray) timeSeries.get("annualCostOfRevenue");
        for(Object obj : costofsales){
            if(obj==null) {costOfSales.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            costOfSales.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray netincome = (JSONArray) timeSeries.get("annualNetIncome");
        for(Object obj : netincome){
            if(obj==null) {netIncome.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            netIncome.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray sharenumber = (JSONArray) timeSeries.get("annualDilutedAverageShares");
        for(Object obj : sharenumber){
            if(obj==null) {numberOfShare.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            numberOfShare.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray operatingincome = (JSONArray) timeSeries.get("annualOperatingIncome");
        for(Object obj : operatingincome){
            if(obj==null) {operatingIncome.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            operatingIncome.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray grossprofit = (JSONArray) timeSeries.get("annualGrossProfit");
        for(Object obj : grossprofit){
            if(obj==null) {grossProfit.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            grossProfit.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray operatingexpenses = (JSONArray) timeSeries.get("annualSellingGeneralAndAdministration");
        for(Object obj : operatingexpenses){
            if(obj==null) {operatingExpenses.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            operatingExpenses.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray tax = (JSONArray) timeSeries.get("annualTaxProvision");
        for(Object obj : tax){
            if(obj==null) {taxation.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            taxation.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray netincomestakeholder = (JSONArray) timeSeries.get("annualNetIncomeCommonStockholders");
        for(Object obj : netincomestakeholder){
            if(obj==null) {netIncomeStakeholder.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            netIncomeStakeholder.add((Long)((JSONObject)jsonObject.get("reportedValue")).get("raw"));
        }
    }

//    public void dataIncomeStatementString(String content,ArrayList<Object>list){
//        System.out.println();
//        System.out.printf("%-30s",content);
//        for (int i=0;i<list.size();i++){
//            if(content.equals("Cost of Sales") || content.equals("Operating Expenses"))
//            System.out.printf("%-20s",list.get(i).toString());
//        }
//    }

    public double doubleConversion(){
        return 0.0;
    }

   
    public void displayLine(int size){
        int dot = 32 + (20*size);
        for(int i=0;i<dot;i++){
            System.out.print("-");
        }
        System.out.println();
    }


    public void display() {
    //    String [] content = {"Date","Revenue","Cost of Sales","Gross profit","Operating income","",
    //                        "Interest Expenses","Profit before tax","Taxation","Profit after tax","Net profit"};

    //    list.add(date); list.add(revenue); list.add(costOfSales); list.add(grossProfit); list.add(operatingIncome); list.add(operatingExpenses); list.add(interestExpX);
    //    list.add(pretaxIncome); list.add(taxation);list.add(incomeAfterTax); list.add(netIncome);

    //    for (int i=0;i<content.length;i++){
    //        dataIncomeStatementString(content[i],list.get(i));
    //    }

        System.out.println();
        System.out.println(YELLOW_BOLD + "\nIncome Statement" + ANSI_RESET);

        displayLine(date.size());
        System.out.printf("|%-30s|","Date (RM '000'000)");
        for (int i=0;i<date.size();i++){
            System.out.printf("%-20s",date.get(i));
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Revenue");
        for (int i=0;i<revenue.size();i++){
            try{
               System.out.printf("%-20s",(double)revenue.get(i)/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Cost of Sales");
        for (int i=0;i<costOfSales.size();i++){
            try{
                System.out.printf("%-20s",-1*(double)costOfSales.get(i)/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Gross profit");
        for (int i=0;i<grossProfit.size();i++){
            try{
                System.out.printf("%-20s",(double)grossProfit.get(i)/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
    System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Other income");
        for (int i=0;i<operatingIncome.size();i++){
            try{
                System.out.printf("%-20s",(double)(operatingIncome.get(i)-(grossProfit.get(i)-operatingExpenses.get(i)))/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Operating expenses");
        for (int i=0;i<operatingExpenses.size();i++){
            try{
                System.out.printf("%-20s",(double)operatingExpenses.get(i)/-1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Operating income");
        for (int i=0;i<operatingIncome.size();i++){
            try{
            System.out.printf("%-20s",(double)operatingIncome.get(i)/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Interest income / expenses");
        for (int i=0;i<operatingIncome.size();i++){
            try{
                System.out.printf("%-20s",(double)(pretaxIncome.get(i)-operatingIncome.get(i))/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Profit before tax");
        for (int i=0;i<pretaxIncome.size();i++){
            try{
                System.out.printf("%-20s",(double)pretaxIncome.get(i)/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Taxation");
        for (int i=0;i<incomeAfterTax.size();i++){
            try{
            if(incomeAfterTax.get(i)>pretaxIncome.get(i))
               System.out.printf("%-20s",(double)taxation.get(i)/1000000.0);
            else
                System.out.printf("%-20s",(double)taxation.get(i)/-1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }   
         }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Profit after tax");
        for (int i=0;i<incomeAfterTax.size();i++){
            try{
            System.out.printf("%-20s",(double)incomeAfterTax.get(i)/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
      System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Extraordinary Items /");
        for (int i=0;i<incomeAfterTax.size();i++){
            try{
            System.out.printf("%-20s",(double)(netIncome.get(i)-incomeAfterTax.get(i))/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");
        
        System.out.println();
        System.out.printf("|%-30s|\n","Minority Interest");

        displayLine(date.size());

        System.out.printf("|%-30s|","Net profit");
        for (int i=0;i<netIncome.size();i++){
            try{
            System.out.printf("%-20s",(double)netIncome.get(i)/1000000.0);
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");
        System.out.println();
        displayLine(date.size());


        System.out.println();
        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Basic EPS");
        for (int i=0;i<basicEPS.size();i++){
            try{
            System.out.printf("%-20s",basicEPS.get(i));
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");

        System.out.println();
        displayLine(date.size());
        System.out.printf("|%-30s|","Diluted EPS");
        for (int i=0;i<dilutedEPS.size();i++){
            try{
            System.out.printf("%-20s",dilutedEPS.get(i));
            }catch(IndexOutOfBoundsException e){
                System.out.printf("%-20s", 0.0);
            }
        }
        System.out.print("|");
        System.out.println();
        displayLine(date.size());

    }

}


