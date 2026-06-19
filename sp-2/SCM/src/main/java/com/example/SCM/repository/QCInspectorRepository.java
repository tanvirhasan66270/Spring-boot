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

    /**
     * 1. অপ্টিমাইজড অল-ইনস্পেক্টর লিস্ট কুয়েরি (Fetch Join)
     * 💡 এটি সিঙ্গেল ডেটাবেজ হিটে সম্পূর্ণ রিলেশনাল অবজেক্ট চেইন (User এবং Location) লোড করে পারফরম্যান্স বাড়াবে।
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

    /**
     * 3. নির্দিষ্ট পুলিশ স্টেশন (থানা) আইডি অনুযায়ী কোয়ালিটি কন্ট্রোল ইনস্পেক্টর ফিল্টার
     */
    List<QCInspector> findByPoliceStationId(Long policeStationId);

    /**
     * 4. নির্দিষ্ট পুলিশ স্টেশনে কর্মরত শুধুমাত্র একটিভ (Active) ইনস্পেক্টরদের তালিকা
     */
    List<QCInspector> findByPoliceStationIdAndIsActiveTrue(Long policeStationId);

    /**
     * 5. মাল্টিপল পুলিশ স্টেশন আইডির ওপর ভিত্তি করে ইনস্পেক্টরদের বাল্ক ফিল্টারিং (যেমন: একটি ডিস্ট্রিক্টের সব থানার জন্য)
     */
    List<QCInspector> findByPoliceStationIdIn(List<Long> policeStationIds);

    /**
     * 6. মাল্টিপল পুলিশ স্টেশন আইডির আন্ডারে থাকা শুধুমাত্র একটিভ ইনস্পেক্টর ফিল্টারিং
     */
    List<QCInspector> findByPoliceStationIdInAndIsActiveTrue(List<Long> policeStationIds);

    /**
     * 7. সোর্স অফ ট্রুথ (User Entity) এর ইমেইল এড্রেস দিয়ে সরাসরি ইনস্পেক্টর প্রোফাইল কুয়েরি
     * 💡 এটি লগইন বা প্রোফাইল ড্যাশবোর্ড ট্র্যাকিংয়ে দারুণ কাজে দেবে।
     */
    Optional<QCInspector> findByUserEmail(String email);
}