package com.isp.ui.utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * UI Helper class providing utility methods for modern UI rendering
 * Includes glassmorphism effects, gradients, shadows, and anti-aliasing
 */
public class UIHelper {

    /**
     * Enable anti-aliasing for smooth graphics rendering
     */
    public static void enableAntiAliasing(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    /**
     * Create a rounded border with specified radius and color
     */
    public static Border createRoundedBorder(int radius, Color color) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(radius/2, radius/2, radius/2, radius/2)
        );
    }

    /**
     * Create a gradient background from two colors
     */
    public static void createGradientBackground(Color c1, Color c2, Graphics2D g2, int width, int height) {
        GradientPaint gradient = new GradientPaint(0, 0, c1, width, height, c2);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);
    }

    /**
     * Create a rounded rectangle gradient background
     */
    public static void createRoundedGradientBackground(Color c1, Color c2, Graphics2D g2, int x, int y, int width, int height, int radius) {
        GradientPaint gradient = new GradientPaint(x, y, c1, x + width, y + height, c2);
        g2.setPaint(gradient);
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(x, y, width, height, radius, radius);
        g2.fill(roundedRect);
    }

    /**
     * Create a shadow effect
     */
    public static void createShadow(Graphics2D g2, int x, int y, int width, int height, int radius, Color shadowColor) {
        g2.setColor(shadowColor);
        RoundRectangle2D shadowRect = new RoundRectangle2D.Float(x + 2, y + 2, width, height, radius, radius);
        g2.fill(shadowRect);
    }

    /**
     * Create a glassmorphism effect
     */
    public static void createGlassmorphismEffect(Graphics2D g2, int x, int y, int width, int height, int radius) {
        // Semi-transparent background
        g2.setColor(UIConstants.GLASS_BACKGROUND);
        RoundRectangle2D glassRect = new RoundRectangle2D.Float(x, y, width, height, radius, radius);
        g2.fill(glassRect);

        // Glass border
        g2.setColor(UIConstants.GLASS_BORDER);
        g2.setStroke(new BasicStroke(1));
        g2.draw(glassRect);
    }

    /**
     * Create a rounded rectangle with specified parameters
     */
    public static void drawRoundedRect(Graphics2D g2, int x, int y, int width, int height, int radius, Color color) {
        g2.setColor(color);
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(x, y, width, height, radius, radius);
        g2.fill(roundedRect);
    }

    /**
     * Create a hover effect by scaling a component
     */
    public static void applyHoverEffect(Component component, float scale) {
        // This would typically be used with custom painting
        // For now, we'll use basic scaling
        Dimension size = component.getPreferredSize();
        component.setPreferredSize(new Dimension(
            (int)(size.width * scale),
            (int)(size.height * scale)
        ));
        component.revalidate();
    }

    /**
     * Create a smooth color transition for animations
     */
    public static Color interpolateColor(Color c1, Color c2, float ratio) {
        int red = (int)(c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int green = (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int blue = (int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        int alpha = (int)(c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * ratio);
        return new Color(red, green, blue, alpha);
    }

    /**
     * Create a buffered image for smooth rendering
     */
    public static BufferedImage createCompatibleImage(int width, int height) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        return gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    /**
     * Apply a blur effect to an image (simple implementation)
     */
    public static BufferedImage applyBlur(BufferedImage image, int radius) {
        // Simple box blur implementation
        BufferedImage blurred = createCompatibleImage(image.getWidth(), image.getHeight());
        Graphics2D g2 = blurred.createGraphics();
        enableAntiAliasing(g2);

        // For simplicity, we'll just draw with reduced opacity
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return blurred;
    }

    /**
     * Create a modern button style with gradient and shadow
     */
    public static void paintModernButton(Graphics2D g2, JButton button, Color startColor, Color endColor, boolean isHovered) {
        int width = button.getWidth();
        int height = button.getHeight();
        int radius = UIConstants.BORDER_RADIUS_BUTTON;

        enableAntiAliasing(g2);

        // Shadow
        if (isHovered) {
            createShadow(g2, 0, 0, width - 2, height - 2, radius, UIConstants.SHADOW_COLOR);
        }

        // Background gradient
        Color actualStart = isHovered ? startColor.darker() : startColor;
        Color actualEnd = isHovered ? endColor.darker() : endColor;
        createRoundedGradientBackground(actualStart, actualEnd, g2, 0, 0, width, height, radius);

        // Border
        g2.setColor(UIConstants.BORDER_COLOR);
        g2.setStroke(new BasicStroke(1));
        RoundRectangle2D borderRect = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, radius, radius);
        g2.draw(borderRect);
    }

    /**
     * Create a modern card style
     */
    public static void paintModernCard(Graphics2D g2, JComponent component, boolean isHovered) {
        int width = component.getWidth();
        int height = component.getHeight();
        int radius = UIConstants.BORDER_RADIUS_CARD;

        enableAntiAliasing(g2);

        // Shadow effect
        if (isHovered) {
            createShadow(g2, 0, 0, width - 2, height - 2, radius, UIConstants.SHADOW_COLOR);
        }

        // Background
        Color bgColor = isHovered ? UIConstants.CARD_BACKGROUND.brighter() : UIConstants.CARD_BACKGROUND;
        drawRoundedRect(g2, 0, 0, width, height, radius, bgColor);

        // Border
        g2.setColor(UIConstants.BORDER_COLOR);
        g2.setStroke(new BasicStroke(1));
        RoundRectangle2D borderRect = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, radius, radius);
        g2.draw(borderRect);
    }

    /**
     * Utility method to center a component in its parent
     */
    public static void centerComponent(Component component, Container parent) {
        Dimension parentSize = parent.getSize();
        Dimension componentSize = component.getPreferredSize();
        int x = (parentSize.width - componentSize.width) / 2;
        int y = (parentSize.height - componentSize.height) / 2;
        component.setLocation(x, y);
    }

    /**
     * Create a fade-in animation timer
     */
    public static Timer createFadeInTimer(JComponent component, int duration) {
        Timer timer = new Timer(16, null); // ~60 FPS
        final long startTime = System.currentTimeMillis();

        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min((float) elapsed / duration, 1.0f);

            // Apply fade effect
            component.setOpaque(true);
            component.setBackground(interpolateColor(UIConstants.PRIMARY_BACKGROUND,
                                                   UIConstants.SECONDARY_BACKGROUND, progress));
            component.repaint();

            if (progress >= 1.0f) {
                timer.stop();
            }
        });

        return timer;
    }

    /**
     * Show a modern toast notification
     */
    public static void showToast(JFrame parent, String message, Color backgroundColor) {
        JDialog toast = new JDialog(parent, false);
        toast.setUndecorated(true);
        toast.setBackground(new Color(0, 0, 0, 0));

        JLabel label = new JLabel(message);
        label.setFont(UIConstants.FONT_BODY);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                enableAntiAliasing(g2);
                drawRoundedRect(g2, 0, 0, getWidth(), getHeight(), 10, backgroundColor);
            }
        };
        panel.setOpaque(false);
        panel.add(label);
        toast.add(panel);

        toast.pack();
        toast.setLocationRelativeTo(parent);

        // Auto-hide after 3 seconds
        Timer timer = new Timer(3000, e -> toast.setVisible(false));
        timer.setRepeats(false);
        timer.start();

        toast.setVisible(true);
    }

    /**
     * Create a loading spinner component
     */
    public static JComponent createLoadingSpinner() {
        return new JComponent() {
            private float angle = 0;

            {
                setPreferredSize(new Dimension(40, 40));
                Timer timer = new Timer(16, e -> {
                    angle += 5;
                    if (angle >= 360) angle = 0;
                    repaint();
                });
                timer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                enableAntiAliasing(g2);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.min(centerX, centerY) - 5;

                g2.setStroke(new BasicStroke(3));
                g2.setColor(UIConstants.ACCENT_PURPLE);

                // Draw arc
                g2.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                          (int) angle, 270);
            }
        };
    }
}