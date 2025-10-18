package com.isp.dao;

import com.isp.model.Customer;
import com.isp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    /**
     * Create a new customer
     * @param customer Customer object
     * @return Generated customer ID
     */
    public int createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (user_id, first_name, last_name, email, phone, address, plan_id, registration_date, connection_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, customer.getUserId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setString(4, customer.getEmail());
            pstmt.setString(5, customer.getPhone());
            pstmt.setString(6, customer.getAddress());
            
            if (customer.getPlanId() != null) {
                pstmt.setInt(7, customer.getPlanId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            pstmt.setDate(8, Date.valueOf(LocalDate.now()));
            pstmt.setString(9, customer.getConnectionStatus());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating customer: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Get customer by ID
     * @param customerId Customer ID
     * @return Customer object
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get customer by user ID
     * @param userId User ID
     * @return Customer object
     */
    public Customer getCustomerByUserId(int userId) {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by user ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all customers
     * @return List of customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY registration_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
        }
        
        return customers;
    }

    /**
     * Update customer
     * @param customer Customer object
     * @return true if update successful, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ?, plan_id = ?, connection_status = ? WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getAddress());
            
            if (customer.getPlanId() != null) {
                pstmt.setInt(6, customer.getPlanId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            
            pstmt.setString(7, customer.getConnectionStatus());
            pstmt.setInt(8, customer.getCustomerId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Delete customer
     * @param customerId Customer ID
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Search customers by name, email, or phone
     * @param searchTerm Search term
     * @return List of matching customers
     */
    public List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY registration_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String search = "%" + searchTerm + "%";
            pstmt.setString(1, search);
            pstmt.setString(2, search);
            pstmt.setString(3, search);
            pstmt.setString(4, search);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }
        
        return customers;
    }

    /**
     * Get customers by connection status
     * @param status Connection status
     * @return List of customers
     */
    public List<Customer> getCustomersByStatus(String status) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE connection_status = ? ORDER BY registration_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting customers by status: " + e.getMessage());
        }
        
        return customers;
    }

    /**
     * Get total customer count
     * @return Total number of customers
     */
    public int getTotalCustomerCount() {
        String sql = "SELECT COUNT(*) FROM customers";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total customer count: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Extract Customer object from ResultSet
     * @param rs ResultSet
     * @return Customer object
     * @throws SQLException
     */
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setUserId(rs.getInt("user_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        
        int planId = rs.getInt("plan_id");
        if (!rs.wasNull()) {
            customer.setPlanId(planId);
        }
        
        Date registrationDate = rs.getDate("registration_date");
        if (registrationDate != null) {
            customer.setRegistrationDate(registrationDate.toLocalDate());
        }
        
        // Load new plan date fields if they exist
        Date planActivatedDate = rs.getDate("plan_activated_date");
        if (planActivatedDate != null) {
            customer.setPlanActivatedDate(planActivatedDate.toLocalDate());
        }
        
        Date planExpiryDate = rs.getDate("plan_expiry_date");
        if (planExpiryDate != null) {
            customer.setPlanExpiryDate(planExpiryDate.toLocalDate());
        }
        
        customer.setConnectionStatus(rs.getString("connection_status"));
        
        return customer;
    }
}
