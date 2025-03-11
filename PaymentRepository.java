package com.example.insurance_management_system.repository;

import com.example.insurance_management_system.model.Payment;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPolicy(Policy policy);
    List<Payment> findByPolicyAndSuccessful(Policy policy, boolean successful);
    List<Payment> findByPolicyUserOrderByPaymentDateDesc(User user);
    List<Payment> findByPolicyUserAndSuccessful(User user, boolean successful);
}