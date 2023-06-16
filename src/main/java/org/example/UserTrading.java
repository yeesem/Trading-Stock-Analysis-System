package org.example;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lenovo
 */
@Service
public class UserTrading {
    @Autowired
    UserInfoRecordRepository userInfoRecordRepository;

    private String name, email, gender, userID, password;
    private double balance;
    private UserInfoRecord userInfoRecord;
    private LocalDate lastCallDate;
    private double startingBalance;
    //PROBLEM: number of lots remained for a day (nust renew the lots every day) 
    //trade history

    public UserTrading(){
        this.userInfoRecord = new UserInfoRecord();
        this.balance = 50000;
        lastCallDate = null;
    }
    //MUST CALL THIS METHOD AFTER INITIALIZE THE USER OBJECT
    public void resetAllValues(String name, String email, String userID, String password){
        userInfoRecord.setName(name);
        userInfoRecord.setEmail(email);
        userInfoRecord.setPassword(password);
        userInfoRecord.setUserID(userID);
        userInfoRecord.setBalance(balance);
        userInfoRecordRepository.save(userInfoRecord);
        System.out.println("Registered successfully!!");
        System.out.println("User ID: " + userInfoRecord.getUserID());
        System.out.println("Initial fund: RM" + this.balance);
    }

    public String getUserID(){
        return userInfoRecord.getUserID();
    }
    
    public double getBalance(){
        return this.balance;
    }

    public void addBalance(double sellMoney){
        this.balance += sellMoney;
        userInfoRecord.setBalance(balance);
        userInfoRecordRepository.save(userInfoRecord);
    }

    public void removeBalance(double buyMoney){
        this.balance -= buyMoney;
        userInfoRecord.setBalance(balance);
        userInfoRecordRepository.save(userInfoRecord);
    }
    
    //get the earliest balance for each of the day before the participant starts trading
    public double getStartingBalance(){
        if(isFirstCallOfDay()){
            startingBalance = getBalance();
            return startingBalance;
        }else{
            return startingBalance;
        }
    }

    //return true for the first call of this method, the 2nd and the subsequent call of this method will return false
    private boolean isFirstCallOfDay() {
        LocalDate currentDate = LocalDate.now();
        if (lastCallDate == null || !lastCallDate.equals(currentDate)) {
            lastCallDate = currentDate;
            return true;
        }
        return false;
    }
}

