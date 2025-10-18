package com.isp.ui.views;

import com.isp.dao.CustomerDAO;
import com.isp.dao.DailyDataUsageDAO;
import com.isp.dao.PlanDAO;
import com.isp.model.Customer;
import com.isp.model.DailyDataUsage;
import com.isp.model.Plan;
import com.isp.model.User;
import com.isp.service.AuthService;
import com.isp.ui.utils.UIConstants;
import com.isp.ui.views.customer.ProfileManagementPanel;
import com.isp.ui.views.customer.MyPlansPanel;
import com.isp.ui.views.customer.MyBillsPanel;
import com.isp.ui.views.customer.MyComplaintsPanel;
import com.isp.ui.views.customer.UsageStatisticsPanel;
import com.isp.ui.views.customer.PaymentHistoryPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CustomerDashboard extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final int currentUserId;
    
    // DAOs for loading real data
    private CustomerDAO customerDAO;
    private PlanDAO planDAO;
    private DailyDataUsageDAO dataUsageDAO;
    private Customer currentCustomer;
    private Plan currentPlan;

    public CustomerDashboard() {
        this.currentUserId = AuthService.getCurrentUser().getUserId();
        this.customerDAO = new CustomerDAO();
        this.planDAO = new PlanDAO();
        this.dataUsageDAO = new DailyDataUsageDAO();
        
        // Load customer data
        this.currentCustomer = customerDAO.getCustomerByUserId(currentUserId);
        if (currentCustomer != null && currentCustomer.getPlanId() != null) {
            this.currentPlan = planDAO.getPlanById(currentCustomer.getPlanId());
        }
        
        initializeUI();
    }

    private void initializeUI() {
        setTitle("ISP Management System - Customer Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.PRIMARY_BACKGROUND,
                    getWidth(), getHeight(), UIConstants.SECONDARY_BACKGROUND);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);

        // Sidebar
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                g2.setColor(UIConstants.PRIMARY_BACKGROUND);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        contentPanel.setOpaque(false);

        // Add different panels
        contentPanel.add(createDashboardHomePanel(), "Dashboard");
        contentPanel.add(new ProfileManagementPanel(currentUserId), "Profile");
        contentPanel.add(new MyPlansPanel(currentUserId), "Plans");
        contentPanel.add(new MyBillsPanel(currentUserId), "Bills");
        contentPanel.add(new MyComplaintsPanel(currentUserId), "Complaints");
        contentPanel.add(new UsageStatisticsPanel(currentUserId), "Usage");
        contentPanel.add(new PaymentHistoryPanel(currentUserId), "Payments");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.SIDEBAR_COLOR,
                    0, getHeight(), new Color(20, 30, 50));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Logo with Red Accent
        JLabel brandLabel = new JLabel("ISP", SwingConstants.CENTER);
        brandLabel.setFont(UIConstants.FONT_TITLE);
        brandLabel.setForeground(UIConstants.AIRTEL_RED);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        brandLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        sidebar.add(brandLabel);

        // User info with modern styling
        User currentUser = AuthService.getCurrentUser();
        String displayName = currentUser.getUsername();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = currentUser.getPhone();
        }

        JPanel userPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                g2.setColor(new Color(30, 40, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        userPanel.setOpaque(false);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        userPanel.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 20, 90));

        JLabel userIcon = new JLabel("👤", SwingConstants.CENTER);
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        userIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(userIcon);

        userPanel.add(Box.createVerticalStrut(8));

        JLabel userLabel = new JLabel(displayName, SwingConstants.CENTER);
        userLabel.setFont(UIConstants.FONT_BUTTON);
        userLabel.setForeground(UIConstants.TEXT_PRIMARY);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPanel.add(userLabel);

        sidebar.add(userPanel);
        sidebar.add(Box.createVerticalStrut(30));

        // Menu items
        String[] menuItems = {"Dashboard", "Profile", "Plans", "Bills", "Complaints", "Usage", "Payments"};
        String[] menuIcons = {"🏠", "👤", "📋", "💳", "⚠️", "📊", "💰"};

        for (int i = 0; i < menuItems.length; i++) {
            com.isp.ui.components.ModernButton menuButton = new com.isp.ui.components.ModernButton(
                menuIcons[i] + "  " + menuItems[i]);
            menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuButton.setMaximumSize(new Dimension(220, 40));
            String panelName = menuItems[i];
            menuButton.addActionListener(e -> cardLayout.show(contentPanel, panelName));
            sidebar.add(menuButton);
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());

        // Logout button
        com.isp.ui.components.ModernButton logoutButton = new com.isp.ui.components.ModernButton("🚪  Logout");
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(220, 40));
        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JButton createMenuButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.MENU_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(UIConstants.SIDEBAR_COLOR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 45));
        button.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 25, 10, 10));

        if (panelName != null) {
            button.addActionListener(e -> cardLayout.show(contentPanel, panelName));
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(UIConstants.SIDEBAR_COLOR);
            }
        });

        return button;
    }

    private JPanel createDashboardHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false); // Transparent for gradient background
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header with consistent styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel headerLabel = new JLabel("My Dashboard");
        headerLabel.setFont(UIConstants.FONT_TITLE);
        headerLabel.setForeground(UIConstants.TEXT_PRIMARY); // White text on dark background
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Main cards panel - 2 columns with modern styling
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Row 1: Active Plan Card (left) and Data Usage Card (right)
        gbc.gridx = 0; gbc.gridy = 0;
        cardsPanel.add(createActivePlanCard(), gbc);
        
        gbc.gridx = 1;
        cardsPanel.add(createDataUsageCard(), gbc);

        // Removed white Quick Recharge card - no longer needed

        panel.add(cardsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActivePlanCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                // Modern card background with gradient
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.CARD_BACKGROUND,
                    0, getHeight(), new Color(45, 55, 72));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.BORDER_RADIUS_CARD, UIConstants.BORDER_RADIUS_CARD);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setMinimumSize(new Dimension(300, 280));
        card.setPreferredSize(new Dimension(400, 300));
        
        // Card Title with accent color
        JLabel titleLabel = new JLabel("Active Plan");
        titleLabel.setFont(UIConstants.FONT_SUBTITLE);
        titleLabel.setForeground(UIConstants.ACCENT_PURPLE); // Purple accent like admin
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        
        card.add(Box.createVerticalStrut(20));
        
        // Check if user has active plan
        if (currentPlan == null || currentCustomer == null) {
            // NO PLAN - Show recharge message
            JLabel noPlanLabel = new JLabel("No Active Plan");
            noPlanLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            noPlanLabel.setForeground(UIConstants.TEXT_PRIMARY); // White text
            noPlanLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(noPlanLabel);
            
            card.add(Box.createVerticalStrut(15));
            
            JButton rechargeNowBtn = createStyledButton("RECHARGE NOW", UIConstants.ACCENT_PURPLE);
            rechargeNowBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            rechargeNowBtn.addActionListener(e -> showRechargeFlow());
            card.add(rechargeNowBtn);
            
            return card;
        }
        
        // HAS PLAN - Show real plan details
        JLabel planLabel = new JLabel(currentPlan.getPlanName());
        planLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        planLabel.setForeground(UIConstants.TEXT_PRIMARY); // White text
        planLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(planLabel);
        
        card.add(Box.createVerticalStrut(12));
        
        // Calculate days remaining
        long daysRemaining = 0;
        if (currentCustomer.getPlanExpiryDate() != null) {
            daysRemaining = ChronoUnit.DAYS.between(
                LocalDate.now(), 
                currentCustomer.getPlanExpiryDate()
            );
        }
        
        JLabel validityLabel = new JLabel(daysRemaining + " days remaining");
        validityLabel.setFont(UIConstants.FONT_BODY);
        validityLabel.setForeground(daysRemaining < 5 ? UIConstants.DANGER_COLOR : UIConstants.TEXT_SECONDARY);
        validityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(validityLabel);
        
        card.add(Box.createVerticalStrut(15));
        
        // Benefits from real plan data
        double dataPerDay = 0.0;
        try {
            if (currentPlan.getDataPerDayGb() != null) {
                dataPerDay = currentPlan.getDataPerDayGb().doubleValue();
            }
        } catch (Exception ignored) { }

        String voiceBenefits = currentPlan.getVoiceBenefits() == null ? "No voice benefits" : currentPlan.getVoiceBenefits();
        int smsPerDay = 0;
        try {
            smsPerDay = currentPlan.getSmsPerDay();
        } catch (Exception ignored) { }

        String benefits = String.format(
            "<html><div style='color: #cbd5e1; font-family: Segoe UI; font-size: 13px;'>" +
            "• %.1fGB/day data<br>" +
            "• %s<br>" +
            "• %d SMS/day</div></html>",
            dataPerDay,
            voiceBenefits,
            smsPerDay
        );
        JLabel benefitsLabel = new JLabel(benefits);
        benefitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(benefitsLabel);
        
        return card;
    }

    private JPanel createDataUsageCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                com.isp.ui.utils.UIHelper.enableAntiAliasing(g2);
                // Modern card background with gradient
                GradientPaint gradient = new GradientPaint(0, 0, UIConstants.CARD_BACKGROUND,
                    0, getHeight(), new Color(45, 55, 72));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.BORDER_RADIUS_CARD, UIConstants.BORDER_RADIUS_CARD);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setMinimumSize(new Dimension(300, 280));
        card.setPreferredSize(new Dimension(400, 300));
        
        // Card Title with accent color
        JLabel titleLabel = new JLabel("Today's Data Usage");
        titleLabel.setFont(UIConstants.FONT_SUBTITLE);
        titleLabel.setForeground(UIConstants.ACCENT_BLUE); // Blue accent
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        
        card.add(Box.createVerticalStrut(20));
        
        // Load real usage data from database
        double dataUsed = 0.0;
        double dataLimit = 0.0;
        int percentage = 0;
        String statusText = "No data";
        Color statusColor = UIConstants.TEXT_SECONDARY;
        
        if (currentCustomer != null && currentPlan != null) {
            try {
                DailyDataUsage todayUsage = dataUsageDAO.getTodayUsage(currentCustomer.getCustomerId());
                if (todayUsage != null) {
                    dataUsed = todayUsage.getDataUsedGb().doubleValue();
                    dataLimit = todayUsage.getDailyLimitGb().doubleValue();
                    if (dataLimit > 0) {
                        percentage = (int) ((dataUsed / dataLimit) * 100);
                    }
                    
                    // Determine status with proper colors
                    if (percentage >= 100) {
                        statusText = "⚠️ Limit exceeded!";
                        statusColor = UIConstants.DANGER_COLOR;
                    } else if (percentage >= 80) {
                        statusText = "⚠️ 80% used - approaching limit";
                        statusColor = UIConstants.WARNING_COLOR;
                    } else {
                        statusText = "✓ Normal usage";
                        statusColor = UIConstants.SUCCESS_COLOR;
                    }
                } else {
                    if (currentPlan.getDataPerDayGb() != null) {
                        dataLimit = currentPlan.getDataPerDayGb().doubleValue();
                    }
                    statusText = "No usage yet today";
                }
            } catch (Exception ex) {
                statusText = "Error loading usage";
            }
        } else {
            statusText = "No active plan";
        }
        
        // Usage Text with white color
        String usageText = String.format("%.2f GB / %.2f GB", dataUsed, dataLimit);
        JLabel usageLabel = new JLabel(usageText);
        usageLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        usageLabel.setForeground(UIConstants.TEXT_PRIMARY); // White text
        usageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(usageLabel);
        
        card.add(Box.createVerticalStrut(18));
        
        // Modern Progress Bar with WHITE text
        JProgressBar progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Force white text color on progress bar
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
        };
        progressBar.setValue(Math.min(percentage, 100));
        progressBar.setStringPainted(true);
        progressBar.setString(percentage + "% used");
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Set white text color for the progress bar string
        UIManager.put("ProgressBar.foreground", Color.WHITE);
        UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
        UIManager.put("ProgressBar.selectionBackground", Color.WHITE);
        
        // Color based on usage - matching admin dashboard style
        if (percentage >= 100) {
            progressBar.setForeground(UIConstants.DANGER_COLOR);
        } else if (percentage >= 80) {
            progressBar.setForeground(UIConstants.WARNING_COLOR);
        } else {
            progressBar.setForeground(UIConstants.SUCCESS_COLOR);
        }
        
        progressBar.setBackground(new Color(30, 41, 59)); // Dark background
        progressBar.setPreferredSize(new Dimension(250, 28));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(progressBar);
        
        card.add(Box.createVerticalStrut(12));
        
        // Status with appropriate color
        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(UIConstants.FONT_BODY);
        statusLabel.setForeground(statusColor);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);
        
        // Add BUY DATA ADDON button when limit exceeded
        if (percentage >= 100) {
            card.add(Box.createVerticalStrut(20));
            
            JButton addonBtn = createStyledButton("🚀 BUY DATA ADDON", UIConstants.ACCENT_ORANGE);
            addonBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            addonBtn.addActionListener(e -> showAddonPurchaseFlow());
            card.add(addonBtn);
        }
        
        return card;
    }
    
    // Helper method to create consistent styled buttons
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setMinimumSize(new Dimension(220, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        Color darkerColor = bgColor.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(darkerColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    // Show addon purchase dialog
    private void showAddonPurchase() {
        JDialog addonDialog = new JDialog(this, "Buy Data Addon", true);
        addonDialog.setSize(550, 500);
        addonDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Header
        JLabel headerLabel = new JLabel("⚠️ Daily Limit Exceeded - Buy Data Addon");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(UIConstants.DANGER_COLOR);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Addons panel
        JPanel addonsPanel = new JPanel();
        addonsPanel.setLayout(new BoxLayout(addonsPanel, BoxLayout.Y_AXIS));
        addonsPanel.setBackground(Color.WHITE);
        
        // Load data addons from database
        java.util.List<Plan> addons = planDAO.getAllPlans().stream()
            .filter(Plan::isAddon)
            .collect(java.util.stream.Collectors.toList());
        
        ButtonGroup addonGroup = new ButtonGroup();
        
        for (Plan addon : addons) {
            JPanel addonCard = createAddonCard(addon, addonGroup);
            addonsPanel.add(addonCard);
            addonsPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(addonsPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelBtn = createRoundedButton("CANCEL", Color.GRAY, 120, 45);
        cancelBtn.addActionListener(e -> addonDialog.dispose());
        
        JButton buyBtn = createRoundedButton("BUY NOW", UIConstants.AIRTEL_RED, 150, 45);
        buyBtn.addActionListener(e -> {
            Plan selectedAddon = null;
            for (java.util.Enumeration<javax.swing.AbstractButton> buttons = addonGroup.getElements(); buttons.hasMoreElements();) {
                javax.swing.AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    selectedAddon = (Plan) ((JRadioButton) button).getClientProperty("plan");
                    break;
                }
            }
            
            if (selectedAddon == null) {
                JOptionPane.showMessageDialog(addonDialog, "Please select an addon!", "No Addon Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            addonDialog.dispose();
            showPaymentOptions(selectedAddon);
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(buyBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        addonDialog.add(mainPanel);
        addonDialog.setVisible(true);
    }
    
    // Create addon card
    private JPanel createAddonCard(Plan addon, ButtonGroup group) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.AIRTEL_RED, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JRadioButton radioBtn = new JRadioButton();
        radioBtn.putClientProperty("plan", addon);
        radioBtn.setBackground(Color.WHITE);
        group.add(radioBtn);
        card.add(radioBtn, BorderLayout.WEST);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(addon.getPlanName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        infoPanel.add(nameLabel);
        
        infoPanel.add(Box.createVerticalStrut(5));
        
        String validityText = addon.getAddonValidityHours() < 24 ? 
            "Valid until midnight" : addon.getAddonValidityHours()/24 + " days";
        JLabel detailsLabel = new JLabel(String.format("%.1fGB data • %s", 
            addon.getTotalDataGb().doubleValue(), validityText));
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsLabel.setForeground(UIConstants.TEXT_SECONDARY);
        infoPanel.add(detailsLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        JLabel priceLabel = new JLabel("₹" + addon.getPriceInr());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(UIConstants.AIRTEL_RED);
        card.add(priceLabel, BorderLayout.EAST);
        
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                radioBtn.setSelected(true);
            }
        });
        
        return card;
    }

    private JPanel createQuickRechargeCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Left: Text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Quick Recharge");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(titleLabel);
        
        textPanel.add(Box.createVerticalStrut(5));
        
        JLabel subtitleLabel = new JLabel("Choose from our best plans");
        subtitleLabel.setFont(UIConstants.SMALL_FONT);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(subtitleLabel);
        
        card.add(textPanel, BorderLayout.WEST);
        
        // Right: Button
        JButton rechargeBtn = new JButton("RECHARGE NOW");
        rechargeBtn.setFont(UIConstants.BUTTON_FONT);
        rechargeBtn.setForeground(Color.WHITE);
        rechargeBtn.setBackground(UIConstants.AIRTEL_RED);
        rechargeBtn.setFocusPainted(false);
        rechargeBtn.setBorderPainted(false);
        rechargeBtn.setPreferredSize(new Dimension(180, 45));
        rechargeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        rechargeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rechargeBtn.setBackground(UIConstants.AIRTEL_RED_DARK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                rechargeBtn.setBackground(UIConstants.AIRTEL_RED);
            }
        });
        
        rechargeBtn.addActionListener(e -> showRechargeFlow());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(rechargeBtn);
        
        card.add(buttonPanel, BorderLayout.EAST);
        
        return card;
    }
    
    // COMPLETE PAYMENT FLOW: Step 1 -> Step 2 -> Step 3
    private void showRechargeFlow() {
        // Step 1: Select Plan
        JDialog planDialog = new JDialog(this, "Choose Your Plan", true);
        planDialog.setSize(700, 600);
        planDialog.setLocationRelativeTo(this);
        planDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Select a Plan to Recharge");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(UIConstants.AIRTEL_RED);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Plans list
        JPanel plansPanel = new JPanel();
        plansPanel.setLayout(new BoxLayout(plansPanel, BoxLayout.Y_AXIS));
        plansPanel.setBackground(Color.WHITE);
        
        // Load real plans from database
        java.util.List<Plan> allPlans = planDAO.getAllPlans();
        ButtonGroup planGroup = new ButtonGroup();
        
        for (Plan plan : allPlans) {
            if (plan.isAddon()) continue; // Skip addons
            
            JPanel planCard = createRoundedPlanCard(plan, planGroup);
            plansPanel.add(planCard);
            plansPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(plansPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelBtn = createRoundedButton("CANCEL", Color.GRAY, 140, 45);
        cancelBtn.addActionListener(e -> planDialog.dispose());
        
        JButton proceedBtn = createRoundedButton("PROCEED TO PAYMENT", UIConstants.AIRTEL_RED, 200, 45);
        proceedBtn.addActionListener(e -> {
            // Get selected plan
            Plan selectedPlan = null;
            for (java.util.Enumeration<javax.swing.AbstractButton> buttons = planGroup.getElements(); buttons.hasMoreElements();) {
                javax.swing.AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    selectedPlan = (Plan) ((JRadioButton) button).getClientProperty("plan");
                    break;
                }
            }
            
            if (selectedPlan == null) {
                JOptionPane.showMessageDialog(planDialog, "Please select a plan!", "No Plan Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            planDialog.dispose();
            showPaymentOptions(selectedPlan);
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(proceedBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        planDialog.add(mainPanel);
        planDialog.setVisible(true);
    }
    
    // Step 2: Payment Options (UPI / Card)
    private void showPaymentOptions(Plan selectedPlan) {
        JDialog paymentDialog = new JDialog(this, "Choose Payment Method", true);
        paymentDialog.setSize(500, 450);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Complete Your Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(UIConstants.AIRTEL_RED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(titleLabel);
        
        headerPanel.add(Box.createVerticalStrut(10));
        
        JLabel planLabel = new JLabel(selectedPlan.getPlanName() + " - ₹" + selectedPlan.getPriceInr());
        planLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        planLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(planLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Payment options
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);
        
        ButtonGroup paymentGroup = new ButtonGroup();
        
        // UPI Option
        JPanel upiPanel = createPaymentOptionPanel("💳 UPI Payment", "Pay using UPI ID", paymentGroup, "UPI");
        optionsPanel.add(upiPanel);
        optionsPanel.add(Box.createVerticalStrut(15));
        
        // Card Option
        JPanel cardPanel = createPaymentOptionPanel("💳 Debit/Credit Card", "Pay using Card", paymentGroup, "CARD");
        optionsPanel.add(cardPanel);
        optionsPanel.add(Box.createVerticalStrut(15));
        
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton backBtn = createRoundedButton("BACK", Color.GRAY, 120, 45);
        backBtn.addActionListener(e -> {
            paymentDialog.dispose();
            showRechargeFlow();
        });
        
        JButton payBtn = createRoundedButton("PAY NOW", UIConstants.AIRTEL_RED, 150, 45);
        payBtn.addActionListener(e -> {
            String paymentMethod = null;
            for (java.util.Enumeration<javax.swing.AbstractButton> buttons = paymentGroup.getElements(); buttons.hasMoreElements();) {
                javax.swing.AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    paymentMethod = button.getActionCommand();
                    break;
                }
            }
            
            if (paymentMethod == null) {
                JOptionPane.showMessageDialog(paymentDialog, "Please select a payment method!", "No Method Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            paymentDialog.dispose();
            showPaymentForm(selectedPlan, paymentMethod);
        });
        
        buttonPanel.add(backBtn);
        buttonPanel.add(payBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        paymentDialog.add(mainPanel);
        paymentDialog.setVisible(true);
    }
    
    // Step 3: Payment Form (UPI ID or Card Details)
    private void showPaymentForm(Plan selectedPlan, String paymentMethod) {
        JDialog formDialog = new JDialog(this, paymentMethod + " Payment", true);
        formDialog.setSize(450, 400);
        formDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Header
        JLabel headerLabel = new JLabel("Enter " + paymentMethod + " Details");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(UIConstants.AIRTEL_RED);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel(new GridLayout(paymentMethod.equals("UPI") ? 2 : 4, 1, 10, 15));
        formPanel.setBackground(Color.WHITE);
        
        JTextField inputField1, inputField2 = null, inputField3 = null;
        
        if (paymentMethod.equals("UPI")) {
            formPanel.add(new JLabel("UPI ID:"));
            inputField1 = new JTextField();
            inputField1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(inputField1);
        } else {
            formPanel.add(new JLabel("Card Number:"));
            inputField1 = new JTextField();
            inputField1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(inputField1);
            
            formPanel.add(new JLabel("Expiry (MM/YY):"));
            inputField2 = new JTextField();
            inputField2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(inputField2);
            
            formPanel.add(new JLabel("CVV:"));
            inputField3 = new JPasswordField();
            inputField3.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(inputField3);
        }
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelBtn = createRoundedButton("CANCEL", Color.GRAY, 120, 45);
        cancelBtn.addActionListener(e -> formDialog.dispose());
        
        JTextField finalInputField1 = inputField1;
        JButton confirmBtn = createRoundedButton("CONFIRM PAYMENT", UIConstants.AIRTEL_RED, 180, 45);
        confirmBtn.addActionListener(e -> {
            if (finalInputField1.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(formDialog, "Please fill in all fields!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            formDialog.dispose();
            processPayment(selectedPlan);
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(confirmBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        formDialog.add(mainPanel);
        formDialog.setVisible(true);
    }
    
    // Step 4: Process Payment & Show Success
    private void processPayment(Plan selectedPlan) {
        // Simulate payment processing
        JDialog processingDialog = new JDialog(this, "Processing...", true);
        processingDialog.setSize(300, 150);
        processingDialog.setLocationRelativeTo(this);
        processingDialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel loadingLabel = new JLabel("Processing your payment...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(loadingLabel);
        
        panel.add(Box.createVerticalStrut(15));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar);
        
        processingDialog.add(panel);
        
        // Show processing dialog for 2 seconds then show success
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(2000);
                
                // Update customer plan in database
                if (currentCustomer != null) {
                    currentCustomer.setPlanId(selectedPlan.getPlanId());
                    currentCustomer.setPlanActivatedDate(LocalDate.now());
                    currentCustomer.setPlanExpiryDate(LocalDate.now().plusDays(selectedPlan.getValidityDays()));
                    customerDAO.updateCustomer(currentCustomer);
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                processingDialog.dispose();
                showPaymentSuccess(selectedPlan);
            }
        };
        
        worker.execute();
        processingDialog.setVisible(true);
    }
    
    // Step 5: Success Screen
    private void showPaymentSuccess(Plan selectedPlan) {
        JDialog successDialog = new JDialog(this, "Payment Successful! ✓", true);
        successDialog.setSize(450, 350);
        successDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Success icon
        JLabel iconLabel = new JLabel("✓", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        iconLabel.setForeground(UIConstants.SUCCESS_COLOR);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(iconLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Success message
        JLabel successLabel = new JLabel("Payment Successful!");
        successLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        successLabel.setForeground(UIConstants.TEXT_PRIMARY);
        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(successLabel);
        
        mainPanel.add(Box.createVerticalStrut(15));
        
        JLabel planLabel = new JLabel(selectedPlan.getPlanName() + " Activated");
        planLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        planLabel.setForeground(UIConstants.TEXT_SECONDARY);
        planLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(planLabel);
        
        mainPanel.add(Box.createVerticalStrut(10));
        
        JLabel validityLabel = new JLabel("Valid for " + selectedPlan.getValidityDays() + " days");
        validityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        validityLabel.setForeground(UIConstants.TEXT_SECONDARY);
        validityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(validityLabel);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Done button
        JButton doneBtn = createRoundedButton("DONE", UIConstants.SUCCESS_COLOR, 150, 45);
        doneBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneBtn.addActionListener(e -> {
            successDialog.dispose();
            // Refresh dashboard
            dispose();
            new CustomerDashboard().setVisible(true);
        });
        mainPanel.add(doneBtn);
        
        successDialog.add(mainPanel);
        successDialog.setVisible(true);
    }
    
    // Show addon purchase flow (midnight-expiry addons)
    private void showAddonPurchaseFlow() {
        JDialog addonDialog = new JDialog(this, "Buy Data Addon", true);
        addonDialog.setSize(550, 520);
        addonDialog.setLocationRelativeTo(this);
        addonDialog.setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UIConstants.AIRTEL_RED);
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Choose Data Addon (Valid Till Midnight)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        addonDialog.add(titlePanel, BorderLayout.NORTH);
        
        // Main panel with addons
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Info label
        JLabel infoLabel = new JLabel("<html><center>⚠️ Addons are valid only until midnight today<br>Choose your addon to continue browsing</center></html>");
        infoLabel.setFont(UIConstants.SMALL_FONT);
        infoLabel.setForeground(UIConstants.WARNING_COLOR);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Addons panel (scrollable)
        JPanel addonsPanel = new JPanel();
        addonsPanel.setLayout(new BoxLayout(addonsPanel, BoxLayout.Y_AXIS));
        addonsPanel.setBackground(Color.WHITE);
        
        // Load midnight-expiry addons from database
        ButtonGroup addonGroup = new ButtonGroup();
        try {
            PlanDAO planDAO = new PlanDAO();
            java.util.List<Plan> allPlans = planDAO.getAllPlans();
            
            // Filter only midnight addons (is_addon=true AND addon_validity_hours=24)
            for (Plan plan : allPlans) {
                if (!plan.isAddon()) continue; // Skip regular plans
                if (plan.getAddonValidityHours() == null || plan.getAddonValidityHours() > 24) continue; // Only midnight addons
                
                JPanel addonCard = createAddonCardFromPlan(plan, addonGroup);
                addonsPanel.add(addonCard);
                addonsPanel.add(Box.createVerticalStrut(15));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading addons. Please try again.");
            errorLabel.setForeground(UIConstants.DANGER_COLOR);
            addonsPanel.add(errorLabel);
        }
        
        JScrollPane scrollPane = new JScrollPane(addonsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane);
        
        addonDialog.add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
        
        JButton buyBtn = createRoundedButton("BUY NOW", UIConstants.AIRTEL_RED, 150, 45);
        buyBtn.addActionListener(e -> {
            Plan selectedAddon = getSelectedAddonPlan(addonGroup);
            if (selectedAddon != null) {
                addonDialog.dispose();
                // Direct to payment options (addon is already a Plan object)
                showPaymentOptions(selectedAddon);
            } else {
                JOptionPane.showMessageDialog(addonDialog, "Please select an addon first!", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton cancelBtn = createRoundedButton("CANCEL", new Color(150, 150, 150), 150, 45);
        cancelBtn.addActionListener(e -> addonDialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(buyBtn);
        
        addonDialog.add(buttonPanel, BorderLayout.SOUTH);
        addonDialog.setVisible(true);
    }
    
    // Helper: Create addon card from Plan
    private JPanel createAddonCardFromPlan(Plan addon, ButtonGroup group) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JRadioButton radioBtn = new JRadioButton();
        radioBtn.putClientProperty("plan", addon); // Store as Plan object
        group.add(radioBtn);
        card.add(radioBtn, BorderLayout.WEST);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(addon.getPlanName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        infoPanel.add(nameLabel);
        
        infoPanel.add(Box.createVerticalStrut(5));
        
        String details = String.format("%.1fGB data • Valid till midnight", addon.getTotalDataGb().doubleValue());
        JLabel detailsLabel = new JLabel(details);
        detailsLabel.setFont(UIConstants.SMALL_FONT);
        detailsLabel.setForeground(UIConstants.TEXT_SECONDARY);
        infoPanel.add(detailsLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        JLabel priceLabel = new JLabel(String.format("₹%.0f", addon.getPriceInr().doubleValue()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        priceLabel.setForeground(UIConstants.AIRTEL_RED);
        card.add(priceLabel, BorderLayout.EAST);
        
        // Click anywhere on card to select
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radioBtn.setSelected(true);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.AIRTEL_RED, 2),
                    new EmptyBorder(15, 15, 15, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!radioBtn.isSelected()) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        new EmptyBorder(15, 15, 15, 15)
                    ));
                }
            }
        });
        
        return card;
    }
    
    // Helper: Get selected addon (returns Plan) from button group
    private Plan getSelectedAddonPlan(ButtonGroup group) {
        for (java.util.Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return (Plan) button.getClientProperty("plan");
            }
        }
        return null;
    }
    
    // Helper: Create rounded plan card
    private JPanel createRoundedPlanCard(Plan plan, ButtonGroup group) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JRadioButton radioBtn = new JRadioButton();
        radioBtn.putClientProperty("plan", plan);
        radioBtn.setBackground(Color.WHITE);
        group.add(radioBtn);
        card.add(radioBtn, BorderLayout.WEST);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(plan.getPlanName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        infoPanel.add(nameLabel);
        
        infoPanel.add(Box.createVerticalStrut(5));
        
        JLabel detailsLabel = new JLabel(String.format("%.1fGB/day • %s • %d days", 
            plan.getDataPerDayGb().doubleValue(), plan.getVoiceBenefits(), plan.getValidityDays()));
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsLabel.setForeground(UIConstants.TEXT_SECONDARY);
        infoPanel.add(detailsLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        JLabel priceLabel = new JLabel("₹" + plan.getPriceInr());
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        priceLabel.setForeground(UIConstants.AIRTEL_RED);
        card.add(priceLabel, BorderLayout.EAST);
        
        // Click anywhere on card to select
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                radioBtn.setSelected(true);
            }
        });
        
        return card;
    }
    
    // Helper: Create payment option panel
    private JPanel createPaymentOptionPanel(String title, String subtitle, ButtonGroup group, String actionCommand) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JRadioButton radioBtn = new JRadioButton();
        radioBtn.setBackground(Color.WHITE);
        radioBtn.setActionCommand(actionCommand);
        group.add(radioBtn);
        panel.add(radioBtn, BorderLayout.WEST);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        textPanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel, BorderLayout.CENTER);
        
        // Click anywhere to select
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                radioBtn.setSelected(true);
            }
        });
        
        return panel;
    }
    
    // Helper: Create rounded button
    private JButton createRoundedButton(String text, Color bgColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        Color darkerColor = bgColor.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkerColor);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            new AuthService().logout();
            dispose();
            new LoginWindow().setVisible(true);
        }
    }
}
