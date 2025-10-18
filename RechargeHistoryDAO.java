package com.isp.dao;

import com.isp.model.RechargeHistory;
import com.isp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RechargeHistoryDAO {

    /**
     * Record a new recharge
     */
    public int recordRecharge(RechargeHistory recharge) {
        String sql = "INSERT INTO recharge_history (customer_id, transaction_id, plan_id, amount_paid, " +
                    "validity_days, recharge_date, validity_start_date, validity_end_date, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, recharge.getCustomerId());
            pstmt.setInt(2, recharge.getTransactionId());
            pstmt.setInt(3, recharge.getPlanId());
            pstmt.setBigDecimal(4, recharge.getAmountPaid());
            pstmt.setInt(5, recharge.getValidityDays());
            pstmt.setTimestamp(6, Timestamp.valueOf(recharge.getRechargeDate()));
            pstmt.setDate(7, Date.valueOf(recharge.getValidityStartDate()));
            pstmt.setDate(8, Date.valueOf(recharge.getValidityEndDate()));
            pstmt.setBoolean(9, recharge.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error recording recharge: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get active recharge for customer
     */
    public RechargeHistory getActiveRecharge(int customerId) {
        String sql = "SELECT * FROM recharge_history WHERE customer_id = ? AND is_active = TRUE " +
                    "ORDER BY validity_end_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractRechargeFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active recharge: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get recharge history for customer
     */
    public List<RechargeHistory> getRechargeHistory(int customerId) {
        List<RechargeHistory> recharges = new ArrayList<>();
        String sql = "SELECT * FROM recharge_history WHERE customer_id = ? ORDER BY recharge_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                recharges.add(extractRechargeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recharge history: " + e.getMessage());
            e.printStackTrace();
        }
        return recharges;
    }

    /**
     * Deactivate expired recharges
     */
    public int deactivateExpiredRecharges() {
        String sql = "UPDATE recharge_history SET is_active = FALSE " +
                    "WHERE is_active = TRUE AND validity_end_date < CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deactivating expired recharges: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get recharges expiring soon (within days)
     */
    public List<RechargeHistory> getRechargesExpiringSoon(int withinDays) {
        List<RechargeHistory> recharges = new ArrayList<>();
        String sql = "SELECT * FROM recharge_history WHERE is_active = TRUE " +
                    "AND validity_end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                    "ORDER BY validity_end_date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, withinDays);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                recharges.add(extractRechargeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recharges expiring soon: " + e.getMessage());
            e.printStackTrace();
        }
        return recharges;
    }

    /**
     * Extract RechargeHistory from ResultSet
     */
    private RechargeHistory extractRechargeFromResultSet(ResultSet rs) throws SQLException {
        RechargeHistory recharge = new RechargeHistory();
        recharge.setRechargeId(rs.getInt("recharge_id"));
        recharge.setCustomerId(rs.getInt("customer_id"));
        recharge.setTransactionId(rs.getInt("transaction_id"));
        recharge.setPlanId(rs.getInt("plan_id"));
        recharge.setAmountPaid(rs.getBigDecimal("amount_paid"));
        recharge.setValidityDays(rs.getInt("validity_days"));
        recharge.setRechargeDate(rs.getTimestamp("recharge_date").toLocalDateTime());
        recharge.setValidityStartDate(rs.getDate("validity_start_date").toLocalDate());
        recharge.setValidityEndDate(rs.getDate("validity_end_date").toLocalDate());
        recharge.setActive(rs.getBoolean("is_active"));
        
        return recharge;
    }
}
