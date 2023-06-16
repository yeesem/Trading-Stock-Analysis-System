package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tradingRecord")
public class tradingrecord {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "No.")
  private Integer number;

  @Column(name = "User ID")
  private String userID;

  @Column(name = "Stock Symbol")
  private String stockSymbol;

  @Column(name = "Position")
  private String position;

  @Column(name = "Number of Shares")
  private Integer share;

  @Column(name = "Market Price")
  private Double marketPrice;

  @Column(name = "Time")
  private String time;

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getStockSymbol() {
    return stockSymbol;
  }

  public void setStockSymbol(String stockSymbol) {
    this.stockSymbol = stockSymbol;
  }

  public String getPosition(){
      return this.position;
  }
  
  public void setPosition(String position){
      this.position = position;
  }
  
  public Integer getShare(){
      return this.share;
  }
  
  public void setShare(Integer share){
      this.share = share;
  }
  
  public Double getMarketPrice(){
      return this.marketPrice;
  }
  
  public void setMarketPrice(Double marketPrice){
      this.marketPrice = marketPrice;
  }

  public String getTime(){
    return this.time;
  }

  public void setTime(String time){
    this.time = time;
  }
}