package com.isp.model;

import java.time.LocalDateTime;

public class UsageLog {
    private int usageId;
    private int customerId;
    private double dataUsedGB; // Data used in GB
    private LocalDateTime timestamp;
    private String usageType; // download, upload, total

    // Constructors
    public UsageLog() {
    }

    public UsageLog(int usageId, int customerId, double dataUsedGB, LocalDateTime timestamp, String usageType) {
        this.usageId = usageId;
        this.customerId = customerId;
        this.dataUsedGB = dataUsedGB;
        this.timestamp = timestamp;
        this.usageType = usageType;
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

    public double getDataUsedGB() {
        return dataUsedGB;
    }

    public void setDataUsedGB(double dataUsedGB) {
        this.dataUsedGB = dataUsedGB;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    @Override
    public String toString() {
        return "UsageLog{" +
                "usageId=" + usageId +
                ", customerId=" + customerId +
                ", dataUsedGB=" + dataUsedGB +
                ", timestamp=" + timestamp +
                ", usageType='" + usageType + '\'' +
                '}';
    }
}
