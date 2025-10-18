package com.isp.model;

import java.math.BigDecimal;

/**
 * Plan model class - Enhanced for Indian Telecom (Airtel-style)
 * Supports daily data limits, validity periods, voice benefits, and add-ons
 */
public class Plan {
    private int planId;
    private String planName;
    private String planType; // voice, unlimited, data_topup
    private BigDecimal priceInr;
    private BigDecimal oldPriceInr; // For showing discounts
    private int validityDays;
    
    // Data benefits
    private BigDecimal dataPerDayGb; // Daily data limit (e.g., 1.5, 2.0)
    private BigDecimal totalDataGb; // Total data for validity period
    
    // Voice benefits
    private String voiceBenefits; // e.g., "Unlimited calling"
    private Integer smsPerDay; // SMS per day limit
    
    // Other features
    private String description;
    private String features; // JSON string for additional features
    private String status; // active, inactive
    private boolean isAddon; // True for addon/topup packs
    private Integer addonValidityHours; // For midnight-expiry addons

    // Constructors
    public Plan() {
    }

    public Plan(int planId, String planName, String planType, BigDecimal priceInr, BigDecimal oldPriceInr,
                int validityDays, BigDecimal dataPerDayGb, BigDecimal totalDataGb, String voiceBenefits,
                Integer smsPerDay, String description, String status, boolean isAddon, Integer addonValidityHours) {
        this.planId = planId;
        this.planName = planName;
        this.planType = planType;
        this.priceInr = priceInr;
        this.oldPriceInr = oldPriceInr;
        this.validityDays = validityDays;
        this.dataPerDayGb = dataPerDayGb;
        this.totalDataGb = totalDataGb;
        this.voiceBenefits = voiceBenefits;
        this.smsPerDay = smsPerDay;
        this.description = description;
        this.status = status;
        this.isAddon = isAddon;
        this.addonValidityHours = addonValidityHours;
    }

    // Getters and Setters
    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public BigDecimal getPriceInr() {
        return priceInr;
    }

    public void setPriceInr(BigDecimal priceInr) {
        this.priceInr = priceInr;
    }

    public BigDecimal getOldPriceInr() {
        return oldPriceInr;
    }

    public void setOldPriceInr(BigDecimal oldPriceInr) {
        this.oldPriceInr = oldPriceInr;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(int validityDays) {
        this.validityDays = validityDays;
    }

    public BigDecimal getDataPerDayGb() {
        return dataPerDayGb;
    }

    public void setDataPerDayGb(BigDecimal dataPerDayGb) {
        this.dataPerDayGb = dataPerDayGb;
    }

    public BigDecimal getTotalDataGb() {
        return totalDataGb;
    }

    public void setTotalDataGb(BigDecimal totalDataGb) {
        this.totalDataGb = totalDataGb;
    }

    public String getVoiceBenefits() {
        return voiceBenefits;
    }

    public void setVoiceBenefits(String voiceBenefits) {
        this.voiceBenefits = voiceBenefits;
    }

    public Integer getSmsPerDay() {
        return smsPerDay;
    }

    public void setSmsPerDay(Integer smsPerDay) {
        this.smsPerDay = smsPerDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAddon() {
        return isAddon;
    }

    public void setAddon(boolean addon) {
        isAddon = addon;
    }

    public Integer getAddonValidityHours() {
        return addonValidityHours;
    }

    public void setAddonValidityHours(Integer addonValidityHours) {
        this.addonValidityHours = addonValidityHours;
    }

    // Utility methods
    
    /**
     * Get formatted price with ₹ symbol
     */
    public String getFormattedPrice() {
        return "₹" + String.format("%,.2f", priceInr);
    }

    /**
     * Get formatted old price (for showing discounts)
     */
    public String getFormattedOldPrice() {
        if (oldPriceInr != null) {
            return "₹" + String.format("%,.2f", oldPriceInr);
        }
        return null;
    }

    /**
     * Calculate discount percentage
     */
    public Integer getDiscountPercentage() {
        if (oldPriceInr != null && oldPriceInr.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = oldPriceInr.subtract(priceInr);
            BigDecimal percentage = discount.divide(oldPriceInr, 4, java.math.RoundingMode.HALF_UP)
                                           .multiply(new BigDecimal("100"));
            return percentage.intValue();
        }
        return null;
    }

    /**
     * Get data description (daily or total)
     */
    public String getDataDescription() {
        if (dataPerDayGb != null) {
            return dataPerDayGb + " GB/day";
        } else if (totalDataGb != null) {
            return totalDataGb + " GB";
        }
        return "No data";
    }

    /**
     * Get validity description
     */
    public String getValidityDescription() {
        if (addonValidityHours != null && addonValidityHours == 24) {
            return "Valid till midnight";
        } else if (addonValidityHours != null) {
            return addonValidityHours + " hours";
        } else if (validityDays > 0) {
            if (validityDays == 28) return "28 days";
            if (validityDays == 56) return "56 days";
            if (validityDays == 84) return "84 days";
            if (validityDays == 365) return "1 Year";
            return validityDays + " days";
        }
        return "Unlimited";
    }

    @Override
    public String toString() {
        return "Plan{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", planType='" + planType + '\'' +
                ", priceInr=" + priceInr +
                ", validityDays=" + validityDays +
                ", dataPerDayGb=" + dataPerDayGb +
                ", totalDataGb=" + totalDataGb +
                ", voiceBenefits='" + voiceBenefits + '\'' +
                ", isAddon=" + isAddon +
                '}';
    }
}
