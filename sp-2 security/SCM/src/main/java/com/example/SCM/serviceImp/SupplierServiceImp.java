package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.auth.AuthService;
import com.example.SCM.dto.mapper.SupplierMapper;
import com.example.SCM.dto.response.SupplierResponseDTO;
import com.example.SCM.dto.request.SupplierRequestDTO;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.Supplier;
import com.example.SCM.entity.User;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.SupplierRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.role.Role;
import com.example.SCM.service.SupplierService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImp implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final SupplierMapper supplierMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public SupplierServiceImp(SupplierRepository supplierRepository, UserRepository userRepository,
                              PoliceStationRepository policeStationRepository, SupplierMapper supplierMapper,
                              MailService mailService, PasswordEncoder passwordEncoder, @Lazy AuthService authService) {
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
        this.policeStationRepository = policeStationRepository;
        this.supplierMapper = supplierMapper;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public SupplierResponseDTO save(SupplierRequestDTO dto, MultipartFile file) {

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Password cannot be empty!");
        }

        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.SUPPLIER);
        user.setActive(false);
        user.setPoliceStation(policeStation);
        User savedUser = userRepository.save(user);

        if (file != null && !file.isEmpty()) {
            String imagePath = uploadImage(file, dto.getName());
            dto.setImage("uploads/supplier/" + imagePath);
        }

        Supplier supplier = supplierMapper.toSupplierEntity(dto, savedUser, policeStation);
        if (file != null && !file.isEmpty()) {
            supplier.setImage(dto.getImage());
        }
        Supplier savedSupplier = supplierRepository.save(supplier);

        authService.sendVerificationEmail(savedUser.getEmail());

        return supplierMapper.toResponseDTO(savedSupplier);
    }

    @Transactional
    @Override
    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto, MultipartFile image) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        PoliceStation policeStation = supplier.getPoliceStation();
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not found"));
            supplier.setPoliceStation(policeStation);
        }

        User user = supplier.getUser();
        if (user != null) {
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhone());
            user.setPoliceStation(policeStation);
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            userRepository.save(user);
        }

        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setNidNumber(dto.getNidNumber());
        supplier.setPassportNumber(dto.getPassportNumber());
        supplier.setGender(dto.getGender());

        if (dto.getDob() != null && !dto.getDob().trim().isEmpty()) {
            try {
                supplier.setDob(String.valueOf(java.sql.Date.valueOf(dto.getDob())));
            } catch (IllegalArgumentException e) {
                try {
                    supplier.setDob(String.valueOf(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dto.getDob())));
                } catch (Exception ex) {
                    // Ignore parsing failure
                }
            }
        }

        supplier.setRating(dto.getRating());
        supplier.setAverageLeadTimeDays(dto.getAverageLeadTimeDays());

        if (image != null && !image.isEmpty()) {
            String newImagePath = uploadImage(image, dto.getName());
            supplier.setImage("uploads/supplier/" + newImagePath);
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDTO(updatedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> findAll() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierResponseDTO> getById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        supplierRepository.delete(supplier);
        if (supplier.getUser() != null) {
            userRepository.delete(supplier.getUser());
        }
    }

    private String uploadImage(MultipartFile file, String supplierName) {
        try {
            Path path = Paths.get(uploadDir, "supplier");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String cleanedName = "supplier";
            if (supplierName != null) {
                cleanedName = supplierName.trim()
                        .replaceAll("[^a-zA-Z0-9\\s]", "")
                        .replaceAll("\\s+", "_");
            }
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Supplier profile image upload failed: " + e.getMessage());
        }
    }


    public void sendSupplierWelcomeEmail(Supplier supplier) {
        if (supplier == null || supplier.getUser() == null || supplier.getUser().getEmail() == null) return;

        User authUser = supplier.getUser();
        String subject = "Welcome to Our Platform – Supplier Node Activated";

        String mailText = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f4f6f9; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 30px auto; padding: 0; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .header { background-color: #388e3c; color: white; padding: 35px 25px; text-align: center; }
        .header h1 { margin: 0; font-size: 28px; font-weight: 600; }
        .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 15px; }
        .content { padding: 30px; }
        .welcome-box { background-color: #e8f5e9; border-left: 5px solid #388e3c; padding: 18px; margin: 20px 0; border-radius: 4px; }
        .profile-details { width: 100%; border-collapse: collapse; margin: 20px 0; }
        .profile-details td { padding: 10px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
        .profile-details td.label { font-weight: bold; color: #64748b; width: 30%; }
        .btn-container { text-align: center; margin: 35px 0; }
        .btn { background-color: #388e3c; color: white !important; padding: 12px 35px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .footer { font-size: 0.85em; color: #64748b; padding: 20px; background-color: #f8fafc; text-align: center; border-top: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <div class='container'>
        <div class='header'>
            <h1>Congratulations {{supplierName}}!</h1>
            <p>Your SCM Supplier Profile is Active</p>
        </div>
        <div class='content'>
            <p>Dear <b>{{supplierName}}</b>,</p>
            <p>A warm welcome to <b>SCM Global Supply Chain Ecosystem</b>! We are excited to collaborate with you as an authorized enterprise supply node partner.</p>
            
            <div class='welcome-box'>
                <p style='margin: 0; font-size: 15px; color: #2e7d32; font-weight: bold;'>Your supply tier integration is live.</p>
                <p style='margin: 5px 0 0 0; font-size: 13px; color: #475569;'>You can now log into your merchant center to process material procurement requests, update inventory lead times, and dispatch shipping invoices.</p>
            </div>

            <p><b>Your Registered SCM Network Credentials:</b></p>
            <table class='profile-details'>
                <tr>
                    <td class='label'>Vendor Name:</td>
                    <td>{{supplierName}}</td>
                </tr>
                <tr>
                    <td class='label'>Primary Email/User:</td>
                    <td>{{userEmail}}</td>
                </tr>
                <tr>
                    <td class='label'>Contact Phone:</td>
                    <td>{{supplierPhone}}</td>
                </tr>
                <tr>
                    <td class='label'>Registered Node Role:</td>
                    <td><span style='background-color:#E2E8F0; padding:3px 8px; border-radius:4px; font-size:12px; font-weight:bold;'>{{userRole}}</span></td>
                </tr>
            </table>

            <div class='btn-container'>
                <a href='http://localhost:4200/login' class='btn'>Launch Merchant Console</a>
            </div>

            <p>If you need catalog integration help or have pipeline connectivity questions, our commercial desk is at your disposal.</p>
            <p>Best regards,<br><b>SCM Global Sourcing Operations</b></p>
        </div>
        <div class='footer'>
            &copy; {{currentYear}} SCM Global Logistics Network Cluster. All rights reserved.
        </div>
    </div>
</body>
</html>
""";

        mailText = mailText
                .replace("{{supplierName}}", authUser.getName() != null ? authUser.getName() : "")
                .replace("{{userEmail}}", authUser.getEmail())
                .replace("{{supplierPhone}}", authUser.getPhoneNumber() != null ? authUser.getPhoneNumber() : "")
                .replace("{{userRole}}", authUser.getRole() != null ? authUser.getRole().toString() : "SUPPLIER")
                .replace("{{currentYear}}", String.valueOf(java.time.Year.now().getValue()));

        try {
            mailService.senderGeneralMail(authUser.getEmail(), subject, mailText);
            System.out.println("Supplier Merchant Welcome Email successfully dispatched to node: " + authUser.getEmail());
        } catch (Exception e) {
            System.err.println("Supplier Activation Engine exception caught: " + e.getMessage());
        }
    }
}