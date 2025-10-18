package com.isp.dao;

import com.isp.model.Bill;
import com.isp.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    /**
     * Create a new bill
     * @param bill Bill object
     * @return Generated bill ID
     */
    public int createBill(Bill bill) {
        // Database schema: base_amount, gst_amount, total_amount, bill_date, billing_period_start, billing_period_end, invoice_number
        String sql = "INSERT INTO bills (customer_id, plan_id, base_amount, gst_amount, total_amount, bill_date, due_date, status, invoice_number, billing_period_start, billing_period_end) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, bill.getCustomerId());
            
            if (bill.getPlanId() != null) {
                pstmt.setInt(2, bill.getPlanId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            
            // Calculate GST (18%) and total from base amount
            BigDecimal baseAmount = bill.getBaseAmount();
            if (baseAmount == null) {
                // Fallback to getAmount() for backward compatibility
                baseAmount = bill.getAmount();
            }
            
            if (baseAmount == null) {
                throw new SQLException("Base amount cannot be null");
            }
            
            BigDecimal gstAmount = baseAmount.multiply(new BigDecimal("0.18"));
            BigDecimal totalAmount = baseAmount.add(gstAmount);
            
            pstmt.setBigDecimal(3, baseAmount); // base_amount
            pstmt.setBigDecimal(4, gstAmount);  // gst_amount  
            pstmt.setBigDecimal(5, totalAmount); // total_amount
            pstmt.setDate(6, Date.valueOf(bill.getBillDate() != null ? bill.getBillDate() : LocalDate.now())); // bill_date
            pstmt.setDate(7, Date.valueOf(bill.getDueDate())); // due_date
            pstmt.setString(8, bill.getStatus());
            
            // Generate invoice number
            String invoiceNumber = "INV-" + System.currentTimeMillis();
            pstmt.setString(9, invoiceNumber);
            
            // Billing period: current month
            LocalDate billDate = bill.getBillDate() != null ? bill.getBillDate() : LocalDate.now();
            LocalDate periodStart = billDate.withDayOfMonth(1);
            LocalDate periodEnd = periodStart.plusMonths(1).minusDays(1);
            pstmt.setDate(10, Date.valueOf(periodStart));
            pstmt.setDate(11, Date.valueOf(periodEnd));
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("✅ Bill created successfully! Rows affected: " + affectedRows);
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int billId = rs.getInt(1);
                    System.out.println("✅ Generated Bill ID: " + billId + ", Invoice: " + invoiceNumber);
                    return billId;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error in createBill: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }

    /**
     * Get bill by ID
     * @param billId Bill ID
     * @return Bill object
     */
    public Bill getBillById(int billId) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractBillFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all bills
     * @return List of all bills
     */
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY bill_id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }
            System.out.println("✅ Loaded " + bills.size() + " bills from database");
        } catch (SQLException e) {
            System.err.println("❌ Database error in getAllBills: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bills;
    }

    /**
     * Get bills by customer ID
     * @param customerId Customer ID
     * @return List of bills for customer
     */
    public List<Bill> getBillsByCustomerId(int customerId) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE customer_id = ? ORDER BY bill_id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }
            System.out.println("✅ Loaded " + bills.size() + " bills for customer " + customerId);
        } catch (SQLException e) {
            System.err.println("❌ Database error in getBillsByCustomerId: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bills;
    }

    /**
     * Get bills by status
     * @param status Bill status
     * @return List of bills with given status
     */
    public List<Bill> getBillsByStatus(String status) {
        List<Bill> bills = new ArrayList<>();
    String sql = "SELECT * FROM bills WHERE status = ? ORDER BY due_date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                bills.add(extractBillFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return bills;
    }

    /**
     * Update bill
     * @param bill Bill object with updated data
     * @return true if update successful
     */
    public boolean updateBill(Bill bill) {
        String sql = "UPDATE bills SET amount = ?, due_date = ?, status = ?, paid_date = ? WHERE bill_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, bill.getAmount());
            pstmt.setDate(2, Date.valueOf(bill.getDueDate()));
            pstmt.setString(3, bill.getStatus());
            
            if (bill.getPaidDate() != null) {
                pstmt.setDate(4, Date.valueOf(bill.getPaidDate()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            
            pstmt.setInt(5, bill.getBillId());
            
            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("✅ Bill " + bill.getBillId() + " updated successfully");
            }
            return success;
        } catch (SQLException e) {
            System.err.println("❌ Database error in updateBill: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Mark bill as paid
     * @param billId Bill ID
     * @return true if update successful
     */
    public boolean markBillAsPaid(int billId) {
    String sql = "UPDATE bills SET status = 'paid', paid_date = ? WHERE bill_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            pstmt.setInt(2, billId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Delete bill
     * @param billId Bill ID
     * @return true if deletion successful
     */
    public boolean deleteBill(int billId) {
    String sql = "DELETE FROM bills WHERE bill_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, billId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get total unpaid bills count
     * @return Number of unpaid bills
     */
    public int getUnpaidBillsCount() {
    String sql = "SELECT COUNT(*) FROM bills WHERE status = 'unpaid' OR status = 'overdue'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Get total revenue from paid bills
     * @return Total revenue
     */
    public double getTotalRevenue() {
    String sql = "SELECT SUM(total_amount) FROM bills WHERE status = 'paid'";
        
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
     * Extract Bill object from ResultSet
     * @param rs ResultSet
     * @return Bill object
     */
    private Bill extractBillFromResultSet(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setCustomerId(rs.getInt("customer_id"));
        
        int planId = rs.getInt("plan_id");
        if (!rs.wasNull()) {
            bill.setPlanId(planId);
        }
        
        // Map all new schema fields
        bill.setBaseAmount(rs.getBigDecimal("base_amount"));
        bill.setGstAmount(rs.getBigDecimal("gst_amount"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        
        Date billDate = rs.getDate("bill_date");
        if (billDate != null) {
            bill.setBillDate(billDate.toLocalDate());
        }
        
        bill.setInvoiceNumber(rs.getString("invoice_number"));
        
        Date periodStart = rs.getDate("billing_period_start");
        if (periodStart != null) {
            bill.setBillingPeriodStart(periodStart.toLocalDate());
        }
        
        Date periodEnd = rs.getDate("billing_period_end");
        if (periodEnd != null) {
            bill.setBillingPeriodEnd(periodEnd.toLocalDate());
        }
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            bill.setDueDate(dueDate.toLocalDate());
        }
        
        bill.setStatus(rs.getString("status"));
        
        Date paidDate = rs.getDate("paid_date");
        if (paidDate != null) {
            bill.setPaidDate(paidDate.toLocalDate());
        }
        
        // Map created_at timestamp
        try {
            java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                bill.setCreatedAt(createdAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Column might not exist in older schema
        }
        
        return bill;
    }
}
