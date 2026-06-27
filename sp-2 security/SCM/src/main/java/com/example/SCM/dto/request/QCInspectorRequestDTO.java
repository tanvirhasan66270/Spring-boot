package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class QCInspectorRequestDTO {
    //  User (Auth) Fields ---
    private String name;
    private String email;
    private String phone;
    private String password;
    private boolean userActive;

    // QC Inspector Profile Fields ---
    private String contactPerson;
    private String address;
    private String nidNumber;
    private String passportNumber;
    private String dob;            // ফ্রন্টঅ্যান্ড থেকে "YYYY-MM-DD" স্ট্রিং আসবে
    private String gender;         // MALE, FEMALE, OTHERS
    private String image;          // ইমেজ পাথ বা ইউআরএল
    private boolean isActive;      // এটি প্রোফাইলের নিজস্ব একটিভ স্টেট
    private String joiningDate;    // "YYYY-MM-DD"
    private String designation;
    private String language;       // BANGLA, ENGLISH, OTHERS
    private Long policeStationId;  // থানা ফরেন কি আইডি
}