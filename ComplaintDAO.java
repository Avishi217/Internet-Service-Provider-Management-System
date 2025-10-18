package com.isp.dao;

import com.isp.model.Complaint;
import com.isp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    /**
     * Create a new complaint
     * @param complaint Complaint object
     * @return Generated complaint ID
     */
    public int createComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints (customer_id, ticket_number, subject, description, category, status, priority) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, complaint.getCustomerId());
            
            // Generate ticket number if not provided
            String ticketNumber = complaint.getTicketNumber();
            if (ticketNumber == null || ticketNumber.trim().isEmpty()) {
                ticketNumber = "TKT-" + System.currentTimeMillis();
            }
            pstmt.setString(2, ticketNumber);
            
            pstmt.setString(3, complaint.getSubject());
            pstmt.setString(4, complaint.getDescription());
            pstmt.setString(5, complaint.getCategory() != null ? complaint.getCategory() : "other");
            pstmt.setString(6, complaint.getStatus() != null ? complaint.getStatus() : "open");
            pstmt.setString(7, complaint.getPriority() != null ? complaint.getPriority() : "medium");
            
            int affectedRows = pstmt.executeUpdate();
            System.out.println("✅ Complaint created successfully! Rows affected: " + affectedRows);
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int complaintId = rs.getInt(1);
                    System.out.println("✅ Generated Complaint ID: " + complaintId + ", Ticket: " + ticketNumber);
                    return complaintId;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error in createComplaint: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }

    /**
     * Get complaint by ID
     * @param complaintId Complaint ID
     * @return Complaint object
     */
    public Complaint getComplaintById(int complaintId) {
    String sql = "SELECT * FROM complaints WHERE complaint_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, complaintId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractComplaintFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all complaints
     * @return List of all complaints
     */
    public List<Complaint> getAllComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM complaints ORDER BY complaint_id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return complaints;
    }

    /**
     * Get complaints by customer ID
     * @param customerId Customer ID
     * @return List of complaints for customer
     */
    public List<Complaint> getComplaintsByCustomerId(int customerId) {
        List<Complaint> complaints = new ArrayList<>();
    String sql = "SELECT * FROM complaints WHERE customer_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return complaints;
    }

    /**
     * Get complaints by status
     * @param status Complaint status
     * @return List of complaints with given status
     */
    public List<Complaint> getComplaintsByStatus(String status) {
        List<Complaint> complaints = new ArrayList<>();
    String sql = "SELECT * FROM complaints WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return complaints;
    }

    /**
     * Get complaints by assigned employee
     * @param employeeId Employee ID
     * @return List of complaints assigned to employee
     */
    public List<Complaint> getComplaintsByEmployeeId(int employeeId) {
        List<Complaint> complaints = new ArrayList<>();
    String sql = "SELECT * FROM complaints WHERE assigned_employee_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                complaints.add(extractComplaintFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return complaints;
    }

    /**
     * Update complaint
     * @param complaint Complaint object with updated data
     * @return true if update successful
     */
    public boolean updateComplaint(Complaint complaint) {
    String sql = "UPDATE complaints SET subject = ?, description = ?, status = ?, assigned_employee_id = ?, resolved_at = ? WHERE complaint_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, complaint.getSubject());
            pstmt.setString(2, complaint.getDescription());
            pstmt.setString(3, complaint.getStatus());
            
            if (complaint.getAssignedEmployeeId() != null) {
                pstmt.setInt(4, complaint.getAssignedEmployeeId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            if (complaint.getResolvedDate() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(complaint.getResolvedDate()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }
            
            pstmt.setInt(6, complaint.getComplaintId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Assign complaint to employee
     * @param complaintId Complaint ID
     * @param employeeId Employee ID
     * @return true if assignment successful
     */
    public boolean assignComplaint(int complaintId, int employeeId) {
        String sql = "UPDATE complaints SET assigned_employee_id = ?, status = 'in_progress' WHERE complaint_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            pstmt.setInt(2, complaintId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Mark complaint as resolved
     * @param complaintId Complaint ID
     * @return true if update successful
     */
    public boolean resolveComplaint(int complaintId) {
    String sql = "UPDATE complaints SET status = 'resolved', resolved_at = ? WHERE complaint_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, complaintId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Delete complaint
     * @param complaintId Complaint ID
     * @return true if deletion successful
     */
    public boolean deleteComplaint(int complaintId) {
    String sql = "DELETE FROM complaints WHERE complaint_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, complaintId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get complaint count by status
     * @param status Complaint status
     * @return Number of complaints with given status
     */
    public int getComplaintCountByStatus(String status) {
    String sql = "SELECT COUNT(*) FROM complaints WHERE status = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Get total complaints count
     * @return Total number of complaints
     */
    public int getTotalComplaintsCount() {
    String sql = "SELECT COUNT(*) FROM complaints";
        
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
     * Extract Complaint object from ResultSet
     * @param rs ResultSet
     * @return Complaint object
     */
    private Complaint extractComplaintFromResultSet(ResultSet rs) throws SQLException {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(rs.getInt("complaint_id"));
        complaint.setCustomerId(rs.getInt("customer_id"));
        complaint.setSubject(rs.getString("subject"));
        complaint.setDescription(rs.getString("description"));
        complaint.setStatus(rs.getString("status"));
        // DB uses created_at timestamp
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            complaint.setCreatedDate(createdTs.toLocalDateTime());
        }
        
        int employeeId = rs.getInt("assigned_employee_id");
        if (!rs.wasNull()) {
            complaint.setAssignedEmployeeId(employeeId);
        }
        
        Timestamp resolvedDate = rs.getTimestamp("resolved_at");
        if (resolvedDate != null) {
            complaint.setResolvedDate(resolvedDate.toLocalDateTime());
        }
        
        return complaint;
    }
}
