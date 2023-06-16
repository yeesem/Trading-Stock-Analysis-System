package org.example;

import java.util.*;

// import org.jcp.xml.dsig.internal.SignerOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.ServerResponse.SseBuilder;

import java.text.DecimalFormat;
import java.time.*;

/**
 *
 * @author Lenovo
 */
@Service
@Scope("prototype")
public class Stock {
    @Autowired
    PortfolioRecordRepository portfolioRecordRepository;

    private String symbol;
    // private int share;
    private double marketPrice;
    private List<Order> listOfOrders;
    private int initialTradingPeriod;
    private int MaxLots = 500000;

    public Stock() {
        this.symbol = "";
        // this.share = 0;
        this.marketPrice = 0;
        this.listOfOrders = new ArrayList<>();
        this.initialTradingPeriod = 3;
    }

    /// MUST RESET ALL THE VALUES AFTER INITIALIZED THE CLASS OBJECT
    public void resetAllTheValues(String symbol, double marketPrice) {
        this.symbol = symbol;
        // this.share = share;
        this.marketPrice = marketPrice;
    }

    // public Stock(String symbol, int share, double marketPrice){
    // this.symbol = symbol;
    // this.share = share;
    // this.marketPrice = marketPrice;
    // }

    public int getLots() {
        return this.MaxLots;
    }

    public void setLots(int lots) {
        this.MaxLots = lots;
    }

    public String getStockSymbol() {
        return this.symbol;
    }

    public List<Order> getListOFOrders() {
        return this.listOfOrders;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getMarketPrice() {
        return this.marketPrice;
    }

    public void displaySuggestedPrice() {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        double suggestedPrice1 = marketPrice * 0.99;
        double suggestedPrice2 = marketPrice * 1.01;
        System.out.printf("Suggested price within RM%.3f - RM%.3f\n",
                Double.parseDouble(decimalFormat.format(suggestedPrice1)),
                Double.parseDouble(decimalFormat.format(suggestedPrice2)));
    }

    // no restriction to the number of shares in the first three days of the
    // competition
    // rule on the minimum number of shares(100 shares)
    // the buy price set by the participant should be between teh
    // return true if the buy order is placed successfully, otherwise false
    // the order will be removed in the TradingMachine class if the buy order is
    // executed
    // implement the 500-lot rule after the first three days of the competition
    public boolean placeBuyOrder(UserTrading user, int share, double buyPrice) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        if (((buyPrice >= Double.parseDouble(decimalFormat.format(this.marketPrice * 0.99)))
                && (buyPrice <= Double.parseDouble(decimalFormat.format(this.marketPrice * 1.01))))
                && (user.getBalance() > 0 && user.getBalance() >= buyPrice * share)
                && (isWithinDuration() ? (share >= 100) : (share <= 50000 && share >= 100))) {
            if (isWithinDuration()) {
                Order order = new Order(user, this, "BUY", share, buyPrice);
                listOfOrders.add(order);
            }
            // implement 500-lot rule after first 3 days
            else {
                Order order = new Order(user, this, "BUY", share, buyPrice);
                listOfOrders.add(order);
            }
            return true;
        } else {
            if (share <= 0) {
                System.out.println("ERROR!! Number of shares should be non-zero and positive value!!");
            }
            if (share < 100 && share > 0) {
                System.out.println("You do not follow the minimum number of shares rule.");
            }
            if (!(initialTradingPeriod > 0) && share > 500) {
                System.out.println("You do not follow the 500-lot rule.");
            }
            if (!(user.getBalance() > 0 || user.getBalance() >= this.marketPrice)) {
                System.out.println("Your current balance is below the market price.");
            }
            if (!((buyPrice >= this.marketPrice * 0.99) && (buyPrice <= this.marketPrice * 1.01))) {
                System.out.println("The buy price is not within the range of suggested price.");
            }
            if (user.getBalance() < buyPrice * share) {
                System.out.println("Your current balance RM" + user.getBalance() + " is not enough to buy " + share
                        + " shares with RM" + buyPrice + " per share for stock " + symbol);
            }
            if (!isWithinDuration() && share > 50000) {
                System.out.println("You do not follow the 500-lot rule!!");
            }
            System.out.println("Fail to place a buy order.");
            return false;
        }
    }

