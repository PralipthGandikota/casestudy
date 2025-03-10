package com.example.insurance_management_system.service;

import com.example.insurance_management_system.service.PolicyService;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.repository.PolicyRepository;
import com.example.insurance_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
	 @Autowired
	    private UserRepository userRepository;
	    
	    @Autowired
	    private PolicyRepository policyRepository;
	    
	    @Transactional(readOnly = true)
	    public List<User> getAllUsers() {
	        return userRepository.findAll();
	    }
	    
	    @Transactional(readOnly = true)
	    public Optional<User> getUserById(Long id) {
	        return userRepository.findById(id);
	    }
	    
	    @Transactional
	    public void disableUser(Long userId) {
	        Optional<User> userOpt = userRepository.findById(userId);
	        
	        if (userOpt.isPresent()) {
	            User user = userOpt.get();
	            user.setEnabled(false);
	            userRepository.save(user);
	        }
	    }
	    
	    @Transactional
	    public void enableUser(Long userId) {
	        Optional<User> userOpt = userRepository.findById(userId);
	        
	        if (userOpt.isPresent()) {
	            User user = userOpt.get();
	            user.setEnabled(true);
	            userRepository.save(user);
	        }
	    }
	    
	    @Transactional(readOnly = true)
	    public List<Policy> getAllPolicies() {
	        return policyRepository.findAll();
	    }
	    
	    @Transactional
	    public boolean cancelPolicyByAdmin(Long policyId) {
	        Optional<Policy> policyOpt = policyRepository.findById(policyId);
	        
	        if (policyOpt.isPresent()) {
	            Policy policy = policyOpt.get();
	            policy.cancel();
	            policyRepository.save(policy);
	            return true;
	        }
	        
	        return false;
	    }
	    
	 // In AdminService.java - Add methods for policy approval
	    @Transactional
	    public Policy approvePolicy(Long policyId) {
	        return policyService.approvePolicy(policyId);
	    }

	    @Transactional
	    public Policy rejectPolicy(Long policyId, String reason) {
	        return policyService.rejectPolicy(policyId, reason);
	    }

	    @Transactional
	    public Policy holdPolicy(Long policyId, String reason) {
	        return policyService.putPolicyOnHold(policyId, reason);
	    }

	    @Transactional(readOnly = true)
	    public List<Policy> getPendingPolicies() {
	        return policyService.getPendingPolicies();
	    }	
}
