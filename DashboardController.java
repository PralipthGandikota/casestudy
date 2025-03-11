package com.example.insurance_management_system.controller;

import com.example.insurance_management_system.model.Payment;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.service.PaymentService;
import com.example.insurance_management_system.service.PolicyService;
import com.example.insurance_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        // Determine user role and redirect to appropriate dashboard
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        
        // For regular user, load their dashboard
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Policy> activePolicies = policyService.getActivePoliciesByUser(user);
        List<Policy> pendingPolicies = policyService.getPendingPoliciesByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("activePolicies", activePolicies);
        model.addAttribute("pendingPolicies", pendingPolicies);
        model.addAttribute("totalActivePolicies", activePolicies.size());
        model.addAttribute("totalPendingPolicies", pendingPolicies.size());
        
        return "dashboard/client";
    }
    
    @GetMapping("/policies")
    public String viewPolicies(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Policy> policies = policyService.getPoliciesByUser(user);
        model.addAttribute("policies", policies);
        
        return "dashboard/policies";
    }
    
    @GetMapping("/pending-policies")
    public String pendingPolicies(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Policy> pendingPolicies = policyService.getPendingPoliciesByUser(user);
        model.addAttribute("pendingPolicies", pendingPolicies);
        return "dashboard/pending-policies";
    }
    
    @GetMapping("/payments")
    public String viewPayments(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get all active and approved policies
        List<Policy> eligiblePolicies = policyService.getActivePoliciesByUser(user).stream()
                .filter(p -> "APPROVED".equals(p.getApprovalStatus()))
                .toList();
        
        model.addAttribute("eligiblePolicies", eligiblePolicies);
        model.addAttribute("payments", paymentService.getPaymentsByUser(user));
        
        return "dashboard/payment-dashboard";
    }
    
    // New endpoint for specific policy payment
    @GetMapping("/payments/{policyId}")
    public String payPolicy(@PathVariable("policyId") Long policyId, 
                           Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get the policy and verify it belongs to the user
        Optional<Policy> policyOpt = policyService.getPolicyById(policyId);
        
        if (policyOpt.isEmpty()) {
            return "redirect:/dashboard/payments?error=policy_not_found";
        }
        
        Policy policy = policyOpt.get();
        
        // Verify the policy belongs to the user
        if (!policy.getUser().getId().equals(user.getId())) {
            return "redirect:/dashboard/payments?error=unauthorized_access";
        }
        
        // Verify the policy is eligible for payment
        if (!"APPROVED".equals(policy.getApprovalStatus()) || !"ACTIVE".equals(policy.getStatus())) {
            return "redirect:/dashboard/payments?error=policy_not_eligible";
        }
        
        model.addAttribute("policy", policy);
        model.addAttribute("recentPayments", paymentService.getPaymentsByPolicy(policy));
        
        return "dashboard/payment";
    }
    
    // New endpoint to process payment
    @PostMapping("/payments/{policyId}/process")
    @ResponseBody
    public ResponseEntity<?> processPayment(
            @PathVariable("policyId") Long policyId,
            @RequestParam("frequency") String frequency,
            @RequestParam("method") String method,
            @RequestParam("paymentDetails") String paymentDetails,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get the policy and verify it belongs to the user
            Optional<Policy> policyOpt = policyService.getPolicyById(policyId);
            
            if (policyOpt.isEmpty()) {
                throw new RuntimeException("Policy not found");
            }
            
            Policy policy = policyOpt.get();
            
            // Verify the policy belongs to the user
            if (!policy.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to policy");
            }
            
            // Convert string frequency to enum
            Payment.PaymentFrequency paymentFrequency = Payment.PaymentFrequency.valueOf(frequency.toUpperCase());
            
            // Convert string method to enum
            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(method.toUpperCase());
            
            // Process the payment
            Payment payment = paymentService.processPayment(policyId, paymentFrequency, paymentMethod, paymentDetails);
            
            // Return success response with payment details
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentId", payment.getPaymentId());
            response.put("policyId", policyId);
            response.put("amount", payment.getAmount());
            response.put("date", payment.getPaymentDate().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // New endpoint to get policy details
    @GetMapping("/payments/{policyId}/details")
    @ResponseBody
    public ResponseEntity<?> getPolicyDetails(
            @PathVariable("policyId") Long policyId,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get the policy and verify it belongs to the user
            Optional<Policy> policyOpt = policyService.getPolicyById(policyId);
            
            if (policyOpt.isEmpty()) {
                throw new RuntimeException("Policy not found");
            }
            
            Policy policy = policyOpt.get();
            
            // Verify the policy belongs to the user
            if (!policy.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to policy");
            }
            
            // Return policy details
            Map<String, Object> response = new HashMap<>();
            response.put("id", policy.getId());
            response.put("policyNumber", policy.getPolicyNumber());
            response.put("policyType", policy.getPolicyType());
            response.put("startDate", policy.getStartDate().toString());
            response.put("endDate", policy.getEndDate().toString());
            response.put("premiumAmount", policy.getPremiumAmount());
            response.put("status", policy.getStatus());
            response.put("approvalStatus", policy.getApprovalStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
