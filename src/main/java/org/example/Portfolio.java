package org.example;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.PropertyMapper.Source;
import org.springframework.stereotype.Service;

@Service
public class Portfolio {
    @Autowired
    PortfolioRecordRepository portfolioRecordRepository;

    // the purchase price always renewable according to the latest merket price
    private Map<Stock, Double> values;
    // different market prices for a particular stock
    private Map<Stock, Double> marketPriceAtParticularTime;
    // number of shares for different market prices for a particular stock
    private Map<Stock, Map<Double, Integer>> numOfShares;

    public Portfolio() {
        values = new HashMap<>();
        marketPriceAtParticularTime = new HashMap<>();
        numOfShares = new HashMap<>();
    }

    public void addStock(UserTrading user, Stock stock, Order buyOrder) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        List<PortfolioRecord> list = portfolioRecordRepository.findAllByStockSymbol(stock.getStockSymbol());
        PortfolioRecord portfolioRecord = new PortfolioRecord();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserID().compareTo(user.getUserID()) == 0) {
                portfolioRecord = list.get(i);
            }
        }
        if (values.containsKey(stock) && marketPriceAtParticularTime.containsKey(stock)
                && numOfShares.containsKey(stock)) {
            if (numOfShares.get(stock) != null && numOfShares.get(stock).containsKey(buyOrder.getPricePerShare())
                    && marketPriceAtParticularTime.containsValue(buyOrder.getPricePerShare())) {
                int requiredNumberOfShares = numOfShares.get(stock).get(buyOrder.getPricePerShare());
                // numOfShares.get(stock).remove(buyOrder.getPricePerShare(),
                // requiredNumberOfShares);
                // numOfShares.get(stock).put(buyOrder.getPricePerShare(),
                // requiredNumberOfShares + buyOrder.getShare());
                numOfShares.get(stock).replace(buyOrder.getPricePerShare(), requiredNumberOfShares,
                        requiredNumberOfShares + buyOrder.getShare());
            } else {
                marketPriceAtParticularTime.put(stock, buyOrder.getPricePerShare());
                if (numOfShares.get(stock) == null) {
                    numOfShares.put(stock, new HashMap<>());
                }
                numOfShares.get(stock).put(buyOrder.getPricePerShare(), buyOrder.getShare());
            }
            double previousValue = values.get(stock);
            double currentValue = buyOrder.getPricePerShare() * getTotalShares(stock);
            values.remove(stock, previousValue);
            values.put(stock, currentValue);
            // values.replace(stock, previousValue, currentValue);
            double totalCostPrices = 0;
            for (Map.Entry<Double, Integer> entry : numOfShares.get(stock).entrySet()) {
                Double particularMarketPrice = entry.getKey();
                Integer sharesOfParticularMarketPrice = entry.getValue();
                totalCostPrices += particularMarketPrice * sharesOfParticularMarketPrice;
            }
            // double currentPL = (buyOrder.getPricePerShare() * buyOrder.getShare()) -
            // totalCostPrices;
            // double overallPL = portfolioRecord.getOverallPL();
            // double currentPoint = currentPL / user.getStartingBalance() * 100;
            // double overallPoint = portfolioRecord.getOverallPoint() + currentPoint;
            System.out.println("11111111111111111111111111111111111111111");
            portfolioRecord.setUserID(user.getUserID());
            portfolioRecord.setCostPrice(Double.parseDouble(decimalFormat.format(totalCostPrices)));
            portfolioRecord.setShare(getTotalShares(stock));
            portfolioRecord.setStockSymbol(stock.getStockSymbol());
            portfolioRecord.setValue(values.get(stock));
            portfolioRecord.setPositionChange(getPositionChange(stock));
            if (portfolioRecord.getCurrentPL() == null) {
                portfolioRecord.setCurrentPL(0.0);
            }
            if (portfolioRecord.getCurrentPoint() == null) {
                portfolioRecord.setCurrentPoint(0.0);
            }
            if (portfolioRecord.getOverallPoint() == null) {
                portfolioRecord.setOverallPoint(0.0);
            }
            if (portfolioRecord.getOverallPL() == null) {
                portfolioRecord.setOverallPL(0.0);
            }
            portfolioRecordRepository.save(portfolioRecord);
        } else {
            values.put(stock, stock.getMarketPrice() * buyOrder.getShare());
            marketPriceAtParticularTime.put(stock, buyOrder.getPricePerShare());
            // Map<Double, Integer> numOfShare = new HashMap<>();
            // numOfShare.put(stock.getMarketPrice(), share);
            // numOfShares.put(stock, numOfShare);
            numOfShares.put(stock, new HashMap<>());
            numOfShares.get(stock).put(buyOrder.getPricePerShare(), buyOrder.getShare());
            // numOfShares.get(stock).put(stock.getMarketPrice(), share);
            double totalCostPrices = 0;
            int totalShares = 0;
            for (Map.Entry<Double, Integer> entry : numOfShares.get(stock).entrySet()) {
                Double particularMarketPrice = entry.getKey();
                Integer sharesOfParticularMarketPrice = entry.getValue();
                totalShares += sharesOfParticularMarketPrice;
                totalCostPrices += particularMarketPrice * sharesOfParticularMarketPrice;
            }
            if (portfolioRecord == null) {
                System.out.println("222222222222222222222222222222222222222222222");
                PortfolioRecord portfolioRecord2 = new PortfolioRecord();
                portfolioRecord2.setUserID(user.getUserID());
                portfolioRecord2.setCostPrice(Double.parseDouble(decimalFormat.format(totalCostPrices)));
                portfolioRecord2.setShare(totalShares);
                portfolioRecord2.setStockSymbol(stock.getStockSymbol());
                portfolioRecord2.setValue(values.get(stock));
                portfolioRecord2.setPositionChange(getPositionChange(stock));
                if (portfolioRecord2.getCurrentPL() == null) {
                    portfolioRecord2.setCurrentPL(0.0);
                }
                if (portfolioRecord2.getCurrentPoint() == null) {
                    portfolioRecord2.setCurrentPoint(0.0);
                }
                if (portfolioRecord2.getOverallPoint() == null) {
                    portfolioRecord2.setOverallPoint(0.0);
                }
                if (portfolioRecord2.getOverallPL() == null) {
                    portfolioRecord2.setOverallPL(0.0);
                }
                portfolioRecordRepository.save(portfolioRecord2);
            } else {
                System.out.println("333333333333333333333333333333333333333333333333");
                portfolioRecord.setUserID(user.getUserID());
                portfolioRecord.setCostPrice(Double.parseDouble(decimalFormat.format(totalCostPrices)));
                portfolioRecord.setShare(totalShares);
                portfolioRecord.setStockSymbol(stock.getStockSymbol());
                portfolioRecord.setValue(values.get(stock));
                portfolioRecord.setPositionChange(getPositionChange(stock));
                if (portfolioRecord.getCurrentPL() == null) {
                    portfolioRecord.setCurrentPL(0.0);
                }
                if (portfolioRecord.getCurrentPoint() == null) {
                    portfolioRecord.setCurrentPoint(0.0);
                }
                if (portfolioRecord.getOverallPoint() == null) {
                    portfolioRecord.setOverallPoint(0.0);
                }
                if (portfolioRecord.getOverallPL() == null) {
                    portfolioRecord.setOverallPL(0.0);
                }
                portfolioRecordRepository.save(portfolioRecord);
            }
        }
    }

    public void removeStock(UserTrading user, Stock stock, Order sellOrder) {
        List<PortfolioRecord> list = portfolioRecordRepository.findAllByStockSymbol(stock.getStockSymbol());
        PortfolioRecord portfolioRecord = portfolioRecordRepository.findByStockSymbol(stock.getStockSymbol());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserID().compareTo(user.getUserID()) == 0) {
                portfolioRecord = list.get(i);
            }
        }
        int requiredNumberOfShares = 0;
        double totalCostPrices = 0;
        synchronized (numOfShares) {
            Iterator<Map.Entry<Double, Integer>> iterator = numOfShares.get(stock).entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Double, Integer> entry = iterator.next();
                Double particularMarketPrice = entry.getKey();
                Integer sharesOfParticularMarketPrice = entry.getValue();
                requiredNumberOfShares += sharesOfParticularMarketPrice;
                iterator.remove();
                // numOfShares.get(stock).remove(particularMarketPrice,
                // sharesOfParticularMarketPrice);
                marketPriceAtParticularTime.remove(stock, particularMarketPrice);
                if (requiredNumberOfShares > sellOrder.getShare()) {
                    int extra = requiredNumberOfShares - sellOrder.getShare();
                    numOfShares.get(stock).put(particularMarketPrice, extra);
                    marketPriceAtParticularTime.put(stock, particularMarketPrice);
                    requiredNumberOfShares -= extra;
                    totalCostPrices += particularMarketPrice * (sharesOfParticularMarketPrice - extra);
                    break; // break if the number of shares is exceeds the required number of shares
                }
                totalCostPrices += particularMarketPrice * sharesOfParticularMarketPrice;
            }
        }
        if (requiredNumberOfShares >= sellOrder.getShare()) {
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            double previousValue = values.get(stock);
            double currentValue = getTotalShares(stock) * sellOrder.getPricePerShare();
            values.replace(stock, previousValue, currentValue);
            double currentPL = (sellOrder.getPricePerShare() * sellOrder.getShare()) - totalCostPrices;
            double overallPL = portfolioRecord.getOverallPL();
            double currentPoint = currentPL / user.getStartingBalance() * 100;
            double overallPoint = portfolioRecord.getOverallPoint() + currentPoint;
            if (numOfShares.get(stock).isEmpty()) {
                numOfShares.remove(stock);
            }
            portfolioRecord.setUserID(user.getUserID());
            portfolioRecord.setCostPrice(Double.parseDouble(decimalFormat.format(totalCostPrices)));
            portfolioRecord.setShare(getTotalShares(stock));
            portfolioRecord.setStockSymbol(stock.getStockSymbol());
            portfolioRecord.setValue(Double.parseDouble(decimalFormat.format(values.get(stock))));
            portfolioRecord.setPositionChange(getPositionChange(stock));
            portfolioRecord.setCurrentPL(Double.parseDouble(decimalFormat.format(currentPL)));
            portfolioRecord.setOverallPL(Double.parseDouble(decimalFormat.format(overallPL + currentPL)));
            portfolioRecord.setCurrentPoint(Double.parseDouble(decimalFormat.format(currentPoint)));
            portfolioRecord.setOverallPoint(Double.parseDouble(decimalFormat.format(overallPoint)));
            portfolioRecordRepository.save(portfolioRecord);
        }
    }

    // total shares of a particular stock
    public int getTotalShares(Stock stock) {
        int totalShares = 0;
        try {
            for (Map.Entry<Double, Integer> entry : numOfShares.get(stock).entrySet()) {
                Integer sharesOfParticularMarketPrice = entry.getValue();
                totalShares += sharesOfParticularMarketPrice;
            }
        } catch (NullPointerException npe) {
            return 0;
        }
        return totalShares;
    }

    // overall values of a particular stock
    public double getValues(Stock stock) {
        return values.get(stock);
    }

    // compare the current value with the previous value to determine whether it is
    // potential profit or loss
    public double getPositionChange(Stock stock) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        double totalCostPrices = 0;
        try {
            for (Map.Entry<Double, Integer> entry : numOfShares.get(stock).entrySet()) {
                Double particularMarketPrice = entry.getKey();
                Integer sharesOfParticularMarketPrice = entry.getValue();
                totalCostPrices += particularMarketPrice * sharesOfParticularMarketPrice;
            }
        } catch (NullPointerException npe) {
        }
        String format = decimalFormat.format(values.get(stock) - totalCostPrices);
        return Double.parseDouble(format);
    }

    public List<PortfolioRecord> displayAll() {
        return portfolioRecordRepository.findAll();
    }

    //get the overall point for all the stocks that the user currently has in his or her portfolio
    public double getOverallPoint(String userID){
        double getOverallPoint = 0;
        List<PortfolioRecord> overallPoint = portfolioRecordRepository.findByUserID(userID);
        for(int i = 0; i < overallPoint.size(); i++){
            getOverallPoint += overallPoint.get(i).getOverallPL();
        }
        return getOverallPoint;
    }

    //get the overall profit and loss for all the stocks that the user currently has in his or her portfolio
    public double getOverallPL(String userID){
        double getOverallPL = 0;
        List<PortfolioRecord> overallPL = portfolioRecordRepository.findByUserID(userID);
        for(int i = 0; i < overallPL.size(); i++){
            getOverallPL += overallPL.get(i).getOverallPL();
        }
        return getOverallPL;
    }

    public void displayPersonalPortfolio(UserTrading user){
        List<PortfolioRecord> personalPortfolio = null;
        try{
            personalPortfolio = portfolioRecordRepository.findByUserID(user.getUserID());
        }catch(NullPointerException npe){
            System.out.println("You don't have any record in your portfolio!!");
        }
        System.out.println(
                                "----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                                "|  User ID  |  Stock Symbol  |  No. shares  |  Position Change  |   Cost Price   |     Value     |  Current P&L  |  Overall P&L  |  Current Point  |  Overall Point  |");
        for(int i = 0; i < personalPortfolio.size(); i++){
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("|  %9s|  %14s|  %12d|  %17.3f|  %14.3f|  %13.3f|  %13.3f|  %13.3f|  %15.3f|  %15.3f|\n", personalPortfolio.get(i).getUserID(), personalPortfolio.get(i).getStockSymbol(), personalPortfolio.get(i).getShare(), personalPortfolio.get(i).getPositionChange(), personalPortfolio.get(i).getCostPrice(), personalPortfolio.get(i).getvalue(), personalPortfolio.get(i).getCurrentPL(), personalPortfolio.get(i).getOverallPL(), personalPortfolio.get(i).getCurrentPoint(), personalPortfolio.get(i).getOverallPoint());

        }
        System.out.println(
                                "----------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}
