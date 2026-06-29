package com.example.SCM.repository;

import com.example.SCM.entity.QCChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QCChecklistRepository extends JpaRepository<QCChecklist, Long> {

    /**
     * একটি নির্দিষ্ট QCInspection-এর আন্ডারে থাকা সমস্ত চেকলিস্ট আইটেম খোঁজা (N+1 কোয়েরি অপ্টিমাইজড)
     */
    @Query("""
        SELECT c FROM QCChecklist c 
        LEFT JOIN FETCH c.qcInspection q 
        WHERE c.qcInspection.id = :inspectionId
    """)
    List<QCChecklist> findByInspectionId(@Param("inspectionId") Long inspectionId);
}