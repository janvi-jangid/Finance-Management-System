package com.binarybrains.project;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private int userId;
    private Expense_Management expenseUI;

    public Dashboard(int userId) {

        this.userId = userId;

        setTitle("Dashboard");
        setSize(600,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255)); // light blue background
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20,20,20,20);

        // Title
        JLabel title = new JLabel("Personal Finance Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(40,40,40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(title, gbc);

        // Open Manager Button
        JButton openManager = new JButton("Open Finance Manager");
        openManager.setPreferredSize(new Dimension(220,45));
        openManager.setFont(new Font("Segoe UI", Font.BOLD, 14));
        openManager.setBackground(new Color(70,130,180));
        openManager.setForeground(Color.WHITE);
        openManager.setFocusPainted(false);

        gbc.gridy = 1;
        panel.add(openManager, gbc);

        // Add Transaction Button
        JButton addBtn = new JButton("Add Transaction");
        addBtn.setPreferredSize(new Dimension(220,45));
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setBackground(new Color(46,204,113));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        gbc.gridy = 2;
        panel.add(addBtn, gbc);

        // Button Actions (UNCHANGED)
        openManager.addActionListener(e -> {
            expenseUI = new Expense_Management(userId);
            expenseUI.setVisible(true);
        });

        addBtn.addActionListener(e -> {
            new AddTransaction(this,userId).setVisible(true);
        });

        setVisible(true);
    }

    public void fetchAndUpdateUI() {

        if(expenseUI != null){
            expenseUI.fetchAndUpdateUI();
        }

        System.out.println("Dashboard refreshed for user "+userId);
    }
}