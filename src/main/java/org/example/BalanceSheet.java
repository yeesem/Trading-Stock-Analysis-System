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
public class BalanceSheet extends YahooFinanceAPI{
    ArrayList<Long>longTermLia = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<Long>totalNetAssetAndEquity = new ArrayList<>();
    ArrayList<Long>shortTermLoan = new ArrayList<>();
    ArrayList<Long>currentAsset = new ArrayList<>();
    ArrayList<Long>receivable = new ArrayList<>();
    ArrayList<Long>capital = new ArrayList<>();
    ArrayList<Long>currentLiability = new ArrayList<>();
    ArrayList<Long>retainedEarning = new ArrayList<>();
    ArrayList<Long>shortTermInvest = new ArrayList<>();
    ArrayList<Long>grossPPE = new ArrayList<>();
    ArrayList<Long>payable = new ArrayList<>();
    ArrayList<Long>goodWill = new ArrayList<>();
    ArrayList<Long>netPPE = new ArrayList<>();
    ArrayList<Long>cashAndCashEquivalent = new ArrayList<>();
    ArrayList<Long>nonCurrentAsset = new ArrayList<>();
    ArrayList<Long>nonCurrentDeferTaxLia = new ArrayList<>();
    ArrayList<Long>totalAsset = new ArrayList<>();
    ArrayList<Long>depreciation = new ArrayList<>();
    ArrayList<Long>totalNonCurrentLia = new ArrayList<>();
    ArrayList<Long>inventory = new ArrayList<>();
    ArrayList<Long>otherCurrentAsset = new ArrayList<>();
    ArrayList<Long>otherCurrentLia = new ArrayList<>();
    ArrayList<Long>netCurrentAsset = new ArrayList<>();
    ArrayList<Long>otherNonCurrentAsset = new ArrayList<>();
    ArrayList<Long>otherNonCurrentLia = new ArrayList<>();
    ArrayList<Long>totalNetAsset = new ArrayList<>();
    ArrayList<Long>shareHolderFund = new ArrayList<>();
    ArrayList<Long>otherShareHolderFund = new ArrayList<>();
    ArrayList<Long>minorityInterest = new ArrayList<>();

    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m";
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


    int temptSize = 0;

    public void clearAll() {
        longTermLia = new ArrayList<>();
        date = new ArrayList<>();
        totalNetAssetAndEquity = new ArrayList<>();
        shortTermLoan = new ArrayList<>();
        currentAsset = new ArrayList<>();
        receivable = new ArrayList<>();
        capital = new ArrayList<>();
        currentLiability = new ArrayList<>();
        retainedEarning = new ArrayList<>();
        shortTermInvest = new ArrayList<>();
        grossPPE = new ArrayList<>();
        payable = new ArrayList<>();
        goodWill = new ArrayList<>();
        netPPE = new ArrayList<>();
        cashAndCashEquivalent = new ArrayList<>();
        nonCurrentAsset = new ArrayList<>();
        nonCurrentDeferTaxLia = new ArrayList<>();
        totalAsset = new ArrayList<>();
        depreciation = new ArrayList<>();
        totalNonCurrentLia = new ArrayList<>();
        inventory = new ArrayList<>();
        otherCurrentAsset = new ArrayList<>();
        otherCurrentLia = new ArrayList<>();
        netCurrentAsset = new ArrayList<>();
        otherNonCurrentAsset = new ArrayList<>();
        otherNonCurrentLia = new ArrayList<>();
        totalNetAsset = new ArrayList<>();
        shareHolderFund = new ArrayList<>();
        otherShareHolderFund = new ArrayList<>();
        minorityInterest = new ArrayList<>();
    }

