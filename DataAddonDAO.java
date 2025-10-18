package com.isp.dao;

import com.isp.model.DataAddon;
import com.isp.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for DataAddon
 * Handles data topup purchases and consumption
 */
public class DataAddonDAO {

    /**
     * Purchase a data addon
     * @param addon DataAddon object
     * @return Generated addon ID
     */
    public int purchaseAddon(DataAddon addon) {
        String sql = "INSERT INTO data_addons (customer_id, plan_id, data_amount_gb, price_inr, " +
                    "purchased_at, expires_at, is_expired, is_consumed, data_used_gb) " +
                    "VALUES (?, ?, ?, ?, ?, ?, FALSE, FALSE, 0)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, addon.getCustomerId());
            pstmt.setInt(2, addon.getPlanId());
            pstmt.setBigDecimal(3, addon.getDataAmountGb());
            pstmt.setBigDecimal(4, addon.getPriceInr());
            pstmt.setTimestamp(5, Timestamp.valueOf(addon.getPurchasedAt()));
            pstmt.setTimestamp(6, Timestamp.valueOf(addon.getExpiresAt()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error purchasing addon: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Get active addons for a customer
     * @param customerId Customer ID
     * @return List of active DataAddons
     */
    public List<DataAddon> getActiveAddons(int customerId) {
        List<DataAddon> addons = new ArrayList<>();
        String sql = "SELECT * FROM data_addons WHERE customer_id = ? " +
                    "AND is_expired = FALSE AND is_consumed = FALSE " +
                    "AND expires_at > NOW() " +
                    "ORDER BY purchased_at ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                addons.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active addons: " + e.getMessage());
        }
        
        return addons;
    }

    /**
     * Consume addon data (oldest addon first)
     * @param customerId Customer ID
     * @param dataUsedGb Data to consume in GB
     * @return true if successful
     */
    public boolean consumeAddonData(int customerId, BigDecimal dataUsedGb) {
        List<DataAddon> activeAddons = getActiveAddons(customerId);
        
        BigDecimal remainingToConsume = dataUsedGb;
        
        for (DataAddon addon : activeAddons) {
            if (remainingToConsume.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            
            BigDecimal addonRemaining = addon.getRemainingDataGb();
            
            if (addonRemaining.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal toConsume = remainingToConsume.min(addonRemaining);
                
                // Update addon data consumption
                String sql = "UPDATE data_addons SET data_used_gb = data_used_gb + ?, " +
                            "is_consumed = (data_used_gb + ? >= data_amount_gb) " +
                            "WHERE addon_id = ?";
                
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setBigDecimal(1, toConsume);
                    pstmt.setBigDecimal(2, toConsume);
                    pstmt.setInt(3, addon.getAddonId());
                    pstmt.executeUpdate();
                    
                    remainingToConsume = remainingToConsume.subtract(toConsume);
                    
                } catch (SQLException e) {
                    System.err.println("Error consuming addon data: " + e.getMessage());
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * Expire addons past their validity period
     * (Called by scheduled task at midnight)
     * @return Number of addons expired
     */
    public int expireAddons() {
        String sql = "UPDATE data_addons SET is_expired = TRUE " +
                    "WHERE is_expired = FALSE AND expires_at <= NOW()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error expiring addons: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Get addon by ID
     * @param addonId Addon ID
     * @return DataAddon object
     */
    public DataAddon getAddonById(int addonId) {
        String sql = "SELECT * FROM data_addons WHERE addon_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, addonId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting addon by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all addons for a customer
     * @param customerId Customer ID
     * @param includeExpired Include expired addons
     * @return List of DataAddons
     */
    public List<DataAddon> getCustomerAddons(int customerId, boolean includeExpired) {
        List<DataAddon> addons = new ArrayList<>();
        String sql = includeExpired ? 
                    "SELECT * FROM data_addons WHERE customer_id = ? ORDER BY purchased_at DESC" :
                    "SELECT * FROM data_addons WHERE customer_id = ? AND is_expired = FALSE ORDER BY purchased_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                addons.add(extractFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer addons: " + e.getMessage());
        }
        
        return addons;
    }

    /**
     * Get total remaining addon data for a customer
     * @param customerId Customer ID
     * @return Total remaining data in GB
     */
    public BigDecimal getTotalRemainingAddonData(int customerId) {
        String sql = "SELECT SUM(data_amount_gb - data_used_gb) as total_remaining " +
                    "FROM data_addons WHERE customer_id = ? " +
                    "AND is_expired = FALSE AND is_consumed = FALSE " +
                    "AND expires_at > NOW()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total_remaining");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Error getting total remaining addon data: " + e.getMessage());
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Archive old addon records (older than 90 days)
     * @return Number of records archived
     */
    public int archiveOldRecords() {
        String sql = "DELETE FROM data_addons WHERE purchased_at < DATE_SUB(NOW(), INTERVAL 90 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error archiving old addon records: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Extract DataAddon from ResultSet
     */
    private DataAddon extractFromResultSet(ResultSet rs) throws SQLException {
        DataAddon addon = new DataAddon();
        addon.setAddonId(rs.getInt("addon_id"));
        addon.setCustomerId(rs.getInt("customer_id"));
        addon.setPlanId(rs.getInt("plan_id"));
        addon.setDataAmountGb(rs.getBigDecimal("data_amount_gb"));
        addon.setPriceInr(rs.getBigDecimal("price_inr"));
        
        Timestamp purchasedAt = rs.getTimestamp("purchased_at");
        if (purchasedAt != null) {
            addon.setPurchasedAt(purchasedAt.toLocalDateTime());
        }
        
        Timestamp expiresAt = rs.getTimestamp("expires_at");
        if (expiresAt != null) {
            addon.setExpiresAt(expiresAt.toLocalDateTime());
        }
        
        addon.setExpired(rs.getBoolean("is_expired"));
        addon.setConsumed(rs.getBoolean("is_consumed"));
        addon.setDataUsedGb(rs.getBigDecimal("data_used_gb"));
        
        return addon;
    }
}
