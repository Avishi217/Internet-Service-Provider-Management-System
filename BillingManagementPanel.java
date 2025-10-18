package com.isp.ui.views.admin;

import com.isp.dao.BillDAO;
import com.isp.dao.CustomerDAO;
import com.isp.dao.PaymentDAO;
import com.isp.dao.PlanDAO;
import com.isp.model.Bill;
import com.isp.model.Customer;
import com.isp.model.Payment;
import com.isp.model.Plan;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillingManagementPanel extends JPanel {
    private JTable billsTable;
    private DefaultTableModel tableModel;
    private final BillDAO billDAO;
    private final CustomerDAO customerDAO;
    private final PaymentDAO paymentDAO;
    private final PlanDAO planDAO;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private com.isp.ui.views.AdminDashboard parentDashboard;

    public BillingManagementPanel() {
        this(null);
    }

    public BillingManagementPanel(com.isp.ui.views.AdminDashboard parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.billDAO = new BillDAO();
        this.customerDAO = new CustomerDAO();
        this.paymentDAO = new PaymentDAO();
        this.planDAO = new PlanDAO();
        initializeUI();
        loadBillsData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Main content panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Title header
        mainPanel.add(createTitleHeaderPanel("Billing Management"));

        // Header panel (search and filters)
        mainPanel.add(createHeaderPanel());

        add(mainPanel, BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitleHeaderPanel(String title) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 0, 0));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        return titlePanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Billing Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(UIConstants.NORMAL_FONT);
        statusLabel.setForeground(Color.WHITE);
        
        statusFilter = new JComboBox<>(new String[]{"All Bills", "paid", "unpaid", "overdue"});
        statusFilter.addActionListener(e -> filterByStatus());

        // Search
        JLabel searchLabel = new JLabel("Search Customer:");
        searchLabel.setFont(UIConstants.NORMAL_FONT);
        searchLabel.setForeground(Color.WHITE);
        
        searchField = new ModernTextField();
        searchField.setPreferredSize(new Dimension(200, 35));

        ModernButton searchButton = new ModernButton("Search");
        searchButton.addActionListener(e -> performSearch());

        ModernButton resetButton = new ModernButton("Reset");
        resetButton.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedIndex(0);
            loadBillsData();
        });

        controlPanel.add(statusLabel);
        controlPanel.add(statusFilter);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(resetButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Bill ID", "Customer", "Amount", "Due Date", "Status", "Generated", "Paid Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billsTable = new JTable(tableModel);
        billsTable.setFont(UIConstants.NORMAL_FONT);
        billsTable.setRowHeight(30);
        billsTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        billsTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        billsTable.getTableHeader().setForeground(Color.WHITE);
        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(billsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton generateButton = new ModernButton("Generate Bill");
        generateButton.addActionListener(e -> showGenerateBillDialog());

        ModernButton markPaidButton = new ModernButton("Mark as Paid");
        markPaidButton.addActionListener(e -> markBillAsPaid());

        ModernButton viewDetailsButton = new ModernButton("View Details");
        viewDetailsButton.addActionListener(e -> viewBillDetails());

        ModernButton deleteButton = new ModernButton("Delete Bill");
        deleteButton.addActionListener(e -> deleteBill());

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> loadBillsData());

        footerPanel.add(generateButton);
        footerPanel.add(markPaidButton);
        footerPanel.add(viewDetailsButton);
        footerPanel.add(deleteButton);
        footerPanel.add(refreshButton);

        return footerPanel;
    }

    private void loadBillsData() {
        tableModel.setRowCount(0);
        List<Bill> bills = billDAO.getAllBills();

        java.text.DecimalFormat currencyFormat = new java.text.DecimalFormat("₹#,##0.00");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Bill bill : bills) {
            Customer customer = customerDAO.getCustomerById(bill.getCustomerId());
            String customerName = customer != null ? 
                customer.getFirstName() + " " + customer.getLastName() : "Unknown";

            String paidDate = bill.getPaidDate() != null ? 
                bill.getPaidDate().format(dateFormatter) : "-";

            Object[] rowData = {
                bill.getBillId(),
                customerName,
                currencyFormat.format(bill.getAmount()),
                bill.getDueDate().format(dateFormatter),
                bill.getStatus(),
                bill.getGeneratedDate().format(dateFormatter),
                paidDate
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

        tableModel.setRowCount(0);
        List<Bill> bills = billDAO.getBillsByStatus(selectedStatus);

        java.text.DecimalFormat currencyFormat = new java.text.DecimalFormat("₹#,##0.00");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Bill bill : bills) {
            Customer customer = customerDAO.getCustomerById(bill.getCustomerId());
            String customerName = customer != null ? 
                customer.getFirstName() + " " + customer.getLastName() : "Unknown";

            String paidDate = bill.getPaidDate() != null ? 
                bill.getPaidDate().format(dateFormatter) : "-";

            Object[] rowData = {
                bill.getBillId(),
                customerName,
                currencyFormat.format(bill.getAmount()),
                bill.getDueDate().format(dateFormatter),
                bill.getStatus(),
                bill.getGeneratedDate().format(dateFormatter),
                paidDate
            };
            tableModel.addRow(rowData);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBillsData();
            return;
        }

        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.searchCustomers(searchTerm);
        
        java.text.DecimalFormat currencyFormat = new java.text.DecimalFormat("₹#,##0.00");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Customer customer : customers) {
            List<Bill> bills = billDAO.getBillsByCustomerId(customer.getCustomerId());
            
            for (Bill bill : bills) {
                String customerName = customer.getFirstName() + " " + customer.getLastName();
                String paidDate = bill.getPaidDate() != null ? 
                    bill.getPaidDate().format(dateFormatter) : "-";

                Object[] rowData = {
                    bill.getBillId(),
                    customerName,
                    currencyFormat.format(bill.getAmount()),
                    bill.getDueDate().format(dateFormatter),
                    bill.getStatus(),
                    bill.getGeneratedDate().format(dateFormatter),
                    paidDate
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void showGenerateBillDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generate Bill", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Customer combo box
        JComboBox<String> customerCombo = new JComboBox<>();
        List<Customer> customers = customerDAO.getAllCustomers();
        for (Customer customer : customers) {
            customerCombo.addItem(customer.getCustomerId() + " - " + 
                customer.getFirstName() + " " + customer.getLastName());
        }

        // Plan dropdown - shows all plans with prices
        JComboBox<String> planCombo = new JComboBox<>();
        List<Plan> allPlans = planDAO.getAllPlans();
        for (Plan plan : allPlans) {
            BigDecimal baseAmount = plan.getPriceInr();
            BigDecimal gstAmount = baseAmount.multiply(new BigDecimal("0.18"));
            BigDecimal totalAmount = baseAmount.add(gstAmount);
            planCombo.addItem(plan.getPlanId() + " - " + plan.getPlanName() + 
                " (₹" + String.format("%.2f", totalAmount) + " = ₹" + String.format("%.2f", baseAmount) + " + GST)");
        }
        
        // Auto-select customer's current plan when customer is selected
        customerCombo.addActionListener(e -> {
            if (customerCombo.getSelectedIndex() != -1) {
                String selectedCustomer = (String) customerCombo.getSelectedItem();
                int customerId = Integer.parseInt(selectedCustomer.split(" - ")[0]);
                Customer customer = customerDAO.getCustomerById(customerId);
                
                if (customer != null && customer.getPlanId() != null) {
                    // Find and select the customer's plan in the dropdown
                    for (int i = 0; i < planCombo.getItemCount(); i++) {
                        String planItem = (String) planCombo.getItemAt(i);
                        int planId = Integer.parseInt(planItem.split(" - ")[0]);
                        if (planId == customer.getPlanId()) {
                            planCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });
        
        // Trigger initial load
        if (customerCombo.getItemCount() > 0) {
            customerCombo.setSelectedIndex(0);
        }

        // Due date spinner (30 days from now)
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dueDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd");
        dueDateSpinner.setEditor(dateEditor);
        dueDateSpinner.setValue(java.util.Date.from(
            LocalDate.now().plusDays(30).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));

        // Status combo
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"unpaid", "paid", "overdue"});

        // Add form fields
        addFormField(formPanel, gbc, "Customer:", customerCombo);
        addFormField(formPanel, gbc, "Select Plan:", planCombo);
        addFormField(formPanel, gbc, "Due Date:", dueDateSpinner);
        addFormField(formPanel, gbc, "Status:", statusCombo);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton generateButton = new ModernButton("Generate");
        generateButton.addActionListener(e -> {
            if (customerCombo.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a customer!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (planCombo.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a plan!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Get selected customer ID
                String selectedCustomer = (String) customerCombo.getSelectedItem();
                int customerId = Integer.parseInt(selectedCustomer.split(" - ")[0]);
                
                Customer customer = customerDAO.getCustomerById(customerId);
                
                if (customer == null) {
                    JOptionPane.showMessageDialog(dialog, "Customer not found!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Get selected plan from dropdown
                String selectedPlan = (String) planCombo.getSelectedItem();
                int planId = Integer.parseInt(selectedPlan.split(" - ")[0]);
                
                Plan plan = planDAO.getPlanById(planId);
                if (plan == null) {
                    JOptionPane.showMessageDialog(dialog, "Plan not found!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                BigDecimal baseAmount = plan.getPriceInr();
                
                // Create bill (createBill will automatically calculate GST and total)
                Bill bill = new Bill();
                bill.setCustomerId(customerId);
                bill.setPlanId(planId);
                bill.setBaseAmount(baseAmount);
                
                java.util.Date selectedDate = (java.util.Date) dueDateSpinner.getValue();
                bill.setDueDate(LocalDate.ofInstant(selectedDate.toInstant(), 
                    java.time.ZoneId.systemDefault()));
                
                bill.setStatus((String) statusCombo.getSelectedItem());
                bill.setBillDate(LocalDate.now());

                int billId = billDAO.createBill(bill);
                if (billId > 0) {
                    JOptionPane.showMessageDialog(dialog, "Bill generated successfully!\nBill ID: " + billId, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBillsData();
                    
                    // Refresh dashboard if available
                    if (parentDashboard != null) {
                        parentDashboard.refreshDashboard();
                    }
                    
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to generate bill!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid customer selection!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void markBillAsPaid() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to mark as paid!", 
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

        if ("paid".equals(bill.getStatus())) {
            JOptionPane.showMessageDialog(this, "This bill is already marked as paid!", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show payment method dialog
        String[] paymentMethods = {"credit_card", "debit_card", "upi", "net_banking", "cash"};
        String method = (String) JOptionPane.showInputDialog(this,
            "Select payment method:",
            "Payment Method",
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentMethods,
            paymentMethods[0]);

        if (method != null) {
            // Create payment record
            Payment payment = new Payment();
            payment.setBillId(billId);
            payment.setCustomerId(bill.getCustomerId());
            payment.setAmount(bill.getAmount());
            payment.setPaymentMethod(method);
            payment.setTransactionId("TXN" + System.currentTimeMillis());

            int paymentId = paymentDAO.createPayment(payment);
            
            if (paymentId > 0 && billDAO.markBillAsPaid(billId)) {
                JOptionPane.showMessageDialog(this, 
                    "Bill marked as paid!\nPayment ID: " + paymentId, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBillsData();
                
                // Refresh dashboard if available
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to mark bill as paid!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewPaymentHistory() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (int) tableModel.getValueAt(selectedRow, 0);
        List<Payment> payments = paymentDAO.getPaymentsByBillId(billId);

        if (payments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No payments found for this bill!", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create payment history dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Payment History - Bill #" + billId, true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"Payment ID", "Amount", "Payment Date", "Method", "Transaction ID"};
        DefaultTableModel paymentModel = new DefaultTableModel(columns, 0);

        java.text.DecimalFormat currencyFormat = new java.text.DecimalFormat("₹#,##0.00");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Payment payment : payments) {
            Object[] row = {
                payment.getPaymentId(),
                currencyFormat.format(payment.getAmount()),
                payment.getPaymentDate().format(dateTimeFormatter),
                payment.getPaymentMethod(),
                payment.getTransactionId()
            };
            paymentModel.addRow(row);
        }

        JTable paymentTable = new JTable(paymentModel);
        paymentTable.setFont(UIConstants.NORMAL_FONT);
        paymentTable.setRowHeight(30);

        dialog.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ModernButton closeButton = new ModernButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewBillDetails() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill!", 
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

        Customer customer = customerDAO.getCustomerById(bill.getCustomerId());
        String planName = "No Plan";
        if (bill.getPlanId() != null) {
            Plan plan = planDAO.getPlanById(bill.getPlanId());
            if (plan != null) {
                planName = plan.getPlanName();
            }
        }

        java.text.DecimalFormat currencyFormat = new java.text.DecimalFormat("₹#,##0.00");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String details = String.format(
            "Bill ID: %d\n\n" +
            "Customer: %s %s\n" +
            "Email: %s\n" +
            "Phone: %s\n\n" +
            "Plan: %s\n" +
            "Amount: %s\n" +
            "Status: %s\n\n" +
            "Generated Date: %s\n" +
            "Due Date: %s\n" +
            "Paid Date: %s",
            bill.getBillId(),
            customer.getFirstName(), customer.getLastName(),
            customer.getEmail(),
            customer.getPhone(),
            planName,
            currencyFormat.format(bill.getAmount()),
            bill.getStatus().toUpperCase(),
            bill.getGeneratedDate().format(dateFormatter),
            bill.getDueDate().format(dateFormatter),
            bill.getPaidDate() != null ? bill.getPaidDate().format(dateFormatter) : "Not Paid"
        );

        JOptionPane.showMessageDialog(this, details, "Bill Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to delete!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId = (int) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete bill #" + billId + " for " + customerName + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (billDAO.deleteBill(billId)) {
                JOptionPane.showMessageDialog(this, "Bill deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBillsData();
                
                // Refresh dashboard if available
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete bill!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.NORMAL_FONT);
        
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        
        gbc.gridy++;
    }
}
