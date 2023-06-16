package org.example;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface KeyStatisticsRepository extends CrudRepository<KeyStatisticsDatabase,Integer> {
    KeyStatisticsDatabase findBySymbol(String symbol);
    List<KeyStatisticsDatabase> findAll();
}
