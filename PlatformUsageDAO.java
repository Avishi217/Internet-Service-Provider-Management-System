package com.isp.dao;

import com.isp.model.PlatformUsage;
import com.isp.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for PlatformUsage
 * Handles app-wise data consumption tracking
 */
public class PlatformUsageDAO {

    /**
     * Record platform usage
     * @param usage PlatformUsage object
     * @return Generated usage ID
     */
    public int recordPlatformUsage(PlatformUsage usage) {
        String sql = "INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, " +
                    "platform_category, platform_name, data_used_mb, session_duration_minutes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, usage.getCustomerId());
            pstmt.setDate(2, Date.valueOf(usage.getUsageDate()));
            pstmt.setInt(3, usage.getHourOfDay());
            pstmt.setString(4, usage.getPlatformCategory());
            pstmt.setString(5, usage.getPlatformName());
            pstmt.setBigDecimal(6, usage.getDataUsedMb());
            pstmt.setInt(7, usage.getSessionDurationMinutes());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error recording platform usage: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Get platform breakdown for a specific date
     * Returns map of platform name to total data used in MB
     * @param customerId Customer ID
     * @param date Usage date
     * @return Map<Platform Name, Data in MB>
     */
    public Map<String, BigDecimal> getPlatformBreakdown(int customerId, LocalDate date) {
        Map<String, BigDecimal> breakdown = new HashMap<>();
        String sql = "SELECT platform_name, SUM(data_used_mb) as total_mb " +
                    "FROM platform_usage WHERE customer_id = ? AND usage_date = ? " +
                    "GROUP BY platform_name ORDER BY total_mb DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                breakdown.put(rs.getString("platform_name"), rs.getBigDecimal("total_mb"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting platform breakdown: " + e.getMessage());
        }
        
        return breakdown;
    }

    /**
     * Get category breakdown for a date range
     * @param customerId Customer ID
     * @param startDate Start date
     * @param endDate End date
     * @return Map<Category, Data in MB>
     */
    public Map<String, BigDecimal> getCategoryTotals(int customerId, LocalDate startDate, LocalDate endDate) {
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        String sql = "SELECT platform_category, SUM(data_used_mb) as total_mb " +
                    "FROM platform_usage WHERE customer_id = ? " +
                    "AND usage_date BETWEEN ? AND ? " +
                    "GROUP BY platform_category ORDER BY total_mb DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                categoryTotals.put(rs.getString("platform_category"), rs.getBigDecimal("total_mb"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting category totals: " + e.getMessage());
        }
        
        return categoryTotals;
    }

    /**
     * Get hourly usage breakdown for a specific date
     * Returns map of hour (0-23) to total data used in MB
     * @param customerId Customer ID
     * @param date Usage date
     * @return Map<Hour, Data in MB>
     */
    public Map<Integer, BigDecimal> getHourlyUsage(int customerId, LocalDate date) {
        Map<Integer, BigDecimal> hourlyUsage = new HashMap<>();
        String sql = "SELECT hour_of_day, SUM(data_used_mb) as total_mb " +
                    "FROM platform_usage WHERE customer_id = ? AND usage_date = ? " +
                    "GROUP BY hour_of_day ORDER BY hour_of_day";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                hourlyUsage.put(rs.getInt("hour_of_day"), rs.getBigDecimal("total_mb"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting hourly usage: " + e.getMessage());
        }
        
        return hourlyUsage;
    }

    /**
     * Get top platforms for a date range
     * @param customerId Customer ID
     * @param startDate Start date
     * @param endDate End date
     * @param limit Number of top platforms to return
     * @return List of PlatformUsage (aggregated)
     */
    public List<PlatformUsage> getTopPlatforms(int customerId, LocalDate startDate, LocalDate endDate, int limit) {
        List<PlatformUsage> topPlatforms = new ArrayList<>();
        String sql = "SELECT platform_name, platform_category, " +
                    "SUM(data_used_mb) as total_mb, SUM(session_duration_minutes) as total_minutes " +
                    "FROM platform_usage WHERE customer_id = ? " +
                    "AND usage_date BETWEEN ? AND ? " +
                    "GROUP BY platform_name, platform_category " +
                    "ORDER BY total_mb DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            pstmt.setInt(4, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PlatformUsage usage = new PlatformUsage();
                usage.setCustomerId(customerId);
                usage.setPlatformName(rs.getString("platform_name"));
                usage.setPlatformCategory(rs.getString("platform_category"));
                usage.setDataUsedMb(rs.getBigDecimal("total_mb"));
                usage.setSessionDurationMinutes(rs.getInt("total_minutes"));
                topPlatforms.add(usage);
            }
        } catch (SQLException e) {
            System.err.println("Error getting top platforms: " + e.getMessage());
        }
        
        return topPlatforms;
    }

    /**
     * Get usage history for a specific platform
     * @param customerId Customer ID
     * @param platformName Platform name
     * @param days Number of days
     * @return List of PlatformUsage
     */
    public List<PlatformUsage> getPlatformHistory(int customerId, String platformName, int days) {
        List<PlatformUsage> history = new ArrayList<>();
        String sql = "SELECT * FROM platform_usage WHERE customer_id = ? " +
                    "AND platform_name = ? " +
                    "AND usage_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                    "ORDER BY usage_date DESC, hour_of_day DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            pstmt.setString(2, platformName);
            pstmt.setInt(3, days);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting platform history: " + e.getMessage());
        }
        
        return history;
    }

    /**
     * Archive old records (older than 90 days)
     * @return Number of records archived
     */
    public int archiveOldRecords() {
        String sql = "DELETE FROM platform_usage WHERE usage_date < DATE_SUB(CURDATE(), INTERVAL 90 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error archiving old records: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Extract PlatformUsage from ResultSet
     */
    private PlatformUsage extractFromResultSet(ResultSet rs) throws SQLException {
        PlatformUsage usage = new PlatformUsage();
        usage.setPlatformUsageId(rs.getInt("platform_usage_id"));
        usage.setCustomerId(rs.getInt("customer_id"));
        
        Date usageDate = rs.getDate("usage_date");
        if (usageDate != null) {
            usage.setUsageDate(usageDate.toLocalDate());
        }
        
        usage.setHourOfDay(rs.getInt("hour_of_day"));
        usage.setPlatformCategory(rs.getString("platform_category"));
        usage.setPlatformName(rs.getString("platform_name"));
        usage.setDataUsedMb(rs.getBigDecimal("data_used_mb"));
        usage.setSessionDurationMinutes(rs.getInt("session_duration_minutes"));
        
        return usage;
    }
}

