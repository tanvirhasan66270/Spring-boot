package com.example.SCM.repository;

import com.example.SCM.entity.User;
import com.example.SCM.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ১. শুধুমাত্র CUSTOMER রোলের ইউজারদের খুঁজে বের করার মেথড
    // (এখানে প্যাকেজের নাম ঠিক করে দেওয়া হয়েছে)
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role = com.example.SCM.role.Role.CUSTOMER")
    Optional<User> findCustomerById(@Param("id") Long id);

    // ২. একাধিক রোল দিয়ে ইউজার খোঁজার মেথড
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findUsersByRoles(@Param("roles") List<Role> roles);

    // ৩. ইমেইল দিয়ে ইউজার খোঁজার মেথড
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);


    Role Role(Role role);
}