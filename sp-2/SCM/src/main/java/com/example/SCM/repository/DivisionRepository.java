package com.example.SCM.repository;

import com.example.SCM.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {

    /**
     * 💡 সার্ভিস লেয়ারের `divisionRepository.findAllDivisionsWithDetails()` এর জন্য।
     * N+1 কুয়েরি প্রবলেম এড়াতে কান্ট্রি এবং চাইল্ড ডিস্ট্রিক্ট একসাথে Fetch করা হচ্ছে।
     */
    @Query("SELECT DISTINCT d FROM Division d LEFT JOIN FETCH d.country LEFT JOIN FETCH d.districts")
    List<Division> findAllDivisionsWithDetails();

    /**
     * 💡 সার্ভিস লেয়ারের `divisionRepository.findAllActiveDivisions()` এর জন্য।
     * শুধুমাত্র একটিভ (active = true) বিভাগগুলো ডিটেইলস সহ তুলে আনবে।
     */
    @Query("SELECT DISTINCT d FROM Division d LEFT JOIN FETCH d.country LEFT JOIN FETCH d.districts WHERE d.active = true")
    List<Division> findAllActiveDivisions();

    /**
     * 💡 সার্ভিস লেয়ারের `divisionRepository.findByIdWithDetails(id)` এর জন্য।
     */
    @Query("SELECT d FROM Division d LEFT JOIN FETCH d.country LEFT JOIN FETCH d.districts WHERE d.id = :id")
    Optional<Division> findByIdWithDetails(@Param("id") Long id);

    /**
     * 💡 সার্ভিস লেয়ারের `divisionRepository.findByCountryId(countryId)` এর জন্য।
     * নির্দিষ্ট কোনো দেশের অধীনস্থ সকল বিভাগ ডিস্ট্রিক্টসহ লোড করার জন্য।
     */
    @Query("SELECT DISTINCT d FROM Division d LEFT JOIN FETCH d.country LEFT JOIN FETCH d.districts WHERE d.country.id = :countryId")
    List<Division> findByCountryId(@Param("countryId") Long countryId);

    /**
     * নাম অনুযায়ী বিভাগ খোঁজার জন্য (ইউনিক নাম ভ্যালিডেশনে কাজে লাগবে)
     */
    Optional<Division> findByName(String name);

    /**
     * আপডেট অপারেশনের সময় ডুপ্লিকেট নাম এড়ানোর ভ্যালিডেশন চেক
     */
    boolean existsByNameAndIdNot(String name, Long id);
}