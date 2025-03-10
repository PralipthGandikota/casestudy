package com.example.insurance_management_system.repository;

import com.example.insurance_management_system.model.InsuranceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface InsuranceDetailsRepository  extends JpaRepository<InsuranceDetails,Long>{

}
