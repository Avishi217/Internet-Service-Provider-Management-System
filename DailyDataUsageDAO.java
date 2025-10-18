package com.isp.dao;

import com.isp.model.DailyDataUsage;
import com.isp.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for DailyDataUsage
 * Handles per-day data consumption tracking
 */
public class DailyDataUsageDAO {

    /**
     * Get today's usage for a customer
     * @param customerId Customer ID
     * @return DailyDataUsage object or null
     */
    public DailyDataUsage getTodayUsage(int customerId) {
        String sql = "SELECT * FROM daily_data_usage WHERE customer_id = ? AND usage_date = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting today's usage: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Update or create today's usage record
     * @param customerId Customer ID
     * @param planId Plan ID
     * @param dailyLimitGb Daily data limit in GB
     * @param dataUsedGb Additional data used in GB
     * @return true if successful
     */
    public boolean updateUsage(int customerId, int planId, BigDecimal dailyLimitGb, BigDecimal dataUsedGb) {
        // First check if today's record exists
        DailyDataUsage existing = getTodayUsage(customerId);
        
        if (existing != null) {
            // Update existing record
            String sql = "UPDATE daily_data_usage SET data_used_gb = data_used_gb + ?, " +
                        "limit_exceeded = (data_used_gb + ? > daily_limit_gb) WHERE usage_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setBigDecimal(1, dataUsedGb);
                pstmt.setBigDecimal(2, dataUsedGb);
                pstmt.setInt(3, existing.getUsageId());
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating usage: " + e.getMessage());
            }
        } else {
            // Create new record for today
            String sql = "INSERT INTO daily_data_usage (customer_id, usage_date, plan_id, data_used_gb, " +
                        "daily_limit_gb, limit_exceeded, addon_data_used_gb, addon_purchased) " +
                        "VALUES (?, CURDATE(), ?, ?, ?, ?, 0, FALSE)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, customerId);
                pstmt.setInt(2, planId);
                pstmt.setBigDecimal(3, dataUsedGb);
                pstmt.setBigDecimal(4, dailyLimitGb);
                pstmt.setBoolean(5, dataUsedGb.compareTo(dailyLimitGb) > 0);
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error creating usage record: " + e.getMessage());
            }
        }
        
        return false;
    }

    /**
     * Get usage history for last N days
     * @param customerId Customer ID
     * @param days Number of days
     * @return List of DailyDataUsage
     */
    public List<DailyDataUsage> getDailyUsageHistory(int customerId, int days) {
        List<DailyDataUsage> usageList = new ArrayList<>();
        String sql = "SELECT * FROM daily_data_usage WHERE customer_id = ? " +
                    "AND usage_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                    "ORDER BY usage_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, days);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                usageList.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting usage history: " + e.getMessage());
        }
        
        return usageList;
    }

    /**
     * Mark addon as purchased for today's usage
     * @param customerId Customer ID
     * @return true if successful
     */
    public boolean markAddonPurchased(int customerId) {
        String sql = "UPDATE daily_data_usage SET addon_purchased = TRUE " +
                    "WHERE customer_id = ? AND usage_date = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error marking addon purchased: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Update addon data usage
     * @param customerId Customer ID
     * @param addonDataUsedGb Addon data consumed in GB
     * @return true if successful
     */
    public boolean updateAddonUsage(int customerId, BigDecimal addonDataUsedGb) {
        String sql = "UPDATE daily_data_usage SET addon_data_used_gb = addon_data_used_gb + ? " +
                    "WHERE customer_id = ? AND usage_date = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, addonDataUsedGb);
            pstmt.setInt(2, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating addon usage: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Reset daily usage (called at midnight by scheduled task)
     * This is handled by database - new records are created daily
     * Old records older than 90 days can be archived
     */
    public int archiveOldRecords() {
        String sql = "DELETE FROM daily_data_usage WHERE usage_date < DATE_SUB(CURDATE(), INTERVAL 90 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error archiving old records: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Get total data used this month
     * @param customerId Customer ID
     * @return Total data in GB
     */
    public BigDecimal getMonthlyUsage(int customerId) {
        String sql = "SELECT SUM(data_used_gb + addon_data_used_gb) as total " +
                    "FROM daily_data_usage WHERE customer_id = ? " +
                    "AND MONTH(usage_date) = MONTH(CURDATE()) " +
                    "AND YEAR(usage_date) = YEAR(CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly usage: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Extract DailyDataUsage from ResultSet
     */
    private DailyDataUsage extractFromResultSet(ResultSet rs) throws SQLException {
        DailyDataUsage usage = new DailyDataUsage();
        usage.setUsageId(rs.getInt("usage_id"));
        usage.setCustomerId(rs.getInt("customer_id"));
        
        Date usageDate = rs.getDate("usage_date");
        if (usageDate != null) {
            usage.setUsageDate(usageDate.toLocalDate());
        }
        
        usage.setPlanId(rs.getInt("plan_id"));
        usage.setDataUsedGb(rs.getBigDecimal("data_used_gb"));
        usage.setDailyLimitGb(rs.getBigDecimal("daily_limit_gb"));
        usage.setLimitExceeded(rs.getBoolean("limit_exceeded"));
        usage.setAddonDataUsedGb(rs.getBigDecimal("addon_data_used_gb"));
        usage.setAddonPurchased(rs.getBoolean("addon_purchased"));
        
        return usage;
    }
}
