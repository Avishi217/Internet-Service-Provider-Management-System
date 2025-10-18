package com.isp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PaymentTransaction model - Track all payment transactions
 * Supports UPI, QR Code, Cards, Net Banking
 */
public class PaymentTransaction {
    private int transactionId;
    private int customerId;
    private Integer billId;
    
    // Transaction details
    private BigDecimal amount;
    private String paymentType; // recharge, bill_payment, addon_purchase
    private String paymentMethod; // upi, qr_code, debit_card, credit_card, net_banking, wallet
    
    // Payment gateway info
    private String gatewayTransactionId;
    private String gatewayName; // e.g., Razorpay, PayU, Paytm
    private String upiId;
    private String cardLast4Digits;
    
    // Status
    private String status; // initiated, pending, success, failed, refunded
    private String failureReason;
    
    // Timestamps
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;

    // Constructors
    public PaymentTransaction() {
        this.status = "initiated";
        this.initiatedAt = LocalDateTime.now();
    }

    public PaymentTransaction(int customerId, BigDecimal amount, String paymentType, String paymentMethod) {
        this();
        this.customerId = customerId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getCardLast4Digits() {
        return cardLast4Digits;
    }

    public void setCardLast4Digits(String cardLast4Digits) {
        this.cardLast4Digits = cardLast4Digits;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(LocalDateTime initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    // Utility methods
    
    /**
     * Get formatted amount with ₹ symbol
     */
    public String getFormattedAmount() {
        return "₹" + String.format("%,.2f", amount);
    }

    /**
     * Get formatted payment method display name
     */
    public String getFormattedPaymentMethod() {
        switch (paymentMethod.toLowerCase()) {
            case "upi": return "UPI";
            case "qr_code": return "QR Code";
            case "debit_card": return "Debit Card";
            case "credit_card": return "Credit Card";
            case "net_banking": return "Net Banking";
            case "wallet": return "Wallet";
            default: return paymentMethod;
        }
    }

    /**
     * Get formatted payment type display name
     */
    public String getFormattedPaymentType() {
        switch (paymentType.toLowerCase()) {
            case "recharge": return "Recharge";
            case "bill_payment": return "Bill Payment";
            case "addon_purchase": return "Data Addon";
            default: return paymentType;
        }
    }

    /**
     * Get status display text with color indicator
     */
    public String getStatusDisplay() {
        switch (status.toLowerCase()) {
            case "success": return "✅ Success";
            case "failed": return "❌ Failed";
            case "pending": return "⏳ Pending";
            case "refunded": return "↩️ Refunded";
            default: return "🔄 " + status;
        }
    }

    /**
     * Check if transaction is successful
     */
    public boolean isSuccessful() {
        return "success".equalsIgnoreCase(status);
    }

    /**
     * Check if transaction is pending
     */
    public boolean isPending() {
        return "pending".equalsIgnoreCase(status) || "initiated".equalsIgnoreCase(status);
    }

    /**
     * Get masked payment details for display
     */
    public String getMaskedPaymentDetails() {
        if (upiId != null && !upiId.isEmpty()) {
            return "UPI: " + upiId;
        } else if (cardLast4Digits != null && !cardLast4Digits.isEmpty()) {
            return "Card: **** " + cardLast4Digits;
        }
        return getFormattedPaymentMethod();
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "transactionId=" + transactionId +
                ", customerId=" + customerId +
                ", amount=" + amount +
                ", paymentType='" + paymentType + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", initiatedAt=" + initiatedAt +
                '}';
    }
}
