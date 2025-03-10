package com.example.insurance_management_system.controller;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
    private AdminService adminService;
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = adminService.getAllUsers();
        List<Policy> policies = adminService.getAllPolicies();
        
        model.addAttribute("users", users);
        model.addAttribute("policies", policies);
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/manage-users";
    }
    
    @PostMapping("/users/{id}/disable")
    public String disableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.disableUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User disabled successfully");
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/enable")
    public String enableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.enableUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User enabled successfully");
        return "redirect:/admin/users";
    }
    
    @GetMapping("/policies")
    public String managePolicies(Model model) {
        List<Policy> policies = adminService.getAllPolicies();
        model.addAttribute("policies", policies);
        return "admin/manage-policies";
    }
    
    @GetMapping("/policies/pending")
    public String pendingPolicies(Model model) {
        List<Policy> pendingPolicies = adminService.getPendingPolicies();
        model.addAttribute("policies", pendingPolicies);
        return "admin/pending-policies";
    }
    
    @PostMapping("/policies/{id}/cancel")
    public String cancelPolicy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean success = adminService.cancelPolicyByAdmin(id);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Policy cancelled successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel policy");
        }
        
        return "redirect:/admin/policies";
    }
    
    @PostMapping("/policies/{id}/approve")
    public String approvePolicy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean success = adminService.approvePolicyByAdmin(id);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Policy approved successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve policy");
        }
        
        return "redirect:/admin/policies/pending";
    }
    
    @PostMapping("/policies/{id}/reject")
    public String rejectPolicy(
            @PathVariable Long id, 
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        boolean success = adminService.rejectPolicyByAdmin(id, reason);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Policy rejected successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reject policy");
        }
        
        return "redirect:/admin/policies/pending";
    }
}
