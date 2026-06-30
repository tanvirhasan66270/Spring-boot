package com.example.SCM.dto.mapper;

import com.example.SCM.dto.request.QCInspectorRequestDTO;
import com.example.SCM.dto.response.QCInspectorResponseDTO;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.QCInspector;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.GenderStatus;
import com.example.SCM.enumClass.LanguageStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class QCInspectorMapper {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

     public QCInspectorResponseDTO convertTOResponseDTO(QCInspector inspector) {


        QCInspectorResponseDTO dto = new QCInspectorResponseDTO();

        // QCInspector প্রোফাইল ফিল্ডস ম্যাপিং
        dto.setId(inspector.getId());
        dto.setContactPerson(inspector.getContactPerson());
        dto.setAddress(inspector.getAddress());
        dto.setNidNumber(inspector.getNidNumber());
        dto.setPassportNumber(inspector.getPassportNumber());
        dto.setGender(inspector.getGender());
        dto.setDob(inspector.getDob());
        dto.setImage(inspector.getImage());
        dto.setActive(inspector.isActive()); // প্রোফাইলের নিজস্ব একটিভ স্টেট
        dto.setJoiningDate(inspector.getJoiningDate());
        dto.setDesignation(inspector.getDesignation());
        dto.setLanguage(inspector.getLanguage());
        dto.setCreatedAt(inspector.getCreatedAt());
        dto.setUpdatedAt(inspector.getUpdatedAt());

        // Auth Account (User) থেকে ডেটা ম্যাপিং (Source of Truth)
        User user = inspector.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhoneNumber());
            dto.setRole(user.getRole());
            dto.setUserActive(user.isActive());
        }

        // Location Hierarchy (PoliceStation -> District -> Division) ম্যাপিং
        PoliceStation policeStation = inspector.getPoliceStation();
        if (policeStation != null) {
            dto.setPoliceStationId(policeStation.getId());
            dto.setPoliceStationName(policeStation.getName());

            if (policeStation.getDistrict() != null) {
                dto.setDistrictName(policeStation.getDistrict().getName());

                if (policeStation.getDistrict().getDivision() != null) {
                    dto.setDivisionName(policeStation.getDistrict().getDivision().getName());
                }
            }
        }

        return dto;
    }




    public QCInspector toQCInspectorEntity(QCInspectorRequestDTO dto, User user, PoliceStation policeStation) {


        QCInspector inspector = new QCInspector();

        // DTO থেকে প্রোফাইল কোর ফিল্ডস সেট করা
        inspector.setContactPerson(dto.getContactPerson());
        inspector.setAddress(dto.getAddress());
        inspector.setNidNumber(dto.getNidNumber());
        inspector.setPassportNumber(dto.getPassportNumber());
        inspector.setImage(dto.getImage());
        inspector.setDesignation(dto.getDesignation());

        // String -> LocalDate ডেট হ্যান্ডেলিং (নাল ও ব্ল্যাঙ্ক সেফটিসহ)
        if (dto.getDob() != null && !dto.getDob().trim().isEmpty()) {
            inspector.setDob(LocalDate.parse(dto.getDob(), dateFormatter));
        }
        if (dto.getJoiningDate() != null && !dto.getJoiningDate().trim().isEmpty()) {
            inspector.setJoiningDate(LocalDate.parse(dto.getJoiningDate(), dateFormatter));
        }

        // String -> Enum কাস্টিং
        if (dto.getGender() != null && !dto.getGender().trim().isEmpty()) {
            inspector.setGender(GenderStatus.valueOf(dto.getGender().toUpperCase()));
        }
        if (dto.getLanguage() != null && !dto.getLanguage().trim().isEmpty()) {
            inspector.setLanguage(LanguageStatus.valueOf(dto.getLanguage().toUpperCase()));
        }

        // ফরেন কি/রিলেশন অবজেক্ট ইনজেক্ট করা
        inspector.setUser(user);
        inspector.setPoliceStation(policeStation);

        return inspector;
    }


    public void updateEntity(QCInspectorRequestDTO dto, QCInspector inspector, PoliceStation policeStation) {

        // ১. সোর্স অফ ট্রুথ (User Entity) আপডেট
        User user = inspector.getUser();
        if (user != null) {
            if (dto.getName() != null) user.setName(dto.getName());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            if (dto.getPhone() != null) user.setPhoneNumber(dto.getPhone());
            user.setActive(dto.isUserActive()); // 💡 আপডেটের সময়ও ইউজারের একটিভ স্টেট সিঙ্ক হবে
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                user.setPassword(dto.getPassword());
            }
        }

        // ২. প্রোফাইল ফিল্ডস আপডেট
        inspector.setContactPerson(dto.getContactPerson());
        inspector.setAddress(dto.getAddress());
        inspector.setNidNumber(dto.getNidNumber());
        inspector.setPassportNumber(dto.getPassportNumber());
        inspector.setImage(dto.getImage());
        inspector.setDesignation(dto.getDesignation());

        if (dto.getDob() != null && !dto.getDob().trim().isEmpty()) {
            inspector.setDob(LocalDate.parse(dto.getDob(), dateFormatter));
        }
        if (dto.getJoiningDate() != null && !dto.getJoiningDate().trim().isEmpty()) {
            inspector.setJoiningDate(LocalDate.parse(dto.getJoiningDate(), dateFormatter));
        }

        if (dto.getGender() != null && !dto.getGender().trim().isEmpty()) {
            inspector.setGender(GenderStatus.valueOf(dto.getGender().toUpperCase()));
        }
        if (dto.getLanguage() != null && !dto.getLanguage().trim().isEmpty()) {
            inspector.setLanguage(LanguageStatus.valueOf(dto.getLanguage().toUpperCase()));
        }

        if (policeStation != null) {
            inspector.setPoliceStation(policeStation);
        }
    }
}