package com.isp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * PlatformUsage model - Track data usage by platform/app
 * Supports categorization (social media, streaming, gaming, etc.)
 */
public class PlatformUsage {
    private int platformUsageId;
    private int customerId;
    private LocalDate usageDate;
    private int hourOfDay; // 0-23
    
    // Platform details
    private String platformCategory; // social_media, streaming, gaming, browsing, messaging, other
    private String platformName; // e.g., YouTube, Instagram, WhatsApp
    
    // Usage data
    private BigDecimal dataUsedMb;
    private int sessionDurationMinutes;
    
    private LocalDateTime createdAt;

    // Constructors
    public PlatformUsage() {
        this.dataUsedMb = BigDecimal.ZERO;
        this.sessionDurationMinutes = 0;
    }

    public PlatformUsage(int customerId, LocalDate usageDate, int hourOfDay, 
                        String platformCategory, String platformName, 
                        BigDecimal dataUsedMb, int sessionDurationMinutes) {
        this.customerId = customerId;
        this.usageDate = usageDate;
        this.hourOfDay = hourOfDay;
        this.platformCategory = platformCategory;
        this.platformName = platformName;
        this.dataUsedMb = dataUsedMb;
        this.sessionDurationMinutes = sessionDurationMinutes;
    }

    // Getters and Setters
    public int getPlatformUsageId() {
        return platformUsageId;
    }

    public void setPlatformUsageId(int platformUsageId) {
        this.platformUsageId = platformUsageId;
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

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public String getPlatformCategory() {
        return platformCategory;
    }

    public void setPlatformCategory(String platformCategory) {
        this.platformCategory = platformCategory;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public BigDecimal getDataUsedMb() {
        return dataUsedMb;
    }

    public void setDataUsedMb(BigDecimal dataUsedMb) {
        this.dataUsedMb = dataUsedMb;
    }

    public int getSessionDurationMinutes() {
        return sessionDurationMinutes;
    }

    public void setSessionDurationMinutes(int sessionDurationMinutes) {
        this.sessionDurationMinutes = sessionDurationMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Utility methods
    
    /**
     * Get data usage in GB
     */
    public BigDecimal getDataUsedGb() {
        return dataUsedMb.divide(new BigDecimal("1024"), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Get formatted category name
     */
    public String getFormattedCategory() {
        switch (platformCategory.toLowerCase()) {
            case "social_media": return "Social Media";
            case "streaming": return "Streaming";
            case "gaming": return "Gaming";
            case "browsing": return "Browsing";
            case "messaging": return "Messaging";
            default: return "Other";
        }
    }

    /**
     * Get time period description
     */
    public String getTimePeriod() {
        if (hourOfDay >= 6 && hourOfDay < 12) {
            return "Morning";
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            return "Afternoon";
        } else if (hourOfDay >= 18 && hourOfDay < 24) {
            return "Evening";
        } else {
            return "Night";
        }
    }

    /**
     * Get formatted duration
     */
    public String getFormattedDuration() {
        if (sessionDurationMinutes < 60) {
            return sessionDurationMinutes + " min";
        } else {
            int hours = sessionDurationMinutes / 60;
            int minutes = sessionDurationMinutes % 60;
            return hours + "h " + minutes + "m";
        }
    }

    @Override
    public String toString() {
        return "PlatformUsage{" +
                "platformUsageId=" + platformUsageId +
                ", customerId=" + customerId +
                ", usageDate=" + usageDate +
                ", platformCategory='" + platformCategory + '\'' +
                ", platformName='" + platformName + '\'' +
                ", dataUsedMb=" + dataUsedMb +
                ", hourOfDay=" + hourOfDay +
                '}';
    }
}
