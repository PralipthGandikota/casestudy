package com.example.insurance_management_system.controller;
import com.example.insurance_management_system.model.InsuranceDetails;

import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.TopUp;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.service.PolicyService;
import com.example.insurance_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("/policy")
public class PolicyController {
	 @Autowired
	    private UserService userService;
	    
	    @Autowired
	    private PolicyService policyService;
	    
	    @GetMapping("/create")
	    public String showPolicyTypeSelection() {
	        return "policy/select-type";
	    }
	    
	    @GetMapping("/create/{type}")
	    public String showCreatePolicyForm(@PathVariable String type, Model model) {
	        model.addAttribute("policyType", type);
	        model.addAttribute("insuranceDetails", new InsuranceDetails());
	        model.addAttribute("topUp", new TopUp());
	        return "policy/create";
	    }
	    
	    @PostMapping("/create/term")
	    public String createTermPolicy(
	            @Valid @ModelAttribute InsuranceDetails insuranceDetails,
	            BindingResult result,
	            @RequestParam int durationYears,
	            @RequestParam(required = false) String topUpType,
	            @RequestParam(required = false, defaultValue = "0") double topUpCoverageAmount,
	            Authentication authentication,
	            RedirectAttributes redirectAttributes) {
	        
	        if (result.hasErrors()) {
	            return "policy/create";
	        }
	     // Process top-up if selected
	        if (topUpType != null && !topUpType.isEmpty() && topUpCoverageAmount > 0) {
	            TopUp topUp = new TopUp();
	            topUp.setTopupType(topUpType);
	            topUp.setCoverageAmount(topUpCoverageAmount);
	            topUp.setInsuranceDetails(insuranceDetails);
	            insuranceDetails.setTopUp(topUp);
	        }
	        
	        String username = authentication.getName();
	        User user = userService.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        
	        try {
	            Policy policy = policyService.createTermInsurancePolicy(user, insuranceDetails, durationYears);
	            redirectAttributes.addFlashAttribute("successMessage", 
	                    "Term Insurance Policy created successfully with policy number: " + policy.getPolicyNumber());
	            return "redirect:/dashboard/policies";
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create policy: " + e.getMessage());
	            return "redirect:/policy/create/term";
	        }
	    }
	    
	    @PostMapping("/create/whole-life")
	    public String createWholeLifePolicy(
	            @Valid @ModelAttribute InsuranceDetails insuranceDetails,
	            BindingResult result,
	            @RequestParam(required = false) String topUpType,
	            @RequestParam(required = false, defaultValue = "0") double topUpCoverageAmount,
	            Authentication authentication,
	            RedirectAttributes redirectAttributes) {
	        
	        if (result.hasErrors()) {
	            return "policy/create";
	        }
	     // Process top-up if selected
	        if (topUpType != null && !topUpType.isEmpty() && topUpCoverageAmount > 0) {
	            TopUp topUp = new TopUp();
	            topUp.setTopupType(topUpType);
	            topUp.setCoverageAmount(topUpCoverageAmount);
	            topUp.setInsuranceDetails(insuranceDetails);
	            insuranceDetails.setTopUp(topUp);
	        }
	        
	        String username = authentication.getName();
	        User user = userService.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        
	        try {
	            Policy policy = policyService.createWholeLifePolicy(user, insuranceDetails);
	            redirectAttributes.addFlashAttribute("successMessage", 
	                    "Whole Life Insurance Policy created successfully with policy number: " + policy.getPolicyNumber());
	            return "redirect:/dashboard/policies";
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create policy: " + e.getMessage());
	            return "redirect:/policy/create/whole-life";
	        }
	    }
	    
	    @PostMapping("/create/money-back")
	    public String createMoneyBackPolicy(
	            @Valid @ModelAttribute InsuranceDetails insuranceDetails,
	            BindingResult result,
	            @RequestParam int durationYears,
	            Authentication authentication,
	            RedirectAttributes redirectAttributes) {
	        
	        if (result.hasErrors()) {
	            return "policy/create";
	        }
	        
	        String username = authentication.getName();
	        User user = userService.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        
	        try {
	            Policy policy = policyService.createMoneyBackPolicy(user, insuranceDetails, durationYears);
	            redirectAttributes.addFlashAttribute("successMessage", 
	                    "Money Back Policy created successfully with policy number: " + policy.getPolicyNumber());
	            return "redirect:/dashboard/policies";
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create policy: " + e.getMessage());
	            return "redirect:/policy/create/money-back";
	        }
	    }
	    
	    @PostMapping("/create/endowment")
	    public String createEndowmentPolicy(
	            @Valid @ModelAttribute InsuranceDetails insuranceDetails,
	            BindingResult result,
	            @RequestParam int durationYears,
	            Authentication authentication,
	            RedirectAttributes redirectAttributes) {
	        
	        if (result.hasErrors()) {
	            return "policy/create";
	        }
	        
	        String username = authentication.getName();
	        User user = userService.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        
	        try {
	            Policy policy = policyService.createEndowmentPolicy(user, insuranceDetails, durationYears);
	            redirectAttributes.addFlashAttribute("successMessage", 
	                    "Endowment Policy created successfully with policy number: " + policy.getPolicyNumber());
	            return "redirect:/dashboard/policies";
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create policy: " + e.getMessage());
	            return "redirect:/policy/create/endowment";
	        }
	    }
	    
	    @GetMapping("/view/{id}")
	    public String viewPolicy(@PathVariable Long id, Authentication authentication, Model model) {
	        String username = authentication.getName();
	        User user = userService.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        
	        Policy policy = policyService.getPolicyById(id)
	                .orElseThrow(() -> new RuntimeException("Policy not found"));
	        
	        // Check if policy belongs to user or user is admin
	        if (!policy.getUser().getId().equals(user.getId()) && 
	            !user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
	            return "redirect:/auth/access-denied";
	        }
	        
	        model.addAttribute("policy", policy);
	        return "policy/details";
	    }
	    
	    @PostMapping("/cancel/{id}")
	    public String cancelPolicy(@PathVariable Long id, 
	                             Authentication authentication,
	                             RedirectAttributes redirectAttributes) {
	        String username = authentication.getName();
	        User user = userService.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        
	        boolean success = policyService.cancelPolicy(id, user);
	        
	        if (success) {
	            redirectAttributes.addFlashAttribute("successMessage", "Policy cancelled successfully");
	        } else {
	            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel policy");
	        }
	        
	        return "redirect:/dashboard/policies";
	    }
}
