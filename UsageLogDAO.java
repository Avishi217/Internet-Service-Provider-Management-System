package com.isp.dao;

import com.isp.model.UsageLog;
import com.isp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsageLogDAO {

    /**
     * Create a new usage log
     * @param usageLog UsageLog object
     * @return Generated usage log ID
     */
    public int createUsageLog(UsageLog usageLog) {
        String sql = "INSERT INTO usage_logs (customer_id, data_used_gb) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, usageLog.getCustomerId());
            pstmt.setDouble(2, usageLog.getDataUsedGB());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Get usage log by ID
     * @param usageId Usage log ID
     * @return UsageLog object
     */
    public UsageLog getUsageLogById(int usageId) {
        String sql = "SELECT * FROM usage_logs WHERE usage_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usageId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUsageLogFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all usage logs
     * @return List of all usage logs
     */
    public List<UsageLog> getAllUsageLogs() {
        List<UsageLog> usageLogs = new ArrayList<>();
        String sql = "SELECT * FROM usage_logs ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usageLogs.add(extractUsageLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return usageLogs;
    }

    /**
     * Get usage logs by customer ID
     * @param customerId Customer ID
     * @return List of usage logs for customer
     */
    public List<UsageLog> getUsageLogsByCustomerId(int customerId) {
        List<UsageLog> usageLogs = new ArrayList<>();
        String sql = "SELECT * FROM usage_logs WHERE customer_id = ? ORDER BY timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                usageLogs.add(extractUsageLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return usageLogs;
    }

    /**
     * Get total data used by customer
     * @param customerId Customer ID
     * @return Total data used in GB
     */
    public double getTotalDataUsedByCustomer(int customerId) {
        String sql = "SELECT SUM(data_used_gb) FROM usage_logs WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * Get data usage for customer in date range
     * @param customerId Customer ID
     * @param startDate Start date
     * @param endDate End date
     * @return Total data used in date range
     */
    public double getDataUsageInDateRange(int customerId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(data_used_gb) FROM usage_logs WHERE customer_id = ? AND timestamp BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setTimestamp(2, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(3, Timestamp.valueOf(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * Delete usage log
     * @param usageId Usage log ID
     * @return true if deletion successful
     */
    public boolean deleteUsageLog(int usageId) {
        String sql = "DELETE FROM usage_logs WHERE usage_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, usageId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Delete old usage logs (older than specified days)
     * @param days Number of days
     * @return true if deletion successful
     */
    public boolean deleteOldUsageLogs(int days) {
        String sql = "DELETE FROM usage_logs WHERE timestamp < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, days);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Extract UsageLog object from ResultSet
     * @param rs ResultSet
     * @return UsageLog object
     */
    private UsageLog extractUsageLogFromResultSet(ResultSet rs) throws SQLException {
        UsageLog usageLog = new UsageLog();
        usageLog.setUsageId(rs.getInt("usage_id"));
        usageLog.setCustomerId(rs.getInt("customer_id"));
        usageLog.setDataUsedGB(rs.getDouble("data_used_gb"));
        usageLog.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        return usageLog;
    }
}
