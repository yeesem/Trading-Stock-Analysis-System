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
public class CashFlow extends YahooFinanceAPI{
    ArrayList<Long>changeInventory = new ArrayList<>();
    ArrayList<String>date = new ArrayList<>();
    ArrayList<Long>operatingCashFlow = new ArrayList<>();
    ArrayList<Long>investingCashFlow = new ArrayList<>();
    ArrayList<Long>capex = new ArrayList<>();
    ArrayList<Long>depreciation = new ArrayList<>();
    ArrayList<Long>netCashGenerating = new ArrayList<>();
    ArrayList<Long>cashBegin = new ArrayList<>();
    ArrayList<Long>cashEnd = new ArrayList<>();
    ArrayList<Long>otherCashFlow = new ArrayList<>();
    ArrayList<Long>cashFlowReceivable = new ArrayList<>();
    ArrayList<Long>cashFlowPayable = new ArrayList<>();
    ArrayList<Long>changeWorkingCapital = new ArrayList<>();
    ArrayList<Long>beforeOperatingCashFlow = new ArrayList<>();
    ArrayList<Long>otherInvestingCashFlow = new ArrayList<>();
    ArrayList<Long>dividendPaid = new ArrayList<>();
    ArrayList<Long>stockFinancing = new ArrayList<>();
    ArrayList<Long>financingCashFlow = new ArrayList<>();
    ArrayList<Long>payDebt = new ArrayList<>();
    BalanceSheet getData = new BalanceSheet();
    IncomeStatement getDataIncome = new IncomeStatement();

    int lineDot = 0;

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

    public CashFlow(){}

    public void clearAll(){
       changeInventory = new ArrayList<>();
       date = new ArrayList<>();
        operatingCashFlow = new ArrayList<>();
        investingCashFlow = new ArrayList<>();
        capex = new ArrayList<>();
        depreciation = new ArrayList<>();
        netCashGenerating = new ArrayList<>();
        cashBegin = new ArrayList<>();
        cashEnd = new ArrayList<>();
        otherCashFlow = new ArrayList<>();
        cashFlowReceivable = new ArrayList<>();
        cashFlowPayable = new ArrayList<>();
        changeWorkingCapital = new ArrayList<>();
        beforeOperatingCashFlow = new ArrayList<>();
        otherInvestingCashFlow = new ArrayList<>();
        dividendPaid = new ArrayList<>();
        stockFinancing = new ArrayList<>();
        financingCashFlow = new ArrayList<>();
        payDebt = new ArrayList<>();
    }

