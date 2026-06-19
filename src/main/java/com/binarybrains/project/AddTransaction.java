package com.binarybrains.project;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;

public class AddTransaction extends JFrame {

    private Dashboard parent;
    private int userId;

    JComboBox<String> typeField;
    JComboBox<String> categoryField;
    JTextField amountField;
    JDateChooser dateField;

    public AddTransaction(Dashboard parent, int userId){

        this.parent = parent;
        this.userId = userId;

        setTitle("Add Transaction");
        setSize(420,380);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240,248,255));
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        JLabel title = new JLabel("Add Transaction");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(50,50,50));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // TYPE
        JLabel typeLabel = new JLabel("Type");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(typeLabel, gbc);

        typeField = new JComboBox<>(new String[]{"Income","Expense"});
        typeField.setPreferredSize(new Dimension(180,30));

        gbc.gridx = 1;
        panel.add(typeField, gbc);

        // CATEGORY
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(categoryLabel, gbc);

        categoryField = new JComboBox<>(new String[]{
                "Food","Rent","Transport","Others"
        });
        categoryField.setPreferredSize(new Dimension(180,30));

        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        // AMOUNT
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(amountLabel, gbc);

        amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(180,30));

        gbc.gridx = 1;
        panel.add(amountField, gbc);

        // DATE
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(dateLabel, gbc);

        dateField = new JDateChooser();
        dateField.setPreferredSize(new Dimension(180,30));
        dateField.setDateFormatString("yyyy-MM-dd");

        gbc.gridx = 1;
        panel.add(dateField, gbc);

        // SAVE BUTTON
        JButton save = new JButton("Save");
        save.setPreferredSize(new Dimension(120,40));
        save.setFont(new Font("Segoe UI", Font.BOLD, 14));
        save.setBackground(new Color(64,153,222));
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(save, gbc);

        save.addActionListener(e -> saveTransaction());

        setVisible(true);
    }

    private void saveTransaction(){

        String type = (String) typeField.getSelectedItem();
        String category = (String) categoryField.getSelectedItem();
        double amount = Double.parseDouble(amountField.getText());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(dateField.getDate());

        try(Connection con = DBConnection.getConnection()){

            String query =
                    "INSERT INTO transactions(user_id,date,type,category,amount) VALUES(?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1,userId);
            ps.setString(2,date);
            ps.setString(3,type);
            ps.setString(4,category);
            ps.setDouble(5,amount);

            ps.executeUpdate();

            String updateBalance;

            if(type.equals("Income")){
                updateBalance = "UPDATE users SET balance = balance + ? WHERE user_id=?";
            }
            else{
                updateBalance = "UPDATE users SET balance = balance - ? WHERE user_id=?";
            }

            PreparedStatement ps2 = con.prepareStatement(updateBalance);
            ps2.setDouble(1,amount);
            ps2.setInt(2,userId);

            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this,"Transaction Added");

            parent.fetchAndUpdateUI();

            dispose();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}