package com.example.insurance_management_system.service;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.InsuranceDetails;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.model.TopUp;
import com.example.insurance_management_system.model.PremiumDetails;
import com.example.insurance_management_system.repository.PolicyRepository;
import com.example.insurance_management_system.repository.InsuranceDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PolicyService {
    @Autowired
    private PolicyRepository policyRepository;
    
    @Autowired
    private InsuranceDetailsRepository insuranceDetailsRepository;
    
    @Transactional(readOnly = true)
    public List<Policy> getPoliciesByUser(User user) {
        return policyRepository.findByUser(user);
    }
    
    @Transactional(readOnly = true)
    public List<Policy> getActivePoliciesByUser(User user) {
        return policyRepository.findByUserAndStatus(user, "ACTIVE");
    }
    
    @Transactional(readOnly = true)
    public Optional<Policy> getPolicyById(Long id) {
        return policyRepository.findById(id);
    }
    
    @Transactional
    public Policy createTermInsurancePolicy(User user, InsuranceDetails details, int durationYears) {
        // Create the policy
        Policy policy = new Policy();
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setPolicyType("TERM");
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusYears(durationYears));
        policy.setStatus("ACTIVE");
        policy.setApprovalStatus("PENDING"); // Set initial approval status
        policy.setUser(user);
        policy.setCreatedAt(LocalDateTime.now()); // Explicitly set createdAt
        
        // Calculate premium using the new method
        PremiumDetails premiumDetails = calculatePremium(details, durationYears);
        policy.setPremiumAmount(premiumDetails.getTotalPremium());
        
        // Set bidirectional relationship before saving
        policy.setInsuranceDetails(details);
        details.setPolicy(policy);
        
        // Save policy with details in a single transaction
        return policyRepository.save(policy);
    }
    
    @Transactional
    public Policy createWholeLifePolicy(User user, InsuranceDetails details) {
        // Create the policy
        Policy policy = new Policy();
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setPolicyType("WHOLE_LIFE");
        policy.setStartDate(LocalDate.now());
        policy.setCreatedAt(LocalDateTime.now()); // Explicitly set createdAt
        
        // For whole life, typically covers until a very old age like 100
        LocalDate endDate = details.getDob().plusYears(100);
        policy.setEndDate(endDate);
        
        policy.setStatus("ACTIVE");
        policy.setApprovalStatus("PENDING");
        policy.setUser(user);
        
        // Calculate premium using the new method
        int durationYears = Period.between(LocalDate.now(), endDate).getYears();
        PremiumDetails premiumDetails = calculatePremium(details, durationYears);
        policy.setPremiumAmount(premiumDetails.getTotalPremium());
        
        // Set bidirectional relationship before saving
        policy.setInsuranceDetails(details);
        details.setPolicy(policy);
        
        // Save policy with details in a single transaction
        return policyRepository.save(policy);
    }
    
    @Transactional
    public Policy createMoneyBackPolicy(User user, InsuranceDetails details, int durationYears) {
        // Create the policy
        Policy policy = new Policy();
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setPolicyType("MONEY_BACK");
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusYears(durationYears));
        policy.setStatus("ACTIVE");
        policy.setApprovalStatus("PENDING");
        policy.setUser(user);
        policy.setCreatedAt(LocalDateTime.now()); // Explicitly set createdAt
        
        // Calculate premium using the new method
        PremiumDetails premiumDetails = calculatePremium(details, durationYears);
        policy.setPremiumAmount(premiumDetails.getTotalPremium());
        
        // Set bidirectional relationship before saving
        policy.setInsuranceDetails(details);
        details.setPolicy(policy);
        
        // Save policy with details in a single transaction
        return policyRepository.save(policy);
    }
    
    @Transactional
    public Policy createEndowmentPolicy(User user, InsuranceDetails details, int durationYears) {
        // Create the policy
        Policy policy = new Policy();
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setPolicyType("ENDOWMENT");
        policy.setStartDate(LocalDate.now());
        policy.setEndDate(LocalDate.now().plusYears(durationYears));
        policy.setStatus("ACTIVE");
        policy.setApprovalStatus("PENDING");
        policy.setUser(user);
        policy.setCreatedAt(LocalDateTime.now()); // Explicitly set createdAt
        
        // Calculate premium using the new method
        PremiumDetails premiumDetails = calculatePremium(details, durationYears);
        policy.setPremiumAmount(premiumDetails.getTotalPremium());
        
        // Set bidirectional relationship before saving
        policy.setInsuranceDetails(details);
        details.setPolicy(policy);
        
        // Save policy with details in a single transaction
        return policyRepository.save(policy);
    }
    
    /**
     * Calculate premium based on user details, policy duration, and coverage amount
     * @param details Insurance details with user information
     * @param durationYears Policy duration in years
     * @return PremiumDetails object containing premium calculation breakdown
     */
    public PremiumDetails calculatePremium(InsuranceDetails details, int durationYears) {
        PremiumDetails premiumDetails = new PremiumDetails();
        
        // Calculate current age
        int currentAge = Period.between(details.getDob(), LocalDate.now()).getYears();
        int premiumAge = durationYears + currentAge;
        
        premiumDetails.setCurrentAge(currentAge);
        premiumDetails.setPolicyDuration(durationYears);
        premiumDetails.setLifeCoverAmount(details.getLifeCoverAmount());
        
        // Initialize risk factor
        double riskFactor = 1.0;
        double tobaccoCharge = 0.0;
        
        // Tobacco consumption charge
        if (details.isTobaccoConsumer()) {
            riskFactor *= 1.2;
            tobaccoCharge = 0.2 * (details.getLifeCoverAmount() / 1000.0);
        }
        premiumDetails.setTobaccoCharge(tobaccoCharge);
        
        // Age-based risk factor
        double ageRiskCharge = 0.0;
        if (premiumAge < 35) {
            // No additional risk for younger ages
        } else if (premiumAge < 65) {
            riskFactor *= (1 + (premiumAge - 35) * 0.01);
            ageRiskCharge = (premiumAge - 35) * 0.01 * (details.getLifeCoverAmount() / 1000.0);
        } else if (premiumAge < 80) {
            double riskAt65 = 1 + (65 - 35) * 0.01;
            riskFactor *= riskAt65 * Math.exp((premiumAge - 65) * 0.05);
            ageRiskCharge = riskFactor * (details.getLifeCoverAmount() / 1000.0);
        } else {
            double riskAt80 = (1 + (65 - 35) * 0.01) * Math.exp((80 - 65) * 0.05);
            riskFactor *= riskAt80 * Math.exp((premiumAge - 80) * 0.1);
            ageRiskCharge = riskFactor * (details.getLifeCoverAmount() / 1000.0);
        }
        premiumDetails.setAgeRiskCharge(ageRiskCharge);
        premiumDetails.setRiskFactor(riskFactor);
        
        // Base premium calculation
        double basePremium = (details.getLifeCoverAmount() / 1000.0) * riskFactor;
        premiumDetails.setBasePremium(basePremium);
        
        // Top-up premium calculation
        double topUpPremium = 0.0;
        if (details.getTopUp() != null) {
            TopUp topUp = details.getTopUp();
            if ("Accident".equalsIgnoreCase(topUp.getTopupType())) {
                topUpPremium = (topUp.getCoverageAmount() / 50000.0) * 50.0;
            } else if ("Critical".equalsIgnoreCase(topUp.getTopupType())) {
                if (topUp.getCoverageAmount() <= 300000) {
                    topUpPremium = 100;
                } else {
                    topUpPremium = 100 + ((topUp.getCoverageAmount() - 300000) / 300000.0) * 400;
                }
            }
        }
        premiumDetails.setTopUpPremium(topUpPremium);
        
        // Calculate GST and total premium
        double totalPremiumBeforeGST = basePremium + topUpPremium;
        double gstAmount = totalPremiumBeforeGST * 0.18;
        double totalPremium = totalPremiumBeforeGST + gstAmount;
        
        premiumDetails.setGstAmount(gstAmount);
        premiumDetails.setTotalPremium(totalPremium);
        
        return premiumDetails;
    }
    
    @Transactional(readOnly = true)
    public List<Policy> getPendingPoliciesByUser(User user) {
        return policyRepository.findByUserAndApprovalStatus(user, "PENDING");
    }
    
    @Transactional
    public boolean cancelPolicy(Long policyId, User user) {
        Optional<Policy> policyOpt = policyRepository.findById(policyId);
        
        if (policyOpt.isPresent()) {
            Policy policy = policyOpt.get();
            
            // Verify the policy belongs to the user
            if (!policy.getUser().getId().equals(user.getId())) {
                return false;
            }
            
            // Cancel the policy
            policy.setStatus("CANCELLED");
            policy.setCancelledAt(LocalDateTime.now());
            policyRepository.save(policy);
            
            return true;
        }
        
        return false;
    }
    
    private String generatePolicyNumber() {
        // Generate a unique policy number
        return "POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
