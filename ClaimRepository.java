package com.example.insurance_management_system.repository;

import com.example.insurance_management_system.model.Claim;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    
    List<Claim> findByPolicyUser(User user);
    
    Page<Claim> findByPolicyUser(User user, Pageable pageable);
    
    @Query("SELECT c FROM Claim c WHERE c.policy.user = :user AND (:status IS NULL OR c.status = :status)")
    Page<Claim> findByPolicyUserAndStatus(@Param("user") User user, @Param("status") String status, Pageable pageable);
    
    @Query("SELECT c FROM Claim c WHERE c.policy.user = :user AND (:status IS NULL OR c.status = :status) " +
           "AND (:policyType IS NULL OR c.policy.policyType = :policyType) " +
           "AND ((:dateFrom IS NULL AND :dateTo IS NULL) OR " +
           "(c.filedDate BETWEEN :dateFrom AND :dateTo))")
    Page<Claim> findClaimsWithFilters(
            @Param("user") User user,
            @Param("status") String status,
            @Param("policyType") String policyType,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable);
            
    List<Claim> findByPolicy(Policy policy);
    
    long countByPolicyUserAndStatus(User user, String status);
}