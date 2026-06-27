package com.example.SCM.repository;

import com.example.SCM.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category , Long> {

    //  ক্যাটাগরির নাম দিয়ে হুবহু সার্চ করা (ইউনিক চেক বা ভ্যালিডেশনের জন্য)
    Optional<Category> findByCategoryName(String categoryName);



}
