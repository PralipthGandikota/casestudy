package com.example.insurance_management_system.controller;

import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.service.PolicyService;
import com.example.insurance_management_system.service.*;
import com.example.insurance_management_system.model.Policy;
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
        
        List<Policy> policies = policyService.getPoliciesByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("policies", policies);
        
        return "dashboard/client";
    }
    
    @GetMapping("/policies")
    public String viewPolicies(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Policy> policies = policyService.getPoliciesByUser(user);
        model.addAttribute("policies", policies);
        
        return "policy/list";
    }
 // In DashboardController.java
    @GetMapping("/pending-policies")
    public String pendingPolicies(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Policy> pendingPolicies = policyService.getPendingPoliciesByUser(user);
        model.addAttribute("pendingPolicies", pendingPolicies);
        return "dashboard/pending-policies";
    }
}
