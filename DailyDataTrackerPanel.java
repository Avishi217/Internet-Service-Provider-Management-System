package com.isp.ui.views.customer;

import com.isp.dao.CustomerDAO;
import com.isp.dao.DailyDataUsageDAO;
import com.isp.dao.PlanDAO;
import com.isp.dao.DataAddonDAO;
import com.isp.model.Customer;
import com.isp.model.DailyDataUsage;
import com.isp.model.Plan;
import com.isp.ui.components.ModernButton;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyDataTrackerPanel extends JPanel {
    private final CustomerDAO customerDAO;
    private final DailyDataUsageDAO dailyDataUsageDAO;
    private final PlanDAO planDAO;
    private final DataAddonDAO dataAddonDAO;
    private Customer customer;
    
    private JLabel titleLabel;
    private JLabel dataUsedLabel;
    private JLabel dataLimitLabel;
    private JProgressBar usageProgressBar;
    private JLabel percentageLabel;
    private JLabel warningLabel;
    private ModernButton buyAddonButton;
    private JPanel addonPanel;

    public DailyDataTrackerPanel(Customer customer) {
        this.customer = customer;
        this.customerDAO = new CustomerDAO();
        this.dailyDataUsageDAO = new DailyDataUsageDAO();
        this.planDAO = new PlanDAO();
        this.dataAddonDAO = new DataAddonDAO();
        
        initializeUI();
        loadTodayUsage();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(UIConstants.BACKGROUND_COLOR);

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            new EmptyBorder(15, 20, 15, 20)
        ));

        titleLabel = new JLabel("📊 Today's Data Usage");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(UIConstants.NORMAL_FONT);
        refreshButton.addActionListener(e -> loadTodayUsage());
        titlePanel.add(refreshButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Main Usage Card
        JPanel usageCard = createUsageCard();
        add(usageCard, BorderLayout.CENTER);

        // Addon Panel (hidden by default)
        addonPanel = createAddonPanel();
        addonPanel.setVisible(false);
        add(addonPanel, BorderLayout.SOUTH);
    }

    private JPanel createUsageCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            new EmptyBorder(30, 30, 30, 30)
        ));

        // Data Used Label
        dataUsedLabel = new JLabel("0.00 GB used");
        dataUsedLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        dataUsedLabel.setForeground(UIConstants.TEXT_PRIMARY);
        dataUsedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Data Limit Label
        dataLimitLabel = new JLabel("of 0.00 GB today");
        dataLimitLabel.setFont(UIConstants.SUBTITLE_FONT);
        dataLimitLabel.setForeground(UIConstants.TEXT_SECONDARY);
        dataLimitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(20));
        card.add(dataUsedLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(dataLimitLabel);
        card.add(Box.createVerticalStrut(30));

        // Progress Bar
        usageProgressBar = new JProgressBar(0, 100);
        usageProgressBar.setPreferredSize(new Dimension(500, 40));
        usageProgressBar.setMaximumSize(new Dimension(500, 40));
        usageProgressBar.setStringPainted(true);
        usageProgressBar.setFont(UIConstants.HEADER_FONT);
        usageProgressBar.setForeground(UIConstants.SUCCESS_COLOR);
        usageProgressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(usageProgressBar);
        card.add(Box.createVerticalStrut(15));

        // Percentage Label
        percentageLabel = new JLabel("0%");
        percentageLabel.setFont(UIConstants.SUBTITLE_FONT);
        percentageLabel.setForeground(UIConstants.TEXT_SECONDARY);
        percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(percentageLabel);
        card.add(Box.createVerticalStrut(20));

        // Warning Label
        warningLabel = new JLabel("");
        warningLabel.setFont(UIConstants.NORMAL_FONT);
        warningLabel.setForeground(UIConstants.WARNING_COLOR);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        warningLabel.setVisible(false);

        card.add(warningLabel);

        return card;
    }

    private JPanel createAddonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(new Color(255, 245, 245)); // Light red
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.DANGER_COLOR, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel alertLabel = new JLabel("⚠️ Daily limit exceeded! Buy data addon to continue.");
        alertLabel.setFont(UIConstants.HEADER_FONT);
        alertLabel.setForeground(UIConstants.DANGER_COLOR);

        buyAddonButton = new ModernButton("💰 Buy Data Addon (₹15 for 1GB)");
        buyAddonButton.setBackground(UIConstants.PRIMARY_COLOR);
        buyAddonButton.setForeground(Color.WHITE);
        buyAddonButton.addActionListener(e -> showAddonPurchaseDialog());

        panel.add(alertLabel, BorderLayout.CENTER);
        panel.add(buyAddonButton, BorderLayout.EAST);

        return panel;
    }

    private void loadTodayUsage() {
        try {
            // Reload customer data
            customer = customerDAO.getCustomerByUserId(customer.getUserId());
            
            if (customer == null || customer.getPlanId() == null) {
                showNoActivePlan();
                return;
            }

            // Get plan details
            Plan plan = planDAO.getPlanById(customer.getPlanId());
            if (plan == null || plan.getDataPerDayGb() == null || plan.getDataPerDayGb().equals(BigDecimal.ZERO)) {
                showUnlimitedPlan();
                return;
            }

            // Get today's usage
            DailyDataUsage usage = dailyDataUsageDAO.getTodayUsage(customer.getCustomerId());
            
            BigDecimal dataUsed = BigDecimal.ZERO;
            BigDecimal dailyLimit = plan.getDataPerDayGb();
            
            if (usage != null) {
                dataUsed = usage.getDataUsedGb();
            }

            // Update UI
            dataUsedLabel.setText(String.format("%.2f GB used", dataUsed));
            dataLimitLabel.setText(String.format("of %.2f GB today", dailyLimit));

            // Calculate percentage
            int percentage = usage != null ? usage.getUsagePercentage() : 0;
            percentageLabel.setText(percentage + "%");
            usageProgressBar.setValue(percentage);

            // Update progress bar color based on usage
            if (percentage >= 100) {
                usageProgressBar.setForeground(UIConstants.DANGER_COLOR);
                warningLabel.setText("❌ Daily limit exceeded!");
                warningLabel.setForeground(UIConstants.DANGER_COLOR);
                warningLabel.setVisible(true);
                addonPanel.setVisible(true);
            } else if (percentage >= 80) {
                usageProgressBar.setForeground(UIConstants.WARNING_COLOR);
                warningLabel.setText("⚠️ Warning: 80% of daily data used!");
                warningLabel.setForeground(UIConstants.WARNING_COLOR);
                warningLabel.setVisible(true);
                addonPanel.setVisible(false);
            } else {
                usageProgressBar.setForeground(UIConstants.SUCCESS_COLOR);
                warningLabel.setVisible(false);
                addonPanel.setVisible(false);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading usage data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showNoActivePlan() {
        dataUsedLabel.setText("No Active Plan");
        dataLimitLabel.setText("Please recharge to activate a plan");
        usageProgressBar.setValue(0);
        percentageLabel.setText("0%");
        warningLabel.setText("⚠️ Recharge now to start using data");
        warningLabel.setForeground(UIConstants.WARNING_COLOR);
        warningLabel.setVisible(true);
    }

    private void showUnlimitedPlan() {
        dataUsedLabel.setText("Unlimited Data");
        dataLimitLabel.setText("No daily limit on your plan");
        usageProgressBar.setValue(0);
        usageProgressBar.setString("Unlimited");
        percentageLabel.setText("∞");
        warningLabel.setVisible(false);
    }

    private void showAddonPurchaseDialog() {
        String[] addons = {
            "₹15 - 1 GB (Valid till midnight)",
            "₹25 - 2 GB (Valid till midnight)",
            "₹58 - 3 GB (Unlimited validity)",
            "₹118 - 12 GB (Unlimited validity)",
            "₹301 - 50 GB (Unlimited validity)"
        };

        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select data addon to purchase:",
            "Buy Data Addon",
            JOptionPane.QUESTION_MESSAGE,
            null,
            addons,
            addons[0]
        );

        if (selected != null) {
            JOptionPane.showMessageDialog(this,
                "Addon purchase feature coming soon!\n\n" +
                "Selected: " + selected + "\n" +
                "This will integrate with payment gateway.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
            
            // 1. Create payment transaction
            // 2. Process payment
            // 3. Add addon to customer account
            // 4. Refresh usage display
        }
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        loadTodayUsage();
    }
}
