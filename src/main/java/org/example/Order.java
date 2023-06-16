package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Order {
    private Stock stock;
    private UserTrading user;
    private String type;
    private int share; //buying or selling numbers of shares
    private double pricePerShare;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private LocalDateTime currentTime;
    
    public Order(UserTrading user, Stock stock, String type, int share, double pricePerShare){
        this.user = user;
        this.stock = stock;
        this.type = type;
        this.share = share;
        this.pricePerShare = pricePerShare;
        this.currentTime = LocalDateTime.now();
    }
    
    public UserTrading getUser(){
        return this.user;
    }
    
    public Stock getStock(){
        return this.stock;
    }
    
    public String getType(){
        return this.type;
    }
    
    public int getShare(){
        return this.share;
    }
    
    public double getPricePerShare(){
        return this.pricePerShare;
    }
    //total buying or selling price
    public double getTotalPrice(){
        return getShare() * getPricePerShare();
    }
    
    public String getCurrentTime(){
        return dtf.format(currentTime);
    }
}
