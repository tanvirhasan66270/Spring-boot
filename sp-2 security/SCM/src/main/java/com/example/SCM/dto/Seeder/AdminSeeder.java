package com.example.SCM.dto.Seeder;

import com.example.SCM.dto.request.AdminRequest;
import com.example.SCM.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final AdminService adminService;

    @Override
    public void run(String... args) throws Exception {
        // ডাটাবেজ ফাঁকা আছে কিনা চেক করা যাতে রিস্টার্ট করলে বারবার সিড না হয়
        if (adminService.getAll().isEmpty()) {
            seedData();
        }
    }

    private void seedData() {
        AdminRequest admin = new AdminRequest();

        // Admin Request Fields
        admin.setName("System Admin");
        admin.setEmail("admin@scm.com");
        admin.setPhone("01999999999");
        admin.setPassword("123456");

        // সার্ভিস অনুযায়ী সঠিক মেথড ও ১টি আর্গুমেন্ট পাস করা হলো
        adminService.create(admin);

        System.out.println("Seeding: Admin record created successfully.");
    }
}