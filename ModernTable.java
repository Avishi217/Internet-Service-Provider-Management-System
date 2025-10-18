package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;
import com.isp.ui.utils.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Modern Table component with dark theme and alternating row colors
 */
public class ModernTable extends JTable {

    public ModernTable() {
        super();
        initializeTable();
    }

    private void initializeTable() {
        setFont(UIConstants.FONT_BODY);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(UIConstants.SECONDARY_BACKGROUND);
        setGridColor(UIConstants.BORDER_COLOR);
        setSelectionBackground(UIConstants.ACCENT_BLUE);
        setSelectionForeground(UIConstants.TEXT_PRIMARY);
        setRowHeight(40);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));

        // Custom header
        JTableHeader header = getTableHeader();
        header.setFont(UIConstants.FONT_BUTTON);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setBackground(UIConstants.PRIMARY_BACKGROUND);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR));

        // Custom cell renderer
        setDefaultRenderer(Object.class, new ModernTableCellRenderer());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        UIHelper.enableAntiAliasing(g2);

        // Background
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Custom cell renderer for modern table appearance
     */
    private static class ModernTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setFont(UIConstants.FONT_BODY);
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

            if (isSelected) {
                setBackground(UIConstants.ACCENT_BLUE);
                setForeground(UIConstants.TEXT_PRIMARY);
            } else {
                // Alternating row colors
                if (row % 2 == 0) {
                    setBackground(UIConstants.SECONDARY_BACKGROUND);
                } else {
                    setBackground(UIConstants.CARD_BACKGROUND);
                }
                setForeground(UIConstants.TEXT_PRIMARY);
            }

            // Hover effect
            if (table.getSelectedRow() == row && !isSelected) {
                setBackground(UIConstants.BORDER_COLOR);
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            UIHelper.enableAntiAliasing(g2);

            // Background
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
            super.paintComponent(g);
        }
    }
}