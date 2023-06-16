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
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import jakarta.websocket.SendHandler;

//this codes will automatically send email best on schedule that we initialize

public class EmailJobScheduler {
    public static void main(String[] args) throws SchedulerException {

        sendSeheduleEmail("Testing", "Testing", "yeesemoon2002@gmail.com");

    }

    public static void sendSeheduleEmail(String sub, String text, String email) throws SchedulerException {
        // Enable TLSv1.2 protocol
        System.setProperty("https.protocols", "TLSv1.2");

        // Enable strong cipher suites
        Security.setProperty("jdk.tls.disabledAlgorithms", "");

        // Quartz scheduler instance
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // job detail
        JobDetail jobDetail = JobBuilder.newJob(EmailJob.class)
                .usingJobData("subject", sub)
                .usingJobData("text", text)
                .usingJobData("toEmail", email)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("EmailTrigger", "EmailGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 */1 * ? * *")) // Run every 1 minutes
                .build();

        scheduler.scheduleJob(jobDetail, trigger);

        scheduler.start();
    }

    public static class EmailJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) {

            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
            String subject = dataMap.getString("subject");
            String text = dataMap.getString("text");
            String toEmail = dataMap.getString("toEmail");

            String host = "smtp.gmail.com";
            String user = "hakimzairol253@gmail.com"; // change accordingly
            String password = "eocfuwdmdhgaerlw"; // change accordingly
            String to = toEmail; // change accordingly

            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "587");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, password);
                        }
                    });

            try {

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(user));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject(subject);
                message.setText(text);

                Transport.send(message);

                System.out.println("Email sent successfully!");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

}
