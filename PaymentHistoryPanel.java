package com.isp.ui.views.customer;

import com.isp.dao.CustomerDAO;
import com.isp.dao.PaymentTransactionDAO;
import com.isp.model.Customer;
import com.isp.model.PaymentTransaction;
import com.isp.ui.components.ModernButton;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentHistoryPanel extends JPanel {
    private final PaymentTransactionDAO paymentTransactionDAO;
    private final CustomerDAO customerDAO;
    private final int userId;
    
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JLabel totalPaymentsLabel;
    private JLabel totalAmountLabel;

    public PaymentHistoryPanel(int userId) {
        this.userId = userId;
        this.paymentTransactionDAO = new PaymentTransactionDAO();
        this.customerDAO = new CustomerDAO();
        
        initializeUI();
        loadPaymentHistory();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createPaymentTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Payment History");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        statsPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        totalPaymentsLabel = new JLabel("Total Payments: 0");
        totalPaymentsLabel.setFont(UIConstants.SUBTITLE_FONT);
        totalPaymentsLabel.setForeground(Color.DARK_GRAY);

        totalAmountLabel = new JLabel("Total Amount: ₹0.00");
        totalAmountLabel.setFont(UIConstants.SUBTITLE_FONT);
        totalAmountLabel.setForeground(UIConstants.SUCCESS_COLOR);

        statsPanel.add(totalPaymentsLabel);
        statsPanel.add(totalAmountLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createPaymentTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        String[] columnNames = {"Transaction ID", "Amount", "Type", "Method", "Status", "Date", "Gateway"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        paymentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        paymentTable.setRowHeight(35);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        paymentTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        paymentTable.getTableHeader().setForeground(Color.WHITE);

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < paymentTable.getColumnCount(); i++) {
            paymentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> loadPaymentHistory());

        ModernButton viewDetailsButton = new ModernButton("View Details");
        viewDetailsButton.addActionListener(e -> viewPaymentDetails());

        ModernButton downloadReceiptButton = new ModernButton("Download Receipt");
        downloadReceiptButton.addActionListener(e -> downloadReceipt());

        ModernButton exportButton = new ModernButton("Export Data");
        exportButton.addActionListener(e -> exportPaymentData());

        footerPanel.add(refreshButton);
        footerPanel.add(viewDetailsButton);
        footerPanel.add(downloadReceiptButton);
        footerPanel.add(exportButton);

        return footerPanel;
    }

    private void loadPaymentHistory() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer profile not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        List<PaymentTransaction> transactions = paymentTransactionDAO.getTransactionsByCustomerId(customer.getCustomerId());

        double totalAmount = 0.0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

        for (PaymentTransaction txn : transactions) {
            String transactionId = txn.getGatewayTransactionId() != null ? 
                txn.getGatewayTransactionId() : "TXN" + txn.getTransactionId();
            
            String amount = String.format("₹%.2f", txn.getAmount());
            String type = formatPaymentType(txn.getPaymentType());
            String method = formatPaymentMethod(txn.getPaymentMethod());
            String status = txn.getStatus() != null ? txn.getStatus().toUpperCase() : "PENDING";
            String date = txn.getCompletedAt() != null ? 
                txn.getCompletedAt().format(dateFormatter) : 
                (txn.getInitiatedAt() != null ? txn.getInitiatedAt().format(dateFormatter) : "N/A");
            String gateway = txn.getGatewayName() != null ? txn.getGatewayName() : "N/A";

            Object[] rowData = {transactionId, amount, type, method, status, date, gateway};
            tableModel.addRow(rowData);

            if ("success".equalsIgnoreCase(txn.getStatus())) {
                totalAmount += txn.getAmount().doubleValue();
            }
        }

        totalPaymentsLabel.setText("Total Payments: " + transactions.size());
        totalAmountLabel.setText(String.format("Total Amount: ₹%.2f", totalAmount));
    }

    private String formatPaymentType(String type) {
        if (type == null) return "N/A";
        switch (type.toLowerCase()) {
            case "bill_payment": return "Bill Payment";
            case "recharge": return "Recharge";
            case "addon_purchase": return "Addon Purchase";
            default: return type;
        }
    }

    private String formatPaymentMethod(String method) {
        if (method == null) return "N/A";
        switch (method.toLowerCase()) {
            case "upi": return "UPI";
            case "qr_code": return "QR Code";
            case "debit_card": return "Debit Card";
            case "credit_card": return "Credit Card";
            case "net_banking": return "Net Banking";
            case "wallet": return "Wallet";
            default: return method;
        }
    }

    private void viewPaymentDetails() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment to view details!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) return;

        List<PaymentTransaction> transactions = paymentTransactionDAO.getTransactionsByCustomerId(customer.getCustomerId());
        if (selectedRow >= transactions.size()) return;

        PaymentTransaction txn = transactions.get(selectedRow);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════════\n");
        details.append("           PAYMENT DETAILS\n");
        details.append("═══════════════════════════════════════\n\n");
        
        details.append("Transaction ID: ").append(txn.getGatewayTransactionId() != null ? 
            txn.getGatewayTransactionId() : "TXN" + txn.getTransactionId()).append("\n");
        details.append("Amount: ₹").append(String.format("%.2f", txn.getAmount())).append("\n");
        details.append("Payment Type: ").append(formatPaymentType(txn.getPaymentType())).append("\n");
        details.append("Payment Method: ").append(formatPaymentMethod(txn.getPaymentMethod())).append("\n");
        details.append("Status: ").append(txn.getStatus() != null ? txn.getStatus().toUpperCase() : "PENDING").append("\n");
        details.append("Gateway: ").append(txn.getGatewayName() != null ? txn.getGatewayName() : "N/A").append("\n\n");
        
        if (txn.getUpiId() != null) {
            details.append("UPI ID: ").append(txn.getUpiId()).append("\n");
        }
        if (txn.getCardLast4Digits() != null) {
            details.append("Card: **** **** **** ").append(txn.getCardLast4Digits()).append("\n");
        }
        
        details.append("\nInitiated At: ").append(txn.getInitiatedAt() != null ? 
            txn.getInitiatedAt().format(dateFormatter) : "N/A").append("\n");
        details.append("Completed At: ").append(txn.getCompletedAt() != null ? 
            txn.getCompletedAt().format(dateFormatter) : "Pending").append("\n");
        
        if (txn.getFailureReason() != null && !txn.getFailureReason().isEmpty()) {
            details.append("\nFailure Reason: ").append(txn.getFailureReason()).append("\n");
        }

        JOptionPane.showMessageDialog(this, details.toString(), 
            "Payment Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void downloadReceipt() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a payment to download receipt!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) return;

        List<PaymentTransaction> transactions = paymentTransactionDAO.getTransactionsByCustomerId(customer.getCustomerId());
        if (selectedRow >= transactions.size()) return;

        PaymentTransaction txn = transactions.get(selectedRow);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");

        String filename = "Payment_Receipt_" + txn.getTransactionId() + ".txt";
        
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("═══════════════════════════════════════════════════════════\n");
            writer.write("                    PAYMENT RECEIPT\n");
            writer.write("═══════════════════════════════════════════════════════════\n\n");
            
            writer.write("Customer Details:\n");
            writer.write("  Name: " + customer.getFirstName() + " " + customer.getLastName() + "\n");
            writer.write("  Email: " + customer.getEmail() + "\n");
            writer.write("  Phone: " + customer.getPhone() + "\n\n");
            
            writer.write("Transaction Details:\n");
            writer.write("  Transaction ID: " + (txn.getGatewayTransactionId() != null ? 
                txn.getGatewayTransactionId() : "TXN" + txn.getTransactionId()) + "\n");
            writer.write("  Amount Paid: ₹" + String.format("%.2f", txn.getAmount()) + "\n");
            writer.write("  Payment Type: " + formatPaymentType(txn.getPaymentType()) + "\n");
            writer.write("  Payment Method: " + formatPaymentMethod(txn.getPaymentMethod()) + "\n");
            writer.write("  Gateway: " + (txn.getGatewayName() != null ? txn.getGatewayName() : "N/A") + "\n");
            writer.write("  Status: " + (txn.getStatus() != null ? txn.getStatus().toUpperCase() : "PENDING") + "\n\n");
            
            if (txn.getUpiId() != null) {
                writer.write("  UPI ID: " + txn.getUpiId() + "\n");
            }
            if (txn.getCardLast4Digits() != null) {
                writer.write("  Card: **** **** **** " + txn.getCardLast4Digits() + "\n");
            }
            
            writer.write("\nTransaction Timeline:\n");
            writer.write("  Initiated: " + (txn.getInitiatedAt() != null ? 
                txn.getInitiatedAt().format(dateFormatter) : "N/A") + "\n");
            writer.write("  Completed: " + (txn.getCompletedAt() != null ? 
                txn.getCompletedAt().format(dateFormatter) : "Pending") + "\n\n");
            
            writer.write("═══════════════════════════════════════════════════════════\n");
            writer.write("          Thank you for using our services!\n");
            writer.write("═══════════════════════════════════════════════════════════\n");

            JOptionPane.showMessageDialog(this, 
                "Receipt downloaded successfully!\nSaved as: " + filename, 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Failed to download receipt: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportPaymentData() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) return;

        List<PaymentTransaction> transactions = paymentTransactionDAO.getTransactionsByCustomerId(customer.getCustomerId());
        
        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No payment data to export!", 
                "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String filename = "Payment_History_" + customer.getCustomerId() + ".csv";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        
        try (FileWriter writer = new FileWriter(filename)) {
            // Write CSV header
            writer.write("Transaction ID,Amount,Type,Method,Status,Gateway,Initiated At,Completed At\n");
            
            // Write data rows
            for (PaymentTransaction txn : transactions) {
                writer.write(String.format("\"%s\",\"₹%.2f\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    txn.getGatewayTransactionId() != null ? txn.getGatewayTransactionId() : "TXN" + txn.getTransactionId(),
                    txn.getAmount(),
                    formatPaymentType(txn.getPaymentType()),
                    formatPaymentMethod(txn.getPaymentMethod()),
                    txn.getStatus() != null ? txn.getStatus().toUpperCase() : "PENDING",
                    txn.getGatewayName() != null ? txn.getGatewayName() : "N/A",
                    txn.getInitiatedAt() != null ? txn.getInitiatedAt().format(dateFormatter) : "N/A",
                    txn.getCompletedAt() != null ? txn.getCompletedAt().format(dateFormatter) : "Pending"
                ));
            }
            
            JOptionPane.showMessageDialog(this, 
                String.format("Payment history exported successfully!\n%d records saved to: %s", 
                    transactions.size(), filename), 
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Failed to export data: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
