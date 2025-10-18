package com.isp.service;

import com.isp.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * OTP Service for generating and validating One-Time Passwords
 * Used for phone-based authentication
 */
public class OTPService {
    
    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;
    
    /**
     * Generates a 6-digit OTP and stores it in database
     * @param phone The phone number to send OTP to
     * @return The generated OTP code
     */
    public static String generateOTP(String phone) {
        // Generate random 6-digit OTP
        String otp = generateRandomOTP();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Delete any old OTPs for this phone number
            String deleteSql = "DELETE FROM otp_verification WHERE phone = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, phone);
                deleteStmt.executeUpdate();
            }
            
            // Insert new OTP
            String insertSql = "INSERT INTO otp_verification (phone, otp_code, expires_at) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, phone);
                insertStmt.setString(2, otp);
                insertStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES)));
                insertStmt.executeUpdate();
            }
            
            // In production, you would send SMS here
            // For now, we'll just print to console
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║         OTP GENERATED                  ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Phone: " + phone + "                  ║");
            System.out.println("║  OTP Code: " + otp + "                      ║");
            System.out.println("║  Valid for: " + OTP_VALIDITY_MINUTES + " minutes                    ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            return otp;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Validates the OTP code for a phone number
     * @param phone The phone number
     * @param otpCode The OTP code to validate
     * @return true if OTP is valid and not expired, false otherwise
     */
    public static boolean validateOTP(String phone, String otpCode) {
        String sql = "SELECT otp_id, expires_at, is_used FROM otp_verification " +
                     "WHERE phone = ? AND otp_code = ? AND is_used = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            stmt.setString(2, otpCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiresAt = rs.getTimestamp("expires_at");
                    int otpId = rs.getInt("otp_id");
                    
                    // Check if OTP has expired
                    if (expiresAt.after(new Timestamp(System.currentTimeMillis()))) {
                        // Mark OTP as used
                        markOTPAsUsed(otpId);
                        System.out.println("✅ OTP validated successfully for phone: " + phone);
                        return true;
                    } else {
                        System.out.println("❌ OTP expired for phone: " + phone);
                        return false;
                    }
                } else {
                    System.out.println("❌ Invalid OTP for phone: " + phone);
                    return false;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Marks an OTP as used so it cannot be reused
     * @param otpId The OTP ID to mark as used
     */
    private static void markOTPAsUsed(int otpId) {
        String sql = "UPDATE otp_verification SET is_used = TRUE WHERE otp_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, otpId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Generates a random 6-digit OTP
     * @return 6-digit OTP as String
     */
    private static String generateRandomOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit number
        return String.valueOf(otp);
    }
    
    /**
     * Checks if a phone number exists in the system
     * @param phone The phone number to check
     * @return true if phone exists, false otherwise
     */
    public static boolean isPhoneRegistered(String phone) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Cleans up expired OTPs from database
     * Should be called periodically
     */
    public static void cleanupExpiredOTPs() {
        String sql = "DELETE FROM otp_verification WHERE expires_at < NOW()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                System.out.println("🧹 Cleaned up " + deleted + " expired OTPs");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
