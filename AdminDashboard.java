package com.isp.ui.views;

import com.isp.service.AuthService;
import com.isp.ui.components.DashboardCard;
import com.isp.ui.components.ModernButton;
import com.isp.ui.utils.UIConstants;
// admin panels are referenced by name when added to the CardLayout

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.Timer;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel dashboardHomePanel;
    private DashboardCard customersCard;
    private DashboardCard revenueCard;
    private DashboardCard pendingBillsCard;
    private DashboardCard complaintsCard;

    public AdminDashboard() {
        initializeUI();
        
        // Set up automatic refresh timer (every 30 seconds)
        Timer refreshTimer = new Timer(30000, e -> refreshDashboard());
        refreshTimer.start();
    }

    private void initializeUI() {
        setTitle("ISP Management System - Admin Dashboard");
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

        // Add different panels (keep references)
        dashboardHomePanel = createDashboardHomePanel();
        contentPanel.add(dashboardHomePanel, "Dashboard");
        contentPanel.add(new com.isp.ui.views.admin.CustomerManagementPanel(this), "Customers");
        contentPanel.add(new com.isp.ui.views.admin.PlansManagementPanel(this), "Plans");
        contentPanel.add(new com.isp.ui.views.admin.BillingManagementPanel(this), "Billing");
        contentPanel.add(new com.isp.ui.views.admin.ComplaintsManagementPanel(this), "Complaints");
        contentPanel.add(new com.isp.ui.views.admin.EmployeeManagementPanel(this), "Employees");

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

        // Logo/Header
        JLabel logoLabel = new JLabel("ISP", SwingConstants.CENTER);
        logoLabel.setFont(UIConstants.SUBTITLE_FONT);
        logoLabel.setForeground(UIConstants.ACCENT_PURPLE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        sidebar.add(logoLabel);

        // User info
        JLabel userLabel = new JLabel("Welcome, " + AuthService.getCurrentUser().getUsername(), SwingConstants.CENTER);
        userLabel.setFont(UIConstants.SMALL_FONT);
        userLabel.setForeground(UIConstants.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        sidebar.add(userLabel);

        // Menu items
        String[] menuItems = {"Dashboard", "Customers", "Plans", "Billing", "Complaints", "Employees"};
        String[] menuIcons = {"🏠", "👥", "📋", "💳", "⚠️", "👷"};
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createSidebarButton(menuIcons[i] + "  " + menuItems[i]);
            String panelName = menuItems[i];
            menuButton.addActionListener(e -> cardLayout.show(contentPanel, panelName));
            sidebar.add(menuButton);
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());

        // Logout button
        JButton logoutButton = createSidebarButton("🚪  Logout");
        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.MENU_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(220, 40));
        button.setPreferredSize(new Dimension(220, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 25, 10, 10));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(UIConstants.ACCENT_PURPLE);
                button.setOpaque(true);
                button.setBackground(new Color(255, 255, 255, 20)); // Slight white overlay
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
                button.setOpaque(false);
                button.setBackground(new Color(0, 0, 0, 0));
            }
        });

        return button;
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
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 20));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header panel with welcome message and date
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel headerLabel = new JLabel("Dashboard Overview");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(UIConstants.TEXT_PRIMARY);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subHeaderLabel = new JLabel("Welcome back, " + AuthService.getCurrentUser().getUsername() + " 👋");
        subHeaderLabel.setFont(UIConstants.HEADER_FONT);
        subHeaderLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subHeaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerLeft.add(headerLabel);
        headerLeft.add(Box.createVerticalStrut(8));
        headerLeft.add(subHeaderLabel);
        
        headerPanel.add(headerLeft, BorderLayout.WEST);
        
        // Date display
        JLabel dateLabel = new JLabel(java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        dateLabel.setFont(UIConstants.NORMAL_FONT);
        dateLabel.setForeground(UIConstants.TEXT_SECONDARY);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Main content with scroll
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Stats cards panel
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Load live metrics from DAOs
        com.isp.dao.CustomerDAO customerDAO = new com.isp.dao.CustomerDAO();
        com.isp.dao.BillDAO billDAO = new com.isp.dao.BillDAO();
        com.isp.dao.ComplaintDAO complaintDAO = new com.isp.dao.ComplaintDAO();

        int totalCustomers = customerDAO.getTotalCustomerCount();
        double totalRevenue = billDAO.getTotalRevenue();
        int unpaidCount = billDAO.getUnpaidBillsCount();
        int activeComplaints = complaintDAO.getComplaintCountByStatus("open") + complaintDAO.getComplaintCountByStatus("in_progress");

        // Create and store dashboard cards for later updates
        customersCard = new DashboardCard("Total Customers", String.valueOf(totalCustomers), "Active connections", UIConstants.ACCENT_COLOR);
        revenueCard = new DashboardCard("Total Revenue", String.format("₹%.0f", totalRevenue), "This month", UIConstants.SUCCESS_COLOR);
        pendingBillsCard = new DashboardCard("Pending Bills", String.valueOf(unpaidCount), "Awaiting payment", UIConstants.WARNING_COLOR);
        complaintsCard = new DashboardCard("Active Complaints", String.valueOf(activeComplaints), "Pending resolution", UIConstants.DANGER_COLOR);
        
        cardsPanel.add(customersCard);
        cardsPanel.add(revenueCard);
        cardsPanel.add(pendingBillsCard);
        cardsPanel.add(complaintsCard);

        mainContent.add(cardsPanel);
        mainContent.add(Box.createVerticalStrut(30));
        
        // Quick actions panel
        JPanel quickActionsPanel = createQuickActionsPanel();
        quickActionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(quickActionsPanel);
        
        mainContent.add(Box.createVerticalStrut(30));
        
        // System health panel
        JPanel systemHealthPanel = createSystemHealthPanel(totalCustomers, unpaidCount, activeComplaints);
        systemHealthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainContent.add(systemHealthPanel);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(15));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonsPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        ModernButton addCustomerBtn = new ModernButton("➕ Add Customer", 
            UIConstants.PRIMARY_GRADIENT_START, UIConstants.PRIMARY_GRADIENT_END);
        addCustomerBtn.addActionListener(e -> cardLayout.show(contentPanel, "Customers"));
        
        ModernButton managePlansBtn = new ModernButton("📋 Manage Plans", 
            UIConstants.ACCENT_GRADIENT_START, UIConstants.ACCENT_GRADIENT_END);
        managePlansBtn.addActionListener(e -> cardLayout.show(contentPanel, "Plans"));
        
        ModernButton viewBillingBtn = new ModernButton("💳 View Billing", 
            UIConstants.SUCCESS_GRADIENT_START, UIConstants.SUCCESS_GRADIENT_END);
        viewBillingBtn.addActionListener(e -> cardLayout.show(contentPanel, "Billing"));
        
        ModernButton resolveComplaintsBtn = new ModernButton("⚠️ Resolve Complaints", 
            UIConstants.WARNING_COLOR, UIConstants.WARNING_DARK);
        resolveComplaintsBtn.addActionListener(e -> cardLayout.show(contentPanel, "Complaints"));
        
        buttonsPanel.add(addCustomerBtn);
        buttonsPanel.add(managePlansBtn);
        buttonsPanel.add(viewBillingBtn);
        buttonsPanel.add(resolveComplaintsBtn);
        
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    private JPanel createSystemHealthPanel(int totalCustomers, int unpaidBills, int activeComplaints) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("System Health & Performance");
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(15));
        
        // Health indicators
        JPanel healthPanel = new JPanel();
        healthPanel.setLayout(new BoxLayout(healthPanel, BoxLayout.Y_AXIS));
        healthPanel.setBackground(Color.WHITE);
        healthPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        healthPanel.setMaximumSize(new Dimension(1100, 200));
        
        // Customer satisfaction
        double satisfactionRate = totalCustomers > 0 ? 
            ((double)(totalCustomers - activeComplaints) / totalCustomers) * 100 : 100;
        healthPanel.add(createHealthIndicator("Customer Satisfaction", 
            satisfactionRate, "Based on active complaints", UIConstants.SUCCESS_COLOR));
        
        healthPanel.add(Box.createVerticalStrut(15));
        
        // Payment collection
        double collectionRate = unpaidBills > 0 && totalCustomers > 0 ? 
            ((double)(totalCustomers - unpaidBills) / totalCustomers) * 100 : 100;
        healthPanel.add(createHealthIndicator("Payment Collection", 
            collectionRate, "Successful bill collections", UIConstants.INFO_COLOR));
        
        healthPanel.add(Box.createVerticalStrut(15));
        
        // Service quality
        double serviceQuality = activeComplaints < 5 ? 95.0 : activeComplaints < 10 ? 85.0 : 75.0;
        healthPanel.add(createHealthIndicator("Service Quality", 
            serviceQuality, "Overall system performance", UIConstants.PRIMARY_COLOR));
        
        panel.add(healthPanel);
        
        return panel;
    }
    
    private JPanel createHealthIndicator(String name, double percentage, String description, Color color) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(1060, 50));
        
        // Left side - name and description
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 50));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.BLACK); // Dark text for white background
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 100, 100)); // Gray text for white background
        
        leftPanel.add(nameLabel);
        leftPanel.add(descLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Center - progress bar
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int)percentage);
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%.1f%%", percentage));
        progressBar.setForeground(color);
        progressBar.setBackground(new Color(240, 240, 240)); // Light gray background
        progressBar.setPreferredSize(new Dimension(0, 30));
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Larger font for visibility
        progressBar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.CENTER);
        
        // Right side - large percentage number
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(120, 50));
        
        JLabel percentageLabel = new JLabel(String.format("%.1f%%", percentage));
        percentageLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Large, bold font
        percentageLabel.setForeground(color); // Use the same color as progress bar
        percentageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        percentageLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(percentageLabel);
        rightPanel.add(Box.createVerticalGlue());
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
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
    
    /**
     * Refresh dashboard statistics - call this after adding/deleting customers or employees
     */
    public void refreshDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Reload metrics from database
                com.isp.dao.CustomerDAO customerDAO = new com.isp.dao.CustomerDAO();
                com.isp.dao.BillDAO billDAO = new com.isp.dao.BillDAO();
                com.isp.dao.ComplaintDAO complaintDAO = new com.isp.dao.ComplaintDAO();
                
                int totalCustomers = customerDAO.getTotalCustomerCount();
                double totalRevenue = billDAO.getTotalRevenue();
                int unpaidCount = billDAO.getUnpaidBillsCount();
                int activeComplaints = complaintDAO.getComplaintCountByStatus("open") + 
                                     complaintDAO.getComplaintCountByStatus("in_progress");
                
                // Remove old cards
                Container cardsContainer = customersCard.getParent();
                if (cardsContainer != null) {
                    cardsContainer.removeAll();
                    
                    // Create new cards with updated data
                    customersCard = new DashboardCard("Total Customers", String.valueOf(totalCustomers), 
                                                     "Active connections", UIConstants.ACCENT_COLOR);
                    revenueCard = new DashboardCard("Total Revenue", String.format("₹%.0f", totalRevenue), 
                                                   "This month", UIConstants.SUCCESS_COLOR);
                    pendingBillsCard = new DashboardCard("Pending Bills", String.valueOf(unpaidCount), 
                                                        "Awaiting payment", UIConstants.WARNING_COLOR);
                    complaintsCard = new DashboardCard("Active Complaints", String.valueOf(activeComplaints), 
                                                      "Pending resolution", UIConstants.DANGER_COLOR);
                    
                    // Add new cards
                    cardsContainer.add(customersCard);
                    cardsContainer.add(revenueCard);
                    cardsContainer.add(pendingBillsCard);
                    cardsContainer.add(complaintsCard);
                    
                    // Refresh the display
                    cardsContainer.revalidate();
                    cardsContainer.repaint();
                }
                
                // Update system health panel with new data
                updateSystemHealthPanel(totalCustomers, unpaidCount, activeComplaints);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error refreshing dashboard: " + e.getMessage(), 
                    "Refresh Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Update the system health panel with new data
     */
    private void updateSystemHealthPanel(int totalCustomers, int unpaidBills, int activeComplaints) {
        // Find the system health panel in the dashboard
        Component[] components = dashboardHomePanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JPanel) {
                    JPanel mainContent = (JPanel) view;
                    Component[] mainComponents = mainContent.getComponents();
                    for (Component mainComp : mainComponents) {
                        if (mainComp instanceof JPanel) {
                            JPanel panel = (JPanel) mainComp;
                            Component[] panelComponents = panel.getComponents();
                            for (Component panelComp : panelComponents) {
                                if (panelComp instanceof JPanel) {
                                    JPanel innerPanel = (JPanel) panelComp;
                                    // Check if this is the system health panel by looking for the title
                                    Component[] innerComponents = innerPanel.getComponents();
                                    for (Component innerComp : innerComponents) {
                                        if (innerComp instanceof JLabel) {
                                            JLabel label = (JLabel) innerComp;
                                            if ("System Health & Performance".equals(label.getText())) {
                                                // Found the system health panel, recreate it
                                                mainContent.remove(panel);
                                                JPanel newSystemHealthPanel = createSystemHealthPanel(totalCustomers, unpaidBills, activeComplaints);
                                                mainContent.add(newSystemHealthPanel);
                                                mainContent.revalidate();
                                                mainContent.repaint();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New inner class for gradient background with glassmorphism effect
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            // Dark gradient background
            GradientPaint gp = new GradientPaint(0, 0, new Color(30, 30, 30, 200), width, height, new Color(10, 10, 10, 200));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
            // Optional glassmorphism overlay (blur and transparency simulation)
            g2d.setColor(new Color(255, 255, 255, 20));
            g2d.fillRoundRect(20, 20, width - 40, height - 40, 25, 25);
        }
    }

    // Helper method to style modern buttons
    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,100)));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(new Color(200,200,255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
        return button;
    }
}
