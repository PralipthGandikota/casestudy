package com.example.insurance_management_system.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "policies")
public class Policy {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(nullable = false)
	    private String policyNumber;
	    
	    @Column(nullable = false)
	    private String policyType;
	    
	    @Column(nullable = false)
	    private LocalDate startDate;
	    
	    @Column(nullable = false)
	    private LocalDate endDate;
	    
	    @Column(nullable = false)
	    private double premiumAmount;
	    
	    @Column(nullable = false)
	    private String status;
	    
	    @Column(nullable = false)
	    private LocalDateTime createdAt;
	    
	    @Column
	    private LocalDateTime cancelledAt;
	    
	    @Column(nullable = false)
	    private String approvalStatus = "PENDING"; // PENDING, APPROVED, REJECTED, ON_HOLD

	    
	    @ManyToOne
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;
	    
	    @OneToOne(mappedBy = "policy", cascade = CascadeType.ALL)
	    private InsuranceDetails insuranceDetails;
	    
	    // Getters and Setters
	    
	    public Long getId() {
	        return id;
	    }
	    
	    public void setId(Long id) {
	        this.id = id;
	    }
	    
	    public String getPolicyNumber() {
	        return policyNumber;
	    }
	    
	    public void setPolicyNumber(String policyNumber) {
	        this.policyNumber = policyNumber;
	    }
	    
	    public String getPolicyType() {
	        return policyType;
	    }
	    
	    public void setPolicyType(String policyType) {
	        this.policyType = policyType;
	    }
	    
	    public LocalDate getStartDate() {
	        return startDate;
	    }
	    
	 // Add getter and setter
	    public String getApprovalStatus() {
	        return approvalStatus;
	    }
	    
	    public void setApprovalStatus(String approvalStatus) {
	        this.approvalStatus = approvalStatus;
	    }
	    
	    public void setStartDate(LocalDate startDate) {
	        this.startDate = startDate;
	    }
	    
	    public LocalDate getEndDate() {
	        return endDate;
	    }
	    
	    public void setEndDate(LocalDate endDate) {
	        this.endDate = endDate;
	    }
	    
	    public double getPremiumAmount() {
	        return premiumAmount;
	    }
	    
	    public void setPremiumAmount(double premiumAmount) {
	        this.premiumAmount = premiumAmount;
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
	    
	    public LocalDateTime getCancelledAt() {
	        return cancelledAt;
	    }
	    
	    public void setCancelledAt(LocalDateTime cancelledAt) {
	        this.cancelledAt = cancelledAt;
	    }
	    
	    public User getUser() {
	        return user;
	    }
	    
	    public void setUser(User user) {
	        this.user = user;
	    }
	    
	    public InsuranceDetails getInsuranceDetails() {
	        return insuranceDetails;
	    }
	    
	    public void setInsuranceDetails(InsuranceDetails insuranceDetails) {
	        this.insuranceDetails = insuranceDetails;
	    }
	    
	    // Helper method to cancel the policy
	    public void cancel() {
	        this.status = "CANCELLED";
	        this.cancelledAt = LocalDateTime.now();
	    }
	    
	    // Helper method to check if policy is active
	    public boolean isActive() {
	    	return "ACTIVE".equals(this.status) && "APPROVED".equals(this.approvalStatus);
	    }
	    
	    @PrePersist
	    protected void onCreate() {
	        this.createdAt = LocalDateTime.now();
	        if (this.status == null) {
	            this.status = "ACTIVE";
	        }
	    }
}
