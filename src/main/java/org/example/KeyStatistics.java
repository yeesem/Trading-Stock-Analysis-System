package org.example;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

@Service
public class KeyStatistics extends YahooFinanceAPI{
    @Autowired
    KeyStatisticsRepository keyStatisticsRepository;

    String stock;
    long [] timeStamp = {1577754000,1577836800,1609376400,1609459200,1640912400,1640995200,1672362000,1672435200};
    ArrayList<Double>sharePrice = new ArrayList<>();
    ArrayList<Double>roe = new ArrayList<>();
    ArrayList<Double>yoyGrowth = new ArrayList<>();
    ArrayList<Double>pe = new ArrayList<>();
    ArrayList<Double>gearing = new ArrayList<>();
    ArrayList<Double>netMargin = new ArrayList<>();
    ArrayList<Double>cashFlow = new ArrayList<>();
    ArrayList<Double>pb = new ArrayList<>();
    ArrayList<Double>DPR = new ArrayList<>();
    ArrayList<Double>overallDividend = new ArrayList<>();
    ArrayList<Double>dividendYield = new ArrayList<>();
    ArrayList<Double>bookValuePerShare = new ArrayList<>();
    ArrayList<Long> getOverallDividendDate = new ArrayList<>();
    ArrayList<Double> CFPerCapex = new ArrayList<>();
    HashMap<String,Double>dividend = new HashMap<>();
    String [] year = {"2019","2020","2021","2022"};
    //January 1,2019
    long dividendPeriod1 = 1546300800;
    //December 31,2022
    long dividendPeriod2 = 1672435200;
    double fourYearGAGR;
    double currentPE;
    double currentFPE;
    double currentDY;
    double currentROE;
    double currentPM;

    double pbDiscount=0.0;
    String stockName;
    String checkCashFlow;
    int lenyanGrowth;
    int lenyanWealth;
    String growth;
    String MoS;

    int lineSize = 0;

    BalanceSheet getBalanceSheet = new BalanceSheet();
    IncomeStatement getIncomeStatement = new IncomeStatement();
    CashFlow getCashFlow = new CashFlow();
    QuarterResult getQuarterResult = new QuarterResult();
    StockSummary summary = new StockSummary();

    int numStockToCompare;
    ArrayList<String>searchStockName;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m";
    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String YELLOW_BOLD = "\033[1;33m"; 
    public static final String WHITE_BOLD = "\033[1;37m";
    public static final String RED_BOLD = "\033[1;31m";    
    public static final String GREEN_BOLD = "\033[1;32m";  
    public static final String BLUE_BOLD = "\033[1;34m";  
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE 

    public KeyStatistics(){}

    public void clearAll(){
        roe = new ArrayList<>();
        yoyGrowth = new ArrayList<>();
        pe = new ArrayList<>();
        gearing = new ArrayList<>();
        netMargin = new ArrayList<>();
        cashFlow = new ArrayList<>();
        pb = new ArrayList<>();
        DPR = new ArrayList<>();
        overallDividend = new ArrayList<>();
        dividendYield = new ArrayList<>();
        bookValuePerShare = new ArrayList<>();
        getOverallDividendDate = new ArrayList<>();
        dividend = new HashMap<>();
        CFPerCapex = new ArrayList<>();
    }

