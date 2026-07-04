package com.example.SCM.repository;

import com.example.SCM.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Load all customers with User + Location hierarchy
     */
    @Query("""
            SELECT DISTINCT c
            FROM Customer c
            LEFT JOIN FETCH c.user u
            LEFT JOIN FETCH c.policeStation ps
            LEFT JOIN FETCH ps.district d
            LEFT JOIN FETCH d.division dv
            LEFT JOIN FETCH dv.country
            ORDER BY c.id DESC
            """)
    List<Customer> findAllCustomersWithDetails();

    /**
     * Load one customer with complete hierarchy
     */
    @Query("""
            SELECT c
            FROM Customer c
            LEFT JOIN FETCH c.user u
            LEFT JOIN FETCH c.policeStation ps
            LEFT JOIN FETCH ps.district d
            LEFT JOIN FETCH d.division dv
            LEFT JOIN FETCH dv.country
            WHERE c.id = :id
            """)
    Optional<Customer> findByIdWithDetails(Long id);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByNidNumber(String nidNumber);

}