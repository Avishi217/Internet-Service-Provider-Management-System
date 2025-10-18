package com.isp.ui.views.customer;

import com.isp.dao.BillDAO;
import com.isp.dao.CustomerDAO;
import com.isp.dao.PaymentDAO;
import com.isp.dao.PlanDAO;
import com.isp.model.Bill;
import com.isp.model.Customer;
import com.isp.model.Payment;
import com.isp.model.Plan;
import com.isp.ui.components.ModernButton;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class MyBillsPanel extends JPanel {
    private final BillDAO billDAO;
    private final PaymentDAO paymentDAO;
    private final CustomerDAO customerDAO;
    private final PlanDAO planDAO;
    private final int userId;
    
    private JTable billsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;

    public MyBillsPanel(int userId) {
        this.userId = userId;
        this.billDAO = new BillDAO();
        this.paymentDAO = new PaymentDAO();
        this.customerDAO = new CustomerDAO();
        this.planDAO = new PlanDAO();
        
        initializeUI();
        loadBillsData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("My Bills");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(UIConstants.NORMAL_FONT);
        
        statusFilter = new JComboBox<>(new String[]{"All Bills", "unpaid", "paid", "overdue"});
        statusFilter.addActionListener(e -> filterByStatus());

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            loadBillsData();
        });

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        // New column order: ID, Amount, Status, Due Date, Billing Period, Payment Date, Payment Method
        String[] columnNames = {"Bill ID", "Amount", "Status", "Due Date", "Billing Period", "Payment Date", "Payment Method"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billsTable = new JTable(tableModel);
        billsTable.setFont(UIConstants.NORMAL_FONT);
        billsTable.setRowHeight(35);
        billsTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        billsTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        billsTable.getTableHeader().setForeground(Color.WHITE);
        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set row colors based on status
        billsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 2); // Status is now column 2
                    if ("paid".equals(status)) {
                        c.setBackground(new Color(230, 255, 230)); // Light green
                    } else if ("overdue".equals(status)) {
                        c.setBackground(new Color(255, 230, 230)); // Light red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(billsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton viewDetailsButton = new ModernButton("View Details");
        viewDetailsButton.addActionListener(e -> viewBillDetails());

        ModernButton payBillButton = new ModernButton("Pay Bill");
        payBillButton.addActionListener(e -> payBill());

        ModernButton downloadPDFButton = new ModernButton("Download PDF");
        downloadPDFButton.addActionListener(e -> downloadPDF());

        footerPanel.add(viewDetailsButton);
        footerPanel.add(payBillButton);
        footerPanel.add(downloadPDFButton);

        return footerPanel;
    }

    private void loadBillsData() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            return;
        }

        tableModel.setRowCount(0);
        List<Bill> bills = billDAO.getBillsByCustomerId(customer.getCustomerId());

        for (Bill bill : bills) {
            // Get payment info if exists
            String paymentDate = "-";
            String paymentMethod = "-";
            
            if ("paid".equals(bill.getStatus())) {
                List<Payment> payments = paymentDAO.getPaymentsByBillId(bill.getBillId());
                if (!payments.isEmpty()) {
                    Payment payment = payments.get(0); // Get the first/only payment
                    paymentDate = payment.getPaymentDate().toString();
                    paymentMethod = payment.getPaymentMethod();
                }
            }

            // Get plan amount (like admin does)
            String planAmount = "₹0.00";
            Plan plan = planDAO.getPlanById(customer.getPlanId());
            if (plan != null) {
                planAmount = String.format("₹%.2f", plan.getPriceInr().doubleValue());
            }

            // New order: Bill ID, Amount, Status, Due Date, Billing Period, Payment Date, Payment Method
            Object[] rowData = {
                bill.getBillId(),
                planAmount,  // Show plan amount with ₹
                bill.getStatus(),
                bill.getDueDate(),
                bill.getGeneratedDate().toString(),
                paymentDate,
                paymentMethod
            };
            tableModel.addRow(rowData);
        }
    }

    private void filterByStatus() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        if ("All Bills".equals(selectedStatus)) {
            loadBillsData();
            return;
        }

        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            return;
        }

        tableModel.setRowCount(0);
        List<Bill> bills = billDAO.getBillsByStatus(selectedStatus);

        for (Bill bill : bills) {
            if (bill.getCustomerId() == customer.getCustomerId()) {
                String paymentDate = "-";
                String paymentMethod = "-";
                
                if ("paid".equals(bill.getStatus())) {
                    List<Payment> payments = paymentDAO.getPaymentsByBillId(bill.getBillId());
                    if (!payments.isEmpty()) {
                        Payment payment = payments.get(0);
                        paymentDate = payment.getPaymentDate().toString();
                        paymentMethod = payment.getPaymentMethod();
                    }
                }

                // Get plan amount (like admin does)
                String planAmount = "₹0.00";
                Plan plan = planDAO.getPlanById(customer.getPlanId());
                if (plan != null) {
                    planAmount = String.format("₹%.2f", plan.getPriceInr().doubleValue());
                }

                // New order: Bill ID, Amount, Status, Due Date, Billing Period, Payment Date, Payment Method
                Object[] rowData = {
                    bill.getBillId(),
                    planAmount,  // Show plan amount with ₹
                    bill.getStatus(),
                    bill.getDueDate(),
                    bill.getGeneratedDate().toString(),
                    paymentDate,
                    paymentMethod
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void viewBillDetails() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to view details!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (int) tableModel.getValueAt(selectedRow, 0);
        Bill bill = billDAO.getBillById(billId);

        if (bill == null) {
            JOptionPane.showMessageDialog(this, "Bill not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get customer and plan info
        Customer customer = customerDAO.getCustomerByUserId(userId);
        String customerName = "Unknown";
        String customerEmail = "N/A";
        String customerPhone = "N/A";
        String planName = "No Plan";
        String planAmount = "₹0.00";
        
        if (customer != null) {
            customerName = customer.getFirstName() + " " + customer.getLastName();
            customerEmail = customer.getEmail();
            customerPhone = customer.getPhone();
            
            Plan plan = planDAO.getPlanById(customer.getPlanId());
            if (plan != null) {
                planName = plan.getPlanName();
                planAmount = String.format("₹%.2f", plan.getPriceInr().doubleValue());
            }
        }

        // Create comprehensive details dialog
        java.text.DecimalFormat currencyFormat = new java.text.DecimalFormat("₹#,##0.00");
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String details = String.format(
            "Bill ID: %d\n\n" +
            "Customer: %s\n" +
            "Email: %s\n" +
            "Phone: %s\n\n" +
            "Plan: %s\n" +
            "Amount: %s\n" +
            "Status: %s\n\n" +
            "Generated Date: %s\n" +
            "Due Date: %s",
            bill.getBillId(),
            customerName,
            customerEmail,
            customerPhone,
            planName,
            planAmount,
            bill.getStatus().toUpperCase(),
            bill.getGeneratedDate().format(dateFormatter),
            bill.getDueDate().format(dateFormatter)
        );

        // Add payment info if paid
        if ("paid".equals(bill.getStatus())) {
            List<Payment> payments = paymentDAO.getPaymentsByBillId(bill.getBillId());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                details += String.format("\n\nPayment Date: %s\nPayment Method: %s\nTransaction ID: %s",
                    payment.getPaymentDate().toString(),
                    payment.getPaymentMethod(),
                    payment.getTransactionId());
            }
        }

        JOptionPane.showMessageDialog(this, details, "Bill Details - #" + billId, 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void payBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to pay!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (int) tableModel.getValueAt(selectedRow, 0);
        payBillById(billId);
    }

    private void payBillById(int billId) {
        Bill bill = billDAO.getBillById(billId);

        if (bill == null) {
            JOptionPane.showMessageDialog(this, "Bill not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("paid".equals(bill.getStatus())) {
            JOptionPane.showMessageDialog(this, "This bill has already been paid!", 
                "Already Paid", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show payment dialog
        String[] paymentMethods = {"Credit Card", "Debit Card", "Net Banking", "UPI", "Cash"};
        String selectedMethod = (String) JOptionPane.showInputDialog(this,
            "Select payment method:\n\nAmount: ₹" + bill.getAmount(),
            "Payment Method",
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentMethods,
            paymentMethods[0]);

        if (selectedMethod != null) {
            // Create payment
            Payment payment = new Payment();
            payment.setBillId(billId);
            payment.setCustomerId(bill.getCustomerId());
            payment.setAmount(bill.getAmount());
            payment.setPaymentMethod(selectedMethod);
            payment.setTransactionId("TXN" + System.currentTimeMillis());

            int paymentId = paymentDAO.createPayment(payment);
            
            if (paymentId > 0) {
                // Update bill status
                bill.setStatus("paid");
                if (billDAO.updateBill(bill)) {
                    JOptionPane.showMessageDialog(this, 
                        "Payment successful!\n\n" +
                        "Transaction ID: " + payment.getTransactionId() + "\n" +
                        "Amount Paid: ₹" + bill.getAmount() + "\n" +
                        "Payment Method: " + selectedMethod, 
                        "Payment Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadBillsData();
                } else {
                    JOptionPane.showMessageDialog(this, "Payment recorded but failed to update bill status!", 
                        "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Payment failed! Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void downloadPDF() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to download!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (int) tableModel.getValueAt(selectedRow, 0);
        Bill bill = billDAO.getBillById(billId);

        if (bill == null) {
            JOptionPane.showMessageDialog(this, "Bill not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get customer and plan info
        Customer customer = customerDAO.getCustomerByUserId(userId);
        String customerName = "Unknown";
        String customerEmail = "N/A";
        String customerPhone = "N/A";
        String planName = "No Plan";
        String planAmount = "₹0.00";
        
        if (customer != null) {
            customerName = customer.getFirstName() + " " + customer.getLastName();
            customerEmail = customer.getEmail();
            customerPhone = customer.getPhone();
            
            Plan plan = planDAO.getPlanById(customer.getPlanId());
            if (plan != null) {
                planName = plan.getPlanName();
                planAmount = String.format("₹%.2f", plan.getPriceInr().doubleValue());
            }
        }

        // Generate PDF content
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        
        StringBuilder pdfContent = new StringBuilder();
        pdfContent.append("═══════════════════════════════════════════════════════════\n");
        pdfContent.append("              ISP MANAGEMENT SYSTEM\n");
        pdfContent.append("                    TAX INVOICE\n");
        pdfContent.append("═══════════════════════════════════════════════════════════\n\n");
        pdfContent.append("INVOICE #").append(bill.getBillId()).append("\n");
        pdfContent.append("Date: ").append(java.time.LocalDate.now().format(dateFormatter)).append("\n\n");
        pdfContent.append("───────────────────────────────────────────────────────────\n\n");
        pdfContent.append("BILL TO:\n");
        pdfContent.append(customerName).append("\n");
        pdfContent.append("Phone: ").append(customerPhone).append("\n");
        pdfContent.append("Email: ").append(customerEmail).append("\n\n");
        pdfContent.append("───────────────────────────────────────────────────────────\n\n");
        pdfContent.append("SERVICE DETAILS:\n");
        pdfContent.append("Plan: ").append(planName).append("\n");
        pdfContent.append("Billing Period: ").append(bill.getGeneratedDate().format(dateFormatter));
        pdfContent.append(" - ");
        pdfContent.append(bill.getDueDate().format(dateFormatter)).append("\n\n");
        pdfContent.append("───────────────────────────────────────────────────────────\n\n");
        pdfContent.append("CHARGES:\n");
        pdfContent.append(String.format("%-40s %15s\n", "Plan Amount", planAmount));
        pdfContent.append(String.format("%-40s %15s\n", "GST (18%)", String.format("₹%.2f", Double.parseDouble(planAmount.substring(1)) * 0.18)));
        pdfContent.append("───────────────────────────────────────────────────────────\n");
        pdfContent.append(String.format("%-40s %15s\n\n", "TOTAL DUE", String.format("₹%.2f", Double.parseDouble(planAmount.substring(1)) * 1.18)));
        pdfContent.append("Due Date: ").append(bill.getDueDate().format(dateFormatter)).append("\n");
        pdfContent.append("Status: ").append(bill.getStatus().toUpperCase()).append("\n\n");
        
        if ("paid".equals(bill.getStatus())) {
            List<Payment> payments = paymentDAO.getPaymentsByBillId(bill.getBillId());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                pdfContent.append("───────────────────────────────────────────────────────────\n\n");
                pdfContent.append("PAYMENT DETAILS:\n");
                pdfContent.append("Payment Date: ").append(payment.getPaymentDate().toString()).append("\n");
                pdfContent.append("Payment Method: ").append(payment.getPaymentMethod()).append("\n");
                pdfContent.append("Transaction ID: ").append(payment.getTransactionId()).append("\n\n");
            }
        }
        
        pdfContent.append("═══════════════════════════════════════════════════════════\n");
        pdfContent.append("         Thank you for choosing our service!\n");
        pdfContent.append("═══════════════════════════════════════════════════════════\n");

        // Save to file
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("Bill_" + billId + ".txt"));
            fileChooser.setDialogTitle("Save Bill");
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(pdfContent.toString());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Bill saved successfully to:\n" + fileToSave.getAbsolutePath(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to save bill!\nError: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(UIConstants.NORMAL_FONT);
        labelComponent.setForeground(UIConstants.TEXT_SECONDARY);
        
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(labelComponent, gbc);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(UIConstants.NORMAL_FONT);
        
        gbc.gridx = 1;
        panel.add(valueComponent, gbc);
        
        gbc.gridy++;
    }
}
