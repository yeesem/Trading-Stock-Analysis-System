package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "stockmysql")
public class KeyStatisticsDatabase {
    @Id
    @Column(name = "Symbol",unique = true)
    private String symbol;
    
    @Column(name = "CompanyName")
    private String companyName;

    @Column(name = "UsedPrice")
    private String price;

    @Column(name = "PB")
    private Double pb;

    @Column(name = "ROE")
    private Double roe;

    @Column(name = "PB Discount%")
    private Double pbDiscount;

    @Column(name = "DY")
    private Double dy;

    @Column(name = "PM")
    private Double pm;

    @Column(name = "FPE")
    private Double fpe;

    @Column(name = "CashFlow")
    private String cf;

    @Column(name = "Gearing")
    private Double gearing;

    @Column(name = "Growth")
    private String growth;

    @Column(name = "MarginOfSafety")
    private String mos;

    @Column(name = "LenYanGrowth")
    private Integer lenyanGrowth;

    @Column(name = "LenYanWealth")
    private Integer lenyanWealth;
    
    //Symbol
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String tempt) {
        this.symbol = tempt;
    }

    //CompanyName
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String tempt) {
        this.companyName = tempt;
    }

    //Used Price
    public String getUsedPrice() {
        return price;
    }

    public void setUsedPrice(String tempt) {
        this.price = tempt;
    }
    
    //ROE
    public double getROE() {
        return roe;
    }

    public void setROE(double tempt) {
        this.roe = tempt;
    }

    //PB
    public double getPB() {
        return pb;
    }

    public void setPB(double tempt) {
        this.pb = tempt;
    }

    //PB Discount
    public double getPBDiscount() {
        return pbDiscount;
    }

    public void setPBDiscount(double tempt) {
        this.pbDiscount = tempt;
    }

    //Dividend yield
    public double getDY() {
        return dy;
    }

    public void setDY(double tempt) {
        this.dy = tempt;
    }

    //Profit margin
    public double getProfitMargin() {
        return pm;
    }

    public void setProfitMargin(double tempt) {
        this.pm = tempt;
    }

    //Cash Flow
    public String getCashFlow() {
        return cf;
    }

    public void setCashFlow(String tempt) {
        this.cf = tempt;
    }

    //Gearing
    public double getGearing() {
        return gearing;
    }

    public void setGearing(double tempt) {
        this.gearing = tempt;
    }

    //Growth
    public String getGrowth() {
        return growth;
    }

    public void setGrowth(String tempt) {
        this.growth = tempt;
    }

    //FPE
    public double getFPE() {
        return fpe;
    }

    public void setFPE(double tempt) {
        this.fpe = tempt;
    }

    //LenYan Growth
    public int getLenYanGrowth() {
        return lenyanGrowth;
    }

    public void setLenYanGrowth(int tempt) {
        this.lenyanGrowth = tempt;
    }

    //MoS
    public String getMoS() {
        return mos;
    }

    public void setMoS(String tempt) {
        this.mos = tempt;
    }

    //LenYan Wealth
    public int getLenYanWealth() {
        return lenyanWealth;
    }

    public void setLenYanWealth(int tempt) {
        this.lenyanWealth = tempt;
    }

}
