package com.example.SCM.repository;

import com.example.SCM.entity.QCInspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QCInspectorRepository extends JpaRepository<QCInspector, Long> {


    Optional<QCInspector> findByUserId(Long userId);

    /**
     * 1. অপ্টিমাইজড অল-ইনস্পেক্টর লিস্ট কুয়েরি (Fetch Join)
     *  এটি সিঙ্গেল ডেটাবেজ হিটে সম্পূর্ণ রিলেশনাল অবজেক্ট চেইন (User এবং Location) লোড করে পারফরম্যান্স বাড়াবে।
     */
    @Query("""
        SELECT DISTINCT q FROM QCInspector q
        LEFT JOIN FETCH q.user
        LEFT JOIN FETCH q.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division
    """)
    List<QCInspector> findAllInspectors();

    /**
     * 2. আইডি দিয়ে সিঙ্গেল ইনস্পেক্টর খোঁজা (Fetch Join মেকানিজম)
     * আপনার 'RiderRepository'-এর 'findByIdWithZones' প্যাটার্ন অনুযায়ী হুবহু সিঙ্কড।
     */
    @Query("""
        SELECT q FROM QCInspector q
        LEFT JOIN FETCH q.user
        LEFT JOIN FETCH q.policeStation ps
        LEFT JOIN FETCH ps.district d
        LEFT JOIN FETCH d.division
        WHERE q.id = :id
    """)
    Optional<QCInspector> findByIdWithDetails(@Param("id") Long id);


}