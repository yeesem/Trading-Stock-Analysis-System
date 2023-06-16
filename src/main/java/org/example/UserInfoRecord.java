package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "UserInfoRecord")
public class UserInfoRecord {

  // @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "User ID")
  private String userID;

  @Column(name = "Balance")
  private Double balance;

  @Id
  @Column(name = "Email")
  private String email;

  @Column(name = "Name")
  private String name;

  @Column(name = "Password")
  private String password;

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public String getEmail(){
    return this.email;
  }

  public void setEmail(String email){
    this.email = email;
  }

  public String getName(){
    return this.name;
  }

  public void setName(String name){
    this.name = name;
  }

  public String getPassword(){
    return this.password;
  }

  public void setPassword(String password){
    this.password = password;
  }
}