    public String getData(String stockName,long period1,long period2){
        String jsonData = null;
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-historical-data?period1=" + period1
                + "&period2=" + period2 + "&symbol=" + stockName;
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

    public void getClosePrice(String stockName) throws ParseException {
        clearAll();
        stock = stockName;
        JSONParser parser = new JSONParser();
        for (int i=0;i<timeStamp.length;i+=2){
            Object object = parser.parse(getData(stockName,timeStamp[i],timeStamp[i+1]));
            JSONObject mainJsonObj = (JSONObject) object;
            JSONArray price = (JSONArray) mainJsonObj.get("prices");
            if(price == null || price.isEmpty()){
                sharePrice.add((double)0);
            }else {
                for (Object obj : price) {
                    JSONObject jsonObject = (JSONObject) obj;
                    if (jsonObject.containsKey("date") && jsonObject.containsKey("adjclose")){
                        if ((long) jsonObject.get("date") == (timeStamp[i])) {
                            sharePrice.add((double) jsonObject.get("adjclose"));
                        }
                    }else
                        sharePrice.add(0.0);
                }
            }
        }

        Object object2 = parser.parse(getData(stockName,dividendPeriod1,dividendPeriod2));
        JSONObject mainJsonObj2 = (JSONObject) object2;
        if(!mainJsonObj2.containsKey("eventsData")){
            overallDividend.add(0.0);
            getOverallDividendDate.add((long)0);
          }
          else {
              JSONArray dividend = (JSONArray) mainJsonObj2.get("eventsData");

              for (Object tempt : dividend) {
                  JSONObject jsonObject2 = (JSONObject) tempt;
                  if (jsonObject2.containsKey("amount")) {
                      overallDividend.add((double) jsonObject2.get("amount"));
                      getOverallDividendDate.add((long) jsonObject2.get("date"));
                  } else {
                      overallDividend.add(0.0);
                  }
              }
          }
    }
    
    public double PBCalculation(ArrayList<Double>list,double currentPrice,double bookValue){
    if(list.size()==0)
        return 0;
    ArrayList<Double>tempt = new ArrayList<>();
    for (int i=0;i<list.size();i++){
        tempt.add(list.get(i));
    }
    Collections.sort(tempt);
//        double median=0;
//        if(tempt.size()%2==0){
//            median = (tempt.get(tempt.size()/2) + tempt.get(tempt.size()/2 -1 ))/2;
//        }else{
//            median = tempt.get(tempt.size()/2);
//        }

//        return (((currentPrice/bookValue) /median)-1)*100;
        int index=0;
        if(tempt.get(0)==0 || tempt.get(1)==0){
            while (tempt.get(index)==0 && index<list.size()-1){
                index++;
            }
            if(tempt.get(index)!=0)
              return (((currentPrice/bookValue) / ( (tempt.get(index))) )-1)*100;
            else
                return 0;
        }
        return (((currentPrice/bookValue) / ( (tempt.get(0)+tempt.get(1))/2 ) )-1)*100;
    }

    public void separateDividend(){
        TimeStampConverter convert = new TimeStampConverter();
        for(int i=0;i<getOverallDividendDate.size();i++){
            String formattedStringTime = convert.timeStampConverter(getOverallDividendDate.get(i));
            String year = formattedStringTime.substring(0,4);
            if(dividend.containsKey(year)){
                dividend.put(year,dividend.get(year)+overallDividend.get(i));
            }else{
                dividend.put(year,overallDividend.get(i));
            }
        }
    //    for (String key : dividend.keySet()){
    //        double value = dividend.get(key);
    //        System.out.println("key : " + key + " , value : " + value);
    //    }
    }
    public void calculateKeyStatistics(String stock) throws ParseException {
        getBalanceSheet.getBalanceSheet(stock);
        getIncomeStatement.getIncomeStatement(stock);
        getCashFlow.getCashFlow(stock);
        getQuarterResult.getQuarterResult(stock);
        summary.getCurrentStockPrice(stock);

        separateDividend();


        if (isValid(getIncomeStatement.netIncome, getBalanceSheet.shareHolderFund, roe)) {
            for (int i = 0; i < getIncomeStatement.date.size(); i++) {
                roe.add((((double) getIncomeStatement.netIncome.get(i) / (double) getBalanceSheet.shareHolderFund.get(i)) * 100));
            }
        }

        for (int i = 1; i < getIncomeStatement.date.size(); i++) {
            yoyGrowth.add(((((double) getIncomeStatement.netIncome.get(i) / (double) getIncomeStatement.netIncome.get(i - 1)) - 1) * 100));
        }
        yoyGrowth.add(0.0);

        if (isValid(sharePrice, getIncomeStatement.basicEPS, pe)) {
            for (int i = 0; i < getIncomeStatement.date.size(); i++) {
                pe.add((sharePrice.get(i) / Double.parseDouble(getIncomeStatement.basicEPS.get(i).toString())));
            }
        }

        if (isValid(getIncomeStatement.netIncome, getIncomeStatement.revenue, netMargin)) {
            for (int i = 0; i < getIncomeStatement.date.size(); i++) {
                netMargin.add(((double) getIncomeStatement.netIncome.get(i) / (double) getIncomeStatement.revenue.get(i)) * 100);
            }
        }

        if (isValid(getBalanceSheet.totalNonCurrentLia, getBalanceSheet.currentLiability, getBalanceSheet.shareHolderFund, gearing)) {
            for (int i = 0; i < getBalanceSheet.totalNonCurrentLia.size(); i++) {
                gearing.add(1 + ((double) getBalanceSheet.totalNonCurrentLia.get(i) + (double) getBalanceSheet.currentLiability.get(i)) / (double) getBalanceSheet.shareHolderFund.get(i));
            }
        }

        if (isValid(getCashFlow.operatingCashFlow, getIncomeStatement.operatingIncome, cashFlow)) {
            for (int i = 0; i <  getCashFlow.date.size(); i++) {
                cashFlow.add((double) getCashFlow.operatingCashFlow.get(i) / (double) getIncomeStatement.operatingIncome.get(i));
            }
        }

        if(dividend.size()==getIncomeStatement.dilutedEPS.size()){
            for (int i = 0; i < getIncomeStatement.date.size(); i++) {
                String time = getIncomeStatement.date.get(i).toString().substring(0, 4);
                if (dividend.containsKey(time)) {
                    DPR.add(dividend.get(time) / (double) getIncomeStatement.dilutedEPS.get(i));
                } else {
                    DPR.add(0.0);
                }
            }
        }else{
            for(int i=0;i<getIncomeStatement.date.size();i++){
                DPR.add(0.0);
            }
        }

        if (isValid(sharePrice, getIncomeStatement.date, dividendYield)){
            for (int i = 0; i < sharePrice.size(); i++) {
                String time = getIncomeStatement.date.get(i).toString().substring(0, 4);
                if (dividend.containsKey(time))
                    dividendYield.add((dividend.get(time) / sharePrice.get(i)) * 100);
                else
                    dividendYield.add(0.0);
            }
        }

        if(isValid(getCashFlow.capex,getCashFlow.operatingCashFlow,CFPerCapex)) {
            for (int i = 0; i < getCashFlow.date.size(); i++) {
                CFPerCapex.add((((double) (getCashFlow.capex.get(i) * -1) / (double) getCashFlow.operatingCashFlow.get(i))) * 100);
            }
        }

        if(getBalanceSheet.currentAsset.size()==getBalanceSheet.nonCurrentAsset.size() && getBalanceSheet.nonCurrentAsset.size()==getBalanceSheet.currentLiability.size() &&
                getBalanceSheet.currentLiability.size() == getBalanceSheet.totalNonCurrentLia.size() && getBalanceSheet.totalNonCurrentLia.size() == getQuarterResult.quarterDilutedNumShares.size()) {
            //System.out.println("here again");
            // System.out.println(getIncomeStatement.date);
            // System.out.println(getBalanceSheet.currentAsset);
            // System.out.println(getBalanceSheet.nonCurrentAsset);
            // System.out.println(getBalanceSheet.currentLiability);
            // System.out.println(getBalanceSheet.totalNonCurrentLia);
            
            // for (int i = 0; i < getIncomeStatement.date.size(); i++) {
            for (int i = 0; i < getBalanceSheet.currentAsset.size(); i++) {
                double bvps = 0;
                double bookValue = (getBalanceSheet.currentAsset.get(i) + getBalanceSheet.nonCurrentAsset.get(i)) - (getBalanceSheet.currentLiability.get(i) + getBalanceSheet.totalNonCurrentLia.get(i));
                if (getQuarterResult.quarterDilutedNumShares.get(i) == 0 && i - 1 >= 0 && getQuarterResult.quarterDilutedNumShares.get(i - 1) != 0) {
                    bvps = bookValue / getQuarterResult.quarterDilutedNumShares.get(i - 1);
                    bookValuePerShare.add(bvps);
                } else if (getQuarterResult.quarterDilutedNumShares.get(i) == 0 && i + 1 < getIncomeStatement.date.size() && getQuarterResult.quarterDilutedNumShares.get(i + 1) != 0) {
                    bvps = bookValue / getQuarterResult.quarterDilutedNumShares.get(i + 1);
                    bookValuePerShare.add(bvps);
                } else {
                    bvps = bookValue / getQuarterResult.quarterDilutedNumShares.get(i);
                    bookValuePerShare.add(bvps);
                }
                pb.add(sharePrice.get(i) / bvps);
            }
        }else{
            for (int i = 0; i < getIncomeStatement.date.size(); i++) {
                pb.add(0.0);bookValuePerShare.add(0.0);
            }
        }

        sharePrice = new ArrayList<>();
    }

    public boolean isValid(ArrayList<?>list,ArrayList<?>list2,ArrayList<Double>list3){
        if(list.size() == list2.size())
            return true;
        else{
            for(int i=0;i<getIncomeStatement.date.size();i++){
                list3.add(0.0);
            }
            return false;
        }
    }

    public boolean isValid(ArrayList<?>list,ArrayList<?>list2,ArrayList<?>list3,ArrayList<Double>list4){
        if(list.size() == list2.size() && list2.size() == list3.size())
            return true;
        else{
            for(int i=0;i<getIncomeStatement.date.size();i++){
                list4.add(0.0);
            }
            return false;
        }
    }

    public void getFundamentalAnalysis() throws ParseException {

        System.out.println(BLACK_BOLD + "\nFundamental Analysis : " + ANSI_RESET);
        System.out.println(summary.shortName + " (" + stock + ") " + summary.fullName);

        currentPE = Double.parseDouble(summary.currentPrice)/getLastItem(getIncomeStatement.dilutedEPS);
        System.out.printf("%-15s%-12.2f%-17s%-5.2f\n","Shares  (B) :" ,(getLastItem(getQuarterResult.quarterDilutedNumShares)/1000000000.0),"PE         :",currentPE);

        double sumEPS = sumEPS(getQuarterResult.quarterDilutedEPS);
        if(sumEPS==0)
            currentFPE = 0;
        else
            currentFPE = Double.parseDouble(summary.currentPrice)/sumEPS(getQuarterResult.quarterDilutedEPS);
        System.out.printf("%-15s%-12.2f%-17s%-5.2f\n","MarketCap   :" ,(getLastItem(getQuarterResult.quarterDilutedNumShares)*Double.parseDouble(summary.currentPrice))/1000000000.0,"FPE        :",currentFPE);

        double bookvalue = bookValuePerShare.get(bookValuePerShare.size() - 1);
        System.out.printf("%-15s%-12.3f%-17s%-5.2f\n","TO Rate     :" ,((double)summary.currentVolume/getLastItem(getQuarterResult.quarterDilutedNumShares)),"PB         :",(bookvalue!=0)?Double.parseDouble(summary.currentPrice)/bookvalue : 0);
        
        double fourQCAGR;
        if(getQuarterResult.quarterNetIncome.isEmpty())
            fourQCAGR =0;
        else
            fourQCAGR = (  Math.pow(getQuarterResult.quarterNetIncome.get(getQuarterResult.quarterNetIncome.size()-1)/getQuarterResult.quarterNetIncome.get(0), (1.0/(double)getQuarterResult.quarterNetIncome.size()))-1)*100;
        System.out.printf("%-15s%-12.2f%-17s%-5.4f\n","4QCAGR (%)  :" ,fourQCAGR,"Book value :",bookvalue);

        fourYearGAGR = (((    Math.pow( (double)getIncomeStatement.netIncome.get(getIncomeStatement.netIncome.size()-1) / (double)getIncomeStatement.netIncome.get(0) ,(1.0/(double)getIncomeStatement.netIncome.size()) )  -1)*100));
        System.out.printf("%-15s%-12.2f%-17s%-5.4f\n","4YCAGR (%)  :" ,fourYearGAGR,"EPS        :",sumEPS(getQuarterResult.quarterBasicEPS));

        if(bookvalue!=0) {
            pbDiscount = PBCalculation(pb, Double.parseDouble(summary.currentPrice), bookvalue);
        }
        System.out.printf("%-15s%-12.2f%-17s%-5.4f\n","PB (%)      :",pbDiscount,"DPS        :",dividend.get(year[year.length-1]));

        double currentDividendYield = 0;
        if(dividend.containsKey(year[year.length-1])){
            currentDividendYield = dividend.get((year[year.length-1]));
        }
        MoS = checkMarginOfSafety(pbDiscount);
        currentDY = (currentDividendYield/Double.parseDouble(summary.currentPrice))*100;
        System.out.printf("%-14s %-18s   %-17s%-5.2f\n","SoM         :",MoS,"DivYield   :",currentDY);

        checkCashFlow = checkCashFlow(cashFlow);
        currentROE = (sumEPS(getQuarterResult.quarterNetIncome)/getBalanceSheet.shareHolderFund.get(getBalanceSheet.shareHolderFund.size()-1))*100;
        System.out.printf("%-14s %-18s   %-17s%-5.2f\n","Cash Flow   :",checkCashFlow,"ROE        :",currentROE);

        growth = checkGrowth(getQuarterResult.quarterNetIncome);
        double sumQuarterIncome = sumEPS(getQuarterResult.quarterNetIncome);
        double sumQuarterRevenue = sumEPS(getQuarterResult.quarterRevenue);
        if(sumQuarterIncome==0 || sumQuarterRevenue==0)
           currentPM = 0;
        else   
           currentPM = (sumQuarterIncome/sumQuarterRevenue)*100;
        System.out.printf("%-14s %-18s   %-17s%-5.2f\n","Growth      :" ,growth,"Net Margin :",currentPM);

        System.out.printf("%-15s%-12s\n","Volume      :" ,checkVolume(summary.currentVolume,summary.tenAvgVolume,summary.threeMonthAvgVolume));
        
        System.out.printf("%-15s%-12s\n","Buffett     :" ,checkBuffett(roe.get(roe.size()-1),gearing.get(gearing.size()-1),checkCashFlow,currentPM));
        
        System.out.printf("%-15s%-12s\n","Sem         :" ,checkSem(roe.get(roe.size()-1),gearing.get(gearing.size()-1),growth,currentPM));
    }

    public String checkBuffett(double roe,double gearing,String cf,double pm){
          if(roe>=8 && gearing<=2 && cf.equals(ANSI_GREEN+ "SUPERB" + ANSI_RESET) && pm>=8)
               return (ANSI_GREEN+ "TRUE" + ANSI_RESET);
          else
              return (ANSI_RED+ "FALSE" + ANSI_RESET);
    }

    public String checkSem(double roe,double gearing,String g,double pm){
          if(roe>=8 && gearing<=2 && g.equals(ANSI_GREEN+ "SUPERB" + ANSI_RESET) && pm>=12)
               return (ANSI_GREEN+ "TRUE" + ANSI_RESET);
          else
              return (ANSI_RED+ "FALSE" + ANSI_RESET);
    }

    public double getCurrentData(ArrayList<Double>list,int index){
        for (int i = index;i>=0;i--){
            if(list.get(i)!=0)
                return list.get(i);
        }
        for(int i = index;i<list.size();i++){
            if(list.get(i)!=0)
                return list.get(i);
        }
        return list.get(0);
    }

    public double getLastItem(ArrayList<Double>list){
        if(list.isEmpty())
          return 0.0;
        for (int i = list.size()-1;i>=0;i--){
            if(list.get(i)!=0)
                return list.get(i);
        }
        return list.get(0);
    }

    public String checkMarginOfSafety(double pb){
        if(pb > 0)
            return (ANSI_RED + "PREMIUM" + ANSI_RESET);
        else if(pb<0 && pb>-20)
            return (ANSI_YELLOW + "GOOD" + ANSI_RESET);
        else if(pb==0)
            return (ANSI_BLUE + "NULL" + ANSI_RESET);
        else
            return (ANSI_GREEN + "SUPERB" + ANSI_RESET);
    }

    public String checkCashFlow(ArrayList<Double>list){
        int check = 0;
        for(int i=0;i<list.size()-1;i++){
            if(list.get(i)>=0.8)
                check++;
        }
        if(check>=2)
            return (ANSI_GREEN + "SUPERB" + ANSI_RESET);
        else
            return (ANSI_RED + "WARN" + ANSI_RESET);
    }

    public String checkGrowth(ArrayList<Double>list){
        if(list.isEmpty())
            return (ANSI_BLUE + "NULL" + ANSI_RESET);
        if(list.get(list.size()-1)<0)
            return (ANSI_RED + "WARN" + ANSI_RESET);
        int index=0;
        while (list.size()>1 && index<list.size()){
            if(list.get(list.size()-1)<list.get(index)) {
                if (index == 1)
                    return (ANSI_RED + "WARN" + ANSI_RESET);
            }else
                break;
            index++;
        }
        for(int i=0;i<list.size()-1;i++){
            if(list.get(list.size()-1)<list.get(i))
                return (ANSI_YELLOW + "WEAK" + ANSI_RESET);
        }
        return (ANSI_GREEN + "SUPERB" + ANSI_RESET);
    }

    public String checkVolume(long regVol,long avgVol,long threeAvgVol){
         if (regVol>=avgVol && regVol >= threeAvgVol)
             return (ANSI_RED + "WARN" + ANSI_RESET);
         else if(regVol < avgVol && regVol < threeAvgVol)
              return (ANSI_GREEN + "SUPERB" + ANSI_RESET);
         else
             return (ANSI_YELLOW + "GOOD" + ANSI_RESET);
    }

    public void displayLine(int size){
         int temptSize = 32 + (20*size);
         for(int i=0;i<temptSize;i++){
            System.out.print("-");
         }
         System.out.println();
    }

    public void displayKeyStatistics(){
        lineSize = getBalanceSheet.date.size();

        System.out.println(BLACK_BOLD + "\nKey Statistics : " + ANSI_RESET);

        displayLine(lineSize);
        System.out.printf("|%-30s|","Year");
        for (int i=0;i<getBalanceSheet.date.size();i++){
            System.out.printf("%-20s",getBalanceSheet.date.get(i).substring(0,4));
        }
        System.out.print("|");
        System.out.println();
        displayLine(lineSize);

        display("ROE",roe);
        display("YoY Growth Rate(%)",yoyGrowth);
        display("PE",pe);
        displayEPS("EPS",getIncomeStatement.dilutedEPS);
        display("Net Profit Margin",netMargin);
        display("Capex/CF",CFPerCapex);
        display("Gearing",gearing);
        display("Cash Flow ",cashFlow);
        display("DPR",DPR);
        display("Dividend Yield",dividendYield);
        display("PB",pb);

    }

    public void display(String content,ArrayList<Double>list){
        System.out.printf("|%-30s|",content);
        for (int j=0;j<list.size();j++){
            System.out.printf("%-20.2f",list.get(j));
        }
        System.out.print("|");
        System.out.println();

        displayLine(lineSize);
    }

    public void displayEPS(String content,ArrayList<Double>list){
        System.out.printf("|%-30s|",content);
        for (int j=0;j<list.size();j++){
            System.out.printf("%-20.4f",list.get(j));
        }
        System.out.print("|");
        System.out.println();

        displayLine(lineSize);
    }

    public double sumEPS(ArrayList<Double>list){
        double eps = 0.0;
        for (int i=0;i<list.size();i++){
            eps+=list.get(i);
        }
        return eps;
    }

    public void lenyanFormula(){
        int growth = checkLenYanGrowth(fourYearGAGR);
        int pe = checkLenYanPE(currentFPE);
        int divYield = checkLenYanDivYield(currentDY);
        int totalWealth = growth + pe + divYield;
        int PM = checkLenYanPM(currentPM);
        int ROE = checkLenYanROE(currentROE);
        int CF = (getCashFlow.operatingCashFlow.size()==0)?0:checkLenYanCF(getIncomeStatement.netIncome.get(getIncomeStatement.netIncome.size()-1),getCashFlow.operatingCashFlow.get(getCashFlow.operatingCashFlow.size()-1));
        int totalProtect = PM + ROE + CF;

        lenyanGrowth = totalWealth;
        lenyanWealth = totalProtect;

        System.out.println(BLACK_BOLD + "\nLen Yan Approach : " + ANSI_RESET);
        System.out.println("Len Yan Formula  : "+ BLACK_BOLD_BRIGHT + "GDP + PRC = Wealth" + ANSI_RESET);
        System.out.printf("%-15s%-12d%-17s%-5d\n","Growth   :",growth,"PM       :",PM);
        System.out.printf("%-15s%-12d%-17s%-5d\n","PE       :",pe,"ROE      :",ROE);
        System.out.printf("%-15s%-12d%-17s%-5d\n","DivYield :",divYield,"CF       :",CF);
        System.out.printf("%-15s%-12d%-17s%-5d\n","Total    :",(growth+pe+divYield),"Total    :",totalProtect);
        System.out.printf("%-14s %-19s  %-17s%-5s\n","Remark   :",checkTotal(totalWealth),"Remark   :",checkTotal(totalProtect));

    }
    public int checkLenYanPM(double value){
        if(value>=1 && value<=5)
        return 5;
        else if(value>5 && value<=10)
            return 10;
        else if(value>10 && value<=15)
            return 15;
        else if(value>15)
            return 20;
        return 0;
    }
    public int checkLenYanROE(double value){
        if(value>=1 && value<=5)
            return 10;
        else if(value>5 && value<=10)
            return 20;
        else if(value>10 && value<=15)
            return 30;
        else if(value>15)
            return 30;
        return 0;
    }
    public int checkLenYanCF(double income,double cashflow){
        if(income>=0 && cashflow>=0)
            return 40;
        if(income<0 && cashflow>=0)
            return 20;
        if(income>=0 && cashflow<0)
            return 30;
        if(income<0 && cashflow<0)
           return 1;

        return 0;
    }
    public int checkLenYanGrowth(double value){
        if(value>=1 && value<=5)
            return 20;
        else if(value>5 && value<=9)
            return 30;
        else if(value>=9 && value<=14)
            return 40;
        else if(value>14)
            return 50;
        return 0;
    }

    public int checkLenYanPE(double value){
        if(value>0 && value<=9)
            return 30;
        else if(value>9 && value<=15)
            return 15;
        else if(value>15 && value<=24)
            return 10;
        else if(value>24)
            return 5;
        return 0;
    }

    public int checkLenYanDivYield(double value){
        if(value>=1 && value<=2)
            return 5;
        else if(value>2 && value<=4)
            return 10;
        else if(value>4 && value<=6)
            return 15;
        else if(value>6)
            return 20;
        return 0;
    }
    public String checkTotal(int value){
        if(value<50)
            return (ANSI_RED + "FAIL" + ANSI_RESET);
        else if(value >= 50 && value <80)
            return (ANSI_YELLOW + "PASS" + ANSI_RESET);
        else
            return (ANSI_GREEN + "EXCELLENT" + ANSI_RESET);
    }

    //Comparison feature
    public void enterNumStockCompare(){
        int numStockCompare = 0;
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number of stock to compare : ");
        numStockCompare = input.nextInt();
        System.out.println();
        this.numStockToCompare = numStockCompare;
    }

    public void enterStockName(){
        Scanner input = new Scanner(System.in);
        searchStockName = new ArrayList<>();
        for (int i=0;i<numStockToCompare;i++){
            System.out.print("Enter the " +  ANSI_RED + (i+1) + ANSI_RESET + " stock's symbol : ");
            searchStockName.add(input.next());
        }
        System.out.println();
    }

    public void displayComparison() throws ParseException {
        enterNumStockCompare();
        enterStockName();

        KeyStatistics [] key = new KeyStatistics[numStockToCompare];

        for (int i=0;i<numStockToCompare;i++){
            key[i].getClosePrice(searchStockName.get(i));
            key[i].calculateKeyStatistics(searchStockName.get(i));
            System.out.println();
        }

        for(int i=0;i<numStockToCompare;i++){
            key[i].getFundamentalAnalysis();
            System.out.println();
        }

        System.out.println();

        for(int i=0;i<numStockToCompare;i++){
            key[i].lenyanFormula();
            System.out.println();
        }

        System.out.println();

        for(int i=0;i<numStockToCompare;i++){
            key[i].displayKeyStatistics();
            System.out.println();
        }

        System.out.println();
    }

    //Database
    public void saveKeyStatistics(String symbol) throws ParseException{
        KeyStatisticsDatabase database = new KeyStatisticsDatabase();
        database.setSymbol(symbol);
        database.setCompanyName(summary.shortName);
        database.setROE(currentROE);
        database.setPB(pb.get(pb.size()-1));
        database.setProfitMargin(currentPM);
        database.setDY(currentDY);
        database.setFPE(currentFPE);
        database.setGearing(gearing.get(gearing.size()-1));
        database.setPBDiscount(pbDiscount);
        database.setCashFlow(checkCashFlow);
        database.setLenYanGrowth(lenyanGrowth);
        database.setLenYanWealth(lenyanWealth);
        database.setGrowth(growth);
        database.setMoS(MoS);
        database.setPrice(summary.currentPrice);
        keyStatisticsRepository.save(database);
    }

    
    public void displayLenYanLine(){
        //System.out.print(YELLOW_BOLD_BRIGHT);
        for(int i=0;i<122;i++){
            System.out.print("-");
        }
        //System.out.println(ANSI_RESET);
    }

    public void displayBuffettLine(){
        //System.out.print(YELLOW_BOLD_BRIGHT);
        for(int i=0;i<110;i++){
            System.out.print("-");
        }
        System.out.println();
        //System.out.println(ANSI_RESET);
    }

    public void displaySemLine(){
        //System.out.print(YELLOW_BOLD_BRIGHT);
        for(int i=0;i<110;i++){
            System.out.print("-");
        }
        System.out.println();
        //System.out.println(ANSI_RESET);
    }
 

    //Show Database
    public void lenyanTable(){
        // System.out.println(keyStatisticsRepository.findBySymbol("5005.KL").getPB());
        // System.out.println(keyStatisticsRepository.findBySymbol("5005.KL").getPB());
        // System.out.println(keyStatisticsRepository.findBySymbol("5005.KL").getPB());
   
        PriorityQueue<LenYanTable>queue = new PriorityQueue<>(Collections.reverseOrder());
        System.out.println();

        List<KeyStatisticsDatabase>list = keyStatisticsRepository.findAll();

        System.out.println();
        System.out.println(YELLOW_BOLD_BRIGHT + "LenYan's Approach" + ANSI_RESET);
        //System.out.println(YELLOW_BOLD_BRIGHT + "------------------" + ANSI_RESET);

        displayLenYanLine();

        System.out.printf("|%-4s%-8s%-10s%-8s%-8s%-8s%-8s%-10s%-8s%-8s%-8s%-8s%-8s%-8s%-8s|\n","No","Symbol","Name","Price","GDP","PRC","Growth","CashFlow","PE","PB","PB Dis","DY","ROE","PM","GR");

        displayLenYanLine();

        for(int i=0;i<list.size();i++){
           if(list.get(i).getLenYanGrowth()>=50 && list.get(i).getLenYanWealth()>=50){
             queue.add(new LenYanTable(list.get(i).getSymbol(),list.get(i).getCompanyName(),list.get(i).getUsedPrice()
                      ,list.get(i).getLenYanGrowth(),list.get(i).getLenYanWealth(),list.get(i).getGrowth(),
                      list.get(i).getCashFlow(),list.get(i).getFPE(),list.get(i).getPB(),
                      list.get(i).getPBDiscount(),list.get(i).getDY(),list.get(i).getROE(),
                      list.get(i).getPm(),list.get(i).getGearing()));
           }
        }

        int size = queue.size();
        for(int i=0;i<size;i++){
            System.out.printf( "|%-4d",(i+1));    
            System.out.println(queue.poll());
            displayLenYanLine();
        }
    }    

    public void buffettTable(){
   
        PriorityQueue<BuffettTable>queue = new PriorityQueue<>(Collections.reverseOrder());
        System.out.println();

        List<KeyStatisticsDatabase>list = keyStatisticsRepository.findAll();

        System.out.println();
        System.out.println(YELLOW_BOLD_BRIGHT + "Buffett's Approach" + ANSI_RESET);
        //System.out.println(BLUE_BOLD_BRIGHT + "-------------------"+ ANSI_RESET);

        displayBuffettLine();
        System.out.printf("|%-4s%-10s%-12s%-8s%-8s%-10s%-8s%-8s%-8s%-8s%-8s%-8s%-8s|\n","No","Symbol","Name","Price","Growth","CashFlow","PE","PB","PB Dis","DY","ROE","PM","GR");
        displayBuffettLine();


        for(int i=0;i<list.size();i++){
           if(list.get(i).getROE()>=8 && list.get(i).getGearing()<=2&&list.get(i).getCashFlow().equals(ANSI_GREEN + "SUPERB" + ANSI_RESET)&&list.get(i).getPm()>=8){
             queue.add(new BuffettTable(list.get(i).getSymbol(),list.get(i).getCompanyName(),list.get(i).getUsedPrice()
                      ,list.get(i).getGrowth(),list.get(i).getCashFlow(),list.get(i).getFPE(),list.get(i).getPB(),
                      list.get(i).getPBDiscount(),list.get(i).getDY(),list.get(i).getROE(),
                      list.get(i).getPm(),list.get(i).getGearing()));
           }
        }

        int size = queue.size();
        for(int i=0;i<size;i++){
            System.out.printf("|%-4d",(i+1));    
            System.out.println(queue.poll());
            displayBuffettLine();
        }
    }    

    public void semTable(){
   
        PriorityQueue<SemTable>queue = new PriorityQueue<>(Collections.reverseOrder());
        System.out.println();

        List<KeyStatisticsDatabase>list = keyStatisticsRepository.findAll();
     
        System.out.println();
        System.out.println(YELLOW_BOLD_BRIGHT + "Sem's Approach" + ANSI_RESET);
        //System.out.println(BLUE_BOLD_BRIGHT +"---------------"+ ANSI_RESET);

        displaySemLine();
        System.out.printf("|%-4s%-10s%-12s%-8s%-8s%-10s%-8s%-8s%-8s%-8s%-8s%-8s%-8s|\n","No","Symbol","Name","Price","Growth","CashFlow","PE","PB","PB Dis","DY","ROE","PM","GR");
        displaySemLine();

        for(int i=0;i<list.size();i++){
           if(list.get(i).getROE()>=8 && list.get(i).getGearing()<=2&&list.get(i).getGrowth().equals(ANSI_GREEN + "SUPERB" + ANSI_RESET)&&list.get(i).getPm()>=12){
             queue.add(new SemTable(list.get(i).getSymbol(),list.get(i).getCompanyName(),list.get(i).getUsedPrice()
                      ,list.get(i).getGrowth(),list.get(i).getCashFlow(),list.get(i).getFPE(),list.get(i).getPB(),
                      list.get(i).getPBDiscount(),list.get(i).getDY(),list.get(i).getROE(),
                      list.get(i).getPm(),list.get(i).getGearing()));
           }
        }

        int size = queue.size();
        for(int i=0;i<size;i++){
            System.out.printf("|%-4d",(i+1));    
            System.out.println(queue.poll());
            displaySemLine();
        }
    }    
}

class LenYanTable implements Comparable<LenYanTable>{

