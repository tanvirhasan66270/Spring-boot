package com.example.SCM.repository;

import com.example.SCM.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * ড্যাশবোর্ড বা সেটিংসের জন্য সম্পূর্ণ দেশ ও তার ডিভিশন চেইন একবারে লোড করার কুয়েরি
     */
    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.divisions")
    List<Country> findAllCountriesWithDivisions();

    /**
     * আইডি দিয়ে সুনির্দিষ্ট দেশ এবং তার ডিভিশন লিস্ট খোঁজা
     */
    @Query("SELECT c FROM Country c LEFT JOIN FETCH c.divisions WHERE c.id = :id")
    Optional<Country> findByIdWithDivisions(@Param("id") Long id);

    /**
     * শুধুমাত্র একটিভ দেশের লিস্ট (যেমন: ফ্রন্টএন্ড ড্রপডাউনগুলোর জন্য)
     */
    @Query("SELECT DISTINCT c FROM Country c LEFT JOIN FETCH c.divisions WHERE c.active = true")
    List<Country> findAllActiveCountriesWithDivisions();
}