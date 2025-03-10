package com.example.insurance_management_system.model;

/**
 * Model class to represent premium calculation details
 */
public class PremiumDetails {
    private int currentAge;
    private int policyDuration;
    private double lifeCoverAmount;
    private double riskFactor;
    private double tobaccoCharge;
    private double ageRiskCharge;
    private double basePremium;
    private double topUpPremium;
    private double gstAmount;
    private double totalPremium;
    
    // Getters and Setters
    public int getCurrentAge() {
        return currentAge;
    }
    
    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }
    
    public int getPolicyDuration() {
        return policyDuration;
    }
    
    public void setPolicyDuration(int policyDuration) {
        this.policyDuration = policyDuration;
    }
    
    public double getLifeCoverAmount() {
        return lifeCoverAmount;
    }
    
    public void setLifeCoverAmount(double lifeCoverAmount) {
        this.lifeCoverAmount = lifeCoverAmount;
    }
    
    public double getRiskFactor() {
        return riskFactor;
    }
    
    public void setRiskFactor(double riskFactor) {
        this.riskFactor = riskFactor;
    }
    
    public double getTobaccoCharge() {
        return tobaccoCharge;
    }
    
    public void setTobaccoCharge(double tobaccoCharge) {
        this.tobaccoCharge = tobaccoCharge;
    }
    
    public double getAgeRiskCharge() {
        return ageRiskCharge;
    }
    
    public void setAgeRiskCharge(double ageRiskCharge) {
        this.ageRiskCharge = ageRiskCharge;
    }
    
    public double getBasePremium() {
        return basePremium;
    }
    
    public void setBasePremium(double basePremium) {
        this.basePremium = basePremium;
    }
    
    public double getTopUpPremium() {
        return topUpPremium;
    }
    
    public void setTopUpPremium(double topUpPremium) {
        this.topUpPremium = topUpPremium;
    }
    
    public double getGstAmount() {
        return gstAmount;
    }
    
    public void setGstAmount(double gstAmount) {
        this.gstAmount = gstAmount;
    }
    
    public double getTotalPremium() {
        return totalPremium;
    }
    
    public void setTotalPremium(double totalPremium) {
        this.totalPremium = totalPremium;
    }
}