package com.isp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DailyDataUsage model - Track daily data consumption per customer
 * Supports per-day data limits (e.g., 2GB/day) with addon tracking
 */
public class DailyDataUsage {
    private int usageId;
    private int customerId;
    private LocalDate usageDate;
    private Integer planId;
    
    // Usage amounts
    private BigDecimal dataUsedGb;
    private BigDecimal dailyLimitGb;
    private BigDecimal addonDataUsedGb;
    
    // Status
    private boolean limitExceeded;
    private boolean addonPurchased;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public DailyDataUsage() {
        this.dataUsedGb = BigDecimal.ZERO;
        this.addonDataUsedGb = BigDecimal.ZERO;
        this.limitExceeded = false;
        this.addonPurchased = false;
    }

    public DailyDataUsage(int customerId, LocalDate usageDate, BigDecimal dailyLimitGb) {
        this();
        this.customerId = customerId;
        this.usageDate = usageDate;
        this.dailyLimitGb = dailyLimitGb;
    }

    // Getters and Setters
    public int getUsageId() {
        return usageId;
    }

    public void setUsageId(int usageId) {
        this.usageId = usageId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public BigDecimal getDataUsedGb() {
        return dataUsedGb;
    }

    public void setDataUsedGb(BigDecimal dataUsedGb) {
        this.dataUsedGb = dataUsedGb;
    }

    public BigDecimal getDailyLimitGb() {
        return dailyLimitGb;
    }

    public void setDailyLimitGb(BigDecimal dailyLimitGb) {
        this.dailyLimitGb = dailyLimitGb;
    }

    public BigDecimal getAddonDataUsedGb() {
        return addonDataUsedGb;
    }

    public void setAddonDataUsedGb(BigDecimal addonDataUsedGb) {
        this.addonDataUsedGb = addonDataUsedGb;
    }

    public boolean isLimitExceeded() {
        return limitExceeded;
    }

    public void setLimitExceeded(boolean limitExceeded) {
        this.limitExceeded = limitExceeded;
    }

    public boolean isAddonPurchased() {
        return addonPurchased;
    }

    public void setAddonPurchased(boolean addonPurchased) {
        this.addonPurchased = addonPurchased;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    
    /**
     * Get usage percentage (0-100)
     */
    public int getUsagePercentage() {
        if (dailyLimitGb == null || dailyLimitGb.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return dataUsedGb.divide(dailyLimitGb, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .intValue();
    }

    /**
     * Get remaining data in GB
     */
    public BigDecimal getRemainingDataGb() {
        if (dailyLimitGb == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal remaining = dailyLimitGb.subtract(dataUsedGb);
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
    }

    /**
     * Get total data available (plan + addon)
     */
    public BigDecimal getTotalAvailableGb() {
        BigDecimal total = dailyLimitGb != null ? dailyLimitGb : BigDecimal.ZERO;
        return total.add(addonDataUsedGb);
    }

    /**
     * Check if usage is above 80% threshold
     */
    public boolean isNearingLimit() {
        return getUsagePercentage() >= 80;
    }

    /**
     * Get formatted usage string
     */
    public String getFormattedUsage() {
        return String.format("%.2f GB used of %.2f GB", 
                           dataUsedGb, 
                           dailyLimitGb != null ? dailyLimitGb : BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return "DailyDataUsage{" +
                "usageId=" + usageId +
                ", customerId=" + customerId +
                ", usageDate=" + usageDate +
                ", dataUsedGb=" + dataUsedGb +
                ", dailyLimitGb=" + dailyLimitGb +
                ", limitExceeded=" + limitExceeded +
                '}';
    }
}
