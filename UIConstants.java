package com.isp.ui.utils;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class UIConstants {

    // Currency
    public static final String CURRENCY_SYMBOL = "₹";
    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("₹#,##0.00");
    public static final DecimalFormat INDIAN_NUMBER_FORMAT = new DecimalFormat("#,##,##0.##");
    
    // Dark Theme Color Palette
    public static final Color PRIMARY_BACKGROUND = new Color(15, 23, 42); // #0f172a
    public static final Color SECONDARY_BACKGROUND = new Color(30, 41, 59); // #1e293b
    public static final Color CARD_BACKGROUND = new Color(51, 65, 85); // #334155
    public static final Color BORDER_COLOR = new Color(71, 85, 105); // #475569
    public static final Color TEXT_PRIMARY = new Color(255, 255, 255); // #ffffff
    public static final Color TEXT_SECONDARY = new Color(203, 213, 225); // #cbd5e1
    public static final Color TEXT_TERTIARY = new Color(148, 163, 184); // #94a3b8
    
    // Accent Colors
    public static final Color ACCENT_PURPLE = new Color(139, 92, 246); // #8b5cf6
    public static final Color ACCENT_BLUE = new Color(59, 130, 246); // #3b82f6
    public static final Color ACCENT_GREEN = new Color(16, 185, 129); // #10b981
    public static final Color ACCENT_RED = new Color(239, 68, 68); // #ef4444
    public static final Color ACCENT_ORANGE = new Color(245, 158, 11); // #f59e0b
    
    // Gradients
    public static final Color PURPLE_GRADIENT_START = new Color(139, 92, 246); // #8b5cf6
    public static final Color PURPLE_GRADIENT_END = new Color(124, 58, 237); // #7c3aed
    public static final Color ACCENT_GRADIENT_START = new Color(59, 130, 246); // #3b82f6
    public static final Color ACCENT_GRADIENT_END = new Color(37, 99, 235); // #2563eb
    public static final Color SUCCESS_GRADIENT_START = new Color(16, 185, 129); // #10b981
    public static final Color SUCCESS_GRADIENT_END = new Color(5, 150, 105); // #059669
    
    // Status Colors
    public static final Color STATUS_ACTIVE = ACCENT_GREEN;
    public static final Color STATUS_PENDING = ACCENT_ORANGE;
    public static final Color STATUS_SUSPENDED = ACCENT_RED;
    public static final Color STATUS_PROCESSING = ACCENT_BLUE;
    
    // Additional Status Colors
    public static final Color SUCCESS_COLOR = STATUS_ACTIVE;
    public static final Color WARNING_COLOR = STATUS_PENDING;
    public static final Color DANGER_COLOR = STATUS_SUSPENDED;
    public static final Color INFO_COLOR = ACCENT_BLUE;
    public static final Color WARNING_DARK = new Color(217, 119, 6); // #d97706
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 10);
    
    // Sizes
    public static final int BORDER_RADIUS_BUTTON = 15;
    public static final int BORDER_RADIUS_TEXTFIELD = 12;
    public static final int BORDER_RADIUS_CARD = 20;
    public static final int BORDER_RADIUS_DIALOG = 20;
    public static final int BORDER_RADIUS_BADGE = 12;
    
    public static final int SIDEBAR_WIDTH = 250;
    public static final int CARD_PADDING = 20;
    public static final int TEXTFIELD_PADDING_VERTICAL = 12;
    public static final int TEXTFIELD_PADDING_HORIZONTAL = 16;
    public static final int BADGE_PADDING_VERTICAL = 6;
    public static final int BADGE_PADDING_HORIZONTAL = 12;
    
    // Animation
    public static final int ANIMATION_DURATION = 200; // milliseconds
    public static final float HOVER_SCALE = 1.05f;
    public static final float PRESSED_SCALE = 0.95f;
    
    // Shadows
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);
    public static final int SHADOW_OFFSET = 4;
    
    // Glassmorphism
    public static final Color GLASS_BACKGROUND = new Color(255, 255, 255, 10);
    public static final Color GLASS_BORDER = new Color(255, 255, 255, 20);
    
    // Legacy Colors (for backward compatibility)
    public static final Color BACKGROUND_COLOR = PRIMARY_BACKGROUND;
    public static final Color SIDEBAR_COLOR = PRIMARY_BACKGROUND;
    public static final Color SIDEBAR_HOVER = SECONDARY_BACKGROUND;
    public static final Color SIDEBAR_SELECTED = ACCENT_PURPLE;
    public static final Color SIDEBAR_TEXT = TEXT_PRIMARY;
    public static final Color PRIMARY_COLOR = PRIMARY_BACKGROUND;
    public static final Color ACCENT_COLOR = ACCENT_PURPLE;
    public static final Color BACKGROUND_DARK = PRIMARY_BACKGROUND;
    public static final Color CARD_HOVER = new Color(71, 85, 105); // #475569
    public static final Color AIRTEL_RED = new Color(220, 38, 38); // #dc2626
    public static final Color AIRTEL_RED_DARK = new Color(185, 28, 28); // #b91c1c
    
    // Gradient aliases for backward compatibility
    public static final Color PRIMARY_GRADIENT_START = PURPLE_GRADIENT_START;
    public static final Color PRIMARY_GRADIENT_END = PURPLE_GRADIENT_END;
    
    // Fonts (legacy)
    public static final Font TITLE_FONT = FONT_TITLE;
    public static final Font SUBTITLE_FONT = FONT_SUBTITLE;
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font NORMAL_FONT = FONT_BODY;
    public static final Font SMALL_FONT = FONT_SMALL;
    public static final Font BUTTON_FONT = FONT_BUTTON;
    public static final Font MENU_FONT = FONT_BODY;
    
    // Dimensions
    public static final int HEADER_HEIGHT = 60;
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 35;
    public static final int BORDER_RADIUS = BORDER_RADIUS_BUTTON;
    
    // Spacing
    public static final int SPACING_SMALL = 5;
    public static final int SPACING_MEDIUM = 10;
    public static final int SPACING_LARGE = 20;
    public static final int SPACING_XLARGE = 30;
    
    /**
     * Format amount as Indian currency: ₹1,299.00
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) return CURRENCY_SYMBOL + "0.00";
        return CURRENCY_FORMAT.format(amount);
    }
    
    /**
     * Format amount as Indian currency: ₹1,299.00
     */
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }
    
    /**
     * Format number in Indian style: 12,34,567
     */
    public static String formatIndianNumber(double number) {
        return INDIAN_NUMBER_FORMAT.format(number);
    }
    
    /**
     * Format number in Indian system: 1,00,000 (1 lakh)
     */
    public static String formatIndianNumber(long number) {
        return INDIAN_NUMBER_FORMAT.format(number);
    }
    
    /**
     * Get status color based on status string
     */
    public static Color getStatusColor(String status) {
        if (status == null) return TEXT_SECONDARY;
        switch (status.toLowerCase()) {
            case "active":
            case "paid":
            case "resolved":
                return STATUS_ACTIVE;
            case "pending":
            case "unpaid":
            case "in_progress":
                return STATUS_PENDING;
            case "suspended":
            case "overdue":
            case "closed":
                return STATUS_SUSPENDED;
            case "processing":
                return STATUS_PROCESSING;
            default:
                return TEXT_SECONDARY;
        }
    }
    
    private UIConstants() {
        // Private constructor to prevent instantiation
    }
}
