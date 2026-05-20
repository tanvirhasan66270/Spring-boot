package com.emranhss.SupplyChainManagement.repository;

import com.emranhss.SupplyChainManagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {



}
