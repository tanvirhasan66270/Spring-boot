package com.example.SCM.repository;

import com.example.SCM.entity.SalesOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalesOfficerRepository extends JpaRepository<SalesOfficer, Long> {

    @Query("SELECT DISTINCT s FROM SalesOfficer s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.policeStation")
    List<SalesOfficer> findAllWithDetails();

    boolean existsByNidNumber(String nidNumber);
}