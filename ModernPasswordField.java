package com.isp.ui.components;

import com.isp.ui.utils.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernPasswordField extends JPasswordField {

    public ModernPasswordField() {
        super();
        initializeStyle();
    }

    public ModernPasswordField(int columns) {
        super(columns);
        initializeStyle();
    }

    private void initializeStyle() {
        setFont(UIConstants.NORMAL_FONT);
        setForeground(UIConstants.TEXT_PRIMARY);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        setPreferredSize(new Dimension(200, UIConstants.INPUT_HEIGHT));
        setCaretColor(UIConstants.PRIMARY_COLOR);
        setEchoChar('•');
        
        // Add focus listener for border color change
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.PRIMARY_COLOR, 2),
                    new EmptyBorder(7, 11, 7, 11)
                ));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
                    new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }
}
