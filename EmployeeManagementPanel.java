package com.isp.ui.views.admin;

import com.isp.dao.EmployeeDAO;
import com.isp.dao.UserDAO;
import com.isp.model.Employee;
import com.isp.model.User;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;
import com.isp.ui.views.AdminDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private final EmployeeDAO employeeDAO;
    private final UserDAO userDAO;
    private JTextField searchField;
    private JComboBox<String> departmentFilter;
    private AdminDashboard parentDashboard;

    public EmployeeManagementPanel() {
        this(null);
    }
    
    public EmployeeManagementPanel(AdminDashboard parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.employeeDAO = new EmployeeDAO();
        this.userDAO = new UserDAO();
        initializeUI();
        loadEmployeeData();
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

        JLabel titleLabel = new JLabel("Employee Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);

        // Search and filter panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Department filter
        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(UIConstants.NORMAL_FONT);
        
        departmentFilter = new JComboBox<>(new String[]{
            "All Departments", "Technical Support", "Customer Service", 
            "Sales", "Finance", "Administration"
        });
        departmentFilter.addActionListener(e -> filterByDepartment());

        // Search
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.NORMAL_FONT);
        
        searchField = new ModernTextField();
        searchField.setPreferredSize(new Dimension(200, 35));

        ModernButton searchButton = new ModernButton("Search");
        searchButton.addActionListener(e -> performSearch());

        ModernButton resetButton = new ModernButton("Reset");
        resetButton.addActionListener(e -> {
            searchField.setText("");
            departmentFilter.setSelectedIndex(0);
            loadEmployeeData();
        });

        controlPanel.add(deptLabel);
        controlPanel.add(departmentFilter);
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
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Department", "Hire Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setFont(UIConstants.NORMAL_FONT);
        employeeTable.setRowHeight(30);
        employeeTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        employeeTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        employeeTable.getTableHeader().setForeground(Color.WHITE);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton addButton = new ModernButton("Add Employee");
        addButton.addActionListener(e -> showAddEmployeeDialog());

        ModernButton editButton = new ModernButton("Edit Employee");
        editButton.addActionListener(e -> showEditEmployeeDialog());

        ModernButton deleteButton = new ModernButton("Delete Employee");
        deleteButton.addActionListener(e -> deleteEmployee());

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> loadEmployeeData());

        footerPanel.add(addButton);
        footerPanel.add(editButton);
        footerPanel.add(deleteButton);
        footerPanel.add(refreshButton);

        return footerPanel;
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAllEmployees();

        for (Employee employee : employees) {
            Object[] rowData = {
                employee.getEmployeeId(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getHireDate(),
                employee.getEmploymentStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void filterByDepartment() {
        String selectedDept = (String) departmentFilter.getSelectedItem();
        
        if ("All Departments".equals(selectedDept)) {
            loadEmployeeData();
            return;
        }

        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getEmployeesByDepartment(selectedDept);

        for (Employee employee : employees) {
            Object[] rowData = {
                employee.getEmployeeId(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getHireDate(),
                employee.getEmploymentStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadEmployeeData();
            return;
        }

        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.searchEmployees(searchTerm);

        for (Employee employee : employees) {
            Object[] rowData = {
                employee.getEmployeeId(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getHireDate(),
                employee.getEmploymentStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Employee", true);
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
        JTextField usernameField = new ModernTextField();
        JPasswordField passwordField = new JPasswordField(20);
        JTextField firstNameField = new ModernTextField();
        JTextField lastNameField = new ModernTextField();
        JTextField emailField = new ModernTextField();
        JTextField phoneField = new ModernTextField();
        
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{
            "Technical Support", "Customer Service", "Sales", "Finance", "Administration"
        });
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "active", "on_leave", "terminated"
        });

        // Add form fields
        addFormField(formPanel, gbc, "Username:", usernameField);
        addFormField(formPanel, gbc, "Password:", passwordField);
        addFormField(formPanel, gbc, "First Name:", firstNameField);
        addFormField(formPanel, gbc, "Last Name:", lastNameField);
        addFormField(formPanel, gbc, "Email:", emailField);
        addFormField(formPanel, gbc, "Phone:", phoneField);
        addFormField(formPanel, gbc, "Department:", deptCombo);
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
            user.setPassword(new String(passwordField.getPassword())); // Don't hash here, UserDAO will handle it
            user.setPhone(phoneField.getText().trim()); // Set phone number
            user.setRole("employee");
            user.setStatus("active");

            int userId = userDAO.createUser(user);
            if (userId > 0) {
                // Create employee
                Employee employee = new Employee();
                employee.setUserId(userId);
                employee.setFirstName(firstNameField.getText().trim());
                employee.setLastName(lastNameField.getText().trim());
                employee.setEmail(emailField.getText().trim());
                employee.setPhone(phoneField.getText().trim());
                employee.setDepartment((String) deptCombo.getSelectedItem());
                employee.setHireDate(LocalDate.now());
                employee.setEmploymentStatus((String) statusCombo.getSelectedItem());

                int employeeId = employeeDAO.createEmployee(employee);
                if (employeeId > 0) {
                    JOptionPane.showMessageDialog(dialog, "Employee added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEmployeeData();
                    
                    // Refresh dashboard if available
                    if (parentDashboard != null) {
                        parentDashboard.refreshDashboard();
                    }
                    
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add employee!", 
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

    private void showEditEmployeeDialog() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        Employee employee = employeeDAO.getEmployeeById(employeeId);

        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Employee not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Employee", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 500);
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
        firstNameField.setText(employee.getFirstName());
        
        JTextField lastNameField = new ModernTextField();
        lastNameField.setText(employee.getLastName());
        
        JTextField emailField = new ModernTextField();
        emailField.setText(employee.getEmail());
        
        JTextField phoneField = new ModernTextField();
        phoneField.setText(employee.getPhone());
        
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{
            "Technical Support", "Customer Service", "Sales", "Finance", "Administration"
        });
        deptCombo.setSelectedItem(employee.getDepartment());
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "active", "on_leave", "terminated"
        });
        statusCombo.setSelectedItem(employee.getEmploymentStatus());

        // Add form fields
        addFormField(formPanel, gbc, "First Name:", firstNameField);
        addFormField(formPanel, gbc, "Last Name:", lastNameField);
        addFormField(formPanel, gbc, "Email:", emailField);
        addFormField(formPanel, gbc, "Phone:", phoneField);
        addFormField(formPanel, gbc, "Department:", deptCombo);
        addFormField(formPanel, gbc, "Status:", statusCombo);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton saveButton = new ModernButton("Save");
        saveButton.addActionListener(e -> {
            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setEmail(emailField.getText().trim());
            employee.setPhone(phoneField.getText().trim());
            employee.setDepartment((String) deptCombo.getSelectedItem());
            employee.setEmploymentStatus((String) statusCombo.getSelectedItem());

            if (employeeDAO.updateEmployee(employee)) {
                JOptionPane.showMessageDialog(dialog, "Employee updated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeData();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update employee!", 
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

    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete employee: " + employeeName + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (employeeDAO.deleteEmployee(employeeId)) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeData();
                
                // Refresh dashboard if available
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee!", 
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
