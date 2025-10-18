package com.isp.ui.views.admin;


import com.isp.dao.PlanDAO;
import com.isp.model.Plan;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PlansManagementPanel extends JPanel {
    private JTable plansTable;
    private DefaultTableModel tableModel;
    private final PlanDAO planDAO;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private com.isp.ui.views.AdminDashboard parentDashboard;

    public PlansManagementPanel() {
        this(null);
    }

    public PlansManagementPanel(com.isp.ui.views.AdminDashboard parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.planDAO = new PlanDAO();
        initializeUI();
        loadPlansData();
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
        mainPanel.add(createTitleHeaderPanel("Plans Management"));

        // Header panel (search and filters)
        mainPanel.add(createHeaderPanel());

        add(mainPanel, BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Internet Plans Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        // Search and filter panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(UIConstants.NORMAL_FONT);
        statusLabel.setForeground(Color.WHITE);
        
        statusFilter = new JComboBox<>(new String[]{"All Plans", "active", "inactive"});
        statusFilter.addActionListener(e -> filterByStatus());

        // Search
        JLabel searchLabel = new JLabel("Search:");
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
            loadPlansData();
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
        String[] columnNames = {"Plan ID", "Plan Name", "Speed", "Data Limit", "Price", "Validity (Days)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        plansTable = new JTable(tableModel);
        plansTable.setFont(UIConstants.NORMAL_FONT);
        plansTable.setRowHeight(30);
        plansTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        plansTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        plansTable.getTableHeader().setForeground(Color.WHITE);
        plansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(plansTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton addButton = new ModernButton("Add Plan");
        addButton.addActionListener(e -> showAddPlanDialog());

        ModernButton editButton = new ModernButton("Edit Plan");
        editButton.addActionListener(e -> showEditPlanDialog());

        ModernButton deleteButton = new ModernButton("Delete Plan");
        deleteButton.addActionListener(e -> deletePlan());

        ModernButton toggleStatusButton = new ModernButton("Toggle Status");
        toggleStatusButton.addActionListener(e -> togglePlanStatus());

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> loadPlansData());

        footerPanel.add(addButton);
        footerPanel.add(editButton);
        footerPanel.add(deleteButton);
        footerPanel.add(toggleStatusButton);
        footerPanel.add(refreshButton);

        return footerPanel;
    }

    private void loadPlansData() {
        tableModel.setRowCount(0);
        List<Plan> plans = planDAO.getAllPlans();

        for (Plan plan : plans) {
            Object[] rowData = {
                plan.getPlanId(),
                plan.getPlanName(),
                plan.getVoiceBenefits(),
                plan.getDataDescription(),
                plan.getFormattedPrice(),
                plan.getValidityDescription(),
                plan.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void filterByStatus() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        if ("All Plans".equals(selectedStatus)) {
            loadPlansData();
            return;
        }

        tableModel.setRowCount(0);
        List<Plan> allPlans = planDAO.getAllPlans();

        for (Plan plan : allPlans) {
            if (plan.getStatus().equals(selectedStatus)) {
                Object[] rowData = {
                    plan.getPlanId(),
                    plan.getPlanName(),
                    plan.getVoiceBenefits(),
                    plan.getDataDescription(),
                    plan.getFormattedPrice(),
                    plan.getValidityDescription(),
                    plan.getStatus()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadPlansData();
            return;
        }

        tableModel.setRowCount(0);
        List<Plan> allPlans = planDAO.getAllPlans();

        for (Plan plan : allPlans) {
            // Search in plan name, voice benefits, or data description
            if (plan.getPlanName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                (plan.getVoiceBenefits() != null && plan.getVoiceBenefits().toLowerCase().contains(searchTerm.toLowerCase())) ||
                plan.getDataDescription().toLowerCase().contains(searchTerm.toLowerCase())) {
                
                Object[] rowData = {
                    plan.getPlanId(),
                    plan.getPlanName(),
                    plan.getVoiceBenefits(),
                    plan.getDataDescription(),
                    plan.getFormattedPrice(),
                    plan.getValidityDescription(),
                    plan.getStatus()
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void showAddPlanDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Internet Plan", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 8, 6, 8);

        // Form fields with smaller fonts
        JTextField planNameField = new ModernTextField();
        planNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JTextField voiceBenefitsField = new ModernTextField();
        voiceBenefitsField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JTextField dataPerDayField = new ModernTextField();
        dataPerDayField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JTextField totalDataField = new ModernTextField();
        totalDataField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JTextField priceField = new ModernTextField();
        priceField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JSpinner validitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        validitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JTextArea descriptionArea = new JTextArea(2, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"active", "inactive"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Add form fields with labels
        addFormFieldCompact(formPanel, gbc, "Plan Name:", planNameField);
        addFormFieldCompact(formPanel, gbc, "Voice Benefits:", voiceBenefitsField);
        
        JLabel voiceHint = new JLabel("(e.g., Unlimited calling, 100 SMS/day)");
        voiceHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        voiceHint.setForeground(Color.GRAY);
        gbc.gridx = 1;
        formPanel.add(voiceHint, gbc);
        gbc.gridy++;
        
        addFormFieldCompact(formPanel, gbc, "Data Per Day (GB):", dataPerDayField);
        
        JLabel dataPerDayHint = new JLabel("(e.g., 1.5, 2.0, or 0 for total data plans)");
        dataPerDayHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dataPerDayHint.setForeground(Color.GRAY);
        gbc.gridx = 1;
        formPanel.add(dataPerDayHint, gbc);
        gbc.gridy++;
        
        addFormFieldCompact(formPanel, gbc, "Total Data (GB):", totalDataField);
        
        JLabel totalDataHint = new JLabel("(e.g., 56, 84, or 0 for unlimited)");
        totalDataHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        totalDataHint.setForeground(Color.GRAY);
        gbc.gridx = 1;
        formPanel.add(totalDataHint, gbc);
        gbc.gridy++;
        
        addFormFieldCompact(formPanel, gbc, "Price (₹):", priceField);
        addFormFieldCompact(formPanel, gbc, "Validity (Days):", validitySpinner);
        addFormFieldCompact(formPanel, gbc, "Description:", new JScrollPane(descriptionArea));
        addFormFieldCompact(formPanel, gbc, "Status:", statusCombo);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton saveButton = new ModernButton("Save");
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (planNameField.getText().trim().isEmpty() || 
                priceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Validate price is a valid number
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Price must be greater than 0!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal dataPerDay = BigDecimal.ZERO;
                if (!dataPerDayField.getText().trim().isEmpty()) {
                    dataPerDay = new BigDecimal(dataPerDayField.getText().trim());
                }

                BigDecimal totalData = BigDecimal.ZERO;
                if (!totalDataField.getText().trim().isEmpty()) {
                    totalData = new BigDecimal(totalDataField.getText().trim());
                }

                // Create plan
                Plan plan = new Plan();
                plan.setPlanName(planNameField.getText().trim());
                plan.setVoiceBenefits(voiceBenefitsField.getText().trim());
                plan.setDataPerDayGb(dataPerDay);
                plan.setTotalDataGb(totalData);
                plan.setPriceInr(price);
                plan.setValidityDays((Integer) validitySpinner.getValue());
                plan.setDescription(descriptionArea.getText().trim());
                plan.setStatus((String) statusCombo.getSelectedItem());
                plan.setPlanType("unlimited");
                plan.setAddon(false);

                int planId = planDAO.createPlan(plan);
                if (planId > 0) {
                    JOptionPane.showMessageDialog(dialog, "Plan added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPlansData();
                    
                    // Refresh dashboard if available
                    if (parentDashboard != null) {
                        parentDashboard.refreshDashboard();
                    }
                    
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add plan!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for price and data fields!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditPlanDialog() {
        int selectedRow = plansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a plan to edit!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int planId = (int) tableModel.getValueAt(selectedRow, 0);  // Column 0 is Plan ID
        Plan plan = planDAO.getPlanById(planId);

        if (plan == null) {
            JOptionPane.showMessageDialog(this, "Plan not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Internet Plan", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Form fields with existing data
        JTextField planNameField = new ModernTextField();
        planNameField.setText(plan.getPlanName());
        
        JTextField voiceBenefitsField = new ModernTextField();
        voiceBenefitsField.setText(plan.getVoiceBenefits() != null ? plan.getVoiceBenefits() : "");
        
        JTextField dataPerDayField = new ModernTextField();
        dataPerDayField.setText(plan.getDataPerDayGb() != null ? plan.getDataPerDayGb().toString() : "0");
        
        JTextField totalDataField = new ModernTextField();
        totalDataField.setText(plan.getTotalDataGb() != null ? plan.getTotalDataGb().toString() : "0");
        
        JTextField priceField = new ModernTextField();
        priceField.setText(plan.getPriceInr().toString());
        
        JSpinner validitySpinner = new JSpinner(new SpinnerNumberModel(plan.getValidityDays(), 1, 365, 1));
        
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setText(plan.getDescription());
        descriptionArea.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"active", "inactive"});
        statusCombo.setSelectedItem(plan.getStatus());

        // Add form fields
        addFormField(formPanel, gbc, "Plan Name:", planNameField);
        addFormField(formPanel, gbc, "Voice Benefits:", voiceBenefitsField);
        addFormField(formPanel, gbc, "Data Per Day (GB):", dataPerDayField);
        addFormField(formPanel, gbc, "Total Data (GB):", totalDataField);
        addFormField(formPanel, gbc, "Price (₹):", priceField);
        addFormField(formPanel, gbc, "Validity (Days):", validitySpinner);
        addFormField(formPanel, gbc, "Description:", new JScrollPane(descriptionArea));
        addFormField(formPanel, gbc, "Status:", statusCombo);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton saveButton = new ModernButton("Save");
        saveButton.addActionListener(e -> {
            try {
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Price must be greater than 0!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal dataPerDay = BigDecimal.ZERO;
                if (!dataPerDayField.getText().trim().isEmpty()) {
                    dataPerDay = new BigDecimal(dataPerDayField.getText().trim());
                }

                BigDecimal totalData = BigDecimal.ZERO;
                if (!totalDataField.getText().trim().isEmpty()) {
                    totalData = new BigDecimal(totalDataField.getText().trim());
                }

                plan.setPlanName(planNameField.getText().trim());
                plan.setVoiceBenefits(voiceBenefitsField.getText().trim());
                plan.setDataPerDayGb(dataPerDay);
                plan.setTotalDataGb(totalData);
                plan.setPriceInr(price);
                plan.setValidityDays((Integer) validitySpinner.getValue());
                plan.setDescription(descriptionArea.getText().trim());
                plan.setStatus((String) statusCombo.getSelectedItem());
                plan.setPlanType(plan.getPlanType()); // Preserve existing plan type

                if (planDAO.updatePlan(plan)) {
                    JOptionPane.showMessageDialog(dialog, "Plan updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPlansData();
                    
                    // Refresh dashboard if available
                    if (parentDashboard != null) {
                        parentDashboard.refreshDashboard();
                    }
                    
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update plan!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deletePlan() {
        int selectedRow = plansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a plan to delete!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int planId = (int) tableModel.getValueAt(selectedRow, 0);  // Column 0 is Plan ID
        String planName = (String) tableModel.getValueAt(selectedRow, 1);  // Column 1 is Plan Name

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete plan: " + planName + "?\n\n" +
            "Warning: This may affect customers using this plan!", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (planDAO.deletePlan(planId)) {
                JOptionPane.showMessageDialog(this, "Plan deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPlansData();
                
                // Refresh dashboard if available
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete plan!\n" +
                    "Plan may be in use by customers.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void togglePlanStatus() {
        int selectedRow = plansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a plan to toggle status!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int planId = (int) tableModel.getValueAt(selectedRow, 0);  // Column 0 is Plan ID
        Plan plan = planDAO.getPlanById(planId);

        if (plan == null) {
            JOptionPane.showMessageDialog(this, "Plan not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Toggle status
        String newStatus = "active".equals(plan.getStatus()) ? "inactive" : "active";
        plan.setStatus(newStatus);

        if (planDAO.updatePlan(plan)) {
            JOptionPane.showMessageDialog(this, 
                "Plan status changed to: " + newStatus, 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadPlansData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update plan status!", 
                "Error", JOptionPane.ERROR_MESSAGE);
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
    
    private JPanel createTitleHeaderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }
    
    private void addFormFieldCompact(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
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
