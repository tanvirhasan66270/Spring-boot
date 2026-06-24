package com.example.SCM.repository;

import com.example.SCM.entity.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProcurementRepository extends JpaRepository<Procurement, Long> {

    @Query("SELECT DISTINCT p FROM Procurement p LEFT JOIN FETCH p.user LEFT JOIN FETCH p.policeStation")
    List<Procurement> findAllWithDetails();

    boolean existsByPassportNumber(String passportNumber);
    boolean existsByNidNumber(String nidNumber);
}