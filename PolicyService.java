package com.example.insurance_management_system.service;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.InsuranceDetails;
import com.example.insurance_management_system.model.User;
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
        
        // Calculate premium (simplified example calculation)
        double basePremium = details.getLifeCoverAmount() * 0.0002; // Simplified premium calculation
        
        // Adjust for age
        LocalDate now = LocalDate.now();
        int age = now.getYear() - details.getDob().getYear();
        double ageFactor = 1.0 + (age / 50.0);
        
        // Adjust for tobacco use
        double tobaccoFactor = details.isTobaccoConsumer() ? 1.5 : 1.0;
        
        double finalPremium = basePremium * ageFactor * tobaccoFactor;
        policy.setPremiumAmount(finalPremium);
        
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
        policy.setUser(user);
        
        // Calculate premium (simplified example calculation)
        double basePremium = details.getLifeCoverAmount() * 0.0005; // Higher premium for whole life
        
        // Adjust for age
        LocalDate now = LocalDate.now();
        int age = now.getYear() - details.getDob().getYear();
        double ageFactor = 1.0 + (age / 40.0);
        
        // Adjust for tobacco use
        double tobaccoFactor = details.isTobaccoConsumer() ? 1.7 : 1.0;
        
        double finalPremium = basePremium * ageFactor * tobaccoFactor;
        policy.setPremiumAmount(finalPremium);
        
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
        policy.setUser(user);
        policy.setCreatedAt(LocalDateTime.now()); // Explicitly set createdAt
        
        // Calculate premium (simplified example calculation)
        double basePremium = details.getLifeCoverAmount() * 0.0003; // Medium premium for money back policy
        
        // Adjust for age
        LocalDate now = LocalDate.now();
        int age = now.getYear() - details.getDob().getYear();
        double ageFactor = 1.0 + (age / 45.0);
        
        // Adjust for tobacco use
        double tobaccoFactor = details.isTobaccoConsumer() ? 1.4 : 1.0;
        
        double finalPremium = basePremium * ageFactor * tobaccoFactor;
        policy.setPremiumAmount(finalPremium);
        
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
        policy.setUser(user);
        policy.setCreatedAt(LocalDateTime.now()); // Explicitly set createdAt
        
        // Calculate premium (simplified example calculation)
        double basePremium = details.getLifeCoverAmount() * 0.0004; // Medium-high premium for endowment
        
        // Adjust for age
        LocalDate now = LocalDate.now();
        int age = now.getYear() - details.getDob().getYear();
        double ageFactor = 1.0 + (age / 45.0);
        
        // Adjust for tobacco use
        double tobaccoFactor = details.isTobaccoConsumer() ? 1.3 : 1.0;
        
        double finalPremium = basePremium * ageFactor * tobaccoFactor;
        policy.setPremiumAmount(finalPremium);
        
        // Set bidirectional relationship before saving
        policy.setInsuranceDetails(details);
        details.setPolicy(policy);
        
        // Save policy with details in a single transaction
        return policyRepository.save(policy);
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