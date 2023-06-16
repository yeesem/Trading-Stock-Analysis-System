package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;

import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.sun.tools.javac.Main;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.Security;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Scanner;
import java.util.List;

@SpringBootApplication
public class MainClass {
    @Autowired
    KeyStatisticsRepository keyStatisticsRepository;
    @Autowired
    tradingRecordRepository tradingRecordRepository;

    @Autowired
    UserInfoRecordRepository userInfoRecordRepository;

    @Autowired
    PortfolioRecordRepository portfolioRecordRepository;

    static StockSummary summary = new StockSummary();
    static IncomeStatement incomeStatement = new IncomeStatement();
    static BalanceSheet balanceSheet = new BalanceSheet();
    static CashFlow cashFlow = new CashFlow();
    static QuarterResult quarterResult = new QuarterResult();
    static StockNameAndSymbol stockNameAndSymbol = new StockNameAndSymbol();
    static String symbol = null;
    static HashMap<String, UserTrading> userTrHashMap = new HashMap<>();
    static SendEmail emailTo = new SendEmail();
    static JFrame f;
    static String emailForEmail = null;
    static String nameForEmail = null;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    public void windowClosing(WindowEvent e) {
        int a = JOptionPane.showConfirmDialog(f, "Are you sure?");
        if (a == JOptionPane.YES_OPTION) {
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public static void stockAnalysis(KeyStatistics key) throws ParseException {

        String initialText = "Enter a stock's symbol : ";
        while (true) {
            f = new JFrame();
            f.setBackground(Color.RED);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(0, 0, 0));
            panel.setSize(new Dimension(250, 32));
            panel.setLayout(null);

            JLabel label = new JLabel(initialText);
            label.setForeground(new Color(255, 255, 0));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setBounds(0, 0, 250, 32);
            panel.add(label);

            UIManager.put("OptionPane.minimumSize", new Dimension(270, 120));

            symbol = JOptionPane.showInputDialog(f, panel, "Stock Analysis",
                    JOptionPane.PLAIN_MESSAGE);
            if (symbol == null) {
                return;
            }

            // symbol = JOptionPane.showInputDialog(null,"Enter stock's symbol: ");
            summary.setSymbol(symbol);
            try {
                summary.getCurrentStockPrice(summary.getSymbol());
                summary.display();
                break;
            } catch (NullPointerException ignored) {
                initialText = "[Invalid stock's symbol] Enter stock's symbol: ";
            }
        }

        String[] financialOption = { "Income Statement", "Balance Sheet", "Cash Flow", "Quarterly Result",
                "Health Status", "Back" };

        ONTO: do {
            var selection = JOptionPane.showOptionDialog(null,
                    "\n                                                                            Financial Statement",
                    (stockNameAndSymbol.shortName.get(stockNameAndSymbol.symbol.indexOf(symbol)) + " (" + symbol + ")"),
                    0,
                    1,
                    null,
                    financialOption,
                    financialOption[0]);
            switch (selection) {
                case 0 -> {
                    incomeStatement.getIncomeStatement(summary.getSymbol());
                    incomeStatement.display();
                }
                case 1 -> {
                    balanceSheet.getBalanceSheet(summary.getSymbol());
                    balanceSheet.display();
                }
                case 2 -> {
                    cashFlow.getCashFlow(summary.getSymbol());
                    cashFlow.display();
                }
                case 3 -> {
                    quarterResult.getQuarterResult(summary.getSymbol());
                    quarterResult.display();
                }
                case 4 -> {
                    key.getClosePrice(summary.getSymbol());
                    key.calculateKeyStatistics(summary.getSymbol());
                    key.getFundamentalAnalysis();
                    key.lenyanFormula();
                    key.displayKeyStatistics();
                }
                case 5 -> {
                    break ONTO;
                }
            }
        } while (true);

    }

    public static void stockModel(KeyStatistics key) {
        ONTO: do {
            String[] modelOption = { "Len Yan's Approach", "Buffett's Approach", "Sem's Approach", "Back" };
            var selection2 = JOptionPane.showOptionDialog(null,
                    "\n                                                       Stock's Model ",
                    "Stock's Model - Select one : ",
                    0,
                    3,
                    null,
                    modelOption,
                    modelOption[0]);
            switch (selection2) {
                case 0 -> {
                    key.lenyanTable();
                }
                case 1 -> {
                    key.buffettTable();
                }
                case 2 -> {
                    key.semTable();
                }
                case 3 -> {
                    break ONTO;
                }
            }
        } while (true);
    }

    public static void method(String name, String email, String userID, String password)
            throws ParseException, ClassNotFoundException {

        emailForEmail = email;
        nameForEmail = name;

        TradingMachine tradingMachine = Global.getApplicationContext().getBean(TradingMachine.class);
        UserTrading user = Global.getApplicationContext().getBean(UserTrading.class);
        if (userTrHashMap.containsKey(userID)) {
            user = userTrHashMap.get(userID);
        } else {
            user.resetAllValues(name, email, userID, password);
        }
        user.getStartingBalance();
        ArrayList<Stock> forTradingMachine = new ArrayList<Stock>();
        Portfolio portfolio = Global.getApplicationContext().getBean(Portfolio.class);
        getSymbolNameAndPrice getSymbolNameAndPrice = new getSymbolNameAndPrice();
        // store all the stocks in the map before trading
        HashMap<String, Stock> stocks = new HashMap<>();
        getSymbolNameAndPrice.loadSymbolAndName();
        ArrayList<String> stockSymbols = getSymbolNameAndPrice.symbol;
        ArrayList<String> shortName = getSymbolNameAndPrice.shortName;
        for (int i = 0; i < stockSymbols.size(); i++) {
            Stock stock = Global.getApplicationContext().getBean(Stock.class);
            stock.resetAllTheValues(stockSymbols.get(i), 0.0);
            stocks.put(stockSymbols.get(i), stock);
            forTradingMachine.add(stock);
        }
        tradingMachine.resetAllValues(forTradingMachine);

        KeyStatistics key = Global.getApplicationContext().getBean(KeyStatistics.class);
        String[] mainOption = { "1", "2", "3", "4", "5", "6", "7","8", "Exit" };

        MAIN_MENU: do {
            Icon icon = new ImageIcon("C:\\Users\\USER\\Pictures\\BursaMalaysia.jpg");
            Image scaledImage = ((ImageIcon) icon).getImage().getScaledInstance(80, 50, Image.SCALE_SMOOTH);
            Icon scaledIcon = new ImageIcon(scaledImage);

            double balance = user.getBalance();

            DecimalFormat decimalFormat = new DecimalFormat("#0.00");

            String formattedBalance = decimalFormat.format(balance);

            var selection = JOptionPane.showOptionDialog(null,
                    "User ID : " + user.getUserID() + "\n" + "Account Balance : RM " + formattedBalance
                            + "\n\n                            Main Menu\n------------------------------------------------------------\n1.Stock Analysis\n2.Stock Model\n3.Place an order\n4.Activity\n5.Cancel order\n6.Personal Portfolio\n7.LeaderBoard\n8.Enquiry",
                    "Main Menu",
                    0,
                    3,
                    scaledIcon,
                    mainOption,
                    mainOption[0]);
            switch (selection) {
                case 0 -> {
                    stockAnalysis(key);
                }
                case 1 -> {
                    stockModel(key);
                }
                case 2 -> {
                    placeOrder(user, portfolio, stocks, tradingMachine, forTradingMachine, getSymbolNameAndPrice);
                }
                case 3 -> {
                    displayBuySellOrders(stocks, tradingMachine);

                }
                case 4 -> {
                    cancelOrder(user, stocks, tradingMachine, stockSymbols, getSymbolNameAndPrice);
                }
                case 5->{
                    portfolio.displayPersonalPortfolio(user);
                }
                case 6 -> {
                    LeaderboardGUI leaderBoard = Global.getApplicationContext().getBean(LeaderboardGUI.class);
                    leaderBoard.getList(name, email, password, userID, portfolio);
                    leaderBoard.setVisible(true);
                    leaderBoard.requestFocus();
                    return;
                }
                case 7-> {
                    enquiry(name);
                }
                case 8 -> {
                    String[] close = { "close" };
                    JOptionPane.showOptionDialog(null,
                            "Thank you for using our program", "",
                            JOptionPane.PLAIN_MESSAGE, 1, null, close, close[0]);
                    // System.exit(0);
                    break MAIN_MENU;
                }
            }
        } while (true);
        var loginForm = Global.getApplicationContext().getBean(LoginForm.class);
        loginForm.setVisible(true);
    }

    public static void enquiry(String name) {
        String initialText = "Please write your message here";
        f = new JFrame();
        f.setBackground(Color.RED);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0));
        panel.setSize(new Dimension(250, 32));
        panel.setLayout(null);

