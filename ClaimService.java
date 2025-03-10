package com.example.insurance_management_system.service;

import com.example.insurance_management_system.model.Claim;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import com.example.insurance_management_system.repository.ClaimRepository;
import com.example.insurance_management_system.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClaimService {
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    private final String UPLOAD_DIR = "./uploads/claims/";
    
    public Page<Claim> getClaimsByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return claimRepository.findByPolicyUser(user, pageable);
    }
    
    public Page<Claim> getClaimsWithFilters(User user, String status, String policyType,
                                          LocalDate dateFrom, LocalDate dateTo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return claimRepository.findClaimsWithFilters(user, status, policyType, dateFrom, dateTo, pageable);
    }
    
    public List<Claim> getAllClaimsByUser(User user) {
        return claimRepository.findByPolicyUser(user);
    }
    
    public Optional<Claim> getClaimById(Long id) {
        return claimRepository.findById(id);
    }
    
    @Transactional
    public Claim createClaim(Claim claim, Long policyId, User user) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        // Check if policy belongs to user
        if (!policy.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Policy does not belong to the current user");
        }
        
        // Check if policy is active
        if (!policy.isActive()) {
            throw new RuntimeException("Cannot file claim for inactive policy");
        }
        
        // Generate unique claim number
        String claimNumber = "CL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        claim.setClaimNumber(claimNumber);
        claim.setPolicy(policy);
        claim.setStatus("PENDING");
        
        return claimRepository.save(claim);
    }
    
    @Transactional
    public Claim updateClaim(Long id, Claim updatedClaim, User user) {
        Claim existingClaim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        
        // Check if claim belongs to user
        if (!existingClaim.getPolicy().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Claim does not belong to the current user");
        }
        
        // Check if claim is in a state that can be updated
        if (!"PENDING".equals(existingClaim.getStatus())) {
            throw new RuntimeException("Only pending claims can be updated");
        }
        
        // Update fields
        existingClaim.setClaimType(updatedClaim.getClaimType());
        existingClaim.setClaimAmount(updatedClaim.getClaimAmount());
        existingClaim.setIncidentDate(updatedClaim.getIncidentDate());
        existingClaim.setDescription(updatedClaim.getDescription());
        
        return claimRepository.save(existingClaim);
    }
    
    @Transactional
    public boolean cancelClaim(Long id, User user) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        
        // Check if claim belongs to user
        if (!claim.getPolicy().getUser().getId().equals(user.getId())) {
            return false;
        }
        
        // Check if claim is in a state that can be cancelled
        if (!"PENDING".equals(claim.getStatus())) {
            return false;
        }
        
        // Remove the claim
        claimRepository.delete(claim);
        return true;
    }
    
    public String uploadClaimDocuments(MultipartFile file, Long claimId) throws IOException {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        
        // Create directory if it doesn't exist
        File directory = new File(UPLOAD_DIR + claimId);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Generate file name and save
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + claimId + "/" + fileName);
        Files.write(filePath, file.getBytes());
        
        // Update claim with document path
        claim.setDocumentsPath(UPLOAD_DIR + claimId + "/" + fileName);
        claimRepository.save(claim);
        
        return fileName;
    }
    
    // For admin operations
    @Transactional
    public Claim processClaimByAdmin(Long id, String action, String rejectionReason) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        
        switch (action) {
            case "APPROVE":
                claim.setStatus("APPROVED");
                break;
            case "REJECT":
                claim.setStatus("REJECTED");
                claim.setRejectionReason(rejectionReason);
                break;
            case "PROCESS":
                claim.setStatus("PROCESSING");
                break;
            default:
                throw new RuntimeException("Invalid action");
        }
        
        return claimRepository.save(claim);
    }
    
    // Get claim statistics for a user
    public ClaimStatistics getClaimStatisticsForUser(User user) {
        long totalClaims = claimRepository.countByPolicyUserAndStatus(user, null);
        long pendingClaims = claimRepository.countByPolicyUserAndStatus(user, "PENDING");
        long processingClaims = claimRepository.countByPolicyUserAndStatus(user, "PROCESSING");
        long approvedClaims = claimRepository.countByPolicyUserAndStatus(user, "APPROVED");
        long rejectedClaims = claimRepository.countByPolicyUserAndStatus(user, "REJECTED");
        
        return new ClaimStatistics(totalClaims, pendingClaims, processingClaims, approvedClaims, rejectedClaims);
    }
    
    // Helper class for claim statistics
    public static class ClaimStatistics {
        private long totalClaims;
        private long pendingClaims;
        private long processingClaims;
        private long approvedClaims;
        private long rejectedClaims;
        
        public ClaimStatistics(long totalClaims, long pendingClaims, long processingClaims, 
                              long approvedClaims, long rejectedClaims) {
            this.totalClaims = totalClaims;
            this.pendingClaims = pendingClaims;
            this.processingClaims = processingClaims;
            this.approvedClaims = approvedClaims;
            this.rejectedClaims = rejectedClaims;
        }
        
        // Getters
        public long getTotalClaims() {
            return totalClaims;
        }
        
        public long getPendingClaims() {
            return pendingClaims;
        }
        
        public long getProcessingClaims() {
            return processingClaims;
        }
        
        public long getApprovedClaims() {
            return approvedClaims;
        }
        
        public long getRejectedClaims() {
            return rejectedClaims;
        }
    }
}