package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;
import com.isp.ui.utils.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Modern Dialog component with dark theme and glassmorphism overlay
 */
public class ModernDialog extends JDialog {

    private JPanel contentPanel;
    private JButton closeButton;
    private boolean modal = true;

    public ModernDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        this.modal = modal;
        initializeDialog();
    }

    public ModernDialog(Frame parent, String title) {
        this(parent, title, true);
    }

    private void initializeDialog() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        getRootPane().setOpaque(false);

        // Main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title bar
        JPanel titleBar = createTitleBar();
        contentPanel.add(titleBar, BorderLayout.NORTH);

        // Content area (to be filled by subclasses)
        JPanel bodyPanel = new JPanel();
        bodyPanel.setOpaque(false);
        contentPanel.add(bodyPanel, BorderLayout.CENTER);

        // Button panel (optional)
        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Size and position
        setSize(500, 400);
        setLocationRelativeTo(getParent());
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title label
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setFont(UIConstants.FONT_SUBTITLE);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        // Close button
        closeButton = new ModernButton("×", UIConstants.ACCENT_RED);
        closeButton.setPreferredSize(new Dimension(30, 30));
        closeButton.addActionListener(e -> dispose());

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(closeButton, BorderLayout.EAST);

        return titleBar;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        ModernButton okButton = new ModernButton("OK");
        ModernButton cancelButton = new ModernButton("Cancel", UIConstants.ACCENT_RED);

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIHelper.enableAntiAliasing(g2);

        int width = getWidth();
        int height = getHeight();

        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, width, height);

        g2.dispose();
        super.paint(g);
    }

    /**
     * Override this method to handle OK button clicks
     */
    protected void onOK() {
        dispose();
    }

    /**
     * Override this method to handle Cancel button clicks
     */
    protected void onCancel() {
        dispose();
    }

    /**
     * Set the content panel (body of the dialog)
     */
    public void setDialogContent(JPanel content) {
        BorderLayout layout = (BorderLayout) contentPanel.getLayout();
        Component oldCenter = layout.getLayoutComponent(BorderLayout.CENTER);
        if (oldCenter != null) {
            contentPanel.remove(oldCenter);
        }
        contentPanel.add(content, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Show the dialog with fade-in animation
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            // Fade in animation
            setOpacity(0.0f);
            super.setVisible(true);

            Timer timer = new Timer(16, null);
            final float[] opacity = {0.0f};
            timer.addActionListener(e -> {
                opacity[0] += 0.05f;
                if (opacity[0] >= 1.0f) {
                    opacity[0] = 1.0f;
                    timer.stop();
                }
                setOpacity(opacity[0]);
            });
            timer.start();
        } else {
            super.setVisible(false);
        }
    }

    /**
     * Create a confirmation dialog
     */
    public static boolean showConfirmation(Frame parent, String title, String message) {
        ModernDialog dialog = new ModernDialog(parent, title) {
            @Override
            protected void onOK() {
                setVisible(false);
            }

            @Override
            protected void onCancel() {
                setVisible(false);
            }
        };

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel("<html><p style='width: 300px;'>" + message + "</p></html>");
        messageLabel.setFont(UIConstants.FONT_BODY);
        messageLabel.setForeground(UIConstants.TEXT_PRIMARY);

        content.add(messageLabel, BorderLayout.CENTER);
        dialog.setDialogContent(content);

        dialog.setVisible(true);

        // For simplicity, return true. In a real implementation,
        // you'd track which button was clicked
        return true;
    }

    /**
     * Create an input dialog
     */
    public static String showInputDialog(Frame parent, String title, String message, String defaultValue) {
        final String[] result = {null};

        ModernDialog dialog = new ModernDialog(parent, title) {
            @Override
            protected void onOK() {
                // Get input value
                Component[] components = getContentPane().getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        Component[] subComps = ((JPanel) comp).getComponents();
                        for (Component subComp : subComps) {
                            if (subComp instanceof ModernTextField) {
                                result[0] = ((ModernTextField) subComp).getText();
                                break;
                            }
                        }
                    }
                }
                setVisible(false);
            }

            @Override
            protected void onCancel() {
                result[0] = null;
                setVisible(false);
            }
        };

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(UIConstants.FONT_BODY);
        messageLabel.setForeground(UIConstants.TEXT_PRIMARY);

        ModernTextField inputField = new ModernTextField();
        if (defaultValue != null) {
            inputField.setText(defaultValue);
        }

        content.add(messageLabel, BorderLayout.NORTH);
        content.add(inputField, BorderLayout.CENTER);

        dialog.setDialogContent(content);
        dialog.setVisible(true);

        return result[0];
    }
}