package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;
import com.isp.ui.utils.UIHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Status Badge component with pill shape and color coding
 */
public class StatusBadge extends JLabel {

    private String status;
    private Color backgroundColor;

    public StatusBadge(String status) {
        super(status.toUpperCase());
        this.status = status;
        this.backgroundColor = UIConstants.getStatusColor(status);

        initializeBadge();
    }

    public StatusBadge(String status, Color customColor) {
        super(status.toUpperCase());
        this.status = status;
        this.backgroundColor = customColor;

        initializeBadge();
    }

    private void initializeBadge() {
        setFont(UIConstants.FONT_SMALL);
        setForeground(UIConstants.TEXT_PRIMARY);
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(UIConstants.BADGE_PADDING_VERTICAL,
                                                UIConstants.BADGE_PADDING_HORIZONTAL,
                                                UIConstants.BADGE_PADDING_VERTICAL,
                                                UIConstants.BADGE_PADDING_HORIZONTAL));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIHelper.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Background
        UIHelper.drawRoundedRect(g2, 0, 0, width, height, UIConstants.BORDER_RADIUS_BADGE, backgroundColor);

        // Subtle border
        g2.setColor(backgroundColor.darker());
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, width - 1, height - 1, UIConstants.BORDER_RADIUS_BADGE, UIConstants.BORDER_RADIUS_BADGE);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width + UIConstants.BADGE_PADDING_HORIZONTAL * 2,
                           UIConstants.FONT_SMALL.getSize() + UIConstants.BADGE_PADDING_VERTICAL * 2);
    }

    // Getters and setters
    public void setStatus(String status) {
        this.status = status;
        this.backgroundColor = UIConstants.getStatusColor(status);
        setText(status.toUpperCase());
        repaint();
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}