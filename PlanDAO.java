package com.isp.dao;

import com.isp.model.Plan;
import com.isp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanDAO {

    /**
     * Create a new plan
     * @param plan Plan object
     * @return Generated plan ID
     */
    public int createPlan(Plan plan) {
        String sql = "INSERT INTO plans (plan_name, plan_type, price_inr, old_price_inr, validity_days, data_per_day_gb, total_data_gb, voice_benefits, sms_per_day, description, is_addon, addon_validity_hours, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, plan.getPlanName());
            pstmt.setString(2, plan.getPlanType());
            pstmt.setBigDecimal(3, plan.getPriceInr());
            pstmt.setBigDecimal(4, plan.getOldPriceInr());
            pstmt.setInt(5, plan.getValidityDays());
            pstmt.setBigDecimal(6, plan.getDataPerDayGb());
            pstmt.setBigDecimal(7, plan.getTotalDataGb());
            pstmt.setString(8, plan.getVoiceBenefits());
            pstmt.setInt(9, plan.getSmsPerDay() != null ? plan.getSmsPerDay() : 100);
            pstmt.setString(10, plan.getDescription());
            pstmt.setBoolean(11, plan.isAddon());
            if (plan.getAddonValidityHours() != null) {
                pstmt.setInt(12, plan.getAddonValidityHours());
            } else {
                pstmt.setNull(12, Types.INTEGER);
            }
            pstmt.setString(13, plan.getStatus());
            
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
     * Get plan by ID
     * @param planId Plan ID
     * @return Plan object
     */
    public Plan getPlanById(int planId) {
        String sql = "SELECT * FROM plans WHERE plan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, planId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractPlanFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all plans
     * @return List of plans
     */
    public List<Plan> getAllPlans() {
        List<Plan> plans = new ArrayList<>();
        String sql = "SELECT * FROM plans ORDER BY price_inr ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                plans.add(extractPlanFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return plans;
    }

    /**
     * Get active plans
     * @return List of active plans
     */
    public List<Plan> getActivePlans() {
        List<Plan> plans = new ArrayList<>();
        String sql = "SELECT * FROM plans WHERE status = 'active' ORDER BY price_inr ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                plans.add(extractPlanFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return plans;
    }

    /**
     * Update plan
     * @param plan Plan object
     * @return true if update successful, false otherwise
     */
    public boolean updatePlan(Plan plan) {
        String sql = "UPDATE plans SET plan_name = ?, plan_type = ?, price_inr = ?, old_price_inr = ?, validity_days = ?, data_per_day_gb = ?, total_data_gb = ?, voice_benefits = ?, sms_per_day = ?, description = ?, is_addon = ?, addon_validity_hours = ?, status = ? WHERE plan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, plan.getPlanName());
            pstmt.setString(2, plan.getPlanType());
            pstmt.setBigDecimal(3, plan.getPriceInr());
            pstmt.setBigDecimal(4, plan.getOldPriceInr());
            pstmt.setInt(5, plan.getValidityDays());
            pstmt.setBigDecimal(6, plan.getDataPerDayGb());
            pstmt.setBigDecimal(7, plan.getTotalDataGb());
            pstmt.setString(8, plan.getVoiceBenefits());
            pstmt.setInt(9, plan.getSmsPerDay() != null ? plan.getSmsPerDay() : 100);
            pstmt.setString(10, plan.getDescription());
            pstmt.setBoolean(11, plan.isAddon());
            if (plan.getAddonValidityHours() != null) {
                pstmt.setInt(12, plan.getAddonValidityHours());
            } else {
                pstmt.setNull(12, Types.INTEGER);
            }
            pstmt.setString(13, plan.getStatus());
            pstmt.setInt(14, plan.getPlanId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Delete plan
     * @param planId Plan ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deletePlan(int planId) {
        String sql = "DELETE FROM plans WHERE plan_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, planId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Extract Plan object from ResultSet
     * @param rs ResultSet
     * @return Plan object
     * @throws SQLException
     */
    private Plan extractPlanFromResultSet(ResultSet rs) throws SQLException {
        Plan plan = new Plan();
        plan.setPlanId(rs.getInt("plan_id"));
        plan.setPlanName(rs.getString("plan_name"));
        plan.setPlanType(rs.getString("plan_type"));
        plan.setPriceInr(rs.getBigDecimal("price_inr"));
        plan.setOldPriceInr(rs.getBigDecimal("old_price_inr"));
        plan.setValidityDays(rs.getInt("validity_days"));
        plan.setDataPerDayGb(rs.getBigDecimal("data_per_day_gb"));
        plan.setTotalDataGb(rs.getBigDecimal("total_data_gb"));
        plan.setVoiceBenefits(rs.getString("voice_benefits"));
        plan.setSmsPerDay(rs.getInt("sms_per_day"));
        plan.setDescription(rs.getString("description"));
        plan.setAddon(rs.getBoolean("is_addon"));
        
        Integer addonValidityHours = rs.getInt("addon_validity_hours");
        if (!rs.wasNull()) {
            plan.setAddonValidityHours(addonValidityHours);
        }
        
        plan.setStatus(rs.getString("status"));
        
        return plan;
    }
}
