package org.example;

import java.security.Security;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;   
  
public class SendEmail {  

 public static void sendAnEmail(String email,String subject,String text){
  // Enable TLSv1.2 protocol
  System.setProperty("https.protocols", "TLSv1.2");

  // Enable strong cipher suites
  Security.setProperty("jdk.tls.disabledAlgorithms", "");

  String host="smtp.gmail.com";  
  final String user="j2372396@gmail.com";//change accordingly  
  final String password="lntqogjoyyzklets";//change accordingly  
    
  String to=email;//change accordingly
  
   //Get the session object  
   Properties props = new Properties();  
   props.put("mail.smtp.host",host);  
   props.put("mail.smtp.auth", "true");  
   props.put("mail.smtp.starttls.enable", "true");
   props.put("mail.smtp.port", "587");
   props.put("mail.smtp.ssl.protocols", "TLSv1.2");
   
     
   Session session = Session.getDefaultInstance(props,  
    new javax.mail.Authenticator() {  
      protected PasswordAuthentication getPasswordAuthentication() {  
    return new PasswordAuthentication(user,password);  
      }  
    });  
  
   //Compose the message  
    try {  
     MimeMessage message = new MimeMessage(session);  
     message.setFrom(new InternetAddress(user));  
     message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
     message.setSubject(subject);  
     message.setText(text);  
       
    //send the message  
    Transport.send(message);  
  
    System.out.println("Message sent successfully!");  
   
    } catch (MessagingException e) {e.printStackTrace();}  
 }
//  public static void main(String[] args) {  
//   //eocfuwdmdhgaerlw - hakim
//   //baqwxihytpaogdwx - pian
//   sendAnEmail("yeesemoon2002@gmail.com","Testing","Hello");
  
//  }  
 
}