    public BalanceSheet(){};
    public String getData(String stockName){
        String jsonData = null;
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-balance-sheet?symbol=" + stockName;
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

    public void getBalanceSheet(String stockName) throws ParseException {
        clearAll();
        JSONParser parser = new JSONParser();
        Object object = parser.parse(getData(stockName));
        JSONObject mainJsonObj = (JSONObject) object;

        JSONObject timeSeries = (JSONObject) mainJsonObj.get("timeSeries");
        JSONArray longlia = (JSONArray) timeSeries.get("annualLongTermDebt");
        for (Object obj : longlia) {
            if(obj==null) {longTermLia.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            longTermLia.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray totalnetassetnequity = (JSONArray) timeSeries.get("annualStockholdersEquity");
        for (Object obj : totalnetassetnequity) {
            if(obj==null) {totalNetAssetAndEquity.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            totalNetAssetAndEquity.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
            date.add((String)jsonObject.get("asOfDate"));
        }

        JSONArray shorttermloan = (JSONArray) timeSeries.get("annualCurrentDebt");
        for (Object obj : shorttermloan) {
            if(obj==null) {shortTermLoan.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            shortTermLoan.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray annualcurrentasset = (JSONArray) timeSeries.get("annualCurrentAssets");
        for (Object obj : annualcurrentasset) {
            if(obj==null) {currentAsset.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            currentAsset.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray receive = (JSONArray) timeSeries.get("annualAccountsReceivable");
        for (Object obj : receive) {
            if(obj==null) {receivable.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            receivable.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray capi = (JSONArray) timeSeries.get("annualCapitalStock");
        for (Object obj : capi) {
            if(obj==null) {capital.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            capital.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray currentLia = (JSONArray) timeSeries.get("annualCurrentLiabilities");
        for (Object obj : currentLia) {
            if(obj==null) {currentLiability.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            currentLiability.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray retainedearning = (JSONArray) timeSeries.get("annualRetainedEarnings");
        for (Object obj : retainedearning) {
            if(obj==null) {retainedEarning.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            retainedEarning.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray shortterminvest = (JSONArray) timeSeries.get("annualOtherShortTermInvestments");
        for (Object obj : shortterminvest) {
            if(obj==null) {shortTermInvest.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            shortTermInvest.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray ppe = (JSONArray) timeSeries.get("annualGrossPPE");
        for (Object obj : capi) {
            if(obj==null) {grossPPE.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            grossPPE.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray paya = (JSONArray) timeSeries.get("annualAccountsPayable");
        for (Object obj : paya) {
            if(obj==null) {payable.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            payable.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray goodwill = (JSONArray) timeSeries.get("annualGoodwill");
        for (Object obj : goodwill) {
            if(obj==null) {goodWill.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            goodWill.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray netppe = (JSONArray) timeSeries.get("annualNetPPE");
        for (Object obj : netppe) {
            if(obj==null) {netPPE.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            netPPE.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray noncurrentasset = (JSONArray) timeSeries.get("annualTotalNonCurrentAssets");
        for (Object obj : noncurrentasset) {
            if(obj==null) {nonCurrentAsset.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            nonCurrentAsset.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray cashandcashequivalent = (JSONArray) timeSeries.get("annualCashCashEquivalentsAndShortTermInvestments");
        for (Object obj : cashandcashequivalent) {
            if(obj==null) {cashAndCashEquivalent.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            cashAndCashEquivalent.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray noncurrentdertaxlia = (JSONArray) timeSeries.get("annualNonCurrentDeferredTaxesLiabilities");
        for (Object obj : noncurrentdertaxlia) {
            if(obj==null) {nonCurrentDeferTaxLia.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            nonCurrentDeferTaxLia.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray totalasset = (JSONArray) timeSeries.get("annualTotalAssets");
        for (Object obj : totalasset) {
            if(obj==null) {totalAsset.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            totalAsset.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray depre = (JSONArray) timeSeries.get("annualAccumulatedDepreciation");
        for (Object obj : depre) {
            if(obj==null) {depreciation.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            depreciation.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray totalnoncurrentlia = (JSONArray) timeSeries.get("annualTotalNonCurrentLiabilitiesNetMinorityInterest");
        for (Object obj : totalnoncurrentlia) {
            if(obj==null) {totalNonCurrentLia.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            totalNonCurrentLia.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray inven = (JSONArray) timeSeries.get("annualInventory");
        for (Object obj : inven) {
            if(obj==null) {inventory.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            inventory.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        JSONArray shareholderfund = (JSONArray) timeSeries.get("annualStockholdersEquity");
        for (Object obj : shareholderfund) {
            if(obj==null) {shareHolderFund.add((long)0);continue;}
            JSONObject jsonObject = (JSONObject) obj;
            shareHolderFund.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
        }

        for (int i=0;i<currentAsset.size();i++){
            if(currentAsset.isEmpty()) fillEmptyList(currentAsset);
            if(receivable.isEmpty()) fillEmptyList(receivable);
            if(inventory.isEmpty()) fillEmptyList(inventory);
            if(cashAndCashEquivalent.isEmpty()) fillEmptyList(cashAndCashEquivalent);
            otherCurrentAsset.add(currentAsset.get(i) - receivable.get(i) - inventory.get(i) - cashAndCashEquivalent.get(i));
        }

        for (int i=0;i<currentAsset.size();i++){
            if(currentLiability.isEmpty()) fillEmptyList(currentLiability);
            netCurrentAsset.add(currentAsset.get(i) - currentLiability.get(i));
        }

        for (int i=0;i<nonCurrentAsset.size();i++){
            if(nonCurrentAsset.isEmpty()) fillEmptyList(nonCurrentAsset);
            if(netPPE.isEmpty()) fillEmptyList(netPPE);
            if(goodWill.isEmpty()) fillEmptyList(goodWill);
            otherNonCurrentAsset.add(nonCurrentAsset.get(i)-netPPE.get(i)-goodWill.get(i));
        }

        for (int i=0;i<totalNonCurrentLia.size();i++){
            if(totalNonCurrentLia.isEmpty()) fillEmptyList(totalNonCurrentLia);
            if(longTermLia.isEmpty()) fillEmptyList(longTermLia);
            if(nonCurrentDeferTaxLia.isEmpty()) fillEmptyList(nonCurrentDeferTaxLia);
            otherNonCurrentLia.add(totalNonCurrentLia.get(i)-longTermLia.get(i)-nonCurrentDeferTaxLia.get(i));
        }

        for (int i=0;i<totalNonCurrentLia.size();i++){
            if(netCurrentAsset.isEmpty()) fillEmptyList(netCurrentAsset);
            if(nonCurrentAsset.isEmpty()) fillEmptyList(nonCurrentAsset);
            if(totalNonCurrentLia.isEmpty()) fillEmptyList(totalNonCurrentLia);
            // System.out.println("before here");
            // System.out.println(netCurrentAsset);
            // System.out.println(nonCurrentAsset);
            // System.out.println(totalNonCurrentLia);
            // System.out.println(totalNetAsset);
            totalNetAsset.add(netCurrentAsset.get(i)+nonCurrentAsset.get(i)-totalNonCurrentLia.get(i));
        }
        for (int i=0;i<totalNonCurrentLia.size();i++){
            if(currentLiability.isEmpty()) fillEmptyList(currentLiability);
            if(payable.isEmpty()) fillEmptyList(payable);
            if(shortTermLoan.isEmpty()) fillEmptyList(shortTermLoan);
            otherCurrentLia.add(currentLiability.get(i)-payable.get(i)-shortTermLoan.get(i));
        }
        for (int i=0;i<shareHolderFund.size();i++){
            if(shareHolderFund.isEmpty()) fillEmptyList(shareHolderFund);
            if(capital.isEmpty()) fillEmptyList(capital);
            if(retainedEarning.isEmpty()) fillEmptyList(retainedEarning);
            otherShareHolderFund.add(shareHolderFund.get(i)-capital.get(i)-retainedEarning.get(i));
        }
        for (int i=0;i<totalNetAsset.size();i++){
            if(totalNetAsset.isEmpty()) fillEmptyList(totalNetAsset);
            if(shareHolderFund.isEmpty()) fillEmptyList(shareHolderFund);
            // System.out.println("here");
            // System.out.println(totalNetAsset);
            // System.out.println(shareHolderFund);
            // System.out.println(minorityInterest);
            minorityInterest.add(totalNetAsset.get(i)-shareHolderFund.get(i));
        }
    }

    public void displayLine(int size){
        int temptSizes = 32 + (20*size);
        for(int i=0;i<temptSizes;i++){
            System.out.print("-");
        }
        System.out.println();
    }

    public void display() {
        temptSize = date.size();
        System.out.println();
        System.out.println(YELLOW_BOLD + "\nBalance Sheet" + ANSI_RESET);

        displayLine(temptSize);
        System.out.printf("|%-30s|","Date (RM '000'000)");
        for (int i=0;i<date.size();i++){
            System.out.printf("%-20s",date.get(i));
        }
        System.out.print("|");
        System.out.println();
        displayLine(temptSize);

        System.out.println(GREEN_BOLD_BRIGHT + "Current Asset : " + ANSI_RESET);
        displayLine(temptSize);

        String[] totalCurrentAsset = {"Cash & Security", "Inventories", "Receivables", "Others", "Total Current Asset"};
        ArrayList<ArrayList<Long>> currentAssetList = new ArrayList<>();
        currentAssetList.add(cashAndCashEquivalent);
        currentAssetList.add(inventory);
        currentAssetList.add(receivable);
        currentAssetList.add(otherCurrentAsset);
        currentAssetList.add(currentAsset);
        display(totalCurrentAsset, currentAssetList);

        System.out.println();

        System.out.println(GREEN_BOLD_BRIGHT + "Current Liabilities : "+ ANSI_RESET);
        displayLine(temptSize);
        String[] totalCurrentLia = {"Short Term Loan", "Payable","Others", "Total Current Liability"};
        ArrayList<ArrayList<Long>>currentLiaList = new ArrayList<>();
        currentLiaList.add(shortTermLoan);
        currentLiaList.add(payable);
        currentLiaList.add(otherCurrentLia);
        currentLiaList.add(currentLiability);
        display(totalCurrentLia, currentAssetList);
        System.out.println();

        displayLine(temptSize);
        System.out.printf("|" + ANSI_YELLOW + "%-30s"+ ANSI_RESET + "|","Net Current Asset  ");
        for (int i=0;i<netCurrentAsset.size();i++){
            System.out.printf("%-20s",(double)netCurrentAsset.get(i)/1000000);
        }
        System.out.println("|");
        displayLine(temptSize);

        System.out.println("\n");

        System.out.println(RED_BOLD_BRIGHT + "Non-Current Asset : " + ANSI_RESET);
        displayLine(temptSize);
        String [] nonCurrentAssetA = {"Property, Plant & Equipment","Intangible Asset","Others","Total Non-Current Asset"};
        ArrayList<ArrayList<Long>> nonCurrentAssetList = new ArrayList<>();
        nonCurrentAssetList.add(netPPE);
        nonCurrentAssetList.add(goodWill);
        nonCurrentAssetList.add(otherNonCurrentAsset);
        nonCurrentAssetList.add(nonCurrentAsset);
        display(nonCurrentAssetA,nonCurrentAssetList);

        System.out.println();

        System.out.println(RED_BOLD_BRIGHT + "Non-Current Liability : " + ANSI_RESET);
        displayLine(temptSize);
        String[]nonCurrentLiaA = {"Long Term Liability","Deferred Tax Liability","Others","Total Non-Current Liability"};
        ArrayList<ArrayList<Long>>nonCurrentLiaList = new ArrayList<>();
        nonCurrentLiaList.add(longTermLia);
        nonCurrentLiaList.add(nonCurrentDeferTaxLia);
        nonCurrentLiaList.add(otherNonCurrentLia);
        nonCurrentLiaList.add(totalNonCurrentLia);
        display(nonCurrentLiaA,nonCurrentLiaList);

        displayLine(temptSize);
        System.out.printf("|" + ANSI_YELLOW + "%-30s"+ ANSI_RESET + "|","Total Net Asset");
        for (int j=0;j<totalNetAsset.size();j++){
            System.out.printf("%-20s",(double)totalNetAsset.get(j)/1000000);
        }
        System.out.println("|");
        displayLine(temptSize);

        System.out.println("\n");
        System.out.println(BLUE_BOLD_BRIGHT + "Shareholder Fund : " + ANSI_RESET);
        displayLine(temptSize);
        String [] shareHolderA = {"Capital","Reserve","Others","Total Shareholder Fund"};
        ArrayList<ArrayList<Long>>shareHolderList = new ArrayList<>();
        shareHolderList.add(capital);
        shareHolderList.add(retainedEarning);
        shareHolderList.add(otherShareHolderFund);
        shareHolderList.add(shareHolderFund);
        display(shareHolderA,shareHolderList);

        System.out.println();

        displayLine(temptSize);
        String [] minorityInterestNEquityA = {"Minority Interest"};
        ArrayList<ArrayList<Long>>minorityInterestNEquityList = new ArrayList<>();
        minorityInterestNEquityList.add(minorityInterest);
        display(minorityInterestNEquityA,minorityInterestNEquityList);

        System.out.printf("|" + ANSI_YELLOW + "%-30s"+ ANSI_RESET + "|","Total Equity");
        for (int j=0;j<totalNetAsset.size();j++){
            System.out.printf("%-20s",(double)totalNetAsset.get(j)/1000000);
        }
        System.out.println("|");
        displayLine(temptSize);


        System.out.println();

        displayLine(temptSize);
        System.out.printf("|" + ANSI_YELLOW + "%-30s"+ ANSI_RESET + "|","Total Asset");
        for (int j=0;j<totalAsset.size();j++){
            System.out.printf("%-20s",(double)totalAsset.get(j)/1000000);
        }
        System.out.println("|");
        displayLine(temptSize);
    }

    public void display(String[]content,ArrayList<ArrayList<Long>>list){
        for (int i=0;i<content.length;i++){
            System.out.printf("|%-30s|",content[i]);
            for (int j=0;j<list.get(i).size();j++){
               System.out.printf("%-20s",(double)list.get(i).get(j)/1000000);
            }
            System.out.println("|");
        }
        displayLine(temptSize);
    }

    public void fillEmptyList(ArrayList<Long>list){
        for (int i=0;i<date.size();i++){
            list.add((long)0);
        }
    }
}
