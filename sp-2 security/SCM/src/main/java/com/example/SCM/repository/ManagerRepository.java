package com.example.SCM.repository;

import com.example.SCM.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    @Query("SELECT DISTINCT m FROM Manager m LEFT JOIN FETCH m.user LEFT JOIN FETCH m.policeStation")
    List<Manager> findAllWithDetails();
}