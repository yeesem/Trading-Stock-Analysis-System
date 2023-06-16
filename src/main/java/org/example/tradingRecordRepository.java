package org.example;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface tradingRecordRepository extends CrudRepository<tradingrecord, Integer> {
  //get all the data that contains the user id
  List<tradingrecord> findByUserID(String userID);
  
  //get all the data that contains the stock symbol
  List<tradingrecord> findByStockSymbol(String symbol);

  List<tradingrecord> findAll();

  void deleteByUserID(String userID);
}