    // the order will be removed in the TradingMachine class if the sell order is
    // executed
    // check does the user has sufficient amount of shares for the stock that he
    // wants to sell
    public boolean placeSellOrder(UserTrading user, int share, double sellPrice) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        int numberOfSharesinPortfolioRecord = -1;
        if (share <= 0) {
            System.out.println("ERROR!! Number of shares should be non-zero and positive value!!");
        }
        if (share < 100 && share > 0) {
            System.out.println("You do not follow the minimum number of shares rule.");
        } 
        else if (((sellPrice >= Double.parseDouble(decimalFormat.format(this.marketPrice * 0.99)))
                && (sellPrice <= Double.parseDouble(decimalFormat.format(this.marketPrice * 1.01))))) {
            if (portfolioRecordRepository != null) {
                // List<PortfolioRecord> stockSymbol =
                // portfolioRecordRepository.findAllByStockSymbol(symbol);
                List<PortfolioRecord> userID = portfolioRecordRepository.findByUserID(user.getUserID());
                for (int i = 0; i < userID.size(); i++) {
                    if (userID.get(i).getStockSymbol().equals(symbol)) {
                        numberOfSharesinPortfolioRecord = userID.get(i).getShare();
                    }
                }
            }
            if (numberOfSharesinPortfolioRecord >= share) {
                Order order = new Order(user, this, "SELL", share, sellPrice);
                listOfOrders.add(order);
                return true;
            } else {
                System.out.println("Number of shares for the stock symbol " + this.getStockSymbol()
                        + " in your portfolio is not enough to place an sell order of " + share
                        + " shares for this particular stock");
            }
        } else if (!((sellPrice >= this.marketPrice * 0.99) && (sellPrice <= this.marketPrice * 1.01))) {
            System.out.println("The sell price is not within the range of suggested price.");
        }
        System.out.println("Fail to place a sell order");
        return false;
    }

    public void updateMarketPrice(double newMarketPrice) {
        this.marketPrice = newMarketPrice;
    }

    // //return true if the order is in the list and is successfully removed from
    // the list, otherwise return false if the order is being processed
    // public boolean cancelOrder(Order order){
    // if(listOfOrders.contains(order)){
    // return listOfOrders.remove(order);
    // }
    // return false;
    // }

    // //update the list of order when an order is being processed and removed from
    // the list
    // public void updateListOrOrders(List<Order> newListOfOrders){
    // this.listOfOrders = newListOfOrders;
    // }

    // //return true if the time is within the market hours, otherwise return false.
    // public boolean isMarketOpen() {
    // ZonedDateTime currentTime =
    // ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
    // DayOfWeek dayOfWeek = currentTime.getDayOfWeek();
    // LocalTime time = currentTime.toLocalTime();

    // // Check if it is a weekday (Monday to Friday) and within regular market
    // hours
    // if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
    // LocalTime marketOpeningTime1 = LocalTime.of(9, 0);
    // LocalTime marketClosingTime1 = LocalTime.of(12, 30);
    // LocalTime marketOpeningTime2 = LocalTime.of(14, 30);
    // LocalTime marketClosingTime2 = LocalTime.of(17, 0);
    // return (time.isAfter(marketOpeningTime1) &&
    // time.isBefore(marketClosingTime1)) || (time.isAfter(marketOpeningTime2) &&
    // time.isBefore(marketClosingTime2));
    // }
    // return false;
    // }

    // return true if is within the 6 week durations, otherwise return false
    // SAMPLE ONLY HAVENT FIX THE START TIME FOR THE COMPETITION (have to set the
    // all the targets)
    public boolean isWithinDuration() {
        int targetStartMonth = 6;
        // int targerEndMonth = 6;
        int targetStartDay = 10;
        // int targetEndDay = 31;

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentDay = currentDate.getDayOfMonth();

        if (currentMonth == targetStartMonth && currentDay >= targetStartDay && currentDay <= targetStartDay + 2) {
            return true;
        }
        // else if (currentMonth == targetStartMonth && currentDay >= targetStartDay &&
        // currentDay <= targetEndDay) {
        // return true;
        // }
        else {
            return false;
        }
    }

}
