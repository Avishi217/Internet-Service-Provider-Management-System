package com.isp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * RechargeHistory model - Track customer recharge history
 */
public class RechargeHistory {
    private int rechargeId;
    private int customerId;
    private int transactionId;
    private int planId;
    
    // Recharge details
    private BigDecimal amountPaid;
    private int validityDays;
    private LocalDateTime rechargeDate;
    private LocalDate validityStartDate;
    private LocalDate validityEndDate;
    
    // Status
    private boolean isActive;

    // Constructors
    public RechargeHistory() {
        this.rechargeDate = LocalDateTime.now();
        this.isActive = true;
    }

    public RechargeHistory(int customerId, int transactionId, int planId, 
                          BigDecimal amountPaid, int validityDays,
                          LocalDate validityStartDate, LocalDate validityEndDate) {
        this();
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.planId = planId;
        this.amountPaid = amountPaid;
        this.validityDays = validityDays;
        this.validityStartDate = validityStartDate;
        this.validityEndDate = validityEndDate;
    }

    // Getters and Setters
    public int getRechargeId() {
        return rechargeId;
    }

    public void setRechargeId(int rechargeId) {
        this.rechargeId = rechargeId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(int validityDays) {
        this.validityDays = validityDays;
    }

    public LocalDateTime getRechargeDate() {
        return rechargeDate;
    }

    public void setRechargeDate(LocalDateTime rechargeDate) {
        this.rechargeDate = rechargeDate;
    }

    public LocalDate getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(LocalDate validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public LocalDate getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(LocalDate validityEndDate) {
        this.validityEndDate = validityEndDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Utility methods
    
    /**
     * Get formatted amount
     */
    public String getFormattedAmount() {
        return "₹" + String.format("%,.2f", amountPaid);
    }

    /**
     * Get days remaining
     */
    public long getDaysRemaining() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(validityEndDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(today, validityEndDate);
    }

    /**
     * Check if plan is expired
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(validityEndDate);
    }

    /**
     * Check if plan is expiring soon (within 3 days)
     */
    public boolean isExpiringSoon() {
        return getDaysRemaining() <= 3 && getDaysRemaining() > 0;
    }

    /**
     * Get validity status text
     */
    public String getValidityStatus() {
        long daysRemaining = getDaysRemaining();
        if (daysRemaining == 0) {
            return "Expired";
        } else if (daysRemaining == 1) {
            return "Expires Today";
        } else if (daysRemaining <= 3) {
            return "Expires in " + daysRemaining + " days";
        } else {
            return daysRemaining + " days remaining";
        }
    }

    /**
     * Get formatted validity period
     */
    public String getFormattedValidityPeriod() {
        return validityStartDate.toString() + " to " + validityEndDate.toString();
    }

    @Override
    public String toString() {
        return "RechargeHistory{" +
                "rechargeId=" + rechargeId +
                ", customerId=" + customerId +
                ", planId=" + planId +
                ", amountPaid=" + amountPaid +
                ", validityDays=" + validityDays +
                ", validityEndDate=" + validityEndDate +
                ", isActive=" + isActive +
                '}';
    }
}
