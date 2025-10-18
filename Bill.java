package com.isp.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Bill {
    private int billId;
    private int customerId;
    private Integer planId;
    
    // New schema fields matching database
    private BigDecimal baseAmount;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    private LocalDate billDate;
    private String invoiceNumber;
    private LocalDate billingPeriodStart;
    private LocalDate billingPeriodEnd;
    
    private LocalDate dueDate;
    private String status; // paid, unpaid, overdue
    private LocalDate paidDate;
    private LocalDateTime createdAt;

    // Constructors
    public Bill() {
    }

    public Bill(int billId, int customerId, Integer planId, BigDecimal baseAmount, 
                BigDecimal gstAmount, BigDecimal totalAmount, LocalDate billDate,
                String invoiceNumber, LocalDate billingPeriodStart, LocalDate billingPeriodEnd,
                LocalDate dueDate, String status, LocalDate paidDate, LocalDateTime createdAt) {
        this.billId = billId;
        this.customerId = customerId;
        this.planId = planId;
        this.baseAmount = baseAmount;
        this.gstAmount = gstAmount;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.invoiceNumber = invoiceNumber;
        this.billingPeriodStart = billingPeriodStart;
        this.billingPeriodEnd = billingPeriodEnd;
        this.dueDate = dueDate;
        this.status = status;
        this.paidDate = paidDate;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getBillingPeriodStart() {
        return billingPeriodStart;
    }

    public void setBillingPeriodStart(LocalDate billingPeriodStart) {
        this.billingPeriodStart = billingPeriodStart;
    }

    public LocalDate getBillingPeriodEnd() {
        return billingPeriodEnd;
    }

    public void setBillingPeriodEnd(LocalDate billingPeriodEnd) {
        this.billingPeriodEnd = billingPeriodEnd;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Backward compatibility method - returns totalAmount
    public BigDecimal getAmount() {
        return totalAmount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.totalAmount = amount;
    }
    
    // Backward compatibility method - returns billDate
    public LocalDate getGeneratedDate() {
        return billDate;
    }
    
    public void setGeneratedDate(LocalDate generatedDate) {
        this.billDate = generatedDate;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", customerId=" + customerId +
                ", planId=" + planId +
                ", baseAmount=" + baseAmount +
                ", gstAmount=" + gstAmount +
                ", totalAmount=" + totalAmount +
                ", billDate=" + billDate +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", billingPeriodStart=" + billingPeriodStart +
                ", billingPeriodEnd=" + billingPeriodEnd +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                ", paidDate=" + paidDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
