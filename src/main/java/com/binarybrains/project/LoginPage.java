package com.binarybrains.project;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {

    private JTextField userField = new JTextField();
    private JPasswordField passField = new JPasswordField();

    public LoginPage() {

        setTitle("Login - Personal Finance Manager");
        setSize(450,320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240,248,255));
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(50,50,50));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lblUser, gbc);

        userField.setPreferredSize(new Dimension(180,30));
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 1;
        panel.add(userField, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblPass, gbc);

        passField.setPreferredSize(new Dimension(180,30));
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 1;
        panel.add(passField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(120,40));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(new Color(64,153,222));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        // LOGIN LOGIC (UNCHANGED)
        loginBtn.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            try (Connection con = DBConnection.getConnection()) {

                String sql = "SELECT user_id FROM users WHERE username=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    int id = rs.getInt("user_id");

                    UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 16));
                    UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
                    UIManager.put("Panel.background", new Color(240,248,255));
                    UIManager.put("OptionPane.background", new Color(240,248,255));

                    JOptionPane.showMessageDialog(
                            this, "Welcome " + username, "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    new Expense_Management(id).setVisible(true);

                    this.dispose();

                } else {

                    JOptionPane.showMessageDialog(this, "Invalid Username or Password");

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, "Database Connection Error!");
                ex.printStackTrace();

            }
        });
    }

    public static void main(String[] args) {

        new LoginPage().setVisible(true);

    }
}