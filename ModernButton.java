package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;
import com.isp.ui.utils.UIHelper;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;

public class ModernButton extends JButton {

    private boolean isHovered = false;
    private float scale = 1.0f;
    private int shadowSize = 0;
    private Timer animationTimer;
    private boolean useGradient = true;
    private Color gradientStartColor;
    private Color gradientEndColor;

    public ModernButton(String text) {
        super(text);
        initializeStyle();
        setupAnimations();
    }

    public ModernButton(String text, Color backgroundColor) {
        super(text);
        this.useGradient = false;
        initializeStyle();
        setBackground(backgroundColor);
        setupAnimations();
    }

    public ModernButton(String text, Color gradientStart, Color gradientEnd) {
        super(text);
        this.useGradient = true;
        this.gradientStartColor = gradientStart;
        this.gradientEndColor = gradientEnd;
        initializeStyle();
        setupAnimations();
    }

    private void initializeStyle() {
        setFont(UIConstants.FONT_BUTTON);
        setForeground(UIConstants.TEXT_PRIMARY);

        if (useGradient && gradientStartColor == null) {
            gradientStartColor = UIConstants.PURPLE_GRADIENT_START;
            gradientEndColor = UIConstants.PURPLE_GRADIENT_END;
        } else if (!useGradient) {
            setBackground(UIConstants.ACCENT_PURPLE);
        }

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(140, UIConstants.BUTTON_HEIGHT + 5));
        setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
    }

    private void setupAnimations() {
        // Mouse hover listener for scale and shadow effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                isHovered = true;
                startScaleAnimation(UIConstants.HOVER_SCALE, UIConstants.SHADOW_OFFSET);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                isHovered = false;
                startScaleAnimation(1.0f, 0);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                startScaleAnimation(UIConstants.PRESSED_SCALE, UIConstants.SHADOW_OFFSET / 2);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (isHovered) {
                    startScaleAnimation(UIConstants.HOVER_SCALE, UIConstants.SHADOW_OFFSET);
                } else {
                    startScaleAnimation(1.0f, 0);
                }
            }
        });
    }

    private void startScaleAnimation(float targetScale, int targetShadow) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(UIConstants.ANIMATION_DURATION / 60, new ActionListener() { // ~60 FPS
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean needsUpdate = false;

                // Animate scale
                if (Math.abs(scale - targetScale) > 0.01f) {
                    scale += (targetScale - scale) * 0.3f;
                    needsUpdate = true;
                } else {
                    scale = targetScale;
                }

                // Animate shadow
                if (Math.abs(shadowSize - targetShadow) > 0.5) {
                    shadowSize += (int)((targetShadow - shadowSize) * 0.3f);
                    needsUpdate = true;
                } else {
                    shadowSize = targetShadow;
                }

                if (needsUpdate) {
                    repaint();
                } else {
                    animationTimer.stop();
                }
            }
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIHelper.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Calculate scaled dimensions
        int scaledWidth = (int)(width * scale);
        int scaledHeight = (int)(height * scale);
        int x = (width - scaledWidth) / 2;
        int y = (height - scaledHeight) / 2;

        // Draw shadow
        if (shadowSize > 0) {
            UIHelper.createShadow(g2, x, y, scaledWidth, scaledHeight, UIConstants.BORDER_RADIUS_BUTTON, UIConstants.SHADOW_COLOR);
        }

        // Draw button background with gradient or solid color
        if (useGradient) {
            UIHelper.createRoundedGradientBackground(
                gradientStartColor, gradientEndColor, g2,
                x, y, scaledWidth, scaledHeight, UIConstants.BORDER_RADIUS_BUTTON
            );
        } else {
            Color bgColor = getBackground();
            if (getModel().isPressed()) {
                bgColor = bgColor.darker();
            } else if (isHovered) {
                bgColor = bgColor.brighter();
            }
            UIHelper.drawRoundedRect(g2, x, y, scaledWidth, scaledHeight, UIConstants.BORDER_RADIUS_BUTTON, bgColor);
        }

        // Add subtle inner highlight for depth
        if (isHovered) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2.setColor(Color.WHITE);
            UIHelper.drawRoundedRect(g2, x, y, scaledWidth, scaledHeight / 2, UIConstants.BORDER_RADIUS_BUTTON, Color.WHITE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // Draw button text
        g2.setColor(getForeground());
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (scaledWidth - fm.stringWidth(getText())) / 2;
        int textY = y + ((scaledHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width + 40, UIConstants.BUTTON_HEIGHT + 5);
    }
}
