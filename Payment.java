package com.example.insurance_management_system.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String paymentId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "policy_id")
    private Policy policy;
    
    private double amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentFrequency frequency;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    
    private LocalDateTime paymentDate;
    
    private boolean successful;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    // For tracking UPI ID or bank used
    private String paymentDetails;
    
    public Payment() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public Policy getPolicy() {
        return policy;
    }
    
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public PaymentFrequency getFrequency() {
        return frequency;
    }
    
    public void setFrequency(PaymentFrequency frequency) {
        this.frequency = frequency;
    }
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public String getPaymentDetails() {
        return paymentDetails;
    }
    
    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
    
    public enum PaymentFrequency {
        MONTHLY,
        HALF_YEARLY,
        YEARLY
    }
    
    public enum PaymentMethod {
        UPI,
        NETBANKING
    }
    
    public enum PaymentStatus {
        PENDING,
        SUCCESSFUL,
        FAILED
    }
}