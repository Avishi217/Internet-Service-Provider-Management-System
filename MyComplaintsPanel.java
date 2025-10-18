package com.isp.ui.views.customer;

import com.isp.dao.ComplaintDAO;
import com.isp.dao.CustomerDAO;
import com.isp.model.Complaint;
import com.isp.model.Customer;
import com.isp.ui.components.ModernButton;
import com.isp.ui.components.ModernTextField;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MyComplaintsPanel extends JPanel {
    private final ComplaintDAO complaintDAO;
    private final CustomerDAO customerDAO;
    private final int userId;
    
    private JTable complaintsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;

    public MyComplaintsPanel(int userId) {
        this.userId = userId;
        this.complaintDAO = new ComplaintDAO();
        this.customerDAO = new CustomerDAO();
        
        initializeUI();
        loadComplaintsData();
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

        JLabel titleLabel = new JLabel("My Complaints");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(UIConstants.NORMAL_FONT);
        
        statusFilter = new JComboBox<>(new String[]{"All Complaints", "open", "in-progress", "resolved", "closed"});
        statusFilter.addActionListener(e -> filterByStatus());

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> {
            statusFilter.setSelectedIndex(0);
            loadComplaintsData();
        });

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Subject", "Priority", "Status", "Submitted Date", "Assigned To"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        complaintsTable = new JTable(tableModel);
        complaintsTable.setFont(UIConstants.NORMAL_FONT);
        complaintsTable.setRowHeight(35);
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

        ModernButton newComplaintButton = new ModernButton("New Complaint");
        newComplaintButton.addActionListener(e -> showNewComplaintDialog());

        ModernButton viewDetailsButton = new ModernButton("View Details");
        viewDetailsButton.addActionListener(e -> viewComplaintDetails());

        ModernButton downloadPDFButton = new ModernButton("Download PDF");
        downloadPDFButton.addActionListener(e -> downloadComplaintPDF());

        footerPanel.add(newComplaintButton);
        footerPanel.add(viewDetailsButton);
        footerPanel.add(downloadPDFButton);

        return footerPanel;
    }

    private void loadComplaintsData() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            return;
        }

        tableModel.setRowCount(0);
        List<Complaint> complaints = complaintDAO.getComplaintsByCustomerId(customer.getCustomerId());

        for (Complaint complaint : complaints) {
            String assignedTo = complaint.getAssignedEmployeeId() != null ? 
                "Employee #" + complaint.getAssignedEmployeeId() : "Unassigned";

            Object[] rowData = {
                complaint.getComplaintId(),
                complaint.getSubject(),
                complaint.getPriority(),
                complaint.getStatus(),
                complaint.getCreatedDate(),
                assignedTo
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

        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            return;
        }

        tableModel.setRowCount(0);
        List<Complaint> complaints = complaintDAO.getComplaintsByStatus(selectedStatus);

        for (Complaint complaint : complaints) {
            if (complaint.getCustomerId() == customer.getCustomerId()) {
                String assignedTo = complaint.getAssignedEmployeeId() != null ? 
                    "Employee #" + complaint.getAssignedEmployeeId() : "Unassigned";

                Object[] rowData = {
                    complaint.getComplaintId(),
                    complaint.getSubject(),
                    complaint.getPriority(),
                    complaint.getStatus(),
                    complaint.getCreatedDate(),
                    assignedTo
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private void showNewComplaintDialog() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer profile not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Submit New Complaint", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        ModernTextField subjectField = new ModernTextField();
        
        String[] priorities = {"low", "medium", "high", "critical"};
        JComboBox<String> priorityCombo = new JComboBox<>(priorities);
        priorityCombo.setSelectedIndex(1);
        
        JTextArea descriptionArea = new JTextArea(8, 30);
        descriptionArea.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_COLOR));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UIConstants.NORMAL_FONT);

        addFormField(formPanel, gbc, "Subject:", subjectField);
        addFormField(formPanel, gbc, "Priority:", priorityCombo);
        
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(UIConstants.NORMAL_FONT);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(descLabel, gbc);
        gbc.gridy++;
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton submitButton = new ModernButton("Submit");
        submitButton.addActionListener(e -> {
            if (subjectField.getText().trim().isEmpty() || 
                descriptionArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in subject and description!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Complaint complaint = new Complaint();
            complaint.setCustomerId(customer.getCustomerId());
            complaint.setSubject(subjectField.getText().trim());
            complaint.setDescription(descriptionArea.getText().trim());
            complaint.setPriority((String) priorityCombo.getSelectedItem());
            complaint.setStatus("open");

            int complaintId = complaintDAO.createComplaint(complaint);
            if (complaintId > 0) {
                JOptionPane.showMessageDialog(dialog, 
                    "Complaint submitted successfully!\nComplaint ID: #" + complaintId + 
                    "\nOur support team will review your complaint soon.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadComplaintsData();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to submit complaint!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void viewComplaintDetails() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to view details!", 
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

        // Get customer info
        Customer customer = customerDAO.getCustomerByUserId(userId);
        String customerName = "Unknown";
        String customerPhone = "N/A";
        
        if (customer != null) {
            customerName = customer.getFirstName() + " " + customer.getLastName();
            customerPhone = customer.getPhone();
        }

        // Create comprehensive details (matching bill format)
        String details = String.format(
            "Complaint ID: %d\n" +
            "Ticket: %s\n\n" +
            "Customer: %s\n" +
            "Phone: %s\n\n" +
            "Subject: %s\n" +
            "Priority: %s\n" +
            "Status: %s\n\n" +
            "Description:\n%s\n\n" +
            "Submitted: %s\n" +
            "Assigned To: %s\n" +
            "Resolved: %s",
            complaint.getComplaintId(),
            complaint.getTicketNumber(),
            customerName,
            customerPhone,
            complaint.getSubject(),
            complaint.getPriority() != null ? complaint.getPriority().toUpperCase() : "NOT SET",
            complaint.getStatus() != null ? complaint.getStatus().toUpperCase() : "UNKNOWN",
            complaint.getDescription(),
            complaint.getCreatedDate().toString(),
            complaint.getAssignedEmployeeId() != null ? "Employee #" + complaint.getAssignedEmployeeId() : "Unassigned",
            complaint.getResolvedDate() != null ? complaint.getResolvedDate().toString() : "Pending"
        );

        JOptionPane.showMessageDialog(this, details, "Complaint Details - #" + complaint.getComplaintId(), 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.NORMAL_FONT);
        
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

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel keyLabel = new JLabel(label);
        keyLabel.setFont(UIConstants.NORMAL_FONT);
        keyLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panel.add(keyLabel, gbc);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UIConstants.NORMAL_FONT);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(valueLabel, gbc);
        
        gbc.gridy++;
    }

    private void downloadComplaintPDF() {
        int selectedRow = complaintsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to download!", 
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

        // Get customer info
        Customer customer = customerDAO.getCustomerByUserId(userId);
        String customerName = "Unknown";
        String customerPhone = "N/A";
        
        if (customer != null) {
            customerName = customer.getFirstName() + " " + customer.getLastName();
            customerPhone = customer.getPhone();
        }

        // Generate PDF content
        StringBuilder pdfContent = new StringBuilder();
        pdfContent.append("═══════════════════════════════════════════════════════════\n");
        pdfContent.append("              COMPLAINT TICKET\n");
        pdfContent.append("           ISP MANAGEMENT SYSTEM\n");
        pdfContent.append("═══════════════════════════════════════════════════════════\n\n");
        pdfContent.append("Ticket: ").append(complaint.getTicketNumber()).append("\n");
        pdfContent.append("Complaint ID: ").append(complaint.getComplaintId()).append("\n\n");
        pdfContent.append("───────────────────────────────────────────────────────────\n\n");
        pdfContent.append("CUSTOMER:\n");
        pdfContent.append(customerName).append("\n");
        pdfContent.append("Phone: ").append(customerPhone).append("\n\n");
        pdfContent.append("───────────────────────────────────────────────────────────\n\n");
        pdfContent.append("COMPLAINT DETAILS:\n");
        pdfContent.append("Subject: ").append(complaint.getSubject()).append("\n");
        pdfContent.append("Priority: ").append(complaint.getPriority() != null ? complaint.getPriority().toUpperCase() : "NOT SET").append("\n");
        pdfContent.append("Status: ").append(complaint.getStatus() != null ? complaint.getStatus().toUpperCase() : "UNKNOWN").append("\n\n");
        pdfContent.append("Description:\n");
        pdfContent.append(complaint.getDescription()).append("\n\n");
        pdfContent.append("───────────────────────────────────────────────────────────\n\n");
        pdfContent.append("Submitted: ").append(complaint.getCreatedDate().toString()).append("\n");
        
        if (complaint.getAssignedEmployeeId() != null) {
            pdfContent.append("Assigned To: Employee #").append(complaint.getAssignedEmployeeId()).append("\n");
        } else {
            pdfContent.append("Assigned To: Unassigned\n");
        }
        
        if (complaint.getResolvedDate() != null) {
            pdfContent.append("Resolved At: ").append(complaint.getResolvedDate().toString()).append("\n");
        } else {
            pdfContent.append("Resolution: Pending\n");
        }
        
        pdfContent.append("\n═══════════════════════════════════════════════════════════\n");
        pdfContent.append("              ISP Management System\n");
        pdfContent.append("═══════════════════════════════════════════════════════════\n");

        // Save to file
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("Complaint_" + complaint.getTicketNumber() + ".txt"));
            fileChooser.setDialogTitle("Save Complaint");
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(pdfContent.toString());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Complaint saved successfully to:\n" + fileToSave.getAbsolutePath(), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to save complaint!\nError: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
