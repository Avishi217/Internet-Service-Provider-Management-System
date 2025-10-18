package com.isp.ui.views.customer;

import com.isp.dao.CustomerDAO;
import com.isp.dao.PlanDAO;
import com.isp.dao.UsageLogDAO;
import com.isp.model.Customer;
import com.isp.model.Plan;
import com.isp.model.UsageLog;
import com.isp.ui.components.ModernButton;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UsageStatisticsPanel extends JPanel {
    private final UsageLogDAO usageLogDAO;
    private final CustomerDAO customerDAO;
    private final PlanDAO planDAO;
    private final int userId;
    
    private JTable usageTable;
    private DefaultTableModel tableModel;
    private JLabel totalUsageLabel;
    private JLabel currentMonthLabel;
    private JProgressBar usageProgressBar;
    private JLabel dataLimitLabel;

    public UsageStatisticsPanel(int userId) {
        this.userId = userId;
        this.usageLogDAO = new UsageLogDAO();
        this.customerDAO = new CustomerDAO();
        this.planDAO = new PlanDAO();
        
        initializeUI();
        loadUsageData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createUsageSummaryPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Usage Statistics");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);

        ModernButton refreshButton = new ModernButton("Refresh");
        refreshButton.addActionListener(e -> loadUsageData());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createUsageSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));
        summaryPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        summaryPanel.add(createCurrentUsagePanel(), BorderLayout.NORTH);
        summaryPanel.add(createUsageTablePanel(), BorderLayout.CENTER);

        return summaryPanel;
    }

    private JPanel createCurrentUsagePanel() {
        JPanel currentUsagePanel = new JPanel();
        currentUsagePanel.setLayout(new BoxLayout(currentUsagePanel, BoxLayout.Y_AXIS));
        currentUsagePanel.setBackground(Color.WHITE);
        currentUsagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));

        currentMonthLabel = new JLabel("Current Month: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        currentMonthLabel.setFont(UIConstants.SUBTITLE_FONT);
        currentMonthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        totalUsageLabel = new JLabel("Total Usage: 0 GB");
        totalUsageLabel.setFont(UIConstants.TITLE_FONT);
        totalUsageLabel.setForeground(UIConstants.PRIMARY_COLOR);
        totalUsageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dataLimitLabel = new JLabel("Data Limit: Loading...");
        dataLimitLabel.setFont(UIConstants.NORMAL_FONT);
        dataLimitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usageProgressBar = new JProgressBar(0, 100);
        usageProgressBar.setStringPainted(true);
        usageProgressBar.setPreferredSize(new Dimension(600, 30));
        usageProgressBar.setMaximumSize(new Dimension(800, 30));
        usageProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        usageProgressBar.setForeground(UIConstants.SUCCESS_COLOR);

        currentUsagePanel.add(currentMonthLabel);
        currentUsagePanel.add(Box.createVerticalStrut(10));
        currentUsagePanel.add(totalUsageLabel);
        currentUsagePanel.add(Box.createVerticalStrut(5));
        currentUsagePanel.add(dataLimitLabel);
        currentUsagePanel.add(Box.createVerticalStrut(15));
        currentUsagePanel.add(usageProgressBar);

        return currentUsagePanel;
    }

    private JScrollPane createUsageTablePanel() {
        String[] columnNames = {"Date", "Upload (GB)", "Download (GB)", "Total (GB)", "Session Duration"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usageTable = new JTable(tableModel);
        usageTable.setFont(UIConstants.NORMAL_FONT);
        usageTable.setRowHeight(30);
        usageTable.getTableHeader().setFont(UIConstants.SUBTITLE_FONT);
        usageTable.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        usageTable.getTableHeader().setForeground(Color.WHITE);
        usageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(usageTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 0, 0),
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR)
        ));

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        ModernButton viewMonthlyButton = new ModernButton("View Monthly Report");
        viewMonthlyButton.addActionListener(e -> showMonthlyReport());

        ModernButton exportButton = new ModernButton("Export Data");
        exportButton.addActionListener(e -> exportUsageData());

        footerPanel.add(viewMonthlyButton);
        footerPanel.add(exportButton);

        return footerPanel;
    }

    private void loadUsageData() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer profile not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if customer has a plan assigned
        Plan plan = null;
        if (customer.getPlanId() != null) {
            plan = planDAO.getPlanById(customer.getPlanId());
            if (plan != null) {
                dataLimitLabel.setText("Data Limit: " + plan.getDataDescription());
            }
        } else {
            dataLimitLabel.setText("Data Limit: No plan assigned");
        }

        tableModel.setRowCount(0);
        List<UsageLog> usageLogs = usageLogDAO.getUsageLogsByCustomerId(customer.getCustomerId());

        BigDecimal totalUsage = BigDecimal.ZERO;
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        for (UsageLog log : usageLogs) {
            LocalDate logDate = log.getTimestamp().toLocalDate();
            
            // Data is already stored in GB in the database, no need to divide
            BigDecimal dataUsedGB = new BigDecimal(log.getDataUsedGB()).setScale(2, RoundingMode.HALF_UP);
            
            // For display purposes, split into upload/download (70% download, 30% upload ratio)
            BigDecimal downloadGB = dataUsedGB.multiply(new BigDecimal("0.70")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal uploadGB = dataUsedGB.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalGB = dataUsedGB;

            if (!logDate.isBefore(currentMonth)) {
                totalUsage = totalUsage.add(totalGB);
            }

            String duration = formatDuration(0);

            Object[] rowData = {
                logDate.toString(),
                uploadGB + " GB",
                downloadGB + " GB",
                totalGB + " GB",
                duration
            };
            tableModel.addRow(rowData);
        }

        totalUsageLabel.setText("Total Usage: " + totalUsage.setScale(2, RoundingMode.HALF_UP) + " GB");
        updateUsageProgressBar(totalUsage, plan);
    }

    private void updateUsageProgressBar(BigDecimal totalUsage, Plan plan) {
        if (plan == null || plan.getTotalDataGb() == null || plan.getTotalDataGb().equals(BigDecimal.ZERO)) {
            usageProgressBar.setValue(0);
            usageProgressBar.setString("Unlimited Plan");
            usageProgressBar.setForeground(UIConstants.SUCCESS_COLOR);
            return;
        }

        try {
            // Use totalDataGb from the new Plan model
            BigDecimal dataLimit = plan.getTotalDataGb();

            BigDecimal usagePercent = totalUsage.divide(dataLimit, 4, RoundingMode.HALF_UP)
                                               .multiply(new BigDecimal("100"));
            int percent = usagePercent.intValue();
            
            usageProgressBar.setValue(Math.min(percent, 100));
            usageProgressBar.setString(percent + "% used");

            if (percent >= 90) {
                usageProgressBar.setForeground(UIConstants.DANGER_COLOR);
            } else if (percent >= 70) {
                usageProgressBar.setForeground(UIConstants.WARNING_COLOR);
            } else {
                usageProgressBar.setForeground(UIConstants.SUCCESS_COLOR);
            }

            if (percent >= 80) {
                dataLimitLabel.setForeground(percent >= 90 ? UIConstants.DANGER_COLOR : UIConstants.WARNING_COLOR);
            }
        } catch (Exception e) {
            usageProgressBar.setValue(0);
            usageProgressBar.setString("Unable to calculate");
        }
    }

    private String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        
        if (hours > 0) {
            return hours + "h " + mins + "m";
        }
        return mins + "m";
    }

    private void showMonthlyReport() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            return;
        }

        List<UsageLog> usageLogs = usageLogDAO.getUsageLogsByCustomerId(customer.getCustomerId());
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Monthly Usage Report", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JPanel reportPanel = new JPanel(new GridBagLayout());
        reportPanel.setBackground(Color.WHITE);
        reportPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Monthly Usage Summary");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        gbc.gridwidth = 2;
        reportPanel.add(titleLabel, gbc);
        gbc.gridy++;

        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        BigDecimal currentMonthUsage = BigDecimal.ZERO;
        BigDecimal lastMonthUsage = BigDecimal.ZERO;
        int currentMonthSessions = 0;
        int lastMonthSessions = 0;

        for (UsageLog log : usageLogs) {
            LocalDate logDate = log.getTimestamp().toLocalDate();
            // Data is already stored in GB in the database, no need to divide
            BigDecimal totalGB = new BigDecimal(log.getDataUsedGB()).setScale(2, RoundingMode.HALF_UP);

            if (!logDate.isBefore(currentMonth)) {
                currentMonthUsage = currentMonthUsage.add(totalGB);
                currentMonthSessions++;
            } else if (logDate.getMonthValue() == currentMonth.minusMonths(1).getMonthValue()) {
                lastMonthUsage = lastMonthUsage.add(totalGB);
                lastMonthSessions++;
            }
        }

        addReportRow(reportPanel, gbc, "Current Month:", 
            currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        addReportRow(reportPanel, gbc, "Current Month Usage:", 
            currentMonthUsage.setScale(2, RoundingMode.HALF_UP) + " GB");
        addReportRow(reportPanel, gbc, "Sessions This Month:", String.valueOf(currentMonthSessions));
        addReportRow(reportPanel, gbc, "Last Month Usage:", 
            lastMonthUsage.setScale(2, RoundingMode.HALF_UP) + " GB");
        addReportRow(reportPanel, gbc, "Sessions Last Month:", String.valueOf(lastMonthSessions));

        if (lastMonthUsage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal changePercent = currentMonthUsage.subtract(lastMonthUsage)
                                                       .divide(lastMonthUsage, 4, RoundingMode.HALF_UP)
                                                       .multiply(new BigDecimal("100"));
            String change = (changePercent.compareTo(BigDecimal.ZERO) > 0 ? "+" : "") + 
                           changePercent.setScale(1, RoundingMode.HALF_UP) + "%";
            addReportRow(reportPanel, gbc, "Change from Last Month:", change);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        ModernButton closeButton = new ModernButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(closeButton);

        dialog.add(new JScrollPane(reportPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addReportRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel keyLabel = new JLabel(label);
        keyLabel.setFont(UIConstants.NORMAL_FONT);
        keyLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(keyLabel, gbc);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UIConstants.SUBTITLE_FONT);
        gbc.gridx = 1;
        panel.add(valueLabel, gbc);
        
        gbc.gridy++;
    }

    private void exportUsageData() {
        Customer customer = customerDAO.getCustomerByUserId(userId);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer profile not found!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<UsageLog> usageLogs = usageLogDAO.getUsageLogsByCustomerId(customer.getCustomerId());
        
        if (usageLogs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No usage data to export!", 
                "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Generate CSV content
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("═══════════════════════════════════════════════════════════\n");
        csvContent.append("              USAGE DATA EXPORT\n");
        csvContent.append("           ISP MANAGEMENT SYSTEM\n");
        csvContent.append("═══════════════════════════════════════════════════════════\n\n");
        csvContent.append("Customer: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n");
        csvContent.append("Phone: ").append(customer.getPhone()).append("\n");
        csvContent.append("Export Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("\n\n");
        csvContent.append("───────────────────────────────────────────────────────────\n\n");
        
        // CSV Header
        csvContent.append("Date,Upload (GB),Download (GB),Total (GB),Session Duration\n");
        
        // Calculate totals
        BigDecimal totalUsage = BigDecimal.ZERO;
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        
        // Add data rows
        for (UsageLog log : usageLogs) {
            LocalDate logDate = log.getTimestamp().toLocalDate();
            BigDecimal uploadGB = BigDecimal.ZERO.divide(new BigDecimal("1024"), 2, RoundingMode.HALF_UP);
            BigDecimal downloadGB = new BigDecimal(log.getDataUsedGB()).divide(new BigDecimal("1024"), 2, RoundingMode.HALF_UP);
            BigDecimal totalGB = uploadGB.add(downloadGB);
            
            if (!logDate.isBefore(currentMonth)) {
                totalUsage = totalUsage.add(totalGB);
            }
            
            String duration = "N/A";
            
            csvContent.append(logDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(",");
            csvContent.append(uploadGB.setScale(2, RoundingMode.HALF_UP)).append(",");
            csvContent.append(downloadGB.setScale(2, RoundingMode.HALF_UP)).append(",");
            csvContent.append(totalGB.setScale(2, RoundingMode.HALF_UP)).append(",");
            csvContent.append(duration).append("\n");
        }
        
        csvContent.append("\n───────────────────────────────────────────────────────────\n\n");
        csvContent.append("SUMMARY:\n");
        csvContent.append("Total Records: ").append(usageLogs.size()).append("\n");
        csvContent.append("Current Month Usage: ").append(totalUsage.setScale(2, RoundingMode.HALF_UP)).append(" GB\n\n");
        csvContent.append("═══════════════════════════════════════════════════════════\n");
        csvContent.append("              ISP Management System\n");
        csvContent.append("═══════════════════════════════════════════════════════════\n");

        // Save to file
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File("Usage_Data_" + customer.getCustomerId() + ".csv"));
            fileChooser.setDialogTitle("Export Usage Data");
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try (FileWriter writer = new FileWriter(fileToSave)) {
                    writer.write(csvContent.toString());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Usage data exported successfully to:\n" + fileToSave.getAbsolutePath() + 
                    "\n\nTotal Records: " + usageLogs.size() +
                    "\nCurrent Month Usage: " + totalUsage.setScale(2, RoundingMode.HALF_UP) + " GB", 
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to export usage data!\nError: " + e.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
