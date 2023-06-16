package org.example;

import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface PortfolioRecordRepository extends CrudRepository<PortfolioRecord, Integer>{
  //get all the data that contains the user id
  List<PortfolioRecord> findByUserID(String userID);
  
  //get all the data that contains the stock symbol
  PortfolioRecord findByStockSymbol(String symbol);

  List<PortfolioRecord> findAllByStockSymbol(String symbol);

  List<PortfolioRecord> findAll();

  void deleteByUserID(String userID);
}

