package com.isp.service;

import com.isp.dao.BillDAO;
import com.isp.dao.CustomerDAO;
import com.isp.dao.PaymentDAO;
import com.isp.model.Bill;
import com.isp.model.Customer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard Analytics Service
 * Provides comprehensive real-time statistics for ISP Management Dashboard
 * 
 * Features:
 * - Revenue Analytics (Total Revenue, Payments, Outstanding, Collection Rate)
 * - Customer Analytics (Total, With Plans, Without Plans, Active, Suspended)
 * - Bill Statistics (Paid, Unpaid, Overdue counts)
 * 
 * All calculations are performed in real-time from the database
 * 
 * @author ISP Management System
 * @version 1.0
 */
public class DashboardAnalyticsService {
    
    private final BillDAO billDAO;
    private final CustomerDAO customerDAO;
    private final PaymentDAO paymentDAO;
    
    /**
     * Constructor - initializes all required DAOs
     */
    public DashboardAnalyticsService() {
        this.billDAO = new BillDAO();
        this.customerDAO = new CustomerDAO();
        this.paymentDAO = new PaymentDAO();
    }
    
    /**
     * Get all dashboard statistics in a structured map
     * @return Map containing all analytics data
     */
    public Map<String, Object> getAllDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Revenue Analytics
        stats.put("revenue", getRevenueAnalytics());
        
        // Customer Analytics
        stats.put("customers", getCustomerAnalytics());
        
        // Bill Analytics
        stats.put("bills", getBillAnalytics());
        
        // Metadata
        stats.put("timestamp", System.currentTimeMillis());
        stats.put("lastUpdated", new java.util.Date().toString());
        
