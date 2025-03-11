package com.example.insurance_management_system.service;

import com.example.insurance_management_system.model.Payment;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.repository.PaymentRepository;
import com.example.insurance_management_system.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    /**
     * Retrieves all payments made by a user
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByPolicyUserOrderByPaymentDateDesc(user);
    }
    
    /**
     * Retrieves all payments for a specific policy
     */
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByPolicy(Policy policy) {
        return paymentRepository.findByPolicy(policy);
    }
    
    /**
     * Calculates premium amount based on payment frequency with applicable discounts
     * 
     * @param policy The insurance policy
     * @param frequency Payment frequency (MONTHLY, HALF_YEARLY, YEARLY)
     * @return Final premium amount after applying discounts
     */
    public double calculatePremiumAmount(Policy policy, Payment.PaymentFrequency frequency) {
        double basePremiumAmount = policy.getPremiumAmount();
        
        switch (frequency) {
            case MONTHLY:
                // No discount for monthly payments
                return basePremiumAmount;
            case HALF_YEARLY:
                // 15% discount on 6 months of premium
                double halfYearlyBase = basePremiumAmount * 6;
                return halfYearlyBase * 0.85; // Apply 15% discount
            case YEARLY:
                // 25% discount on 12 months of premium
                double yearlyBase = basePremiumAmount * 12;
                return yearlyBase * 0.75; // Apply 25% discount
            default:
                return basePremiumAmount;
        }
    }
    
    /**
     * Process a payment for a policy
     * 
     * @param policyId The ID of the policy
     * @param frequency Payment frequency (MONTHLY, HALF_YEARLY, YEARLY)
     * @param method Payment method (UPI, NETBANKING)
     * @param paymentDetails Additional payment details (UPI ID or bank name)
     * @return The created Payment object if successful
     */
    @Transactional
    public Payment processPayment(Long policyId, Payment.PaymentFrequency frequency, 
                                 Payment.PaymentMethod method, String paymentDetails) {
        Optional<Policy> policyOpt = policyRepository.findById(policyId);
        
        if (!policyOpt.isPresent()) {
            throw new RuntimeException("Policy not found");
        }
        
        Policy policy = policyOpt.get();
        
        // Check if policy is eligible for payment
        if (!"APPROVED".equals(policy.getApprovalStatus()) || !"ACTIVE".equals(policy.getStatus())) {
            throw new RuntimeException("Policy is not eligible for payment");
        }
        
        // Calculate the premium amount with applicable discounts
        double amount = calculatePremiumAmount(policy, frequency);
        
        // Create a new payment record
        Payment payment = new Payment();
        payment.setPaymentId(generatePaymentId());
        payment.setPolicy(policy);
        payment.setAmount(amount);
        payment.setFrequency(frequency);
        payment.setMethod(method);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentDetails(paymentDetails);
        
        // For this implementation, all payments are successful immediately
        payment.setSuccessful(true);
        payment.setStatus(Payment.PaymentStatus.SUCCESSFUL);
        
        return paymentRepository.save(payment);
    }
    
    /**
     * Generate a unique payment ID
     */
    private String generatePaymentId() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Get payment details by payment ID
     */
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentId().equals(paymentId))
                .findFirst();
    }
}