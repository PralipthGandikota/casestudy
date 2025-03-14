package com.example.insurance_management_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "insurance_details")
public class InsuranceDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private LocalDate dob;
    
    @Column(nullable = false)
    private String gender;
    
    @Column(nullable = false)
    private String email;
    
    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;
    
    @Column(name = "tobacco_consumer", nullable = false)
    private boolean tobaccoConsumer;
    
    @Column(name = "annual_income", nullable = false)
    private double annualIncome;
    
    @Column(name = "life_cover_amount", nullable = false)
    private double lifeCoverAmount;
    
    @Column(name = "life_cover_age", nullable = false)
    private int lifeCoverAge;
    
    @OneToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;
    
    @OneToOne(mappedBy = "insuranceDetails", cascade = CascadeType.ALL)
    private TopUp topUp;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDate getDob() {
        return dob;
    }
    
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getMobileNumber() {
        return mobileNumber;
    }
    
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    
    public boolean isTobaccoConsumer() {
        return tobaccoConsumer;
    }
    
    public void setTobaccoConsumer(boolean tobaccoConsumer) {
        this.tobaccoConsumer = tobaccoConsumer;
    }
    
    public double getAnnualIncome() {
        return annualIncome;
    }
    
    public void setAnnualIncome(double annualIncome) {
        this.annualIncome = annualIncome;
    }
    
    public double getLifeCoverAmount() {
        return lifeCoverAmount;
    }
    
    public void setLifeCoverAmount(double lifeCoverAmount) {
        final double MAX_LIMIT = 30000000.0;
        if (lifeCoverAmount > MAX_LIMIT) {
            throw new IllegalArgumentException("Life cover amount cannot exceed 3 crores INR (30,000,000)");
        }
        this.lifeCoverAmount = lifeCoverAmount;
    }
    
    public int getLifeCoverAge() {
        return lifeCoverAge;
    }
    
    public void setLifeCoverAge(int lifeCoverAge) {
        if (lifeCoverAge <= 0 || lifeCoverAge > 100) {
            throw new IllegalArgumentException("Life cover age must be between 1 and 100 years");
        }
        this.lifeCoverAge = lifeCoverAge;
    }
    
    public Policy getPolicy() {
        return policy;
    }
    
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
    
    public TopUp getTopUp() {
        return topUp;
    }
    
    public void setTopUp(TopUp topUp) {
        this.topUp = topUp;
    }
}
