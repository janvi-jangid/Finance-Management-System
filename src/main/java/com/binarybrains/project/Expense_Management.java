package com.binarybrains.project;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class Expense_Management extends JFrame {

    private int userId;

    private JLabel balValue;
    private JLabel incValue;
    private JLabel expValue;

    private DefaultCategoryDataset dataset;

    public Expense_Management(int userId) {

        this.userId = userId;

        setTitle("Personal Finance Manager");
        setSize(800,500);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(245,245,245));

        JLabel title = new JLabel("Personal Finance Manager");
        title.setFont(new Font("Segoe UI",Font.BOLD,28));
        title.setBounds(220,10,400,40);
        add(title);

        // GRAPH
        dataset = new DefaultCategoryDataset();

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Expenses",
                "Category",
                "Amount",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBounds(30,180,460,250);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        add(chartPanel);

        // BALANCE PANEL
        JPanel balancePanel = new JPanel();
        balancePanel.setBounds(30,80,200,80);
        balancePanel.setLayout(new GridLayout(2,1));
        balancePanel.setBackground(Color.WHITE);
        balancePanel.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        JLabel balTitle = new JLabel("Current Balance",SwingConstants.CENTER);

        balValue = new JLabel("$ 0",SwingConstants.CENTER);
        balValue.setFont(new Font("Segoe UI",Font.BOLD,18));

        balancePanel.add(balTitle);
        balancePanel.add(balValue);
        add(balancePanel);

        // INCOME PANEL
        JPanel incomePanel = new JPanel();
        incomePanel.setBounds(250,80,200,80);
        incomePanel.setLayout(new GridLayout(2,1));
        incomePanel.setBackground(Color.WHITE);
        incomePanel.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        JLabel incTitle = new JLabel("Total Income",SwingConstants.CENTER);

        incValue = new JLabel("$ 0",SwingConstants.CENTER);
        incValue.setFont(new Font("Segoe UI",Font.BOLD,18));

        incomePanel.add(incTitle);
        incomePanel.add(incValue);
        add(incomePanel);

        // EXPENSE PANEL
        JPanel expensePanel = new JPanel();
        expensePanel.setBounds(470,80,200,80);
        expensePanel.setLayout(new GridLayout(2,1));
        expensePanel.setBackground(Color.WHITE);
        expensePanel.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        JLabel expTitle = new JLabel("Total Expenses",SwingConstants.CENTER);

        expValue = new JLabel("$ 0",SwingConstants.CENTER);
        expValue.setFont(new Font("Segoe UI",Font.BOLD,18));

        expensePanel.add(expTitle);
        expensePanel.add(expValue);
        add(expensePanel);

        // BUTTONS
        JButton addBtn = new JButton("Add Transaction");
        addBtn.setBounds(520,200,200,45);
        addBtn.setFont(new Font("Segoe UI",Font.BOLD,14));
        addBtn.setBackground(new Color(70,150,220));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        add(addBtn);

        JButton reportBtn = new JButton("View Reports");
        reportBtn.addActionListener(e ->
                new ReportsPage(userId).setVisible(true)
        );
        reportBtn.setBounds(520,260,200,45);
        reportBtn.setFont(new Font("Segoe UI",Font.BOLD,14));
        reportBtn.setBackground(new Color(70,150,220));
        reportBtn.setForeground(Color.WHITE);
        reportBtn.setFocusPainted(false);
        add(reportBtn);

//        JButton budgetBtn = new JButton("Budget Planning");
//        budgetBtn.setBounds(520,320,200,45);
//        budgetBtn.setFont(new Font("Segoe UI",Font.BOLD,14));
//        budgetBtn.setBackground(new Color(70,150,220));
//        budgetBtn.setForeground(Color.WHITE);
//        budgetBtn.setFocusPainted(false);
//        add(budgetBtn);

        addBtn.addActionListener(e -> new AddTransaction(new Dashboard(userId), userId).setVisible(true));

        fetchAndUpdateUI();

        setVisible(true);
    }

    public void fetchAndUpdateUI() {

        try(Connection con = DBConnection.getConnection()){

            String balQuery = "SELECT balance FROM users WHERE user_id=?";
            PreparedStatement ps1 = con.prepareStatement(balQuery);
            ps1.setInt(1,userId);

            ResultSet rs1 = ps1.executeQuery();

            if(rs1.next()){
                balValue.setText("$ "+rs1.getDouble("balance"));
            }

            String incQuery =
                    "SELECT SUM(amount) FROM transactions WHERE user_id=? AND type='Income'";

            PreparedStatement ps2 = con.prepareStatement(incQuery);
            ps2.setInt(1,userId);

            ResultSet rs2 = ps2.executeQuery();

            if(rs2.next()){
                incValue.setText("$ "+rs2.getDouble(1));
            }

            String expQuery =
                    "SELECT SUM(amount) FROM transactions WHERE user_id=? AND type='Expense'";

            PreparedStatement ps3 = con.prepareStatement(expQuery);
            ps3.setInt(1,userId);

            ResultSet rs3 = ps3.executeQuery();

            if(rs3.next()){
                expValue.setText("$ "+rs3.getDouble(1));
            }

            dataset.clear();

            String[] categories = {"Food","Rent","Transport","Others"};

            for(String cat : categories){

                String graphQuery =
                        "SELECT SUM(amount) FROM transactions WHERE user_id=? AND type='Expense' AND category=?";

                PreparedStatement ps4 = con.prepareStatement(graphQuery);
                ps4.setInt(1,userId);
                ps4.setString(2,cat);

                ResultSet rs4 = ps4.executeQuery();

                double value = 0;

                if(rs4.next()){
                    value = rs4.getDouble(1);
                }

                dataset.addValue(value,"Expense",cat);
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}