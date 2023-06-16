package org.example;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<userDatabase,Integer> {
    //userDatabase findbyUserID(String userID);
    List<userDatabase> findAll();
}