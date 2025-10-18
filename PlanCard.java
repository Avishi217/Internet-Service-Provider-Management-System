package com.isp.ui.components;

import com.isp.model.Plan;
import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern, vibrant plan card with gradient background, benefits list, and interactive hover effects
 */
public class PlanCard extends JPanel {
    
    private Plan plan;
    private boolean isHovered = false;
    private boolean isPopular = false;
    private Color cardStartColor;
    private Color cardEndColor;
    private Runnable onSubscribeAction;
    
    public PlanCard(Plan plan) {
        this(plan, false, null);
    }
    
    public PlanCard(Plan plan, boolean isPopular, Runnable onSubscribeAction) {
        this.plan = plan;
        this.isPopular = isPopular;
        this.onSubscribeAction = onSubscribeAction;
        
        // Assign vibrant gradient colors based on plan price
        assignCardColors();
        
        setLayout(new BorderLayout(0, 15));
        setBackground(UIConstants.CARD_BACKGROUND);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(320, 520));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        initializeComponents();
        setupHoverEffect();
    }
    
    private void assignCardColors() {
        // Assign vibrant gradients based on plan price range
        BigDecimal price = plan.getPriceInr();
        if (price.compareTo(new BigDecimal("1000")) < 0) {
            // Budget plans: Blue to Cyan
            cardStartColor = new Color(59, 130, 246); // Blue
            cardEndColor = new Color(14, 165, 233); // Sky Blue
        } else if (price.compareTo(new BigDecimal("2000")) < 0) {
            // Mid-range plans: Purple to Pink
            cardStartColor = UIConstants.PRIMARY_GRADIENT_START;
            cardEndColor = UIConstants.PRIMARY_GRADIENT_END;
        } else {
            // Premium plans: Orange to Red
            cardStartColor = new Color(249, 115, 22); // Orange
            cardEndColor = new Color(220, 38, 38); // Red
        }
    }
    
    private void initializeComponents() {
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(0, 15));
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Popular badge (if applicable)
        if (isPopular) {
            JLabel popularBadge = new JLabel("⭐ MOST POPULAR", SwingConstants.CENTER);
            popularBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            popularBadge.setForeground(UIConstants.WARNING_COLOR);
            popularBadge.setOpaque(true);
            popularBadge.setBackground(new Color(254, 243, 199)); // Light yellow
            popularBadge.setBorder(new EmptyBorder(6, 12, 6, 12));
            JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            badgePanel.setOpaque(false);
            badgePanel.add(popularBadge);
            mainContainer.add(badgePanel, BorderLayout.NORTH);
        }
        
        // Center content panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Plan name with icon
        JLabel planNameLabel = new JLabel(plan.getPlanName(), SwingConstants.CENTER);
        planNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        planNameLabel.setForeground(UIConstants.TEXT_PRIMARY);
        planNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(planNameLabel);
        
        centerPanel.add(Box.createVerticalStrut(10));
        
        // Price display with rupee symbol
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pricePanel.setOpaque(false);
        
        JLabel rupeeSymbol = new JLabel("₹");
        rupeeSymbol.setFont(new Font("Segoe UI", Font.BOLD, 28));
        rupeeSymbol.setForeground(cardStartColor);
        
        JLabel priceLabel = new JLabel(String.format("%.0f", plan.getPriceInr()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        priceLabel.setForeground(cardStartColor);
        
        JLabel perMonthLabel = new JLabel("/month");
        perMonthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        perMonthLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        pricePanel.add(rupeeSymbol);
        pricePanel.add(priceLabel);
        pricePanel.add(perMonthLabel);
        centerPanel.add(pricePanel);
        
        centerPanel.add(Box.createVerticalStrut(10));
        
        // Speed and data limit info
        JPanel specPanel = new JPanel();
        specPanel.setLayout(new BoxLayout(specPanel, BoxLayout.Y_AXIS));
        specPanel.setOpaque(false);
        specPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        String speedDisplay = getSpeedDisplay(plan);
        JLabel speedLabel = new JLabel("🚀 " + speedDisplay, SwingConstants.CENTER);
        speedLabel.setFont(UIConstants.HEADER_FONT);
        speedLabel.setForeground(UIConstants.TEXT_PRIMARY);
        speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        specPanel.add(speedLabel);
        
        specPanel.add(Box.createVerticalStrut(8));
        
        String dataDisplay = getDataDisplay(plan);
        JLabel dataLabel = new JLabel("📊 " + dataDisplay, SwingConstants.CENTER);
        dataLabel.setFont(UIConstants.NORMAL_FONT);
        dataLabel.setForeground(UIConstants.TEXT_SECONDARY);
        dataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        specPanel.add(dataLabel);
        
        centerPanel.add(specPanel);
        
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Divider
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(270, 1));
        separator.setForeground(UIConstants.BORDER_COLOR);
        centerPanel.add(separator);
        
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Benefits section
        JLabel benefitsTitle = new JLabel("WHAT'S INCLUDED", SwingConstants.CENTER);
        benefitsTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        benefitsTitle.setForeground(UIConstants.TEXT_SECONDARY);
        benefitsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(benefitsTitle);
        
        centerPanel.add(Box.createVerticalStrut(12));
        
        // Benefits list
        JPanel benefitsPanel = new JPanel();
        benefitsPanel.setLayout(new BoxLayout(benefitsPanel, BoxLayout.Y_AXIS));
        benefitsPanel.setOpaque(false);
        
        List<String> benefits = generateBenefitsForPlan(plan);
        for (String benefit : benefits) {
            JPanel benefitRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
            benefitRow.setOpaque(false);
            benefitRow.setMaximumSize(new Dimension(270, 35));
            
            JLabel checkIcon = new JLabel("✓");
            checkIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
            checkIcon.setForeground(UIConstants.SUCCESS_COLOR);
            
            JLabel benefitLabel = new JLabel(benefit);
            benefitLabel.setFont(UIConstants.NORMAL_FONT);
            benefitLabel.setForeground(UIConstants.TEXT_PRIMARY);
            
            benefitRow.add(checkIcon);
            benefitRow.add(benefitLabel);
            benefitsPanel.add(benefitRow);
        }
        
        centerPanel.add(benefitsPanel);
        
        mainContainer.add(centerPanel, BorderLayout.CENTER);
        
        // Subscribe button at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setOpaque(false);
        
        ModernButton subscribeButton = new ModernButton("Subscribe Now", cardStartColor, cardEndColor);
        subscribeButton.setPreferredSize(new Dimension(220, 45));
        subscribeButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        if (onSubscribeAction != null) {
            subscribeButton.addActionListener(e -> onSubscribeAction.run());
        }
        
        buttonPanel.add(subscribeButton);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private List<String> generateBenefitsForPlan(Plan plan) {
        List<String> benefits = new ArrayList<>();
        
        // Get data information
        BigDecimal dataPerDay = plan.getDataPerDayGb();
        BigDecimal totalData = plan.getTotalDataGb();
        int validityDays = plan.getValidityDays();
        
        // Add benefits based on plan features
        benefits.add("High-speed internet access");
        
        if (dataPerDay != null && dataPerDay.compareTo(BigDecimal.ZERO) > 0) {
            benefits.add(dataPerDay + " GB/day data");
        }
        
        if (totalData != null && totalData.compareTo(BigDecimal.ZERO) > 0) {
            benefits.add(totalData + " GB total data");
        }
        
        benefits.add(validityDays + " days validity");
        
        if (plan.getVoiceBenefits() != null && !plan.getVoiceBenefits().isEmpty()) {
            benefits.add(plan.getVoiceBenefits());
        }
        
        if (plan.getSmsPerDay() != null && plan.getSmsPerDay() > 0) {
            benefits.add(plan.getSmsPerDay() + " SMS per day");
        }
        
        if (dataPerDay != null && dataPerDay.compareTo(new BigDecimal("2")) >= 0) {
            benefits.add("4K streaming support");
            benefits.add("Online gaming optimized");
        }
        
        if (plan.getPriceInr().compareTo(new BigDecimal("1500")) >= 0) {
            benefits.add("24/7 priority support");
            benefits.add("Free router included");
        } else {
            benefits.add("24/7 customer support");
        }
        
        benefits.add("Easy online billing");
        benefits.add("Quick installation");
        
        return benefits;
    }
    
    private String getSpeedDisplay(Plan plan) {
        // Calculate approximate speed based on data limits
        BigDecimal dataPerDay = plan.getDataPerDayGb();
        if (dataPerDay != null) {
            if (dataPerDay.compareTo(new BigDecimal("3")) >= 0) {
                return "Up to 300 Mbps";
            } else if (dataPerDay.compareTo(new BigDecimal("2")) >= 0) {
                return "Up to 200 Mbps";
            } else if (dataPerDay.compareTo(new BigDecimal("1")) >= 0) {
                return "Up to 100 Mbps";
            }
        }
        return "Up to 50 Mbps";
    }
    
    private String getDataDisplay(Plan plan) {
        BigDecimal dataPerDay = plan.getDataPerDayGb();
        BigDecimal totalData = plan.getTotalDataGb();
        
        if (dataPerDay != null && dataPerDay.compareTo(BigDecimal.ZERO) > 0) {
            return dataPerDay + " GB/day";
        } else if (totalData != null && totalData.compareTo(BigDecimal.ZERO) > 0) {
            return totalData + " GB total";
        }
        return "Fair usage policy";
    }
    
    private void setupHoverEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw shadow with hover effect
        int shadowOffset = isHovered ? 12 : 6;
        float shadowAlpha = isHovered ? 0.25f : 0.15f;
        
        for (int i = 0; i < shadowOffset; i++) {
            float alpha = shadowAlpha * (1 - ((float)i / shadowOffset));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.BLACK);
            g2.fillRoundRect(i, i, width - i, height - i, 20, 20);
        }
        
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Draw white background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width, height, 20, 20);
        
        // Draw gradient border on hover
        if (isHovered) {
            g2.setStroke(new BasicStroke(3));
            GradientPaint borderGradient = new GradientPaint(
                0, 0, cardStartColor,
                width, height, cardEndColor
            );
            g2.setPaint(borderGradient);
            g2.drawRoundRect(1, 1, width - 3, height - 3, 20, 20);
        } else {
            g2.setColor(UIConstants.BORDER_COLOR);
            g2.drawRoundRect(0, 0, width - 1, height - 1, 20, 20);
        }
        
        // Draw gradient accent bar at top
        GradientPaint topGradient = new GradientPaint(
            0, 0, cardStartColor,
            width, 0, cardEndColor
        );
        g2.setPaint(topGradient);
        g2.fillRoundRect(0, 0, width, 6, 20, 20);
        
        g2.dispose();
        super.paintComponent(g);
    }
}
