package com.isp.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Customer {
    private int customerId;
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Integer planId;
    private LocalDate planActivatedDate;
    private LocalDate planExpiryDate;
    private LocalDate registrationDate;
    private String connectionStatus; // active, suspended, expired, terminated

    // Constructors
    public Customer() {
    }

    public Customer(int customerId, int userId, String firstName, String lastName, String email, 
                    String phone, String address, Integer planId, LocalDate planActivatedDate,
                    LocalDate planExpiryDate, LocalDate registrationDate, String connectionStatus) {
        this.customerId = customerId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.planId = planId;
        this.planActivatedDate = planActivatedDate;
        this.planExpiryDate = planExpiryDate;
        this.registrationDate = registrationDate;
        this.connectionStatus = connectionStatus;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public LocalDate getPlanActivatedDate() {
        return planActivatedDate;
    }

    public void setPlanActivatedDate(LocalDate planActivatedDate) {
        this.planActivatedDate = planActivatedDate;
    }

    public LocalDate getPlanExpiryDate() {
        return planExpiryDate;
    }

    public void setPlanExpiryDate(LocalDate planExpiryDate) {
        this.planExpiryDate = planExpiryDate;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    // Utility methods
    
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if customer has an active plan
     */
    public boolean hasActivePlan() {
        return planId != null && planExpiryDate != null && LocalDate.now().isBefore(planExpiryDate);
    }

    /**
     * Check if plan is expired
     */
    public boolean isPlanExpired() {
        return planExpiryDate != null && LocalDate.now().isAfter(planExpiryDate);
    }

    /**
     * Get days remaining in current plan
     */
    public long getDaysRemaining() {
        if (planExpiryDate == null) {
            return 0;
        }
        LocalDate today = LocalDate.now();
        if (today.isAfter(planExpiryDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(today, planExpiryDate);
    }

    /**
     * Check if plan is expiring soon (within 3 days)
     */
    public boolean isPlanExpiringSoon() {
        long daysRemaining = getDaysRemaining();
        return daysRemaining > 0 && daysRemaining <= 3;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", connectionStatus='" + connectionStatus + '\'' +
                '}';
    }
}
