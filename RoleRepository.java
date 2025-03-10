package com.example.insurance_management_system.repository;

import com.example.insurance_management_system.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
