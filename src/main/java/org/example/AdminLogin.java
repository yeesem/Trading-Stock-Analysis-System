package org.example;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminLogin extends JFrame implements ActionListener {

    private static final boolean True = false;
    private JLabel usernameLabel, passwordLabel;
    private JTextField username;
    private JPasswordField password;
    private JButton exitButton, loginButton;
    private JCheckBox showPassword;

    public AdminLogin() {
        setTitle("Admin Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 255, 255));

        usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(80, 60, 100, 30);
        panel.add(usernameLabel);

        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(80, 130, 100, 30);
        panel.add(passwordLabel);

        username = new JTextField();
        username.setBounds(230, 60, 190, 30);
        panel.add(username);

        password = new JPasswordField();
        password.setBounds(230, 130, 190, 30);
        panel.add(password);

        exitButton = new JButton("EXIT");
        exitButton.setBounds(230, 240, 80, 25);
        exitButton.addActionListener(this);
        panel.add(exitButton);

        loginButton = new JButton("LOGIN");
        loginButton.setBounds(350, 240, 80, 25);
        loginButton.addActionListener(this);
        panel.add(loginButton);

        showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(340, 180, 130, 25);
        showPassword.addActionListener(this);
        panel.add(showPassword);

        add(panel);
        setSize(550, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitButton) {
            System.exit(0);
        } else if (e.getSource() == loginButton) {
            String enteredUsername = username.getText();
            String enteredPassword = new String(password.getPassword());

            if (enteredUsername.equals("")) {
                JOptionPane.showMessageDialog(this, "Please enter username");
            } else if (enteredPassword.equals("")) {
                JOptionPane.showMessageDialog(this, "Please enter password");
            } else if (enteredUsername.equals("hakimzairol") && enteredPassword.equals("hakimzairol")) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                //AdminRemoveUser adminRemoveUser = new AdminRemoveUser();
                //adminRemoveUser.setVisible(true);
                //dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong Username or Password");
            }
        } else if (e.getSource() == showPassword) {
            if (showPassword.isSelected()) {
                password.setEchoChar((char) 0);
            } else {
                password.setEchoChar('*');
            }
        }
    }

    public static void main(String[] args) {
        new AdminLogin().setVisible(true);
    }
}