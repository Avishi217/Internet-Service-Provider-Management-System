package com.isp.ui.views.admin;

import com.isp.dao.SimpleEmployeeDAO;
import com.isp.model.SimpleEmployee;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class SimpleEmployeeManagementPanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private final SimpleEmployeeDAO employeeDAO;

    public SimpleEmployeeManagementPanel() {
        this.employeeDAO = new SimpleEmployeeDAO();
        initializeUI();
        loadEmployeeData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Employee Management");
        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Create table model
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Department", "Salary"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setBackground(UIConstants.SECONDARY_BACKGROUND);
        employeeTable.setForeground(UIConstants.TEXT_PRIMARY);
        employeeTable.setFont(UIConstants.FONT_BODY);
        employeeTable.setRowHeight(30);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setBackground(UIConstants.CARD_BACKGROUND);
        employeeTable.getTableHeader().setForeground(UIConstants.TEXT_PRIMARY);
        employeeTable.getTableHeader().setFont(UIConstants.FONT_BUTTON);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton addButton = new ModernButton("Add Employee");
        addButton.addActionListener(e -> showAddEmployeeDialog());

        ModernButton viewButton = new ModernButton("View Employees");
        viewButton.addActionListener(e -> loadEmployeeData());

        ModernButton deleteButton = new ModernButton("Delete Employee");
        deleteButton.addActionListener(e -> deleteSelectedEmployee());

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Employee", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(UIConstants.SECONDARY_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Form fields
        ModernTextField nameField = new ModernTextField(20);
        ModernTextField emailField = new ModernTextField(20);
        ModernTextField phoneField = new ModernTextField(20);
        ModernTextField departmentField = new ModernTextField(20);
        ModernTextField salaryField = new ModernTextField(20);

        contentPanel.add(new JLabel("Name:"), gbc);
        contentPanel.add(nameField, gbc);

        contentPanel.add(new JLabel("Email:"), gbc);
        contentPanel.add(emailField, gbc);

        contentPanel.add(new JLabel("Phone:"), gbc);
        contentPanel.add(phoneField, gbc);

        contentPanel.add(new JLabel("Department:"), gbc);
        contentPanel.add(departmentField, gbc);

        contentPanel.add(new JLabel("Salary:"), gbc);
        contentPanel.add(salaryField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.SECONDARY_BACKGROUND);

        ModernButton saveButton = new ModernButton("Save");
        saveButton.addActionListener(e -> {
            if (saveEmployee(nameField.getText(), emailField.getText(),
                           phoneField.getText(), departmentField.getText(),
                           salaryField.getText())) {
                dialog.dispose();
                loadEmployeeData();
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean saveEmployee(String name, String email, String phone, String department, String salaryStr) {
        // Validation
        if (name.trim().isEmpty() || email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (employeeDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            BigDecimal salary = new BigDecimal(salaryStr);
            SimpleEmployee employee = new SimpleEmployee(name, email, phone, department, salary);

            if (employeeDAO.addEmployee(employee)) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add employee!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid salary format!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0); // Clear existing data

        List<SimpleEmployee> employees = employeeDAO.getAllEmployees();
        for (SimpleEmployee employee : employees) {
            Object[] row = {
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getSalary()
            };
            tableModel.addRow(row);
        }
    }

    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String employeeName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete employee: " + employeeName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (employeeDAO.deleteEmployee(employeeId)) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}