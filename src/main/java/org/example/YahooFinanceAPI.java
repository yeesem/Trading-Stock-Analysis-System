package org.example;

import java.util.Scanner;


public abstract class YahooFinanceAPI {
      
    Scanner input = new Scanner(System.in);
    protected String symbol;
    protected String interval;
    protected String range;

    public YahooFinanceAPI(){}

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        // System.out.print("Enter stock's symbol : ");
        // String symbol = input.next();
        this.symbol = symbol;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval() {
        System.out.print("Enter interval [1m|2m|5m|15m|30m|60m|1d|1wk|1mo]        : ");
        String interval = input.next();
        this.interval = interval;
    }

    public String getRange() {
        return range;
    }

    public void setRange() {
        System.out.print("Enter range [1d|5d|1mo|3mo|6mo|1y|2y|5y|10y|ytd|max] : ");
        String interval = input.next();
        this.range = interval;
    }

}
