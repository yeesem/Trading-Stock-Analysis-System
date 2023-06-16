package org.example;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
public class Refresh {
    @Autowired
    tradingRecordRepository tradingRecordRepository;

    @Autowired
    UserInfoRecordRepository userInfoRecordRepository;

    @Autowired
    PortfolioRecordRepository portfolioRecordRepository;

    public void removeUsers(UserTrading user){
        double compute = user.getBalance() / user.getStartingBalance() * 100;
        if(compute >= 50){
            List<PortfolioRecord> deletePortfolio = portfolioRecordRepository.findByUserID(user.getUserID());
            for(int i = 0; i < deletePortfolio.size(); i++){
                portfolioRecordRepository.deleteByUserID(user.getUserID());
            }
            List<tradingrecord> deleteTrading = tradingRecordRepository.findByUserID(user.getUserID());
            for(int i = 0; i < deleteTrading.size(); i++){
                tradingRecordRepository.deleteByUserID(user.getUserID());
            }
            List<UserInfoRecord> deleteUser = userInfoRecordRepository.findByUserID(user.getUserID());
            for(int i = 0; i < deleteUser.size(); i++){
                userInfoRecordRepository.deleteByUserID(user.getUserID());
            }
        }
    }
}
