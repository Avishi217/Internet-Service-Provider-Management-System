package com.isp.dao;

import com.isp.model.Notification;
import com.isp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    /**
     * Create a new notification
     */
    public int createNotification(Notification notification) {
        String sql = "INSERT INTO notifications (customer_id, type, title, message, is_read, " +
                    "priority, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, notification.getCustomerId());
            pstmt.setString(2, notification.getType());
            pstmt.setString(3, notification.getTitle());
            pstmt.setString(4, notification.getMessage());
            pstmt.setBoolean(5, notification.isRead());
            pstmt.setString(6, notification.getPriority());
            pstmt.setTimestamp(7, Timestamp.valueOf(notification.getCreatedAt()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get unread notifications for customer
     */
    public List<Notification> getUnreadNotifications(int customerId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE customer_id = ? AND is_read = FALSE " +
                    "ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting unread notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Get all notifications for customer
     */
    public List<Notification> getAllNotifications(int customerId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE customer_id = ? ORDER BY created_at DESC LIMIT 50";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Mark notification as read
     */
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE, read_at = NOW() WHERE notification_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, notificationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error marking notification as read: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mark all notifications as read for customer
     */
    public int markAllAsRead(int customerId) {
        String sql = "UPDATE notifications SET is_read = TRUE, read_at = NOW() " +
                    "WHERE customer_id = ? AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error marking all notifications as read: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get unread count for customer
     */
    public int getUnreadCount(int customerId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE customer_id = ? AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting unread count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Delete old notifications (older than specified days)
     */
    public int deleteOldNotifications(int olderThanDays) {
        String sql = "DELETE FROM notifications WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, olderThanDays);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting old notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract Notification from ResultSet
     */
    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setCustomerId(rs.getInt("customer_id"));
        notification.setType(rs.getString("type"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setPriority(rs.getString("priority"));
        notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp readAt = rs.getTimestamp("read_at");
        if (readAt != null) {
            notification.setReadAt(readAt.toLocalDateTime());
        }
        
        return notification;
    }
}
