package com.example.insurance_management_system.controller;

import com.example.insurance_management_system.model.Payment;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.service.PaymentService;
import com.example.insurance_management_system.service.PolicyService;
import com.example.insurance_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Shows the payment page for a specific policy
     */
    @GetMapping("/policy/{policyId}")
    public String showPaymentPage(@PathVariable Long policyId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<Policy> policyOpt = policyService.getPolicyById(policyId);
        
        if (!policyOpt.isPresent()) {
            model.addAttribute("errorMessage", "Policy not found");
            return "error/not-found";
        }
        
        Policy policy = policyOpt.get();
        
        // Check if policy belongs to user
        if (!policy.getUser().getId().equals(user.getId())) {
            return "redirect:/auth/access-denied";
        }
        
        model.addAttribute("policy", policy);
        
        // Calculate premium options for different frequencies
        Map<String, Double> premiumOptions = new HashMap<>();
        premiumOptions.put("monthly", paymentService.calculatePremiumAmount(policy, Payment.PaymentFrequency.MONTHLY));
        premiumOptions.put("halfYearly", paymentService.calculatePremiumAmount(policy, Payment.PaymentFrequency.HALF_YEARLY));
        premiumOptions.put("yearly", paymentService.calculatePremiumAmount(policy, Payment.PaymentFrequency.YEARLY));
        
        model.addAttribute("premiumOptions", premiumOptions);
        
        // Calculate savings
        double monthlySixMonths = premiumOptions.get("monthly") * 6;
        double monthlyTwelveMonths = premiumOptions.get("monthly") * 12;
        
        Map<String, Double> savings = new HashMap<>();
        savings.put("halfYearly", monthlySixMonths - premiumOptions.get("halfYearly"));
        savings.put("yearly", monthlyTwelveMonths - premiumOptions.get("yearly"));
        
        model.addAttribute("savings", savings);
        
        return "dashboard/payments";
    }
    
    /**
     * Process a payment API endpoint
     */
    @PostMapping("/process")
    @ResponseBody
    public ResponseEntity<?> processPayment(@RequestParam Long policyId,
                                           @RequestParam String frequency,
                                           @RequestParam String method,
                                           @RequestParam(required = false) String paymentDetails,
                                           Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<Policy> policyOpt = policyService.getPolicyById(policyId);
        
        if (!policyOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Policy not found"));
        }
        
        Policy policy = policyOpt.get();
        
        // Check if policy belongs to user
        if (!policy.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Access denied"));
        }
        
        try {
            // Convert string parameters to enum values
            Payment.PaymentFrequency paymentFrequency = Payment.PaymentFrequency.valueOf(frequency.toUpperCase());
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(method.toUpperCase());
            
            // Process the payment
            Payment payment = paymentService.processPayment(policyId, paymentFrequency, paymentMethod, paymentDetails);
            
            // Return success response with payment details
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentId", payment.getPaymentId());
            response.put("policyId", policy.getId());
            response.put("policyNumber", policy.getPolicyNumber());
            response.put("amount", payment.getAmount());
            response.put("date", payment.getPaymentDate().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    /**
     * View all payments for the current user
     */
    @GetMapping("/history")
    public String viewPaymentHistory(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("payments", paymentService.getPaymentsByUser(user));
        return "dashboard/payment-history";
    }
    
    /**
     * View payment receipt for a specific payment
     */
    @GetMapping("/receipt/{paymentId}")
    public String viewPaymentReceipt(@PathVariable String paymentId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<Payment> paymentOpt = paymentService.getPaymentByPaymentId(paymentId);
        
        if (!paymentOpt.isPresent()) {
            model.addAttribute("errorMessage", "Payment not found");
            return "error/not-found";
        }
        
        Payment payment = paymentOpt.get();
        
        // Check if payment belongs to user's policy
        if (!payment.getPolicy().getUser().getId().equals(user.getId())) {
            return "redirect:/auth/access-denied";
        }
        
        model.addAttribute("payment", payment);
        return "dashboard/payment-receipt";
    }
}