package com.example.SCM.repository;

import com.example.SCM.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * 1. অপ্টিমাইজড অল-ভেহিক্যাল লিস্ট কুয়েরি (Fetch Join)
     * 💡 এটি সিঙ্গেল ডাটাবেজ হিটে ড্রাইভার অ্যাসোসিয়েশনসহ সম্পূর্ণ অবজেক্ট লোড করে N+1 কোয়েরি প্রবলেম এড়াবে।
     * আপনার সার্ভিস ক্লাসের 'findAll()' মেথডের জন্য এটি আবশ্যক।
     */
    @Query("""
        SELECT DISTINCT v FROM Vehicle v
        LEFT JOIN FETCH v.driver d
    """)
    List<Vehicle> findAllWithDriverDetails();

    /**
     * 2. আইডি দিয়ে সিঙ্গেল ভেহিক্যাল খোঁজা (Fetch Join মেকানিজম)
     * 💡 আপনার সার্ভিস ক্লাসের 'getById(id)' মেথডের সাথে হুবহু সিঙ্কড।
     */
    @Query("""
        SELECT v FROM Vehicle v
        LEFT JOIN FETCH v.driver d
        WHERE v.id = :id
    """)
    Optional<Vehicle> findByIdWithDriverDetails(@Param("id") Long id);

    /**
     * 3. ইউনিক প্লেট নাম্বার দিয়ে কুয়েরি করা (নতুন ভেহিক্যাল সেভ করার সময় ভ্যালিডেশনের জন্য)
     * আপনার সার্ভিস ক্লাসের 'save()' মেথডে এটি ব্যবহার করা হয়েছে।
     */
    Optional<Vehicle> findByPlateNumber(String plateNumber);

    /**
     * 4. আপডেট অপারেশনের সময় প্লেট নাম্বার অন্য কারো সাথে ডুপ্লিকেট হচ্ছে কি না তা চেক করার ভ্যালিডেশন
     * আপনার সার্ভিস ক্লাসের 'update()' মেথডে এটি ব্যবহার করা হয়েছে।
     */
    boolean existsByPlateNumberAndIdNot(String plateNumber, Long id);
}