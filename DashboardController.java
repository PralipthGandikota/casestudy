package com.example.insurance_management_system.controller;

import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.service.PaymentService;
import com.example.insurance_management_system.service.PolicyService;
import com.example.insurance_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}
