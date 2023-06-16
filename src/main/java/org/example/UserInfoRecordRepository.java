package org.example;
import org.springframework.data.repository.CrudRepository;

import jakarta.persistence.Id;

import java.util.List;
import java.util.Optional;


public interface UserInfoRecordRepository extends CrudRepository<UserInfoRecord, String>{
    //return the first row containing the user ID
    List<UserInfoRecord> findByUserID(String userID);

    //return the first row containing the password
    List<UserInfoRecord> findByPassword(String password);

    //get all the users' info from the UserInfoRecord database
    List<UserInfoRecord> findAll();

    void deleteByUserID(String userID);
}