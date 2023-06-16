package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "user")
public class userDatabase {
    @Id
    @Column(name = "FirstName")
    private String firstName;
    
    @Column(name = "LastName")
    private String lastName;

    @Column(name = "UserID")
    private String userID;

    @Column(name = "Password")
    private String password;

    @Column(name = "Gmail")
    private String gmail;

   
    //FirstName
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String tempt) {
        this.firstName = tempt;
    }

    //LastName
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String tempt) {
        this.lastName = tempt;
    }

    //Used ID
    public String getUserID() {
        return userID;
    }

    public void setUsedID(String tempt) {
        this.userID = tempt;
    }
    
    //gmail
    public String getGmail() {
        return gmail;
    }

    public void setGmail(String tempt) {
        this.gmail = tempt;
    }

    //password
    public String getPassword() {
        return password;
    }

    public void setPassword(String tempt) {
        this.password = tempt;
    }

    
}