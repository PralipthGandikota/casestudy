package com.example.insurance_management_system.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import com.example.insurance_management_system.model.Policy;
import com.example.insurance_management_system.model.User;

@Repository
public interface PolicyRepository extends JpaRepository <Policy, Long> {
	List<Policy> findByUser(User user);
    List<Policy> findByUserAndStatus(User user, String status);
    Optional<Policy> findByPolicyNumber(String policyNumber);
}
