package com.example.insurance_management_system.service;

import com.example.insurance_management_system.model.Claim;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.repository.ClaimRepository;
import com.example.insurance_management_system.repository.PolicyRepository;
import com.example.insurance_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
	 @Autowired
	    private UserRepository userRepository;
	 
	 @Autowired
	 private ClaimRepository claimRepository;
	    
	    @Autowired
	    private PolicyRepository policyRepository;
	    
	    @Transactional(readOnly = true)
	    public List<User> getAllUsers() {
	        return userRepository.findAll();
	    }
	    @Transactional(readOnly = true)
	    public List<Claim> getAllClaims() {
	        return claimRepository.findAll();
	    }
	    @Transactional
	    public boolean approveClaimByAdmin(Long claimId) {
	        Optional<Claim> claimOpt = claimRepository.findById(claimId);
	        
	        if (claimOpt.isPresent()) {
	            Claim claim = claimOpt.get();
	            
	            // Only approve if it's in PENDING status
	            if ("PENDING".equals(claim.getStatus())) {
	                claim.setStatus("APPROVED");
	                claim.setProcessedAt(LocalDateTime.now());
	                claimRepository.save(claim);
	                return true;
	            }
	        }
	        
	        return false;
	    }

	    @Transactional
	    public boolean processClaimByAdmin(Long claimId) {
	        Optional<Claim> claimOpt = claimRepository.findById(claimId);
	        
	        if (claimOpt.isPresent()) {
	            Claim claim = claimOpt.get();
	            
	            // Only mark as processing if it's in PENDING status
	            if ("PENDING".equals(claim.getStatus())) {
	                claim.setStatus("PROCESSING");
	                claimRepository.save(claim);
	                return true;
	            }
	        }
	        
	        return false;
	    }

	    @Transactional
	    public boolean rejectClaimByAdmin(Long claimId, String reason) {
	        Optional<Claim> claimOpt = claimRepository.findById(claimId);
	        
	        if (claimOpt.isPresent()) {
	            Claim claim = claimOpt.get();
	            
	            // Only reject if it's in PENDING status
	            if (claim.isProcessing() || claim.isPending()) {
	                claim.setStatus("REJECTED");
	                claim.setRejectionReason(reason);
	                claim.setProcessedAt(LocalDateTime.now());
	                claimRepository.save(claim);
	                return true;
	            }
	        }
	        
	        return false;
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
	    
	    @Transactional(readOnly = true)
	    public List<Policy> getPendingPolicies() {
	        return policyRepository.findByApprovalStatus("PENDING");
	    }
	    
	    @Transactional
	    public boolean cancelPolicyByAdmin(Long policyId) {
	        Optional<Policy> policyOpt = policyRepository.findById(policyId);
	        
	        if (policyOpt.isPresent()) {
	            Policy policy = policyOpt.get();
	            policy.setStatus("CANCELLED");
	            policy.setCancelledAt(LocalDateTime.now());
	            policyRepository.save(policy);
	            return true;
	        }
	        
	        return false;
	    }
	    
	    @Transactional
	    public boolean approvePolicyByAdmin(Long policyId) {
	        Optional<Policy> policyOpt = policyRepository.findById(policyId);
	        
	        if (policyOpt.isPresent()) {
	            Policy policy = policyOpt.get();
	            
	            // Only approve if it's in PENDING status
	            if ("PENDING".equals(policy.getApprovalStatus())) {
	                policy.setApprovalStatus("APPROVED");
	                policy.setApprovedAt(LocalDateTime.now());
	                policyRepository.save(policy);
	                return true;
	            }
	        }
	        
	        return false;
	    }
	    
	    @Transactional
	    public boolean rejectPolicyByAdmin(Long policyId, String reason) {
	        Optional<Policy> policyOpt = policyRepository.findById(policyId);
	        
	        if (policyOpt.isPresent()) {
	            Policy policy = policyOpt.get();
	            
	            // Only reject if it's in PENDING status
	            if ("PENDING".equals(policy.getApprovalStatus())) {
	                policy.setApprovalStatus("REJECTED");
	                policy.setRejectionReason(reason);
	                policy.setRejectedAt(LocalDateTime.now());
	                policyRepository.save(policy);
	                return true;
	            }
	        }
	        
	        return false;
	    }
}
