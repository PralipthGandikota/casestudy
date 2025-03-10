package com.example.insurance_management_system.model;

import jakarta.persistence.*;

/**
 * Model class to represent insurance policy top-ups
 */
@Entity
@Table(name = "top_ups")
public class TopUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String topupType; // "Accident" or "Critical"
    
    @Column(nullable = false)
    private double coverageAmount;
    
    @OneToOne
    @JoinColumn(name = "insurance_details_id")
    private InsuranceDetails insuranceDetails;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTopupType() {
        return topupType;
    }
    
    public void setTopupType(String topupType) {
        this.topupType = topupType;
    }
    
    public double getCoverageAmount() {
        return coverageAmount;
    }
    
    public void setCoverageAmount(double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }
    
    public InsuranceDetails getInsuranceDetails() {
        return insuranceDetails;
    }
    
    public void setInsuranceDetails(InsuranceDetails insuranceDetails) {
        this.insuranceDetails = insuranceDetails;
    }
}