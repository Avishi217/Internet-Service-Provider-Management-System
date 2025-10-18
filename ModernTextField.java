package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;
import com.isp.ui.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;

public class ModernTextField extends JTextField {

    private String placeholder = "";
    private boolean isFocused = false;
    private Color borderColor = UIConstants.BORDER_COLOR;
    private Color focusBorderColor = UIConstants.ACCENT_BLUE;
    private Color errorBorderColor = UIConstants.ACCENT_RED;
    private boolean hasError = false;
    private String errorMessage = "";
    private Icon leftIcon = null;
    private int leftIconPadding = 8;

    public ModernTextField() {
        super();
        initializeStyle();
    }

    public ModernTextField(int columns) {
        super(columns);
        initializeStyle();
    }

    public ModernTextField(String text) {
        super(text);
        initializeStyle();
    }

    private void initializeStyle() {
        setFont(UIConstants.FONT_BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(UIConstants.SECONDARY_BACKGROUND);
        setCaretColor(UIConstants.TEXT_PRIMARY);
        setBorder(new EmptyBorder(UIConstants.TEXTFIELD_PADDING_VERTICAL,
                                UIConstants.TEXTFIELD_PADDING_HORIZONTAL,
                                UIConstants.TEXTFIELD_PADDING_VERTICAL,
                                UIConstants.TEXTFIELD_PADDING_HORIZONTAL));
        setOpaque(false);

        // Focus listener for border color and glow effect
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public void setLeftIcon(Icon icon) {
        this.leftIcon = icon;
        repaint();
    }

    public void setError(boolean hasError, String message) {
        this.hasError = hasError;
        this.errorMessage = message;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIHelper.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Draw background
        UIHelper.drawRoundedRect(g2, 0, 0, width, height, UIConstants.BORDER_RADIUS_TEXTFIELD, getBackground());

        // Draw border with glow effect when focused
        Color currentBorderColor = hasError ? errorBorderColor :
                                  (isFocused ? focusBorderColor : borderColor);

        if (isFocused && !hasError) {
            // Glow effect
            g2.setColor(new Color(focusBorderColor.getRed(), focusBorderColor.getGreen(),
                                focusBorderColor.getBlue(), 50));
            UIHelper.drawRoundedRect(g2, -1, -1, width + 2, height + 2, UIConstants.BORDER_RADIUS_TEXTFIELD + 1,
                                   new Color(focusBorderColor.getRed(), focusBorderColor.getGreen(),
                                           focusBorderColor.getBlue(), 50));
        }

        // Border
        g2.setColor(currentBorderColor);
        g2.setStroke(new BasicStroke(hasError || isFocused ? 2 : 1));
        RoundRectangle2D borderRect = new RoundRectangle2D.Float(
            hasError || isFocused ? 0.5f : 0, hasError || isFocused ? 0.5f : 0,
            width - (hasError || isFocused ? 1 : 0), height - (hasError || isFocused ? 1 : 0),
            UIConstants.BORDER_RADIUS_TEXTFIELD, UIConstants.BORDER_RADIUS_TEXTFIELD
        );
        g2.draw(borderRect);

        // Draw left icon if present
        int textX = UIConstants.TEXTFIELD_PADDING_HORIZONTAL;
        if (leftIcon != null) {
            int iconY = (height - leftIcon.getIconHeight()) / 2;
            leftIcon.paintIcon(this, g2, UIConstants.TEXTFIELD_PADDING_HORIZONTAL, iconY);
            textX += leftIcon.getIconWidth() + leftIconPadding;
        }

        // Draw placeholder text
        if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(UIConstants.TEXT_SECONDARY);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int placeholderY = (height + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(placeholder, textX, placeholderY);
        }

        g2.dispose();

        // Call super to draw text
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(Math.max(size.width, 200), UIConstants.INPUT_HEIGHT);
    }

    // Override to adjust insets for icon
    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        if (leftIcon != null) {
            insets.left += leftIcon.getIconWidth() + leftIconPadding;
        }
        return insets;
    }
}
