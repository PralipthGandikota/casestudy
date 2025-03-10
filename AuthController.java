package com.example.insurance_management_system.controller;
import com.example.insurance_management_system.model.Role;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              @RequestParam(defaultValue = "USER") String roleName,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameError", "Username is already taken");
            return "auth/register";
        }
        
        // Check if email already exists
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email is already in use");
            return "auth/register";
        }
        
        try {
            // Register new user with the selected role
            userService.registerNewUser(user, roleName);
            return "redirect:/auth/login?registered";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred during registration: " + e.getMessage());
            return "auth/register";
        }
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/auth/login";
    }
}