    public String getData(String stockName){
        String jsonData = null;
        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-cash-flow?symbol=" + stockName;
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
    public void getCashFlow(String stockName) throws ParseException {
        clearAll();
        JSONParser parser = new JSONParser();
        Object object = parser.parse(getData(stockName));
        JSONObject mainJsonObj = (JSONObject) object;

        JSONObject timeSeries = (JSONObject) mainJsonObj.get("timeSeries");

        JSONArray Date = (JSONArray) timeSeries.get("annualOperatingCashFlow");
        for (Object obj : Date) {
            JSONObject jsonObject = (JSONObject) obj;
            date.add((String)jsonObject.get("asOfDate"));
        }
        loadData(timeSeries,"annualChangeInInventory",changeInventory);
        loadData(timeSeries,"annualOperatingCashFlow",operatingCashFlow);
        loadData(timeSeries,"annualInvestingCashFlow",investingCashFlow);
        loadData(timeSeries,"annualCapitalExpenditure",capex);
        loadData(timeSeries,"annualDepreciationAndAmortization",depreciation);
        loadData(timeSeries,"annualChangeInCashSupplementalAsReported",netCashGenerating);
        loadData(timeSeries,"annualBeginningCashPosition",cashBegin);
        loadData(timeSeries,"annualEndCashPosition",cashEnd);
        loadData(timeSeries,"annualChangeInWorkingCapital",changeWorkingCapital);
        loadData(timeSeries,"annualCashDividendsPaid",dividendPaid);
        loadData(timeSeries,"annualRepaymentOfDebt",payDebt);
        loadData(timeSeries,"annualCommonStockIssuance",stockFinancing);

        for (int i=0;i<date.size();i++){
            otherCashFlow.add(cashEnd.get(i) - cashBegin.get(i) - netCashGenerating.get(i));
        }

        for (int i=0;i<date.size();i++){
            beforeOperatingCashFlow.add(operatingCashFlow.get(i) - changeWorkingCapital.get(i));
        }

        for (int i=0;i<date.size();i++){
            otherInvestingCashFlow.add(netCashGenerating.get(i) - operatingCashFlow.get(i) - investingCashFlow.get(i));
        }

        for (int i=0;i<date.size();i++){
            financingCashFlow.add(netCashGenerating.get(i) - operatingCashFlow.get(i) - investingCashFlow.get(i));
        }

        cashFlowReceivable.add((long)0);
        cashFlowPayable.add((long)0);
        getData.getBalanceSheet(stockName);
        getDataIncome.getIncomeStatement(stockName);

        if(!getData.receivable.isEmpty()) {
            for (int i = 1; i < getData.receivable.size(); i++) {
                cashFlowReceivable.add(getData.receivable.get(i) - getData.receivable.get(i - 1));
            }
        }else{
            for (int i = 1;i<date.size();i++)
                cashFlowReceivable.add((long)0);
        }

        if(!getData.payable.isEmpty()) {
            for (int i = 1; i < getData.payable.size(); i++) {
                cashFlowPayable.add(getData.payable.get(i) - getData.payable.get(i - 1));
            }
        }else{
            for (int i = 1;i<date.size();i++)
                cashFlowPayable.add((long)0);
        }
    }
    public void loadData(JSONObject timeSeries,String financialTerm,ArrayList<Long>list){
        JSONArray object = (JSONArray) timeSeries.get(financialTerm);
        if(!object.isEmpty()) {
            for (Object obj : object) {
                if (obj == null) {list.add((long) 0);continue;}
                JSONObject jsonObject = (JSONObject) obj;
                list.add((Long) ((JSONObject) jsonObject.get("reportedValue")).get("raw"));
            }
        }else{
            for (int i=0;i<date.size();i++){
                list.add((long)0);
            }
        }
    }

    public void displayLine(int size){
        int dot = 32 + (20*size);
        for(int i=0;i<dot;i++){
            System.out.print("-");
        }
        System.out.println();
    }

    public void display(){

        lineDot = date.size();

        System.out.println(YELLOW_BOLD +"\n\nCash Flow Statement" + ANSI_RESET);

        displayLine(lineDot);
        System.out.printf("|%-30s|","Date (RM '000'000)");
        for (int i=0;i<date.size();i++){
            System.out.printf("%-20s",date.get(i));
        }
        System.out.print("|");
        System.out.println();

        displayLine(lineDot);
        System.out.println(GREEN_BOLD_BRIGHT + "Operating Activities : " + ANSI_RESET);

        displayLine(lineDot);
        display("Profit Before Tax",getDataIncome.pretaxIncome);
        displayLine(lineDot);

        System.out.println(ANSI_GREEN + "\nAdjustment for : " + ANSI_RESET);
        displayLine(lineDot);
        display("Depreciation/Amortization",changeInventory);
        displayLine(lineDot);
        display("Operating profit before",getDataIncome.pretaxIncome,changeInventory);
        System.out.printf("|%-30s|","working capital changes");
        System.out.println();
        displayLine(lineDot);

        System.out.println();

        System.out.println(ANSI_GREEN + "Changes in Working Capital : " + ANSI_RESET);
        displayLine(lineDot);
        display("Receivable",cashFlowReceivable);
        display("Payable",cashFlowPayable);
        display("Inventory",changeInventory);
        displayLine(lineDot);
        display("Operating activities",operatingCashFlow);
        displayLine(lineDot);

        System.out.println();

        System.out.println(RED_BOLD_BRIGHT + "Investing Activity : " + ANSI_RESET);
        displayLine(lineDot);
        display("Capex",capex);
        display("Others",otherInvestingCashFlow);
        displayLine(lineDot);
        display("Investment activities",investingCashFlow);
        displayLine(lineDot);

        System.out.println();

        System.out.println(BLUE_BOLD_BRIGHT + "Financing Activity : " + ANSI_RESET);
        displayLine(lineDot);
        display("Dividend Paid",dividendPaid);
        display("Stock Financing",stockFinancing);
        display("Repayment of Borrowings",payDebt);
        displayLine(lineDot);
        display("Financing Cash Flow",financingCashFlow);
        displayLine(lineDot);

        System.out.println();

        System.out.println(YELLOW_BOLD_BRIGHT + "Cash & Cash Equivalents : " + ANSI_RESET);
        displayLine(lineDot);
        display("Net Cash Generating",netCashGenerating);
        display("Cash Beginning of year",cashBegin);
        display("Others",otherCashFlow);
        displayLine(lineDot);
        display("Cash End of Year",cashEnd);
        displayLine(lineDot);

    }

    public void display(String content,ArrayList<Long>list){
        System.out.printf("|%-30s|",content);
        for (int j=0;j<list.size();j++){
            System.out.printf("%-20s",(double)list.get(j)/1000000);
        }
        System.out.print("|");
        System.out.println();
    }

    public void display(String content,ArrayList<Long>list,ArrayList<Long>list2){
        int size = Math.min(list.size(),list2.size());
        System.out.printf("|%-30s|",content);
        for (int j=0;j<size;j++){
            System.out.printf("%-20s",(( ((double)list.get(j)) + ((double)list2.get(j)) )/1000000));
        }
        System.out.print("|");
        System.out.println();
    }

}
