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
@Table(name = "PortfolioRecord")
public class PortfolioRecord {

  //must assign ID to the one of the columns that the values are unique so that the table can be created
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "No.")
  private Integer number;

  @Column(name = "User ID")
  private String userID;  

  //must assign ID to the one of the columns that the values are unique so that the table can be created
  @Column(name = "Stock Symbol")
  private String stockSymbol;

  @Column(name = "Number of Shares")
  private Integer share;

  @Column(name = "Position Change")
  private Double positionChange;

  @Column(name = "Cost Price")
  private Double costPrice;

  @Column(name = "Value")
  private Double value;

  @Column(name = "Current Profit & Loss")
  private Double currentPL;

  @Column(name = "Overall Profit & Loss")
  private Double overallPL;

  @Column(name = "Current Point")
  private Double currentPoint;

  @Column(name = "Overall Point")
  private Double overallPoint;

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
  
  public Integer getShare(){
      return this.share;
  }
  
  public void setShare(Integer share){
      this.share = share;
  }

  public Double getPositionChange(){
    return positionChange;
  }

  public void setPositionChange(Double positionChange){
    this.positionChange = positionChange;
  }

  public Double getCostPrice(){
    return costPrice;
  }

  public void setCostPrice(Double costPrice){
    this.costPrice = costPrice;
  }

  public Double getvalue(){
      return this.value;
  }

  public void setValue(Double value){
      this.value = value;
  }

  public Double getCurrentPL(){
      return this.currentPL;
  }

  public void setCurrentPL(Double currentPL){
      this.currentPL = currentPL;
  }
  
  public Double getOverallPL(){
      return this.overallPL;
  }

  public void setOverallPL(Double overallPL){
      this.overallPL = overallPL;
  }

    public Double getCurrentPoint(){
      return this.currentPoint;
  }

  public void setCurrentPoint(Double currentPoint){
      this.currentPoint = currentPoint;
  }

    public Double getOverallPoint(){
      return this.overallPoint;
  }

  public void setOverallPoint(Double overallPoint){
      this.overallPoint = overallPoint;
  }

}
