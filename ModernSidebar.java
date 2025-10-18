package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;
import com.isp.ui.utils.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Sidebar component for navigation with dark theme
 */
public class ModernSidebar extends JPanel {

    private List<SidebarItem> menuItems;
    private SidebarItem selectedItem;
    private JPanel profilePanel;
    private JButton logoutButton;

    public ModernSidebar() {
        this.menuItems = new ArrayList<>();
        initializeSidebar();
    }

    private void initializeSidebar() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.PRIMARY_BACKGROUND);
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.BORDER_COLOR));

        // Profile panel at top
        profilePanel = createProfilePanel();
        add(profilePanel, BorderLayout.NORTH);

        // Menu items in center
        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.CENTER);

        // Logout button at bottom
        logoutButton = createLogoutButton();
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bottomPanel.add(logoutButton, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        // Profile picture placeholder
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                UIHelper.enableAntiAliasing(g2);

                int size = 50;
                UIHelper.createRoundedGradientBackground(
                    UIConstants.ACCENT_PURPLE, UIConstants.ACCENT_BLUE, g2,
                    0, 0, size, size, size / 2
                );

                // Draw user icon
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "👤";
                int x = (size - fm.stringWidth(icon)) / 2;
                int y = (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(icon, x, y);

                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(50, 50));
        avatarPanel.setOpaque(false);

        // Profile info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Admin User");
        nameLabel.setFont(UIConstants.FONT_BUTTON);
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel roleLabel = new JLabel("Administrator");
        roleLabel.setFont(UIConstants.FONT_SMALL);
        roleLabel.setForeground(UIConstants.TEXT_SECONDARY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(roleLabel);

        panel.add(avatarPanel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Add default menu items
        addMenuItem("Dashboard", "📊");
        addMenuItem("Customers", "👥");
        addMenuItem("Plans", "📋");
        addMenuItem("Billing", "💰");
        addMenuItem("Complaints", "⚠️");
        addMenuItem("Reports", "📈");

        return panel;
    }

    private JButton createLogoutButton() {
        ModernButton logoutBtn = new ModernButton("Logout", UIConstants.ACCENT_RED);
        logoutBtn.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 40, 40));
        return logoutBtn;
    }

    public void addMenuItem(String text, String icon) {
        SidebarItem item = new SidebarItem(text, icon);
        menuItems.add(item);

        // Add to the menu panel (assuming it's the center component)
        Container parent = getParent();
        if (parent != null && parent.getComponentCount() > 1) {
            Component centerComp = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (centerComp instanceof JPanel) {
                ((JPanel) centerComp).add(item);
                revalidate();
                repaint();
            }
        }
    }

    public void setSelectedItem(String text) {
        for (SidebarItem item : menuItems) {
            if (item.getText().equals(text)) {
                if (selectedItem != null) {
                    selectedItem.setSelected(false);
                }
                item.setSelected(true);
                selectedItem = item;
                break;
            }
        }
    }

    public void setProfileInfo(String name, String role) {
        // Update profile panel labels
        Component[] components = profilePanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        if (label.getFont().getSize() == UIConstants.FONT_BUTTON.getSize()) {
                            label.setText(name);
                        } else if (label.getFont().getSize() == UIConstants.FONT_SMALL.getSize()) {
                            label.setText(role);
                        }
                    }
                }
            }
        }
        repaint();
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }

    /**
     * Inner class for sidebar menu items
     */
    private class SidebarItem extends JPanel {
        private String text;
        private String icon;
        private boolean isSelected = false;
        private boolean isHovered = false;

        public SidebarItem(String text, String icon) {
            this.text = text;
            this.icon = icon;

            initializeItem();
        }

        private void initializeItem() {
            setLayout(new BorderLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 50));
            setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 50));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

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

                @Override
                public void mouseClicked(MouseEvent e) {
                    setSelectedItem(text);
                    // Fire action event
                    firePropertyChange("selectedItem", null, text);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            UIHelper.enableAntiAliasing(g2);

            int width = getWidth();
            int height = getHeight();

            Color bgColor;
            if (isSelected) {
                bgColor = UIConstants.ACCENT_PURPLE;
            } else if (isHovered) {
                bgColor = UIConstants.SECONDARY_BACKGROUND;
            } else {
                bgColor = UIConstants.PRIMARY_BACKGROUND;
            }

            UIHelper.drawRoundedRect(g2, 0, 0, width, height, 8, bgColor);

            // Text and icon
            g2.setColor(UIConstants.TEXT_PRIMARY);
            g2.setFont(UIConstants.FONT_BODY);

            // Icon
            FontMetrics fm = g2.getFontMetrics();
            int iconY = (height + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(icon, 20, iconY);

            // Text
            int textX = 60;
            int textY = (height + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, textX, textY);

            g2.dispose();
        }

        public String getText() {
            return text;
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
            repaint();
        }
    }
}