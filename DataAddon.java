package com.isp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DataAddon model - Track data topup/addon purchases
 * Supports midnight-expiry addons (₹15/1GB, ₹25/2GB)
 */
public class DataAddon {
    private int addonId;
    private int customerId;
    private int planId; // References addon plan from plans table
    private BigDecimal dataAmountGb;
    private BigDecimal priceInr;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;
    private boolean isExpired;
    private boolean isConsumed;
    private BigDecimal dataUsedGb;

    // Constructors
    public DataAddon() {
        this.isExpired = false;
        this.isConsumed = false;
        this.dataUsedGb = BigDecimal.ZERO;
    }

    public DataAddon(int customerId, int planId, BigDecimal dataAmountGb, 
                     BigDecimal priceInr, LocalDateTime expiresAt) {
        this();
        this.customerId = customerId;
        this.planId = planId;
        this.dataAmountGb = dataAmountGb;
        this.priceInr = priceInr;
        this.purchasedAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public int getAddonId() {
        return addonId;
    }

    public void setAddonId(int addonId) {
        this.addonId = addonId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public BigDecimal getDataAmountGb() {
        return dataAmountGb;
    }

    public void setDataAmountGb(BigDecimal dataAmountGb) {
        this.dataAmountGb = dataAmountGb;
    }

    public BigDecimal getPriceInr() {
        return priceInr;
    }

    public void setPriceInr(BigDecimal priceInr) {
        this.priceInr = priceInr;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    public void setPurchasedAt(LocalDateTime purchasedAt) {
        this.purchasedAt = purchasedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    public void setConsumed(boolean consumed) {
        isConsumed = consumed;
    }

    public BigDecimal getDataUsedGb() {
        return dataUsedGb;
    }

    public void setDataUsedGb(BigDecimal dataUsedGb) {
        this.dataUsedGb = dataUsedGb;
    }

    // Utility methods
    
    /**
     * Get remaining data in GB
     */
    public BigDecimal getRemainingDataGb() {
        BigDecimal remaining = dataAmountGb.subtract(dataUsedGb);
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
    }

    /**
     * Check if addon is currently active
     */
    public boolean isActive() {
        return !isExpired && !isConsumed && LocalDateTime.now().isBefore(expiresAt);
    }

    /**
     * Get formatted price
     */
    public String getFormattedPrice() {
        return "₹" + String.format("%.0f", priceInr);
    }

    /**
     * Get formatted data amount
     */
    public String getFormattedData() {
        return String.format("%.0f GB", dataAmountGb);
    }

    /**
     * Get time remaining until expiry
     */
    public String getTimeRemaining() {
        if (isExpired || LocalDateTime.now().isAfter(expiresAt)) {
            return "Expired";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long hoursRemaining = java.time.Duration.between(now, expiresAt).toHours();
        long minutesRemaining = java.time.Duration.between(now, expiresAt).toMinutes() % 60;
        
        if (hoursRemaining > 0) {
            return hoursRemaining + "h " + minutesRemaining + "m";
        } else {
            return minutesRemaining + " min";
        }
    }

    @Override
    public String toString() {
        return "DataAddon{" +
                "addonId=" + addonId +
                ", customerId=" + customerId +
                ", dataAmountGb=" + dataAmountGb +
                ", priceInr=" + priceInr +
                ", expiresAt=" + expiresAt +
                ", isExpired=" + isExpired +
                ", isConsumed=" + isConsumed +
                '}';
    }
}