        JLabel label = new JLabel(initialText);
        label.setForeground(new Color(255, 255, 0));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setBounds(0, 0, 250, 32);
        panel.add(label);

        UIManager.put("OptionPane.minimumSize", new Dimension(270, 120));

        String text = JOptionPane.showInputDialog(f, panel, "Enquiry",
                JOptionPane.PLAIN_MESSAGE);

        if(text==null)
           return;

        SendEmail.sendAnEmail("s2143263@siswa.um.edu.my", ("Enquiry from " + name), text);

        String message = "Thanks for your enquiry";
        JOptionPane.showMessageDialog(null, message);

    }

    public static void main(String[] args) throws ParseException, ClassNotFoundException {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(MainClass.class);
        builder.headless(false);
        ConfigurableApplicationContext applicationContext = builder.run(args);
        Global.setApplicationContext(applicationContext);

        KeyStatistics key = applicationContext.getBean(KeyStatistics.class);

        stockNameAndSymbol.loadSymbolAndName();

        var loginForm = Global.getApplicationContext().getBean(LoginForm.class);
        loginForm.setVisible(true);

        // summary.setSymbol();
        // summary.getCurrentStockPrice(summary.getSymbol());
        // summary.display();

        // balanceSheet.getBalanceSheet(summary.getSymbol());
        // balanceSheet.display();

        // incomeStatement.getIncomeStatement(summary.getSymbol());
        // incomeStatement.display();

        // cashFlow.getCashFlow(summary.getSymbol());
        // cashFlow.display();
        // quarterResult.getQuarterResult(summary.getSymbol());
        // quarterResult.display();

        // key.getClosePrice(summary.getSymbol());
        // key.calculateKeyStatistics(summary.getSymbol());
        // key.getFundamentalAnalysis();
        // key.lenyanFormula();
        // key.displayKeyStatistics();
        // key.saveKeyStatistics(summary.getSymbol());

        // key.displayComparison();

        // Loop for data storing
        // for(int i=954;i<=954;i++){
        // key.getClosePrice(stockNameAndSymbol.symbol.get(i));
        // key.calculateKeyStatistics(stockNameAndSymbol.symbol.get(i));
        // key.getFundamentalAnalysis();
        // key.lenyanFormula();
        // key.displayKeyStatistics();
        // key.saveKeyStatistics(stockNameAndSymbol.symbol.get(i));
        // }

        // key.lenyanTable();
        // key.buffettTable();
        // key.semTable();

    }

    private static void displayBuySellOrders(HashMap<String, Stock> stocks, TradingMachine tradingMachine) {

        do {
            String initialText = "Enter a stock symbol: ";
            f = new JFrame();
            f.setBackground(Color.RED);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(0, 0, 0));
            panel.setSize(new Dimension(250, 32));
            panel.setLayout(null);

            JLabel label = new JLabel(initialText);
            label.setForeground(new Color(255, 255, 0));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setBounds(0, 0, 250, 32);
            panel.add(label);

            UIManager.put("OptionPane.minimumSize", new Dimension(270, 120));

            String symbol = JOptionPane.showInputDialog(f, panel, "Activities",
                    JOptionPane.PLAIN_MESSAGE);

            if (symbol == null)
                return;
            symbol = symbol.toUpperCase();
            System.out.println(symbol);

            if (stocks.containsKey(symbol))
                break;

            String message = "Invalid stock symbol";
            JOptionPane.showMessageDialog(null, message);

        } while (true);

        // Scanner input = new Scanner(System.in);
        // System.out.print("Enter a stock symbol: ");
        // String symbol = input.next();
        Stock stock = stocks.get(symbol);
        if (!tradingMachine.displayListOfBuyOrders(stock) && !tradingMachine.displayListOfSellOrders(stock)) {
            JOptionPane.showMessageDialog(null, "No any buy order is placed.\nNo any sell order is placed.");
        } else if (!tradingMachine.displayListOfBuyOrders(stock)) {
            JOptionPane.showMessageDialog(null, "No any buy order is placed.");
        } else if (!tradingMachine.displayListOfSellOrders(stock)) {
            JOptionPane.showMessageDialog(null, "No any sell order is placed.");
        }
    }

    private static void placeOrder(UserTrading user, Portfolio portfolio, HashMap<String, Stock> stocks,
            TradingMachine tradingMachine, ArrayList<Stock> forTradingMachine,
            getSymbolNameAndPrice getSymbolNameAndPrice) throws ParseException {

        String initialText = "Enter a stock's symbol : ";
        double marketPrice = -1;
        String stockSymbol = null;
        while (true) {
            f = new JFrame();
            f.setBackground(Color.RED);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(0, 0, 0));
            panel.setSize(new Dimension(250, 32));
            panel.setLayout(null);

            JLabel label = new JLabel(initialText);
            label.setForeground(new Color(255, 255, 0));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setBounds(0, 0, 250, 32);
            panel.add(label);

            UIManager.put("OptionPane.minimumSize", new Dimension(270, 120));

            stockSymbol = JOptionPane.showInputDialog(f, panel, "Buy / Sell",
                    JOptionPane.PLAIN_MESSAGE);
            if (stockSymbol == null) {
                return;
            }

            stockSymbol = stockSymbol.toUpperCase();

            try {
                marketPrice = getSymbolNameAndPrice.loadPrice(stockSymbol);
                break;
            } catch (NullPointerException npe) {
                initialText = "[Invalid stock's symbol] Enter stock symbol :";
            }
        }

        Stock stock = stocks.get(stockSymbol);
        forTradingMachine.remove(stock);
        stock.setMarketPrice(marketPrice);
        forTradingMachine.add(stock);
        stock.displaySuggestedPrice();
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        double suggestedPrice1 = Double.parseDouble(decimalFormat.format(marketPrice * 0.99));
        double suggestedPrice2 = Double.parseDouble(decimalFormat.format(marketPrice * 1.01));
        String initialTextPrice = "Display current market price: RM "
                + Double.parseDouble(decimalFormat.format(marketPrice)) + "\nSuggested price within RM"
                + suggestedPrice1 + " - RM" + suggestedPrice2
                + "\n\n                                                                ***Kindly Reminder***"
                + "\nTrading requires matching the price you set with other participants within the designated range,\nrather than precisely aligning with the price."
                + "\n\nEnter price : RM";
        String temptPrice = null;
        double price = 0.0;
        while (true) {
            temptPrice = JOptionPane.showInputDialog(null, initialTextPrice, "Buy / Sell",
                    JOptionPane.PLAIN_MESSAGE);
            if (temptPrice == null) {
                return;
            }
            try {
                price = Double.parseDouble(temptPrice);
                if (price >= suggestedPrice1 && price <= suggestedPrice2) {
                    break;
                }
            } catch (InputMismatchException npe) {
                initialText = "[Invalid price] Enter price : RM";
            }
        }

        String initialTextNumShare = "Number of shares(minimum 1 lot = 100 shares): ";
        String temptNumShare = null;
        int share = 0;
        while (true) {
            temptNumShare = JOptionPane.showInputDialog(null, initialTextNumShare, "Buy / Sell",
                    JOptionPane.PLAIN_MESSAGE);
            if (temptNumShare == null) {
                return;
            }
            try {
                share = Integer.parseInt(temptNumShare);
                break;
            } catch (InputMismatchException npe) {
                initialText = "[Invalid price] Enter price : RM";
            }
        }

        int type;
        ONTO: do {
            String[] option = { "Buy", "sell", "Back" };
            type = JOptionPane.showOptionDialog(null,
                    "               Buy / Sell",
                    "Buy / Sell",
                    0,
                    3,
                    null,
                    option,
                    option[0]);
            switch (type) {
                case 0 -> {
                    stock.placeBuyOrder(user, share, price);
                    Order order = new Order(user, stocks.get(stockSymbol), "BUY", share, price);
                    JOptionPane.showMessageDialog(null, "Traded Successfully!!!");
                    System.out.println("Traded Successfully!!");

                    // Enable TLSv1.2 protocol
                    System.setProperty("https.protocols", "TLSv1.2");

                    // Enable strong cipher suites
                    Security.setProperty("jdk.tls.disabledAlgorithms", "");

                    SendEmail.sendAnEmail(emailForEmail, "Purchased Notification",
                            ("Dear " + nameForEmail + ",\n\n  You have purchased "
                                    + getSymbolNameAndPrice.shortName
                                            .get(getSymbolNameAndPrice.symbol.indexOf(stockSymbol))
                                    + " (" + stockSymbol + ") at price RM " + price + " with " + share + " quantity "
                                    + ". \n\nThank you."));

                    tradingMachine.executeOrder(order, portfolio);
                    break ONTO;
                }
                case 1 -> {
                    stock.placeSellOrder(user, share, price);
                    Order order = new Order(user, stocks.get(stockSymbol), "SELL", share, price);
                    JOptionPane.showMessageDialog(null, "Traded Successfully!!!");
                    System.out.println("Traded Successfully!!");

                    // Enable TLSv1.2 protocol
                    System.setProperty("https.protocols", "TLSv1.2");

                    // Enable strong cipher suites
                    Security.setProperty("jdk.tls.disabledAlgorithms", "");

                    SendEmail.sendAnEmail(emailForEmail, "Sell Notification",
                            ("Dear " + nameForEmail + ",\n\n  You have selled "
                                    + getSymbolNameAndPrice.shortName
                                            .get(getSymbolNameAndPrice.symbol.indexOf(stockSymbol))
                                    + " (" + stockSymbol + ") at price RM " + price + " with " + share + " quantity "
                                    + ". \n\nThank you."));

                    tradingMachine.executeOrder(order, portfolio);
                    break ONTO;
                }
                case 2 -> {
                    break ONTO;
                }

            }
        } while (true);

    }

    private static void cancelOrder(UserTrading user, HashMap<String, Stock> stocks, TradingMachine tradingMachine,
            ArrayList<String> stockSymbols, getSymbolNameAndPrice getSymbolNameAndPrice) {

        String initialText = "Enter a stock's symbol : ";
        String stockSymbol = null;
        do {
            f = new JFrame();
            f.setBackground(Color.RED);

            JPanel panel = new JPanel();
            panel.setBackground(new Color(0, 0, 0));
            panel.setSize(new Dimension(250, 32));
            panel.setLayout(null);

            JLabel label = new JLabel(initialText);
            label.setForeground(new Color(255, 255, 0));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setFont(new Font("Arial", Font.BOLD, 11));
            label.setBounds(0, 0, 250, 32);
            panel.add(label);

            UIManager.put("OptionPane.minimumSize", new Dimension(270, 120));

            stockSymbol = JOptionPane.showInputDialog(f, panel, "Cancel Order",
                    JOptionPane.PLAIN_MESSAGE);
            if (stockSymbol == null) {
                return;
            }
            stockSymbol = stockSymbol.toUpperCase();
        } while (!stockSymbols.contains(stockSymbol));

        int type;
        ONTO: do {
            String[] option = { "1", "2", "Cancel" };
            type = JOptionPane.showOptionDialog(null,
                    "\n          Type of order you want to cancel\n----------------------------------------------------------------------\n1.Buy\n2.Sell",
                    "Cancel Order",
                    0,
                    3,
                    null,
                    option,
                    option[0]);
            switch (type) {
                case 0 -> {
                    cancelOrder(user, tradingMachine, stocks, stockSymbol, getSymbolNameAndPrice, type);
                    break ONTO;
                }
                case 1 -> {
                    cancelOrder(user, tradingMachine, stocks, stockSymbol, getSymbolNameAndPrice, type);
                    break ONTO;
                }
                case 2 -> {
                    break ONTO;
                }

            }
        } while (true);

    }

    public static void cancelOrder(UserTrading user, TradingMachine tradingMachine, HashMap<String, Stock> stocks,
            String stockSymbol, getSymbolNameAndPrice getSymbolNameAndPrice, int typeOrder) {
        int type;
        ONTO: do {
            String[] option = { "1", "2", "Cancel" };
            type = JOptionPane.showOptionDialog(null,
                    "\n                            Type\n----------------------------------------------------------\n1.Cancel earliest order\n2.Cancel highest price order\n3.Cancel",
                    "Cancel Order",
                    0,
                    3,
                    null,
                    option,
                    option[0]);
            switch (type) {
                case 0 -> {
                    if (tradingMachine.cancelEarliestOrder(user, stocks.get(stockSymbol), type)) {
                        JOptionPane.showMessageDialog(null,
                                ("Successfully removed  the " + (typeOrder == 0 ? "buy " : "sell ")
                                        + "earliest order for stock "
                                        + getSymbolNameAndPrice.shortName
                                                .get(getSymbolNameAndPrice.symbol.indexOf(stockSymbol))
                                        + " (" + stockSymbol + ")" + "."));

                        SendEmail.sendAnEmail(emailForEmail, "Cancel Order Notification",
                                ("Dear " + nameForEmail + ",\n\n  You have cancelled "
                                        + (typeOrder == 0 ? "buy " : "sell ")
                                        + "the earliest order for the stock "
                                        + getSymbolNameAndPrice.shortName
                                                .get(getSymbolNameAndPrice.symbol.indexOf(stockSymbol))
                                        + " (" + stockSymbol + ")" + ". \n\nThank you."));
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "You did not make any " + (typeOrder == 0 ? "buy " : "sell ") + "order for stock "
                                        + getSymbolNameAndPrice.shortName
                                                .get(getSymbolNameAndPrice.symbol.indexOf(stockSymbol))
                                        + " (" + stockSymbol + ")");
                    }
                    break ONTO;
                }
                case 1 -> {
                    if (tradingMachine.cancelHighestPriceOrder(user, stocks.get(stockSymbol), type)) {
                        JOptionPane.showMessageDialog(null,
                                "Successfully removed the highest " + (typeOrder == 0 ? "buy " : "sell ")
                                        + "order for stock "
                                        + getSymbolNameAndPrice.shortName
                                                .get(getSymbolNameAndPrice.symbol.indexOf(stockSymbol))
                                        + " (" + stockSymbol + ")" + ".");

                        SendEmail.sendAnEmail(emailForEmail, "Cancel Order Notification",
                                ("Dear " + nameForEmail + ",\n\n  You have cancelled the highest "
                                        + (typeOrder == 0 ? "buy " : "sell ") + "order for stock for the stock "
                                        + getSymbolNameAndPrice.shortName
                                                .get(getSymbolNameAndPrice.symbol.indexOf(stockSymbol))
                                        + " (" + stockSymbol + ")" + ". \n\nThank you."));
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "You did not make any " + (typeOrder == 0 ? "buy " : "sell ") + "order for stock "
                                        + getSymbolNameAndPrice.shortName
                                                .get(getSymbolNameAndPrice.symbol.indexOf(stockSymbol))
                                        + " (" + stockSymbol + ")");
                    }
                    break ONTO;
                }
                case 2 -> {
                    break ONTO;
                }

            }
        } while (true);
    }

    // return true if the time is within the market hours, otherwise return false.
    public static boolean isMarketOpen() {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"));
        DayOfWeek dayOfWeek = currentTime.getDayOfWeek();
        LocalTime time = currentTime.toLocalTime();
        // Check if it is a weekday (Monday to Friday) and within regular market hours
        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
            LocalTime marketOpeningTime1 = LocalTime.of(9, 0);
            LocalTime marketClosingTime1 = LocalTime.of(12, 30);
            LocalTime marketOpeningTime2 = LocalTime.of(14, 30);
            LocalTime marketClosingTime2 = LocalTime.of(17, 0);
            boolean isOpen = (time.isAfter(marketOpeningTime1) && time.isBefore(marketClosingTime1))
                    || (time.isAfter(marketOpeningTime2) && time.isBefore(marketClosingTime2));
            if (isOpen) {
                return true;
            } else {
                System.out.println("The current time is not within the duration of working hours.");
                System.out.println("Fail to place an order");
                return false;
            }
        }
        System.out.println("Today is not wihtin the weekdays");
        System.out.println("Fail to place an order");
        return false;
    }
}
