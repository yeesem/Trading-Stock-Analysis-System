package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.hibernate.annotations.SourceType;
// import org.jcp.xml.dsig.internal.SignerOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lenovo
 */
@Service
public class TradingMachine {
    @Autowired
    tradingRecordRepository tradingRecordRepository;

    private Map<Stock, List<Order>> buyOrders;
    private Map<Stock, List<Order>> sellOrders;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public TradingMachine() {
        this.buyOrders = new HashMap<>();
        this.sellOrders = new HashMap<>();
    }

    // MUST RESET ALL THE VALUES AFTER INITIALIZED THE CLASS OBJECT
    public void resetAllValues(ArrayList<Stock> stocks) {
        for (Stock stock : stocks) {
            buyOrders.put(stock, new ArrayList<>());
            sellOrders.put(stock, new ArrayList<>());
        }
    }

    // public TradingMachine(List<Stock> stocks){
    // this.stocks = stocks;
    // buyOrders = new HashMap<>();
    // sellOrders = new HashMap<>();
    // for(Stock stock: stocks){
    // buyOrders.put(stock, new ArrayList<>());
    // sellOrders.put(stock, new ArrayList<>());
    // }
    // }

    public boolean displayListOfBuyOrders(Stock stock) {
        List<Order> buyOrder = buyOrders.get(stock);
        if (buyOrder == null) {
            // System.out.println("No any buy order is placed.");
            return false;
        } else {
            if (buyOrder.size() == 0) {
                // System.out.println("No any buy order is placed.");
                return false;
            } else {
                System.out.println("List of buy orders");
                System.out.println(stock.getStockSymbol() + ": ");
                System.out.println(
                        "-------------------------------------------------------------------------------------------------");
                System.out.println(
                        "|  User ID  |  Price per Share(RM)  |  No. shares  |  Total price(RM)  |         Time           |");
                for (int i = 0; i < buyOrder.size(); i++) {
                    System.out.println(
                            "-------------------------------------------------------------------------------------------------");
                    System.out.printf("| %10s| %22.3f| %13d| %18.3f| %23s|\n",
                            buyOrder.get(i).getUser().getUserID(), buyOrder.get(i).getPricePerShare(),
                            buyOrder.get(i).getShare(), buyOrder.get(i).getTotalPrice(),
                            buyOrder.get(i).getCurrentTime());
                }
                System.out.println(
                        "-------------------------------------------------------------------------------------------------");
                        return true;
            }
        }
    }

    public boolean displayListOfSellOrders(Stock stock) {
        List<Order> sellOrder = sellOrders.get(stock);
        if (sellOrder == null) {
            // System.out.println("No any sell order is placed.");
            return false;
        } else {
            if (sellOrder.size() == 0) {
                // System.out.println("No any sell order is placed.");
                return false;
            } else {
                System.out.println("List of sell orders");
                System.out.println(stock.getStockSymbol() + ": ");
                System.out.println(
                        "-------------------------------------------------------------------------------------------------");
                System.out.println(
                        "|  User ID  |  Price per Share(RM)  |  No. shares  |  Total price(RM)  |         Time           |");
                for (int i = 0; i < sellOrder.size(); i++) {
                    System.out.println(
                            "-------------------------------------------------------------------------------------------------");
                    System.out.printf("| %10s| %22.3f| %13d| %18.3f| %23s|\n",
                            sellOrder.get(i).getUser().getUserID(), sellOrder.get(i).getPricePerShare(),
                            sellOrder.get(i).getShare(), sellOrder.get(i).getTotalPrice(),
                            sellOrder.get(i).getCurrentTime());
                }
                System.out.println(
                        "-------------------------------------------------------------------------------------------------");
                        return true;
            }
        }
    }

    public void executeOrder(Order order, Portfolio portfolio) {
        if (order.getType().equals("BUY")) {
            buyOrders.get(order.getStock()).add(order);
            if(sellOrders.size() != 0){
                tryExecuteBuyAndSellOrderBetweenParticipants(order.getStock(), portfolio);
            }
            System.out.println(order.getStock().getStockSymbol());
            tryExecuteBuyOrder(order.getStock(), portfolio);
        } else if (order.getType().equals("SELL")) {
            sellOrders.get(order.getStock()).add(order);
            if(buyOrders.size() != 0){
                tryExecuteBuyAndSellOrderBetweenParticipants(order.getStock(), portfolio);
            }
            tryExecuteSellOrder(order.getStock(), portfolio);
        }
    }

