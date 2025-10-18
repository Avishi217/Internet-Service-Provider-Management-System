package com.isp.ui.views.admin;

import com.isp.dao.ComplaintDAO;
import com.isp.dao.CustomerDAO;
import com.isp.dao.EmployeeDAO;
import com.isp.model.Complaint;
import com.isp.model.Customer;
import com.isp.model.Employee;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ComplaintsManagementPanel extends JPanel {
    private JTable complaintsTable;
    private DefaultTableModel tableModel;
    private final ComplaintDAO complaintDAO;
    private final CustomerDAO customerDAO;
    private final EmployeeDAO employeeDAO;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private com.isp.ui.views.AdminDashboard parentDashboard;

    public ComplaintsManagementPanel() {
        this(null);
    }

    public ComplaintsManagementPanel(com.isp.ui.views.AdminDashboard parentDashboard) {
        this.parentDashboard = parentDashboard;
        this.complaintDAO = new ComplaintDAO();
        this.customerDAO = new CustomerDAO();
        this.employeeDAO = new EmployeeDAO();
        initializeUI();
        loadComplaintsData();
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
        mainPanel.add(createTitleHeaderPanel("Complaints Management"));

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

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Status filter
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(UIConstants.NORMAL_FONT);
        statusLabel.setForeground(Color.WHITE);
        
        statusFilter = new JComboBox<>(new String[]{"All Complaints", "open", "in_progress", "resolved"});
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
            loadComplaintsData();
        });

        controlPanel.add(statusLabel);
        controlPanel.add(statusFilter);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(resetButton);

        headerPanel.add(controlPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Customer", "Subject", "Status", "Priority", "Assigned To", "Created", "Resolved"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        complaintsTable = new JTable(tableModel);
        complaintsTable.setFont(UIConstants.NORMAL_FONT);
        complaintsTable.setRowHeight(30);
        complaintsTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        complaintsTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        complaintsTable.getTableHeader().setForeground(Color.WHITE);
        complaintsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton addButton = new ModernButton("Add Complaint");
        addButton.addActionListener(e -> showAddComplaintDialog());

        // REMOVED: Assign to Employee button - employee management removed from system

        ModernButton updateStatusButton = new ModernButton("Update Status");
        updateStatusButton.addActionListener(e -> updateComplaintStatus());

        ModernButton resolveButton = new ModernButton("Mark as Resolved");
        resolveButton.addActionListener(e -> resolveComplaint());

        ModernButton viewDetailsButton = new ModernButton("View Details");
        viewDetailsButton.addActionListener(e -> viewComplaintDetails());

        ModernButton deleteButton = new ModernButton("Delete");
        deleteButton.addActionListener(e -> deleteComplaint());

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> loadComplaintsData());

        footerPanel.add(addButton);
        // footerPanel.add(assignButton);  // REMOVED
        footerPanel.add(updateStatusButton);
        footerPanel.add(resolveButton);
        footerPanel.add(viewDetailsButton);
        footerPanel.add(deleteButton);
        footerPanel.add(refreshButton);

        return footerPanel;
    }

    private void loadComplaintsData() {
        tableModel.setRowCount(0);
        List<Complaint> complaints = complaintDAO.getAllComplaints();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Complaint complaint : complaints) {
            Customer customer = customerDAO.getCustomerById(complaint.getCustomerId());
            String customerName = customer != null ? 
                customer.getFirstName() + " " + customer.getLastName() : "Unknown";

            String assignedTo = "-";
            if (complaint.getAssignedEmployeeId() != null) {
                Employee employee = employeeDAO.getEmployeeById(complaint.getAssignedEmployeeId());
                if (employee != null) {
                    assignedTo = employee.getFirstName() + " " + employee.getLastName();
                }
            }

            String resolvedDate = complaint.getResolvedDate() != null ? 
                complaint.getResolvedDate().format(dateFormatter) : "-";

            Object[] rowData = {
                complaint.getComplaintId(),
                customerName,
                complaint.getSubject(),
                complaint.getStatus(),
                complaint.getPriority(),
                assignedTo,
                complaint.getCreatedDate().format(dateFormatter),
                resolvedDate
            };
            tableModel.addRow(rowData);
        }
    }

    private void filterByStatus() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        if ("All Complaints".equals(selectedStatus)) {
            loadComplaintsData();
            return;
        }

        tableModel.setRowCount(0);
        List<Complaint> complaints = complaintDAO.getComplaintsByStatus(selectedStatus);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Complaint complaint : complaints) {
            Customer customer = customerDAO.getCustomerById(complaint.getCustomerId());
            String customerName = customer != null ? 
                customer.getFirstName() + " " + customer.getLastName() : "Unknown";

            String assignedTo = "-";
            if (complaint.getAssignedEmployeeId() != null) {
                Employee employee = employeeDAO.getEmployeeById(complaint.getAssignedEmployeeId());
                if (employee != null) {
                    assignedTo = employee.getFirstName() + " " + employee.getLastName();
                }
            }

            String resolvedDate = complaint.getResolvedDate() != null ? 
                complaint.getResolvedDate().format(dateFormatter) : "-";

            Object[] rowData = {
                complaint.getComplaintId(),
                customerName,
                complaint.getSubject(),
                complaint.getStatus(),
                complaint.getPriority(),
                assignedTo,
                complaint.getCreatedDate().format(dateFormatter),
                resolvedDate
            };
            tableModel.addRow(rowData);
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadComplaintsData();
            return;
        }

        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.searchCustomers(searchTerm);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Customer customer : customers) {
            List<Complaint> complaints = complaintDAO.getComplaintsByCustomerId(customer.getCustomerId());
            
            for (Complaint complaint : complaints) {
                String customerName = customer.getFirstName() + " " + customer.getLastName();

                String assignedTo = "-";
                if (complaint.getAssignedEmployeeId() != null) {
                    Employee employee = employeeDAO.getEmployeeById(complaint.getAssignedEmployeeId());
                    if (employee != null) {
                        assignedTo = employee.getFirstName() + " " + employee.getLastName();
                    }
                }

                String resolvedDate = complaint.getResolvedDate() != null ? 
                    complaint.getResolvedDate().format(dateFormatter) : "-";

                Object[] rowData = {
                    complaint.getComplaintId(),
                    customerName,
                    complaint.getSubject(),
                    complaint.getStatus(),
                    complaint.getPriority(),
                    assignedTo,
                    complaint.getCreatedDate().format(dateFormatter),
                    resolvedDate
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void showAddComplaintDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Complaint", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
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

        // Subject field
        JTextField subjectField = new ModernTextField();

        // Description text area
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UIConstants.NORMAL_FONT);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);

        // Priority combo
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"low", "medium", "high"});

        // Status combo
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"open", "in_progress", "resolved"});

        // Add form fields
        addFormField(formPanel, gbc, "Customer:", customerCombo);
        addFormField(formPanel, gbc, "Subject:", subjectField);
        addFormField(formPanel, gbc, "Description:", descScrollPane);
        addFormField(formPanel, gbc, "Priority:", priorityCombo);
        addFormField(formPanel, gbc, "Status:", statusCombo);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton addButton = new ModernButton("Add");
        addButton.addActionListener(e -> {
            if (customerCombo.getSelectedIndex() == -1 || subjectField.getText().trim().isEmpty() ||
                descriptionArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected customer ID
            String selectedCustomer = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selectedCustomer.split(" - ")[0]);

            // Create complaint
            Complaint complaint = new Complaint();
            complaint.setCustomerId(customerId);
            complaint.setSubject(subjectField.getText().trim());
            complaint.setDescription(descriptionArea.getText().trim());
            complaint.setPriority((String) priorityCombo.getSelectedItem());
            complaint.setStatus((String) statusCombo.getSelectedItem());
            complaint.setCreatedDate(LocalDateTime.now());

            int complaintId = complaintDAO.createComplaint(complaint);
            if (complaintId > 0) {
                JOptionPane.showMessageDialog(dialog, "Complaint registered successfully!\nComplaint ID: " + complaintId, 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadComplaintsData();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to register complaint!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // REMOVED: assignComplaint() method - employee management removed from system

    private void updateComplaintStatus() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to update!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 3);

        String[] statusOptions = {"open", "in_progress", "resolved"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Current Status: " + currentStatus + "\n\nSelect new status:",
            "Update Complaint Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            Complaint complaint = complaintDAO.getComplaintById(complaintId);
            if (complaint != null) {
                complaint.setStatus(newStatus);
                
                if (complaintDAO.updateComplaint(complaint)) {
                    JOptionPane.showMessageDialog(this, "Status updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadComplaintsData();
                    
                    // Refresh dashboard if available
                    if (parentDashboard != null) {
                        parentDashboard.refreshDashboard();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update status!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void resolveComplaint() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to resolve!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 3);

        if ("resolved".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This complaint is already resolved!", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to mark this complaint as resolved?",
            "Resolve Complaint",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (complaintDAO.resolveComplaint(complaintId)) {
                JOptionPane.showMessageDialog(this, "Complaint marked as resolved!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadComplaintsData();
                
                // Refresh dashboard if available
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to resolve complaint!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewComplaintDetails() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to view!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        Complaint complaint = complaintDAO.getComplaintById(complaintId);

        if (complaint == null) {
            JOptionPane.showMessageDialog(this, "Complaint not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = customerDAO.getCustomerById(complaint.getCustomerId());
        String customerName = customer != null ? 
            customer.getFirstName() + " " + customer.getLastName() : "Unknown";

        String assignedTo = "Not Assigned";
        if (complaint.getAssignedEmployeeId() != null) {
            Employee employee = employeeDAO.getEmployeeById(complaint.getAssignedEmployeeId());
            if (employee != null) {
                assignedTo = employee.getFirstName() + " " + employee.getLastName() + 
                    " (" + employee.getDepartment() + ")";
            }
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String details = String.format(
            "Complaint ID: %d\n\n" +
            "Customer: %s\n" +
            "Email: %s\n" +
            "Phone: %s\n\n" +
            "Subject: %s\n" +
            "Description:\n%s\n\n" +
            "Priority: %s\n" +
            "Status: %s\n" +
            "Assigned To: %s\n\n" +
            "Created: %s\n" +
            "Resolved: %s",
            complaint.getComplaintId(),
            customerName,
            customer != null ? customer.getEmail() : "N/A",
            customer != null ? customer.getPhone() : "N/A",
            complaint.getSubject(),
            complaint.getDescription(),
            complaint.getPriority() != null ? complaint.getPriority().toUpperCase() : "NOT SET",
            complaint.getStatus().toUpperCase(),
            assignedTo,
            complaint.getCreatedDate().format(dateFormatter),
            complaint.getResolvedDate() != null ? complaint.getResolvedDate().format(dateFormatter) : "Not Resolved"
        );

        JTextArea textArea = new JTextArea(details);
        textArea.setEditable(false);
        textArea.setFont(UIConstants.NORMAL_FONT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Complaint Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteComplaint() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to delete!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        String subject = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete complaint #" + complaintId + "?\nSubject: " + subject, 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (complaintDAO.deleteComplaint(complaintId)) {
                JOptionPane.showMessageDialog(this, "Complaint deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadComplaintsData();
                
                // Refresh dashboard if available
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete complaint!", 
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
