package com.isp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Notification model - Track customer notifications and alerts
 */
public class Notification {
    private int notificationId;
    private int customerId;
    
    // Notification details
    private String type; // data_limit_alert, plan_expiry, bill_due, payment_success, addon_expired, promotional
    private String title;
    private String message;
    
    // Status
    private boolean isRead;
    private String priority; // low, medium, high
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // Constructors
    public Notification() {
        this.isRead = false;
        this.priority = "medium";
        this.createdAt = LocalDateTime.now();
    }

    public Notification(int customerId, String type, String title, String message) {
        this();
        this.customerId = customerId;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    public Notification(int customerId, String type, String title, String message, String priority) {
        this(customerId, type, title, message);
        this.priority = priority;
    }

    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    // Utility methods
    
    /**
     * Mark notification as read
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * Get icon based on notification type
     */
    public String getIcon() {
        switch (type.toLowerCase()) {
            case "data_limit_alert": return "⚠️";
            case "plan_expiry": return "⏰";
            case "bill_due": return "💰";
            case "payment_success": return "✅";
            case "addon_expired": return "⏱️";
            case "promotional": return "🎁";
            default: return "📢";
        }
    }

    /**
     * Get color based on priority
     */
    public String getPriorityColor() {
        switch (priority.toLowerCase()) {
            case "high": return "#FF0000"; // Red
            case "medium": return "#FFA500"; // Orange
            case "low": return "#808080"; // Gray
            default: return "#000000"; // Black
        }
    }

    /**
     * Get formatted time (e.g., "2 hours ago", "Yesterday")
     */
    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(createdAt, now).getSeconds();
        
        if (seconds < 60) {
            return "Just now";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (seconds < 172800) {
            return "Yesterday";
        } else if (seconds < 604800) {
            long days = seconds / 86400;
            return days + " days ago";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            return createdAt.format(formatter);
        }
    }

    /**
     * Get formatted date time
     */
    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        return createdAt.format(formatter);
    }

    /**
     * Check if notification is high priority
     */
    public boolean isHighPriority() {
        return "high".equalsIgnoreCase(priority);
    }

    /**
     * Get notification category display name
     */
    public String getCategoryDisplay() {
        switch (type.toLowerCase()) {
            case "data_limit_alert": return "Data Usage";
            case "plan_expiry": return "Plan Status";
            case "bill_due": return "Billing";
            case "payment_success": return "Payment";
            case "addon_expired": return "Add-ons";
            case "promotional": return "Offers";
            default: return "General";
        }
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", customerId=" + customerId +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                ", priority='" + priority + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
