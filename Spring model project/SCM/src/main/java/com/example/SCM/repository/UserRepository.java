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
    // (এটি ব্যবহার করলে ভুল করে কোনো ADMIN বা salesOfficer কাস্টমার হিসেবে ঢুকতে পারবে না)
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.role = 'CUSTOMER'")
    Optional<User> findCustomerById(@Param("id") Long id);



    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findUsersByRoles(@Param("roles") List<Role> roles);
}