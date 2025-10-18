package com.isp.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.isp.util.DatabaseConnection;

public class BillingSystem extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    
    public BillingSystem(){
        setTitle("Billing System");
        setSize(900,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        JButton btnViewBills = new JButton("View Bills");
        JButton btnGenerateBill = new JButton("Generate Bill");
        JButton btnMarkPaid = new JButton("Mark Paid");
        JButton btnViewPayments = new JButton("View Payments");
        JButton btnRecordPayment = new JButton("Record Payment");
        JButton btnDeleteBill = new JButton("Delete Bill");
        
        topPanel.add(btnViewBills);
        topPanel.add(btnGenerateBill);
        topPanel.add(btnMarkPaid);
        topPanel.add(btnRecordPayment);
        topPanel.add(btnViewPayments);
        topPanel.add(btnDeleteBill);
        
        model = new DefaultTableModel(new String[]{"Bill ID","Customer ID","Amount","Due Date","Status","Generated Date"},0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
        
        btnViewBills.addActionListener(e -> loadBills());
        btnGenerateBill.addActionListener(e -> generateBill());
        btnMarkPaid.addActionListener(e -> markBillPaid());
        btnViewPayments.addActionListener(e -> viewPayments());
        btnRecordPayment.addActionListener(e -> recordPayment());
        btnDeleteBill.addActionListener(e -> deleteBill());
    }
    
    private void loadBills(){
        try(Connection conn = DatabaseConnection.getConnection()){
            String sql = "SELECT * FROM bills ORDER BY bill_id DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("bill_id"),
                    rs.getInt("customer_id"),
                    rs.getDouble("amount"),
                    rs.getDate("due_date"),
                    rs.getString("status"),
                    rs.getDate("generated_date")
                });
            }
            JOptionPane.showMessageDialog(this, "Bills loaded successfully!");
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error loading bills: " + ex.getMessage());
        }
    }
    
    private void generateBill(){
        // First, show available customers
        try(Connection conn = DatabaseConnection.getConnection()){
            String sqlCustomers = "SELECT customer_id, name FROM customers ORDER BY customer_id";
            PreparedStatement psCustomers = conn.prepareStatement(sqlCustomers);
            ResultSet rsCustomers = psCustomers.executeQuery();
            
            StringBuilder customerList = new StringBuilder("Available Customers:\n");
            boolean hasCustomers = false;
            while(rsCustomers.next()){
                hasCustomers = true;
                customerList.append("ID: ").append(rsCustomers.getInt("customer_id"))
                           .append(" - ").append(rsCustomers.getString("name")).append("\n");
            }
            
            if(!hasCustomers){
                JOptionPane.showMessageDialog(this, "No customers found! Please add customers first.");
                return;
            }
            
            JTextField customerIdField = new JTextField();
            JTextField amountField = new JTextField();
            JTextField baseAmountField = new JTextField();
            JTextField dueDateField = new JTextField("2025-12-31");
            
            Object[] message = {
                customerList.toString(),
                "Customer ID:", customerIdField,
                "Base Amount:", baseAmountField,
                "Total Amount:", amountField,
                "Due Date (yyyy-MM-dd):", dueDateField
            };
            
            int option = JOptionPane.showConfirmDialog(this, message, "Generate Bill", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION){
                try {
                    if(customerIdField.getText().trim().isEmpty()){
                        JOptionPane.showMessageDialog(this, "Customer ID cannot be empty!");
                        return;
                    }
                    if(amountField.getText().trim().isEmpty()){
                        JOptionPane.showMessageDialog(this, "Amount cannot be empty!");
                        return;
                    }
                    if(baseAmountField.getText().trim().isEmpty()){
                        JOptionPane.showMessageDialog(this, "Base Amount cannot be empty!");
                        return;
                    }
                    
                    int customerId = Integer.parseInt(customerIdField.getText().trim());
                    double amount = Double.parseDouble(amountField.getText().trim());
                    double baseAmount = Double.parseDouble(baseAmountField.getText().trim());
                    
                    if(amount <= 0 || baseAmount <= 0){
                        JOptionPane.showMessageDialog(this, "Amount must be greater than 0!");
                        return;
                    }
                    
                    java.sql.Date dueDate = java.sql.Date.valueOf(dueDateField.getText().trim());
                    
                    // Verify customer exists
                    String sqlCheck = "SELECT customer_id FROM customers WHERE customer_id = ?";
                    PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
                    psCheck.setInt(1, customerId);
                    ResultSet rsCheck = psCheck.executeQuery();
                    
                    if(!rsCheck.next()){
                        JOptionPane.showMessageDialog(this, "Customer ID " + customerId + " does not exist!");
                        return;
                    }
                    
                    // Try to insert the bill with base_amount
                    String sql = "INSERT INTO bills (customer_id, base_amount, amount, due_date, status, generated_date) VALUES (?, ?, ?, ?, 'unpaid', NOW())";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, customerId);
                    ps.setDouble(2, baseAmount);
                    ps.setDouble(3, amount);
                    ps.setDate(4, dueDate);
                    int res = ps.executeUpdate();
                    if(res > 0){
                        JOptionPane.showMessageDialog(this, "Bill generated successfully!\nCustomer ID: " + customerId + "\nBase Amount: " + baseAmount + "\nTotal Amount: " + amount);
                        loadBills();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to generate bill.");
                    }
                } catch(NumberFormatException ex){
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers!\nCustomer ID, Base Amount, and Amount must be numeric.");
                } catch(IllegalArgumentException ex){
                    JOptionPane.showMessageDialog(this, "Please enter date in correct format: yyyy-MM-dd\nExample: 2025-12-31");
                } catch(SQLException ex){
                    JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage() + "\n" + ex.getClass().getName());
                }
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
        }
    }
    
    private void markBillPaid(){
        int selectedRow = table.getSelectedRow();
        if(selectedRow >= 0){
            int billId = (int) model.getValueAt(selectedRow, 0);
            String currentStatus = (String) model.getValueAt(selectedRow, 4);
            
            if("paid".equalsIgnoreCase(currentStatus)){
                JOptionPane.showMessageDialog(this, "This bill is already marked as paid.");
                return;
            }
            
            try(Connection conn = DatabaseConnection.getConnection()){
                String sql = "UPDATE bills SET status='paid' WHERE bill_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, billId);
                int res = ps.executeUpdate();
                if(res > 0){
                    JOptionPane.showMessageDialog(this, "Bill marked as paid successfully!");
                    loadBills();
                }
            } catch(Exception ex){
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to mark as paid.");
        }
    }
    
    private void recordPayment(){
        int selectedRow = table.getSelectedRow();
        if(selectedRow >= 0){
            int billId = (int) model.getValueAt(selectedRow, 0);
            double billAmount = (double) model.getValueAt(selectedRow, 2);
            
            JTextField amountField = new JTextField(String.valueOf(billAmount));
            JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Online Transfer", "UPI"});
            
            Object[] message = {
                "Bill ID:", billId,
                "Amount:", amountField,
                "Payment Method:", methodCombo
            };
            
            int option = JOptionPane.showConfirmDialog(this, message, "Record Payment", JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION){
                try {
                    double amount = Double.parseDouble(amountField.getText().trim());
                    String method = (String) methodCombo.getSelectedItem();
                    
                    try(Connection conn = DatabaseConnection.getConnection()){
                        conn.setAutoCommit(false);
                        
                        // Insert payment record
                        String sqlPayment = "INSERT INTO payments (bill_id, amount, payment_date, payment_method) VALUES (?, ?, NOW(), ?)";
                        PreparedStatement psPayment = conn.prepareStatement(sqlPayment);
                        psPayment.setInt(1, billId);
                        psPayment.setDouble(2, amount);
                        psPayment.setString(3, method);
                        psPayment.executeUpdate();
                        
                        // Update bill status to paid
                        String sqlBill = "UPDATE bills SET status='paid' WHERE bill_id=?";
                        PreparedStatement psBill = conn.prepareStatement(sqlBill);
                        psBill.setInt(1, billId);
                        psBill.executeUpdate();
                        
                        conn.commit();
                        JOptionPane.showMessageDialog(this, "Payment recorded successfully!");
                        loadBills();
                    } catch(SQLException ex){
                        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                    }
                } catch(NumberFormatException ex){
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to record payment.");
        }
    }
    
    private void viewPayments(){
        try(Connection conn = DatabaseConnection.getConnection()){
            String sql = "SELECT p.*, b.customer_id FROM payments p LEFT JOIN bills b ON p.bill_id = b.bill_id ORDER BY p.payment_id DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            // Create a new table model for payments
            DefaultTableModel paymentModel = new DefaultTableModel(
                new String[]{"Payment ID", "Bill ID", "Customer ID", "Amount", "Method", "Date"}, 0
            );
            
            boolean hasPayments = false;
            while(rs.next()){
                hasPayments = true;
                paymentModel.addRow(new Object[]{
                    rs.getInt("payment_id"),
                    rs.getInt("bill_id"),
                    rs.getInt("customer_id"),
                    rs.getDouble("amount"),
                    rs.getString("payment_method"),
                    rs.getDate("payment_date")
                });
            }
            
            if(!hasPayments){
                JOptionPane.showMessageDialog(this, "No payments found in the system.");
            } else {
                // Show payments in a new dialog
                JTable paymentTable = new JTable(paymentModel);
                JScrollPane scrollPane = new JScrollPane(paymentTable);
                scrollPane.setPreferredSize(new Dimension(700, 400));
                JOptionPane.showMessageDialog(this, scrollPane, "Payment History", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error loading payments: " + ex.getMessage());
        }
    }
    
    private void deleteBill(){
        int selectedRow = table.getSelectedRow();
        if(selectedRow >= 0){
            int billId = (int) model.getValueAt(selectedRow, 0);
            String status = (String) model.getValueAt(selectedRow, 4);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this bill?\nBill ID: " + billId + "\nStatus: " + status, 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
                
            if(confirm == JOptionPane.YES_OPTION){
                try(Connection conn = DatabaseConnection.getConnection()){
                    // First delete related payments
                    String sqlPayments = "DELETE FROM payments WHERE bill_id=?";
                    PreparedStatement psPayments = conn.prepareStatement(sqlPayments);
                    psPayments.setInt(1, billId);
                    psPayments.executeUpdate();
                    
                    // Then delete the bill
                    String sqlBill = "DELETE FROM bills WHERE bill_id=?";
                    PreparedStatement psBill = conn.prepareStatement(sqlBill);
                    psBill.setInt(1, billId);
                    int res = psBill.executeUpdate();
                    
                    if(res > 0){
                        JOptionPane.showMessageDialog(this, "Bill deleted successfully!");
                        loadBills();
                    }
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(this, "Error deleting bill: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to delete.");
        }
    }
}
