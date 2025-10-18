package com.isp.ui.views.admin;

import com.isp.dao.CustomerDAO;
import com.isp.dao.PlanDAO;
import com.isp.dao.UserDAO;
import com.isp.model.Customer;
import com.isp.model.Plan;
import com.isp.model.User;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;
import com.isp.ui.views.AdminDashboard;
import com.isp.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CustomerManagementPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private final CustomerDAO customerDAO;
    private final UserDAO userDAO;
    private final PlanDAO planDAO;
    private JTextField searchField;
    private AdminDashboard parentDashboard;

    public CustomerManagementPanel() {
        this(null);
    }
    
    public CustomerManagementPanel(AdminDashboard parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.customerDAO = new CustomerDAO();
        this.userDAO = new UserDAO();
        this.planDAO = new PlanDAO();
        initializeUI();
        loadCustomerData();
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
        mainPanel.add(createTitleHeaderPanel("Customer Management"));

        // Header panel (search and filters)
        mainPanel.add(createHeaderPanel());

        add(mainPanel, BorderLayout.NORTH);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Footer with action buttons
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.NORMAL_FONT);
        searchLabel.setForeground(Color.WHITE);
        searchField = new ModernTextField();
        searchField.setPreferredSize(new Dimension(200, 35));

        JButton searchButton = new ModernButton("Search");
        searchButton.addActionListener(e -> performSearch());

        JButton resetButton = new ModernButton("Reset");
        resetButton.addActionListener(e -> {
            searchField.setText("");
            loadCustomerData();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Address", "Plan", "Status", "Registration Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setFont(UIConstants.NORMAL_FONT);
        customerTable.setRowHeight(30);
        customerTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        customerTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        customerTable.getTableHeader().setForeground(Color.WHITE);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton addButton = new ModernButton("Add Customer");
        addButton.addActionListener(e -> showAddCustomerDialog());

        ModernButton editButton = new ModernButton("Edit Customer");
        editButton.addActionListener(e -> showEditCustomerDialog());

        ModernButton deleteButton = new ModernButton("Delete Customer");
        deleteButton.addActionListener(e -> deleteCustomer());

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> loadCustomerData());

        footerPanel.add(addButton);
        footerPanel.add(editButton);
        footerPanel.add(deleteButton);
        footerPanel.add(refreshButton);

        return footerPanel;
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.getAllCustomers();

        for (Customer customer : customers) {
            String planName = "No Plan";
            if (customer.getPlanId() != null) {
                Plan plan = planDAO.getPlanById(customer.getPlanId());
                if (plan != null) {
                    planName = plan.getPlanName();
                }
            }

            Object[] rowData = {
                customer.getCustomerId(),
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                planName,
                customer.getConnectionStatus(),
                customer.getRegistrationDate()
            };
            tableModel.addRow(rowData);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadCustomerData();
            return;
        }

        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.searchCustomers(searchTerm);

        for (Customer customer : customers) {
            String planName = "No Plan";
            if (customer.getPlanId() != null) {
                Plan plan = planDAO.getPlanById(customer.getPlanId());
                if (plan != null) {
                    planName = plan.getPlanName();
                }
            }

            Object[] rowData = {
                customer.getCustomerId(),
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                planName,
                customer.getConnectionStatus(),
                customer.getRegistrationDate()
            };
            tableModel.addRow(rowData);
        }
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Customer", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Form fields
        JTextField usernameField = new ModernTextField();
        JPasswordField passwordField = new JPasswordField(20);
        JTextField firstNameField = new ModernTextField();
        JTextField lastNameField = new ModernTextField();
        JTextField emailField = new ModernTextField();
        JTextField phoneField = new ModernTextField();
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        // Plan combo box
        JComboBox<String> planCombo = new JComboBox<>();
        planCombo.addItem("No Plan");
        List<Plan> plans = planDAO.getAllPlans();
        for (Plan plan : plans) {
            planCombo.addItem(plan.getPlanName());
        }

        // Status combo box
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"active", "suspended", "terminated"});

        // Add form fields
        addFormField(formPanel, gbc, "Username:", usernameField);
        addFormField(formPanel, gbc, "Password:", passwordField);
        addFormField(formPanel, gbc, "First Name:", firstNameField);
        addFormField(formPanel, gbc, "Last Name:", lastNameField);
        addFormField(formPanel, gbc, "Email:", emailField);
        addFormField(formPanel, gbc, "Phone:", phoneField);
        addFormField(formPanel, gbc, "Address:", new JScrollPane(addressArea));
        addFormField(formPanel, gbc, "Plan:", planCombo);
        addFormField(formPanel, gbc, "Status:", statusCombo);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton saveButton = new ModernButton("Save");
        saveButton.addActionListener(e -> {
            // Validate inputs
            if (usernameField.getText().trim().isEmpty() || 
                passwordField.getPassword().length == 0 ||
                firstNameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create user first
            User user = new User();
            user.setUsername(usernameField.getText().trim());
            user.setPassword(PasswordUtil.hashPassword(new String(passwordField.getPassword())));
            user.setPhone(phoneField.getText().trim());  // FIX: Set phone number for user account
            user.setRole("customer");
            user.setStatus("active");

            int userId = userDAO.createUser(user);
            if (userId > 0) {
                // Create customer
                Customer customer = new Customer();
                customer.setUserId(userId);
                customer.setFirstName(firstNameField.getText().trim());
                customer.setLastName(lastNameField.getText().trim());
                customer.setEmail(emailField.getText().trim());
                customer.setPhone(phoneField.getText().trim());
                customer.setAddress(addressArea.getText().trim());
                customer.setConnectionStatus((String) statusCombo.getSelectedItem());
                customer.setRegistrationDate(LocalDate.now());

                // Set plan ID
                int planIndex = planCombo.getSelectedIndex();
                if (planIndex > 0) {
                    customer.setPlanId(plans.get(planIndex - 1).getPlanId());
                }

                int customerId = customerDAO.createCustomer(customer);
                if (customerId > 0) {
                    JOptionPane.showMessageDialog(dialog, "Customer added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomerData();
                    
                    // Refresh dashboard if available
                    if (parentDashboard != null) {
                        parentDashboard.refreshDashboard();
                    }
                    
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add customer!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to create user account!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
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

    private void showEditCustomerDialog() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        Customer customer = customerDAO.getCustomerById(customerId);

        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Customer", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Form fields
        JTextField firstNameField = new ModernTextField();
        firstNameField.setText(customer.getFirstName());
        
        JTextField lastNameField = new ModernTextField();
        lastNameField.setText(customer.getLastName());
        
        JTextField emailField = new ModernTextField();
        emailField.setText(customer.getEmail());
        
        JTextField phoneField = new ModernTextField();
        phoneField.setText(customer.getPhone());
        
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setText(customer.getAddress());
        addressArea.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        // Plan combo box
        JComboBox<String> planCombo = new JComboBox<>();
        planCombo.addItem("No Plan");
        List<Plan> plans = planDAO.getAllPlans();
        int selectedPlanIndex = 0;
        for (int i = 0; i < plans.size(); i++) {
            Plan plan = plans.get(i);
            planCombo.addItem(plan.getPlanName());
            if (customer.getPlanId() != null && customer.getPlanId() == plan.getPlanId()) {
                selectedPlanIndex = i + 1;
            }
        }
        planCombo.setSelectedIndex(selectedPlanIndex);

        // Status combo box
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"active", "suspended", "terminated"});
        statusCombo.setSelectedItem(customer.getConnectionStatus());

        // Add form fields
        addFormField(formPanel, gbc, "First Name:", firstNameField);
        addFormField(formPanel, gbc, "Last Name:", lastNameField);
        addFormField(formPanel, gbc, "Email:", emailField);
        addFormField(formPanel, gbc, "Phone:", phoneField);
        addFormField(formPanel, gbc, "Address:", new JScrollPane(addressArea));
        addFormField(formPanel, gbc, "Plan:", planCombo);
        addFormField(formPanel, gbc, "Status:", statusCombo);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton saveButton = new ModernButton("Save");
        saveButton.addActionListener(e -> {
            customer.setFirstName(firstNameField.getText().trim());
            customer.setLastName(lastNameField.getText().trim());
            customer.setEmail(emailField.getText().trim());
            customer.setPhone(phoneField.getText().trim());
            customer.setAddress(addressArea.getText().trim());
            customer.setConnectionStatus((String) statusCombo.getSelectedItem());

            // Set plan ID
            int planIndex = planCombo.getSelectedIndex();
            if (planIndex > 0) {
                customer.setPlanId(plans.get(planIndex - 1).getPlanId());
            } else {
                customer.setPlanId(null);
            }

            if (customerDAO.updateCustomer(customer)) {
                JOptionPane.showMessageDialog(dialog, "Customer updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData();
                
                // Refresh dashboard if available
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
                
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update customer!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
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

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        String customerName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete customer: " + customerName + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (customerDAO.deleteCustomer(customerId)) {
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData();
                
                // Refresh dashboard if available
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete customer!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