    String symbol;
    String name;
    String price;
    Integer lenyanG;
    Integer lenyanW;
    String growth;
    String cashFlow;
    double pe;
    double pb;
    double pbDis;
    double dy;
    double roe;
    double pm;
    double gearing;

    public LenYanTable(String s,String n,String p,int lg,int lw,String g,String c,double pe,
    double pb,double pbdis,double dy,double roe,double pm,double gearing){
          this.symbol = s;
          this.name = n;
          this.price = p;
          this.lenyanG = lg;
          this.lenyanW = lw;
          this.growth = g;
          this.cashFlow = c;
          this.pe = pe;
          this.pb = pb;
          this.pbDis = pbdis;
          this.dy = dy;
          this.roe = roe;
          this.pm = pm;
          this.gearing = gearing;
    }


    @Override
    public int compareTo(LenYanTable o) {
        return this.lenyanG.compareTo(o.lenyanG);
    }
       
    @Override
    public String toString(){
        return String.format("%-8s%-10s%-8s%-8s%-8s%-18s%-18s%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f|",
        symbol,name,price,lenyanG,lenyanW,growth,cashFlow,pe,pb,pbDis,dy,roe,pm,gearing);
    }
}

class BuffettTable implements Comparable<BuffettTable>{

    String symbol;
    String name;
    String price;
    String growth;
    String cashFlow;
    double pe;
    double pb;
    Double pbDis;
    double dy;
    double roe;
    double pm;
    double gearing;