        return stats;
    }
    
    /**
     * Get Revenue Analytics
     * 
     * SQL Queries Used:
     * - Total Revenue Generated: SELECT SUM(total_amount) FROM bills WHERE status='paid'
     * - Unpaid Bills Count: SELECT COUNT(*) FROM bills WHERE status='unpaid'
     * - Total Payments: SELECT SUM(amount) FROM payments
     * 
     * @return Map with revenue metrics
     */
    public Map<String, Object> getRevenueAnalytics() {
        Map<String, Object> revenue = new HashMap<>();
        
        try {
            // 1. Total Revenue Generated (from paid bills)
            // SQL: SELECT SUM(total_amount) FROM bills WHERE status='paid'
            List<Bill> paidBills = billDAO.getBillsByStatus("paid");
            BigDecimal totalRevenueGenerated = paidBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            revenue.put("totalRevenueGenerated", totalRevenueGenerated);
            revenue.put("totalRevenueGeneratedFormatted", formatCurrency(totalRevenueGenerated));
            
            // 2. Total Payments Received
            // SQL: SELECT SUM(amount) FROM payments
            double totalPayments = paymentDAO.getTotalPayments();
            BigDecimal totalPaymentsBD = BigDecimal.valueOf(totalPayments);
            
            revenue.put("totalPaymentsReceived", totalPaymentsBD);
            revenue.put("totalPaymentsReceivedFormatted", formatCurrency(totalPaymentsBD));
            
            // 3. Unpaid Bills Count
            // SQL: SELECT COUNT(*) FROM bills WHERE status='unpaid'
            int unpaidBillsCount = billDAO.getUnpaidBillsCount();
            revenue.put("unpaidBillsCount", unpaidBillsCount);
            
            // 4. Paid Bills Count
            revenue.put("paidBillsCount", paidBills.size());
            
            // 5. Outstanding Amount (Revenue - Payments)
            BigDecimal outstanding = totalRevenueGenerated.subtract(totalPaymentsBD);
            revenue.put("outstandingAmount", outstanding);
            revenue.put("outstandingAmountFormatted", formatCurrency(outstanding));
            
            // 6. Collection Rate (%)
            double collectionRate = totalRevenueGenerated.doubleValue() > 0 
                ? (totalPayments / totalRevenueGenerated.doubleValue() * 100) 
                : 0;
            revenue.put("collectionRate", roundToOneDecimal(collectionRate));
            revenue.put("collectionRateFormatted", String.format("%.1f%%", collectionRate));
            
            // 7. Average Bill Amount
            BigDecimal avgBillAmount = paidBills.size() > 0 
                ? totalRevenueGenerated.divide(BigDecimal.valueOf(paidBills.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            revenue.put("averageBillAmount", avgBillAmount);
            revenue.put("averageBillAmountFormatted", formatCurrency(avgBillAmount));
            
        } catch (Exception e) {
            System.err.println("Error calculating revenue analytics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return revenue;
    }
    
    /**
     * Get Customer Analytics
     * 
     * SQL Queries Used:
     * - Total Customers: SELECT COUNT(*) FROM customers
     * - Customers with Plans: SELECT COUNT(*) FROM customers WHERE plan_id IS NOT NULL
     * - Customers without Plans: Total - With Plans
     * - Active Customers: SELECT COUNT(*) FROM customers WHERE connection_status='active'
     * 
     * @return Map with customer metrics
     */
    public Map<String, Object> getCustomerAnalytics() {
        Map<String, Object> customers = new HashMap<>();
        
        try {
            // Fetch all customers once for efficiency
            // SQL: SELECT * FROM customers
            List<Customer> allCustomers = customerDAO.getAllCustomers();
            
            // 1. Total Customers
            int totalCustomers = allCustomers.size();
            customers.put("totalCustomers", totalCustomers);
            
            // 2. Customers with Plans
            // SQL Equivalent: SELECT COUNT(*) FROM customers WHERE plan_id IS NOT NULL
            long customersWithPlans = allCustomers.stream()
                .filter(c -> c.getPlanId() != null)
                .count();
            customers.put("customersWithPlans", customersWithPlans);
            
            // 3. Customers without Plans
            // SQL Equivalent: SELECT COUNT(*) FROM customers WHERE plan_id IS NULL
            long customersWithoutPlans = totalCustomers - customersWithPlans;
            customers.put("customersWithoutPlans", customersWithoutPlans);
            
            // 4. Active Customers
            // SQL Equivalent: SELECT COUNT(*) FROM customers WHERE connection_status='active'
            long activeCustomers = allCustomers.stream()
                .filter(c -> "active".equalsIgnoreCase(c.getConnectionStatus()))
                .count();
            customers.put("activeCustomers", activeCustomers);
            
            // 5. Suspended Customers
            // SQL Equivalent: SELECT COUNT(*) FROM customers WHERE connection_status='suspended'
            long suspendedCustomers = allCustomers.stream()
                .filter(c -> "suspended".equalsIgnoreCase(c.getConnectionStatus()))
                .count();
            customers.put("suspendedCustomers", suspendedCustomers);
            
            // 6. Terminated Customers
            // SQL Equivalent: SELECT COUNT(*) FROM customers WHERE connection_status='terminated'
            long terminatedCustomers = allCustomers.stream()
                .filter(c -> "terminated".equalsIgnoreCase(c.getConnectionStatus()))
                .count();
            customers.put("terminatedCustomers", terminatedCustomers);
            
            // 7. Customer Distribution Percentages
            if (totalCustomers > 0) {
                customers.put("withPlansPercentage", roundToOneDecimal((customersWithPlans * 100.0) / totalCustomers));
                customers.put("withoutPlansPercentage", roundToOneDecimal((customersWithoutPlans * 100.0) / totalCustomers));
                customers.put("activePercentage", roundToOneDecimal((activeCustomers * 100.0) / totalCustomers));
            }
            
        } catch (Exception e) {
            System.err.println("Error calculating customer analytics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customers;
    }
    
    /**
     * Get Bill Analytics
     * 
     * SQL Queries Used:
     * - Paid Bills: SELECT COUNT(*), SUM(total_amount) FROM bills WHERE status='paid'
     * - Unpaid Bills: SELECT COUNT(*), SUM(total_amount) FROM bills WHERE status='unpaid'
     * - Overdue Bills: SELECT COUNT(*), SUM(total_amount) FROM bills WHERE status='overdue'
     * 
     * @return Map with bill statistics
     */
    public Map<String, Object> getBillAnalytics() {
        Map<String, Object> bills = new HashMap<>();
        
        try {
            // Get bills by status
            List<Bill> paidBills = billDAO.getBillsByStatus("paid");
            List<Bill> unpaidBills = billDAO.getBillsByStatus("unpaid");
            List<Bill> overdueBills = billDAO.getBillsByStatus("overdue");
            
            // Paid Bills
            bills.put("paidCount", paidBills.size());
            BigDecimal paidAmount = paidBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            bills.put("paidAmount", paidAmount);
            bills.put("paidAmountFormatted", formatCurrency(paidAmount));
            
            // Unpaid Bills
            bills.put("unpaidCount", unpaidBills.size());
            BigDecimal unpaidAmount = unpaidBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            bills.put("unpaidAmount", unpaidAmount);
            bills.put("unpaidAmountFormatted", formatCurrency(unpaidAmount));
            
            // Overdue Bills
            bills.put("overdueCount", overdueBills.size());
            BigDecimal overdueAmount = overdueBills.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            bills.put("overdueAmount", overdueAmount);
            bills.put("overdueAmountFormatted", formatCurrency(overdueAmount));
            
            // Total Bills
            int totalBills = paidBills.size() + unpaidBills.size() + overdueBills.size();
            bills.put("totalCount", totalBills);
            
        } catch (Exception e) {
            System.err.println("Error calculating bill analytics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return bills;
    }
    
    /**
     * Format BigDecimal as Indian currency (₹)
     */
    private String formatCurrency(BigDecimal amount) {
        return "₹" + String.format("%,.2f", amount);
    }
    
    /**
     * Round double to one decimal place
     */
    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
    
    /**
     * Print all statistics to console (for debugging)
     */
    public void printAllStats() {
        Map<String, Object> stats = getAllDashboardStats();
        
        System.out.println("=".repeat(70));
        System.out.println("📊 ISP DASHBOARD ANALYTICS");
        System.out.println("=".repeat(70));
        
        // Revenue Analytics
        @SuppressWarnings("unchecked")
        Map<String, Object> revenue = (Map<String, Object>) stats.get("revenue");
        System.out.println("\n💰 REVENUE ANALYTICS:");
        System.out.println("  Total Revenue Generated: " + revenue.get("totalRevenueGeneratedFormatted"));
        System.out.println("  Total Payments Received: " + revenue.get("totalPaymentsReceivedFormatted"));
        System.out.println("  Outstanding Amount: " + revenue.get("outstandingAmountFormatted"));
        System.out.println("  Collection Rate: " + revenue.get("collectionRateFormatted"));
        System.out.println("  Unpaid Bills Count: " + revenue.get("unpaidBillsCount"));
        System.out.println("  Paid Bills Count: " + revenue.get("paidBillsCount"));
        
        // Customer Analytics
        @SuppressWarnings("unchecked")
        Map<String, Object> customers = (Map<String, Object>) stats.get("customers");
        System.out.println("\n👥 CUSTOMER ANALYTICS:");
        System.out.println("  Total Customers: " + customers.get("totalCustomers"));
        System.out.println("  Customers with Plans: " + customers.get("customersWithPlans"));
        System.out.println("  Customers without Plans: " + customers.get("customersWithoutPlans"));
        System.out.println("  Active Customers: " + customers.get("activeCustomers"));
        System.out.println("  Suspended Customers: " + customers.get("suspendedCustomers"));
        
    }
}
