package com.isp.dao;

import com.isp.model.Payment;
import com.isp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    /**
     * Create a new payment
     * @param payment Payment object
     * @return Generated payment ID
     */
    public int createPayment(Payment payment) {
        String sql = "INSERT INTO payments (bill_id, customer_id, amount, payment_method, transaction_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, payment.getBillId());
            pstmt.setInt(2, payment.getCustomerId());
            pstmt.setBigDecimal(3, payment.getAmount());
            pstmt.setString(4, payment.getPaymentMethod());
            pstmt.setString(5, payment.getTransactionId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int paymentId = rs.getInt(1);
                    System.out.println("✅ Payment created successfully! Payment ID: " + paymentId + ", Transaction: " + payment.getTransactionId());
                    return paymentId;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error in createPayment: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }

    /**
     * Get payment by ID
     * @param paymentId Payment ID
     * @return Payment object
     */
    public Payment getPaymentById(int paymentId) {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, paymentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all payments
     * @return List of all payments
     */
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return payments;
    }

    /**
     * Get payments by customer ID
     * @param customerId Customer ID
     * @return List of payments for customer
     */
    public List<Payment> getPaymentsByCustomerId(int customerId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE customer_id = ? ORDER BY payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return payments;
    }

    /**
     * Get payments by bill ID
     * @param billId Bill ID
     * @return List of payments for bill
     */
    public List<Payment> getPaymentsByBillId(int billId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE bill_id = ? ORDER BY payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
            System.out.println("✅ Loaded " + payments.size() + " payments for bill " + billId);
        } catch (SQLException e) {
            System.err.println("❌ Database error in getPaymentsByBillId: " + e.getMessage());
            e.printStackTrace();
        }
        
        return payments;
    }

    /**
     * Delete payment
     * @param paymentId Payment ID
     * @return true if deletion successful
     */
    public boolean deletePayment(int paymentId) {
        String sql = "DELETE FROM payments WHERE payment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, paymentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get total payments amount
     * @return Total payments amount
     */
    public double getTotalPayments() {
        String sql = "SELECT SUM(amount) FROM payments";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * Extract Payment object from ResultSet
     * @param rs ResultSet
     * @return Payment object
     */
    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setBillId(rs.getInt("bill_id"));
        payment.setCustomerId(rs.getInt("customer_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setTransactionId(rs.getString("transaction_id"));
        return payment;
    }
}
