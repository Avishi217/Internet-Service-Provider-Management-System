package com.isp.ui.views.customer;

import com.isp.dao.CustomerDAO;
import com.isp.dao.PlanDAO;
import com.isp.model.Customer;
import com.isp.model.Plan;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.PaymentGatewayDialog;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyPlansPanel extends JPanel {
    private final CustomerDAO customerDAO;
    private final PlanDAO planDAO;
    private final int userId;
    
    private JTable plansTable;
    private DefaultTableModel tableModel;
    private JPanel currentPlanPanel;
    private JLabel currentPlanNameLabel;
    private JLabel currentPlanSpeedLabel;
    private JLabel currentPlanDataLabel;
    private JLabel currentPlanPriceLabel;
    private JLabel currentPlanValidityLabel;
    private JTextArea currentPlanDescArea;

    public MyPlansPanel(int userId) {
        this.userId = userId;
        this.customerDAO = new CustomerDAO();
        this.planDAO = new PlanDAO();
        
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        centerPanel.add(createCurrentPlanPanel());
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(createAvailablePlansPanel());
        
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("My Plans");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        return headerPanel;
    }

    private JPanel createCurrentPlanPanel() {
        currentPlanPanel = new JPanel(new GridBagLayout());
        currentPlanPanel.setBackground(Color.WHITE);
        currentPlanPanel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR), 
                "Current Plan", TitledBorder.LEFT, TitledBorder.TOP, 
                UIConstants.SUBTITLE_FONT, UIConstants.TEXT_PRIMARY),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        currentPlanNameLabel = new JLabel();
        currentPlanNameLabel.setFont(UIConstants.TITLE_FONT);
        currentPlanNameLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        currentPlanSpeedLabel = new JLabel();
        currentPlanSpeedLabel.setFont(UIConstants.SUBTITLE_FONT);
        
        currentPlanDataLabel = new JLabel();
        currentPlanDataLabel.setFont(UIConstants.NORMAL_FONT);
        
        currentPlanPriceLabel = new JLabel();
        currentPlanPriceLabel.setFont(new Font(UIConstants.NORMAL_FONT.getName(), Font.BOLD, 18));
        currentPlanPriceLabel.setForeground(UIConstants.SUCCESS_COLOR);
        
        currentPlanValidityLabel = new JLabel();
        currentPlanValidityLabel.setFont(UIConstants.NORMAL_FONT);
        
        currentPlanDescArea = new JTextArea(3, 40);
        currentPlanDescArea.setEditable(false);
        currentPlanDescArea.setLineWrap(true);
        currentPlanDescArea.setWrapStyleWord(true);
        currentPlanDescArea.setFont(UIConstants.NORMAL_FONT);
        currentPlanDescArea.setBackground(Color.WHITE);
        currentPlanDescArea.setBorder(null);

        gbc.gridwidth = 2;
        currentPlanPanel.add(currentPlanNameLabel, gbc);
        gbc.gridy++;
        
        currentPlanPanel.add(currentPlanSpeedLabel, gbc);
        gbc.gridy++;
        
        addFormField(currentPlanPanel, gbc, "Data Limit:", currentPlanDataLabel);
        addFormField(currentPlanPanel, gbc, "Monthly Price:", currentPlanPriceLabel);
        addFormField(currentPlanPanel, gbc, "Validity:", currentPlanValidityLabel);
        
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        currentPlanPanel.add(currentPlanDescArea, gbc);

        return currentPlanPanel;
    }

    private JPanel createAvailablePlansPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR), 
                "Available Plans", TitledBorder.LEFT, TitledBorder.TOP, 
                UIConstants.SUBTITLE_FONT, UIConstants.TEXT_PRIMARY),
            new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columnNames = {"Plan Name", "Speed", "Data Limit", "Price/Month", "Validity (Days)", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        plansTable = new JTable(tableModel);
        plansTable.setFont(UIConstants.NORMAL_FONT);
        plansTable.setRowHeight(40);
        plansTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        plansTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        plansTable.getTableHeader().setForeground(Color.WHITE);
        plansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        plansTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        plansTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        plansTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        plansTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        plansTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        plansTable.getColumnModel().getColumn(5).setPreferredWidth(300);

        JScrollPane scrollPane = new JScrollPane(plansTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton requestChangeButton = new ModernButton("Request Plan Change");
        requestChangeButton.addActionListener(e -> requestPlanChange());

        ModernButton viewDetailsButton = new ModernButton("View Details");
        viewDetailsButton.addActionListener(e -> viewPlanDetails());

        ModernButton purchaseButton = new ModernButton("💳 Purchase/Recharge");
        purchaseButton.setBackground(UIConstants.SUCCESS_COLOR);
        purchaseButton.addActionListener(e -> purchasePlan());

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(purchaseButton);
        buttonPanel.add(requestChangeButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadData() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        
        if (customer != null && customer.getPlanId() != null) {
            Plan currentPlan = planDAO.getPlanById(customer.getPlanId());
            if (currentPlan != null) {
                currentPlanNameLabel.setText(currentPlan.getPlanName());
                currentPlanSpeedLabel.setText("⚡ " + currentPlan.getVoiceBenefits());
                currentPlanDataLabel.setText(currentPlan.getDataDescription());
                currentPlanPriceLabel.setText(currentPlan.getFormattedPrice() + "/month");
                currentPlanValidityLabel.setText(currentPlan.getValidityDescription());
                currentPlanDescArea.setText(currentPlan.getDescription());
            }
        } else {
            currentPlanNameLabel.setText("No Plan Assigned");
            currentPlanSpeedLabel.setText("");
            currentPlanDataLabel.setText("-");
            currentPlanPriceLabel.setText("-");
            currentPlanValidityLabel.setText("-");
            currentPlanDescArea.setText("You don't have an active plan. Please contact support or select a plan below.");
        }

        loadAvailablePlans();
    }

    private void loadAvailablePlans() {
        tableModel.setRowCount(0);
        List<Plan> plans = planDAO.getAllPlans();

        for (Plan plan : plans) {
            if ("active".equals(plan.getStatus())) {
                Object[] rowData = {
                    plan.getPlanName(),
                    plan.getVoiceBenefits(),
                    plan.getDataDescription(),
                    plan.getFormattedPrice(),
                    plan.getValidityDescription(),
                    plan.getDescription()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void requestPlanChange() {
        int selectedRow = plansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a plan to request!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String planName = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Do you want to request a change to " + planName + "?\n\n" +
            "An administrator will review your request and contact you.", 
            "Confirm Plan Change Request", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // In a real application, this would create a request in the database
            JOptionPane.showMessageDialog(this, 
                "Your plan change request has been submitted!\n\n" +
                "Plan: " + planName + "\n" +
                "An administrator will contact you within 24-48 hours.", 
                "Request Submitted", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewPlanDetails() {
        int selectedRow = plansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a plan to view details!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String planName = (String) tableModel.getValueAt(selectedRow, 0);
        String speed = (String) tableModel.getValueAt(selectedRow, 1);
        String dataLimit = (String) tableModel.getValueAt(selectedRow, 2);
        String price = (String) tableModel.getValueAt(selectedRow, 3);
        String validity = tableModel.getValueAt(selectedRow, 4).toString();
        String description = (String) tableModel.getValueAt(selectedRow, 5);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Plan Details: " + planName, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(planName);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel speedLabel = new JLabel("Speed: " + speed);
        speedLabel.setFont(UIConstants.SUBTITLE_FONT);
        speedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dataLabel = new JLabel("Data Limit: " + dataLimit);
        dataLabel.setFont(UIConstants.NORMAL_FONT);
        dataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("Price: " + price);
        priceLabel.setFont(new Font(UIConstants.NORMAL_FONT.getName(), Font.BOLD, 16));
        priceLabel.setForeground(UIConstants.SUCCESS_COLOR);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel validityLabel = new JLabel("Validity: " + validity + " days");
        validityLabel.setFont(UIConstants.NORMAL_FONT);
        validityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(UIConstants.NORMAL_FONT);
        descArea.setBackground(Color.WHITE);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(speedLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(dataLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(validityLabel);
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(new JLabel("Description:"));
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(descArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton closeButton = new ModernButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(closeButton);

        dialog.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.NORMAL_FONT);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        
        gbc.gridy++;
    }

    private void purchasePlan() {
        int selectedRow = plansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a plan to purchase!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String planName = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find the selected plan object
        Plan selectedPlan = null;
        for (Plan plan : planDAO.getAllPlans()) {
            if (plan.getPlanName().equals(planName)) {
                selectedPlan = plan;
                break;
            }
        }

        if (selectedPlan == null) {
            JOptionPane.showMessageDialog(this, "Plan not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show payment gateway dialog
        PaymentGatewayDialog paymentDialog = new PaymentGatewayDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            selectedPlan, 
            userId
        );
        paymentDialog.setVisible(true);

        // Check if payment was successful
        if (paymentDialog.isPaymentSuccessful()) {
            JOptionPane.showMessageDialog(this, 
                "🎉 Plan purchased successfully!\n\n" +
                "Plan: " + selectedPlan.getPlanName() + "\n" +
                "Amount: " + selectedPlan.getFormattedPrice() + "\n" +
                "Transaction ID: " + paymentDialog.getTransactionId() + "\n\n" +
                "Your plan is now active!",
                "Purchase Successful", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh the data to show updated plan if needed
            loadData();
        }
    }
}
