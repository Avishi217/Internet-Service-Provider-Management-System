package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;
import com.isp.ui.utils.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern Card component with dark theme, glassmorphism effects, and smooth animations
 */
public class ModernCard extends JPanel {

    private String title;
    private String value;
    private String subtitle;
    private Icon icon;
    private Color accentColor;
    private boolean isHovered = false;

    public ModernCard(String title, String value, String subtitle, Icon icon, Color accentColor) {
        this.title = title;
        this.value = value;
        this.subtitle = subtitle;
        this.icon = icon;
        this.accentColor = accentColor;

        initializeCard();
        setupHoverEffect();
    }

    public ModernCard(String title, String value, String subtitle, Color accentColor) {
        this(title, value, subtitle, null, accentColor);
    }

    private void initializeCard() {
        setLayout(new BorderLayout(UIConstants.CARD_PADDING, UIConstants.CARD_PADDING));
        setBackground(UIConstants.CARD_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(UIConstants.CARD_PADDING, UIConstants.CARD_PADDING,
                                                UIConstants.CARD_PADDING, UIConstants.CARD_PADDING));
        setPreferredSize(new Dimension(280, 140));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon panel
        if (icon != null) {
            JPanel iconPanel = createIconPanel();
            add(iconPanel, BorderLayout.WEST);
        }

        // Content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createIconPanel() {
        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setOpaque(false);

        // Icon circle background
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIHelper.enableAntiAliasing(g2);

                int size = 50;
                UIHelper.createRoundedGradientBackground(
                    accentColor, accentColor.brighter(), g2,
                    0, 0, size, size, size / 2
                );

                // Draw icon
                if (icon != null) {
                    int iconX = (size - icon.getIconWidth()) / 2;
                    int iconY = (size - icon.getIconHeight()) / 2;
                    icon.paintIcon(this, g2, iconX, iconY);
                }

                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(50, 50));
        iconCircle.setOpaque(false);

        iconPanel.add(iconCircle);
        return iconPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.FONT_BODY);
        titleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UIConstants.FONT_TITLE);
        valueLabel.setForeground(UIConstants.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UIConstants.FONT_SMALL);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(4));
        contentPanel.add(subtitleLabel);

        return contentPanel;
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
        UIHelper.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Shadow effect
        if (isHovered) {
            UIHelper.createShadow(g2, 0, 0, width, height, UIConstants.BORDER_RADIUS_CARD, UIConstants.SHADOW_COLOR);
        }

        // Card background
        Color bgColor = isHovered ? UIConstants.CARD_BACKGROUND.brighter() : UIConstants.CARD_BACKGROUND;
        UIHelper.drawRoundedRect(g2, 0, 0, width, height, UIConstants.BORDER_RADIUS_CARD, bgColor);

        // Subtle border
        g2.setColor(UIConstants.BORDER_COLOR);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(1, 1, width - 2, height - 2, UIConstants.BORDER_RADIUS_CARD, UIConstants.BORDER_RADIUS_CARD);

        // Accent top border
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, width, 3, UIConstants.BORDER_RADIUS_CARD, UIConstants.BORDER_RADIUS_CARD);

        g2.dispose();
        super.paintComponent(g);
    }

    // Getters and setters
    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    public void setValue(String value) {
        this.value = value;
        repaint();
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        repaint();
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
        repaint();
    }
}