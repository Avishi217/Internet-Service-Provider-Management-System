package com.isp.model;

import java.time.LocalDateTime;

public class Complaint {
    private int complaintId;
    private int customerId;
    private String ticketNumber;
    private String subject;
    private String description;
    private String category; // billing, technical, network, plan, other
    private String status; // open, in_progress, resolved, closed, escalated
    private String priority; // low, medium, high, critical
    private LocalDateTime createdDate;
    private LocalDateTime resolvedDate;
    private Integer assignedEmployeeId;

    // Constructors
    public Complaint() {
    }

    public Complaint(int complaintId, int customerId, String ticketNumber, String subject, String description,
                     String category, String status, String priority, LocalDateTime createdDate,
                     LocalDateTime resolvedDate, Integer assignedEmployeeId) {
        this.complaintId = complaintId;
        this.customerId = customerId;
        this.ticketNumber = ticketNumber;
        this.subject = subject;
        this.description = description;
        this.category = category;
        this.status = status;
        this.priority = priority;
        this.createdDate = createdDate;
        this.resolvedDate = resolvedDate;
        this.assignedEmployeeId = assignedEmployeeId;
    }

    // Getters and Setters
    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public Integer getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(Integer assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "complaintId=" + complaintId +
                ", customerId=" + customerId +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", subject='" + subject + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
