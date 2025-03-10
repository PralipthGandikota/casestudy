package com.example.insurance_management_system.controller;

import com.example.insurance_management_system.model.Claim;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.service.ClaimService;
import com.example.insurance_management_system.service.PolicyService;
import com.example.insurance_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;
    
    @Autowired
    private PolicyService policyService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/create")
    public String showCreateClaimForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Policy> activeUserPolicies = policyService.getActivePoliciesByUser(user);
        if (activeUserPolicies.isEmpty()) {
            model.addAttribute("errorMessage", "You don't have any active policies to file a claim against.");
            return "redirect:/dashboard/policies";
        }
        
        model.addAttribute("policies", activeUserPolicies);
        model.addAttribute("claim", new Claim());
        return "claims/create";
    }
    
    @PostMapping("/create")
    public String createClaim(
            @Valid @ModelAttribute Claim claim,
            BindingResult result,
            @RequestParam Long policyId,
            @RequestParam(required = false) MultipartFile document,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Policy> activeUserPolicies = policyService.getActivePoliciesByUser(user);
            model.addAttribute("policies", activeUserPolicies);
            return "claims/create";
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            Claim savedClaim = claimService.createClaim(claim, policyId, user);
            
            // Upload document if provided
            if (document != null && !document.isEmpty()) {
                try {
                    claimService.uploadClaimDocuments(document, savedClaim.getId());
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("warningMessage", 
                            "Claim created but document upload failed: " + e.getMessage());
                    return "redirect:/dashboard/claims";
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Claim submitted successfully with claim number: " + savedClaim.getClaimNumber());
            return "redirect:/dashboard/claims";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create claim: " + e.getMessage());
            return "redirect:/claims/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditClaimForm(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Claim claim = claimService.getClaimById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        
        // Check if claim belongs to user
        if (!claim.getPolicy().getUser().getId().equals(user.getId())) {
            return "redirect:/auth/access-denied";
        }
        
        // Check if claim is in a state that can be edited
        if (!"PENDING".equals(claim.getStatus())) {
            model.addAttribute("errorMessage", "Only pending claims can be edited");
            return "redirect:/dashboard/claims";
        }
        
        model.addAttribute("claim", claim);
        return "claims/edit";
    }
    
    @PostMapping("/edit/{id}")
    public String updateClaim(
            @PathVariable Long id,
            @Valid @ModelAttribute Claim claim,
            BindingResult result,
            @RequestParam(required = false) MultipartFile document,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "claims/edit";
        }
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        try {
            Claim updatedClaim = claimService.updateClaim(id, claim, user);
            
            // Upload document if provided
            if (document != null && !document.isEmpty()) {
                try {
                    claimService.uploadClaimDocuments(document, updatedClaim.getId());
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("warningMessage", 
                            "Claim updated but document upload failed: " + e.getMessage());
                    return "redirect:/dashboard/claims";
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Claim updated successfully");
            return "redirect:/dashboard/claims";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update claim: " + e.getMessage());
            return "redirect:/claims/edit/" + id;
        }
    }
    
    @GetMapping("/view/{id}")
    public String viewClaim(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Claim claim = claimService.getClaimById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        
        // Check if claim belongs to user or user is admin
        if (!claim.getPolicy().getUser().getId().equals(user.getId()) && 
            !user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            return "redirect:/auth/access-denied";
        }
        
        model.addAttribute("claim", claim);
        return "claims/details";
    }
    
    @PostMapping("/cancel/{id}")
    public String cancelClaim(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean success = claimService.cancelClaim(id, user);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Claim cancelled successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to cancel claim. Only pending claims can be cancelled.");
        }
        
        return "redirect:/dashboard/claims";
    }
}