package com.example.SCM.dto.Seeder;

import com.example.SCM.dto.request.ManagerRequestDTO;
import com.example.SCM.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerSeeder implements CommandLineRunner {

    private final ManagerService managerService;

    @Override
    public void run(String... args) throws Exception {
        // Check if database is empty to prevent re-seeding on every restart
        if (managerService.findAll().isEmpty()) {
            seedData();
        }
    }

    private void seedData() {
        ManagerRequestDTO manager = new ManagerRequestDTO();

        // Auth User Fields
        manager.setName("Admin Manager");
        manager.setEmail("manager@scm.com");
        manager.setPhone("01000000000");
        manager.setPassword("password123");


        // Personal Details
        manager.setAddress("Gulshan, Dhaka");
        manager.setNidNumber("9988776655");
        manager.setPassportNumber("A1234567");
        manager.setDob("1992-08-20");
        manager.setGender("Male");
        manager.setJoiningDate("2026-01-01");
        manager.setDesignation("Operations Manager");
        manager.setLanguage("BANGLA");

        managerService.save(manager,null);

        System.out.println("Seeding: Manager record created successfully.");
    }
}