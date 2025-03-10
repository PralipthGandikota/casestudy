package com.example.insurance_management_system.controller;

import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.model.Claim;
import com.example.insurance_management_system.service.ClaimService;
import com.example.insurance_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/dashboard")
public class DashboardClaimsController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ClaimService claimService;
    
    @GetMapping("/claims")
    public String viewClaims(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String policyType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Authentication authentication,
            Model model) {
        
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get claims with filters if provided
        Page<Claim> claimsPage;
        if (status != null || policyType != null || dateFrom != null || dateTo != null) {
            // If dateTo is provided but dateFrom is not, set dateFrom to a reasonable past date
            if (dateFrom == null && dateTo != null) {
                dateFrom = LocalDate.now().minusYears(10);
            }
            // If dateFrom is provided but dateTo is not, set dateTo to today
            if (dateFrom != null && dateTo == null) {
                dateTo = LocalDate.now();
            }
            
            claimsPage = claimService.getClaimsWithFilters(user, status, policyType, dateFrom, dateTo, page, size);
        } else {
            claimsPage = claimService.getClaimsByUser(user, page, size);
        }
        
        // Get claim statistics
        ClaimService.ClaimStatistics stats = claimService.getClaimStatisticsForUser(user);
        
        model.addAttribute("claims", claimsPage.getContent());
        model.addAttribute("currentPage", claimsPage.getNumber());
        model.addAttribute("totalPages", claimsPage.getTotalPages());
        model.addAttribute("totalClaims", stats.getTotalClaims());
        model.addAttribute("pendingClaims", stats.getPendingClaims());
        model.addAttribute("processingClaims", stats.getProcessingClaims());
        model.addAttribute("approvedClaims", stats.getApprovedClaims());
        model.addAttribute("rejectedClaims", stats.getRejectedClaims());
        
        // Add filter parameters back to the model for form persistence
        model.addAttribute("statusFilter", status);
        model.addAttribute("policyTypeFilter", policyType);
        model.addAttribute("dateFromFilter", dateFrom);
        model.addAttribute("dateToFilter", dateTo);
        
        return "dashboard/claims";
    }
}