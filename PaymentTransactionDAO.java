package com.isp.dao;

import com.isp.model.PaymentTransaction;
import com.isp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentTransactionDAO {

    /**
     * Create a new payment transaction
     */
    public int createTransaction(PaymentTransaction transaction) {
        String sql = "INSERT INTO payment_transactions (customer_id, bill_id, amount, payment_type, " +
                    "payment_method, gateway_transaction_id, gateway_name, upi_id, card_last_4_digits, " +
                    "status, failure_reason, initiated_at, completed_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, transaction.getCustomerId());
            if (transaction.getBillId() != null) {
                pstmt.setInt(2, transaction.getBillId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getPaymentType());
            pstmt.setString(5, transaction.getPaymentMethod());
            pstmt.setString(6, transaction.getGatewayTransactionId());
            pstmt.setString(7, transaction.getGatewayName());
            pstmt.setString(8, transaction.getUpiId());
            pstmt.setString(9, transaction.getCardLast4Digits());
            pstmt.setString(10, transaction.getStatus());
            pstmt.setString(11, transaction.getFailureReason());
            pstmt.setTimestamp(12, Timestamp.valueOf(transaction.getInitiatedAt()));
            if (transaction.getCompletedAt() != null) {
                pstmt.setTimestamp(13, Timestamp.valueOf(transaction.getCompletedAt()));
            } else {
                pstmt.setNull(13, Types.TIMESTAMP);
            }
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating payment transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update transaction status
     */
    public boolean updateTransactionStatus(int transactionId, String status, String failureReason) {
        String sql = "UPDATE payment_transactions SET status = ?, failure_reason = ? WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, failureReason);
            pstmt.setInt(3, transactionId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating transaction status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get transaction by ID
     */
    public PaymentTransaction getTransactionById(int transactionId) {
        String sql = "SELECT * FROM payment_transactions WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get customer's transaction history
     */
    public List<PaymentTransaction> getCustomerTransactions(int customerId) {
        List<PaymentTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM payment_transactions WHERE customer_id = ? ORDER BY initiated_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }
    
    /**
     * Get transactions by customer ID (alias for getCustomerTransactions)
     */
    public List<PaymentTransaction> getTransactionsByCustomerId(int customerId) {
        return getCustomerTransactions(customerId);
    }

    /**
     * Get pending transactions
     */
    public List<PaymentTransaction> getPendingTransactions() {
        List<PaymentTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM payment_transactions WHERE status IN ('initiated', 'pending') " +
                    "ORDER BY initiated_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Get successful transactions for a date range
     */
    public List<PaymentTransaction> getSuccessfulTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        List<PaymentTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM payment_transactions WHERE status = 'success' " +
                    "AND initiated_at BETWEEN ? AND ? ORDER BY initiated_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting successful transactions: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Extract PaymentTransaction from ResultSet
     */
    private PaymentTransaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(rs.getInt("transaction_id"));
        transaction.setCustomerId(rs.getInt("customer_id"));
        
        int billId = rs.getInt("bill_id");
        if (!rs.wasNull()) {
            transaction.setBillId(billId);
        }
        
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setPaymentType(rs.getString("payment_type"));
        transaction.setPaymentMethod(rs.getString("payment_method"));
        transaction.setGatewayTransactionId(rs.getString("gateway_transaction_id"));
        transaction.setGatewayName(rs.getString("gateway_name"));
        transaction.setUpiId(rs.getString("upi_id"));
        transaction.setCardLast4Digits(rs.getString("card_last_4_digits"));
        transaction.setStatus(rs.getString("status"));
        transaction.setFailureReason(rs.getString("failure_reason"));
        
        Timestamp initiatedAt = rs.getTimestamp("initiated_at");
        if (initiatedAt != null) {
            transaction.setInitiatedAt(initiatedAt.toLocalDateTime());
        }
        
        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) {
            transaction.setCompletedAt(completedAt.toLocalDateTime());
        }
        
        return transaction;
    }
}