    public BuffettTable(String s,String n,String p,String g,String c,double pe,
    double pb,double pbdis,double dy,double roe,double pm,double gearing){
          this.symbol = s;
          this.name = n;
          this.price = p;
          this.growth = g;
          this.cashFlow = c;
          this.pe = pe;
          this.pb = pb;
          this.pbDis = pbdis;
          this.dy = dy;
          this.roe = roe;
          this.pm = pm;
          this.gearing = gearing;
    }


    @Override
    public int compareTo(BuffettTable o) {
        return o.pbDis.compareTo(this.pbDis);
    }
       
    @Override
    public String toString(){
        return String.format("%-10s%-12s%-8s%-18s%-18s%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f|",
        symbol,name,price,growth,cashFlow,pe,pb,pbDis,dy,roe,pm,gearing);
    }
}

class SemTable implements Comparable<SemTable>{

    String symbol;
    String name;
    String price;
    String growth;
    String cashFlow;
    double pe;
    double pb;
    double pbDis;
    double dy;
    Double roe;
    double pm;
    double gearing;

    public SemTable(String s,String n,String p,String g,String c,double pe,
    double pb,double pbdis,double dy,double roe,double pm,double gearing){
          this.symbol = s;
          this.name = n;
          this.price = p;
          this.growth = g;
          this.cashFlow = c;
          this.pe = pe;
          this.pb = pb;
          this.pbDis = pbdis;
          this.dy = dy;
          this.roe = roe;
          this.pm = pm;
          this.gearing = gearing;
    }


    @Override
    public int compareTo(SemTable o) {
        return this.roe.compareTo(o.roe);
    }
       
    @Override
    public String toString(){
        return String.format("%-10s%-12s%-8s%-18s%-18s%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f%-8.2f|",
        symbol,name,price,growth,cashFlow,pe,pb,pbDis,dy,roe,pm,gearing);
    }
}