    //trading between buyer and the system
    private void tryExecuteBuyOrder(Stock stock, Portfolio portfolio) {
        tradingrecord tradingrecord = new tradingrecord();
        List<Order> orders = buyOrders.get(stock);
        double marketPrice = stock.getMarketPrice();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getPricePerShare() == marketPrice && (stock.isWithinDuration() ? true : stock.getLots() > 0)) {
                int currentShares = order.getShare();
                double totalPrice = order.getTotalPrice();
                // if (portfolio.getValues(stock) >= totalPrice) {
                    LocalDateTime currentTime = LocalDateTime.now();
                    if (!stock.isWithinDuration()) {
                        int currentStockLots = stock.getLots() - order.getShare();
                        stock.setLots(currentStockLots);
                    }
                    portfolio.addStock(order.getUser(), stock, order);
                    tradingrecord.setUserID(order.getUser().getUserID());
                    tradingrecord.setMarketPrice(marketPrice);
                    tradingrecord.setPosition(order.getType());
                    tradingrecord.setShare(order.getShare());
                    tradingrecord.setStockSymbol(order.getStock().getStockSymbol());
                    tradingrecord.setTime(dtf.format(currentTime));
                    tradingRecordRepository.save(tradingrecord);
                    System.out.println(order.getUser().getUserID() + " has successfully bought " + order.getShare()
                            + " share(s) of stock " + stock.getStockSymbol() + " at " + dtf.format(currentTime));
                    order.getUser().removeBalance(totalPrice);
                    orders.remove(order);
                    buyOrders.remove(order);
                    // orders.remove(i);
                    // buyOrders.get(stock).remove(i);
                    i--;
                }
            // }
        }
    }

    // trading between seller and the system
    // sell order will have higher priority than buy order, sell order can only be
    // executed if the sell order is equal to buy order
    private void tryExecuteSellOrder(Stock stock, Portfolio portfolio) {
        tradingrecord tradingrecord = new tradingrecord();
        List<Order> orders = sellOrders.get(stock);
        double marketPrice = stock.getMarketPrice();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getPricePerShare() == marketPrice) {
                int currentShares = portfolio.getTotalShares(stock);
                double totalPrice = order.getTotalPrice();
                if (currentShares >= order.getShare()) {
                    LocalDateTime currentTime = LocalDateTime.now();
                    portfolio.removeStock(order.getUser(), stock, order);
                    tradingrecord.setUserID(order.getUser().getUserID());
                    tradingrecord.setMarketPrice(marketPrice);
                    tradingrecord.setPosition(order.getType());
                    tradingrecord.setShare(order.getShare());
                    tradingrecord.setStockSymbol(order.getStock().getStockSymbol());
                    tradingrecord.setTime(dtf.format(currentTime));
                    tradingRecordRepository.save(tradingrecord);
                    System.out.println(order.getUser().getUserID() + " has successfully sold " + order.getShare()
                            + " share(s) of stock " + stock.getStockSymbol() + " at " + dtf.format(currentTime));
                    order.getUser().addBalance(totalPrice);
                    orders.remove(order);
                    sellOrders.get(stock).remove(order);
                    // orders.remove(i);
                    // sellOrders.get(stock).remove(i);
                    i--;
                }
            }
        }
    }

    // trading between participants
    // sell order has higher priority than system, successfully execute if the sell order is equals to buy order
    // if you want to buy 100 shares at price RM1.20 for each share, you have to
    // wait until the seller sells 100 shares at the price RM1.20 for each share
    private void tryExecuteBuyAndSellOrderBetweenParticipants(Stock stock, Portfolio portfolio) {
        tradingrecord tradingrecordBuy = new tradingrecord();
        tradingrecord tradingrecordSell = new tradingrecord();
        List<Order> sellOrder = sellOrders.get(stock);
        List<Order> buyOrder = buyOrders.get(stock);
        double marketPrice = stock.getMarketPrice();
        for (int i = 0; i < sellOrder.size(); i++) {
            for (int j = 0; j < buyOrder.size(); j++) {
                Order sell = sellOrder.get(i);
                Order buy = buyOrder.get(j);
                if (sell.getPricePerShare() == buyOrder.get(j).getPricePerShare()
                        && sell.getShare() == buyOrder.get(j).getShare()) {
                    Double totalPrices = sell.getTotalPrice();
                    LocalDateTime currentTime = LocalDateTime.now();
                    portfolio.removeStock(sell.getUser(), stock, sell);
                    portfolio.addStock(buy.getUser(), stock, buy);
                    tradingrecordSell.setUserID(sell.getUser().getUserID());
                    tradingrecordSell.setMarketPrice(marketPrice);
                    tradingrecordSell.setPosition(sell.getType());
                    tradingrecordSell.setShare(sell.getShare());
                    tradingrecordSell.setStockSymbol(sell.getStock().getStockSymbol());
                    tradingrecordSell.setTime(dtf.format(currentTime));
                    tradingrecordBuy.setUserID(buy.getUser().getUserID());
                    tradingrecordBuy.setMarketPrice(marketPrice);
                    tradingrecordBuy.setPosition(buy.getType());
                    tradingrecordBuy.setShare(buy.getShare());
                    tradingrecordBuy.setStockSymbol(buy.getStock().getStockSymbol());
                    tradingrecordBuy.setTime(dtf.format(currentTime));
                    tradingRecordRepository.save(tradingrecordBuy);
                    tradingRecordRepository.save(tradingrecordSell);
                    System.out.println(sell.getUser().getUserID() + " has successfully sold " + sell.getShare()
                            + " share(s) of stock " + stock.getStockSymbol() + " to " + buy.getUser().getUserID()
                            + " at " + dtf.format(currentTime));
                    sell.getUser().addBalance(totalPrices);
                    buy.getUser().removeBalance(totalPrices);
                    sellOrder.remove(sell);
                    buyOrder.remove(buy);
                    sellOrders.get(stock).remove(sell);
                    buyOrders.get(stock).remove(buy);
                    // sellOrder.remove(i);
                    // buyOrder.remove(j);
                    // sellOrders.get(stock).remove(i);
                    // buyOrders.get(stock).remove(j);
                    // i--;
                    // j--;
                }
            }
        }
    }

    // cancel the order with highest price
    public boolean cancelHighestPriceOrder(UserTrading user, Stock stock, int type) {
        List<Order> cancel = (type == 0 ? buyOrders.get(stock) : sellOrders.get(stock));
        if (cancel == null) {
            System.out.println("Failed to remove an order!!");
            return false;
        }
        if (cancel.size() == 0) {
            System.out.println("You did not make any " + (type == 0 ? "buy " : "sell ") + "order for "
                    + stock.getStockSymbol() + " stock");
            return false;
        }
        double highestPrice = cancel.get(0).getTotalPrice();
        int indexHighestPirce = 0;
        for (int i = 1; i < cancel.size(); i++) {
            if (cancel.get(i).getUser().getUserID().equals(user.getUserID())) {
                if (cancel.get(i).getTotalPrice() > highestPrice) {
                    highestPrice = cancel.get(i).getTotalPrice();
                    indexHighestPirce = i;
                }
            }
        }
        cancel.remove(cancel.get(indexHighestPirce));
        return true;
    }

    public boolean cancelEarliestOrder(UserTrading user, Stock stock, int type) {
        List<Order> cancel = (type == 0 ? buyOrders.get(stock) : sellOrders.get(stock));
        if (cancel == null) {
            System.out.println("Failed to remove an order!!");
            return false;
        }
        if (cancel.size() == 0) {
            System.out.println("You did not make any " + (type == 0 ? "buy " : "sell ") + "order for stock "
                    + stock.getStockSymbol());
            return false;
        }
        Order earliestOrder = cancel.get(0);
        LocalDateTime earliestOrderTime = LocalDateTime.parse(earliestOrder.getCurrentTime());
        for(int i = 1; i < cancel.size(); i++){
            LocalDateTime orderTime = LocalDateTime.parse(cancel.get(i).getCurrentTime());
            if(orderTime.isBefore(earliestOrderTime)){
                earliestOrder = cancel.get(i);
                earliestOrderTime = orderTime;
            }
        }
        
        cancel.remove(earliestOrder);
        return true;
    }
}
