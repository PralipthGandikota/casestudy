package com.example.insurance_management_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "claim_number", nullable = false, unique = true)
    private String claimNumber;
    
    @Column(name = "claim_type", nullable = false)
    private String claimType;
    
    @Column(name = "claim_amount", nullable = false)
    private double claimAmount;
    
    @Column(name = "filed_date", nullable = false)
    private LocalDate filedDate;
    
    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "documents_path")
    private String documentsPath;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getClaimNumber() {
        return claimNumber;
    }
    
    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }
    
    public String getClaimType() {
        return claimType;
    }
    
    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }
    
    public double getClaimAmount() {
        return claimAmount;
    }
    
    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }
    
    public LocalDate getFiledDate() {
        return filedDate;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    public void setFiledDate(LocalDate filedDate) {
        this.filedDate = filedDate;
    }
    
    public LocalDate getIncidentDate() {
        return incidentDate;
    }
    
    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public String getDocumentsPath() {
        return documentsPath;
    }
    
    public void setDocumentsPath(String documentsPath) {
        this.documentsPath = documentsPath;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public Policy getPolicy() {
        return policy;
    }
    
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
    
    // Helper methods
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
    
    public boolean isProcessing() {
        return "PROCESSING".equals(this.status);
    }
    
    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }
    
    public boolean isRejected() {
        return "REJECTED".equals(this.status);
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.filedDate == null) {
            this.filedDate = LocalDate.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
