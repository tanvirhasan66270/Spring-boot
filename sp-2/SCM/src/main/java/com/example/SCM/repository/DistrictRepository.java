package com.example.SCM.repository;

import com.example.SCM.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

    List<District> findByDivisionId(Long divisionId);

    List<District> findByDivisionName(String divisionName);



}
