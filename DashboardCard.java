package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardCard extends JPanel {
    
    private Color accentColor;
    private boolean isHovered = false;
    
    public DashboardCard(String title, String value, String description, Color accentColor) {
        this.accentColor = accentColor;
        
        setLayout(new BorderLayout(15, 0));
        setBackground(UIConstants.CARD_BACKGROUND);
        setBorder(new EmptyBorder(25, 25, 25, 25));
        setPreferredSize(new Dimension(280, 140));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
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
        
        // Icon panel on the left
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.Y_AXIS));
        iconPanel.setOpaque(false);
        
        // Create icon circle with gradient
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, accentColor,
                    getWidth(), getHeight(), accentColor.brighter()
                );
                g2.setPaint(gradient);
                g2.fillOval(0, 0, 60, 60);
                
                // Icon (using emoji/symbol)
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                String icon = getIconForColor(accentColor);
                FontMetrics fm = g2.getFontMetrics();
                int x = (60 - fm.stringWidth(icon)) / 2;
                int y = ((60 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(icon, x, y);
                
                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(60, 60));
        iconCircle.setMaximumSize(new Dimension(60, 60));
        iconCircle.setOpaque(false);
        
        iconPanel.add(iconCircle);
        add(iconPanel, BorderLayout.WEST);
        
        // Content panel on the right
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.CARD_BACKGROUND);
        contentPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.NORMAL_FONT);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(UIConstants.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(UIConstants.SMALL_FONT);
        descLabel.setForeground(UIConstants.TEXT_TERTIARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(descLabel);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private String getIconForColor(Color color) {
        // Return appropriate icon based on accent color
        if (color.equals(UIConstants.ACCENT_COLOR)) {
            return "👥"; // Users
        } else if (color.equals(UIConstants.SUCCESS_COLOR)) {
            return "₹"; // Money
        } else if (color.equals(UIConstants.WARNING_COLOR)) {
            return "⚠️"; // Warning
        } else if (color.equals(UIConstants.DANGER_COLOR)) {
            return "📋"; // Tasks
        } else if (color.equals(UIConstants.INFO_COLOR)) {
            return "📊"; // Stats
        }
        return "📈"; // Default
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw shadow with hover effect
        int shadowOffset = isHovered ? 10 : 4;
        float shadowAlpha = isHovered ? 0.2f : 0.1f;
        
        for (int i = 0; i < shadowOffset; i++) {
            float alpha = shadowAlpha * (1 - ((float)i / shadowOffset));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.BLACK);
            g2.fillRoundRect(i, i, width - i, height - i, 15, 15);
        }
        
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Draw white background
        g2.setColor(isHovered ? UIConstants.CARD_HOVER : Color.WHITE);
        g2.fillRoundRect(0, 0, width, height, 15, 15);
        
        // Draw subtle border
        g2.setColor(UIConstants.BORDER_COLOR);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);
        
        // Draw accent bar on top
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, width, 4, 15, 15);
        
        g2.dispose();
        super.paintComponent(g);
    }
}
