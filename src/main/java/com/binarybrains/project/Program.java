package com.binarybrains.project;

public class Program {
    // 1. Added 'public' to the main method
    public static void main(String[] args) {

        // 2. Defined a variable for the user ID.
        // In a real scenario, this comes from your database after login.
        int loggedInUserId = 1;

        // 3. Initialized the dashboard
        Expense_Management expenseManagement = new Expense_Management(loggedInUserId);
        expenseManagement.setVisible(true);
    }
}