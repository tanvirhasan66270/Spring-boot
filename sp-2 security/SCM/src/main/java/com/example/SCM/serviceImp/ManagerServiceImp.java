package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.auth.AuthService; // 🌟 ইনপোর্ট করা হলো
import com.example.SCM.dto.mapper.ManagerMapper;
import com.example.SCM.dto.request.ManagerRequestDTO;
import com.example.SCM.dto.response.ManagerResponseDTO;
import com.example.SCM.entity.Manager;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.GenderStatus;
import com.example.SCM.enumClass.LanguageStatus;
import com.example.SCM.repository.ManagerRepository;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.role.Role;
import com.example.SCM.service.ManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy; // 🌟 ইনপোর্ট করা হলো
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManagerServiceImp implements ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final ManagerMapper managerMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService; // 🌟 যুক্ত করা হলো

    // 🌟 @Lazy ইনজেকশন ব্যবহার করা হলো যাতে সার্কুলার ডিপেন্ডেন্সি লক না হয়
    public ManagerServiceImp(ManagerRepository managerRepository, UserRepository userRepository,
                             PoliceStationRepository policeStationRepository, ManagerMapper managerMapper,
                             MailService mailService, PasswordEncoder passwordEncoder, @Lazy AuthService authService) {
        this.managerRepository = managerRepository;
        this.userRepository = userRepository;
        this.policeStationRepository = policeStationRepository;
        this.managerMapper = managerMapper;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public ManagerResponseDTO save(ManagerRequestDTO dto, MultipartFile image) {
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Credential password cannot be empty for Management creation!");
        }

        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not resolved with ID: " + dto.getPoliceStationId()));
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.MANAGER);
        user.setActive(false);
        user.setPoliceStation(policeStation);

        User savedUser = userRepository.save(user);

        if (image != null && !image.isEmpty()) {
            String imagePath = uploadImage(image, dto.getName());
            dto.setAddress("uploads/manager/" + imagePath);
        }

        Manager manager = managerMapper.toManagerEntity(dto, savedUser, policeStation);
        if (image != null && !image.isEmpty()) {
            manager.setImage(dto.getAddress());
        }

        Manager savedManager = managerRepository.save(manager);


        authService.sendVerificationEmail(savedUser.getEmail());

        return managerMapper.convertTOResponseDTO(savedManager);
    }

    @Override
    @Transactional
    public ManagerResponseDTO update(Long id, ManagerRequestDTO dto, MultipartFile file) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager profile not found at ID: " + id));

        PoliceStation policeStation = manager.getPoliceStation();
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station node mismatch"));
            manager.setPoliceStation(policeStation);
        }

        User user = manager.getUser();
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

        manager.setAddress(dto.getAddress());
        manager.setNidNumber(dto.getNidNumber());
        manager.setPassportNumber(dto.getPassportNumber());
        manager.setDesignation(dto.getDesignation());


        if (dto.getDob() != null && !dto.getDob().isBlank()) {
            manager.setDob(LocalDate.parse(dto.getDob()));
        }
        if (dto.getJoiningDate() != null && !dto.getJoiningDate().isBlank()) {
            manager.setJoiningDate(LocalDate.parse(dto.getJoiningDate()));
        }
        if (dto.getGender() != null) {
            manager.setGender(GenderStatus.valueOf(dto.getGender().toUpperCase()));
        }
        if (dto.getLanguage() != null) {
            manager.setLanguage(LanguageStatus.valueOf(dto.getLanguage().toUpperCase()));
        }

        if (file != null && !file.isEmpty()) {
            String newImagePath = uploadImage(file, dto.getName());
            manager.setImage("uploads/manager/" + newImagePath);
        }

        return managerMapper.convertTOResponseDTO(managerRepository.save(manager));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagerResponseDTO> findAll() {
        return managerRepository.findAllWithDetails().stream()
                .map(managerMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ManagerResponseDTO> getById(Long id) {
        return managerRepository.findById(id).map(managerMapper::convertTOResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target Manager key mapping missing"));
        managerRepository.delete(manager);
        if (manager.getUser() != null) {
            userRepository.delete(manager.getUser());
        }
    }

    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "manager");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String cleanedName = "manager";
            if (name != null) {
                cleanedName = name.trim()
                        .replaceAll("[^a-zA-Z0-9\\s]", "")
                        .replaceAll("\\s+", "_");
            }
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Manager system file upload fault: " + e.getMessage());
        }
    }


    public void sendManagerWelcomeEmail(Manager manager) {
        if (manager == null || manager.getUser() == null || manager.getUser().getEmail() == null) return;

        User authUser = manager.getUser();
        String subject = "Welcome to SCM Enterprise! Your Account is Ready ";

        String mailText = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f4f6f9; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 30px auto; padding: 0; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .header { background-color: #2196F3; color: white; padding: 35px 25px; text-align: center; }
        .header h1 { margin: 0; font-size: 28px; font-weight: 600; }
        .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 15px; }
        .content { padding: 30px; }
        .welcome-box { background-color: #e3f2fd; border-left: 5px solid #2196F3; padding: 18px; margin: 20px 0; border-radius: 4px; }
        .profile-details { width: 100%; border-collapse: collapse; margin: 20px 0; }
        .profile-details td { padding: 10px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
        .profile-details td.label { font-weight: bold; color: #64748b; width: 30%; }
        .btn-container { text-align: center; margin: 35px 0; }
        .btn { background-color: #2196F3; color: white !important; padding: 12px 35px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .footer { font-size: 0.85em; color: #64748b; padding: 20px; background-color: #f8fafc; text-align: center; border-top: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <div class='container'>
        <div class='header'>
            <h1>Congratulations {{managerName}}!</h1>
            <p>Your SCM Portal Account is Successfully Activated</p>
        </div>
        <div class='content'>
            <p>Dear <b>{{managerName}}</b>,</p>
            <p>A warm welcome to <b>SCM Enterprise Cluster</b>! We are absolutely thrilled to have you onboard as a core administration management partner in our digital global logistics ecosystem.</p>
            
            <div class='welcome-box'>
                <p style='margin: 0; font-size: 15px; color: #0d47a1; font-weight: bold;'>Your administrative workstation node is live.</p>
                <p style='margin: 5px 0 0 0; font-size: 13px; color: #475569;'>You can now log into your console node to supervise platform modules, authorize operational profiles, and monitor real-time network supply tracks.</p>
            </div>

            <p><b>Your Registered SCM Network Credentials:</b></p>
            <table class='profile-details'>
                <tr>
                    <td class='label'>Authorized Name:</td>
                    <td>{{managerName}}</td>
                </tr>
                <tr>
                    <td class='label'>Primary Email/User:</td>
                    <td>{{userEmail}}</td>
                </tr>
                <tr>
                    <td class='label'>Contact Phone:</td>
                    <td>{{managerPhone}}</td>
                </tr>
                <tr>
                    <td class='label'>Registered Node Role:</td>
                    <td><span style='background-color:#E2E8F0; padding:3px 8px; border-radius:4px; font-size:12px; font-weight:bold;'>{{userRole}}</span></td>
                </tr>
            </table>

            <div class='btn-container'>
                <a href='http://localhost:4200/login' class='btn'>Log Into Management Console</a>
            </div>

            <p>If you require setup assistance or technical infrastructure clearance, our central security team desk is available 24/7.</p>
            <p>Best regards,<br><b>SCM Corporate Administration Team</b></p>
        </div>
        <div class='footer'>
            &copy; {{currentYear}} SCM Global Logistics Network Cluster. All rights reserved.
        </div>
    </div>
</body>
</html>
""";

        mailText = mailText
                .replace("{{managerName}}", authUser.getName() != null ? authUser.getName() : "")
                .replace("{{userEmail}}", authUser.getEmail())
                .replace("{{managerPhone}}", authUser.getPhoneNumber() != null ? authUser.getPhoneNumber() : "")
                .replace("{{userRole}}", authUser.getRole() != null ? authUser.getRole().toString() : "MANAGER")
                .replace("{{currentYear}}", String.valueOf(java.time.Year.now().getValue()));

        try {
            mailService.senderGeneralMail(authUser.getEmail(), subject, mailText);
            System.out.println("Manager Registration Onboarding Email successfully dispatched to node: " + authUser.getEmail());
        } catch (Exception e) {
            System.err.println("Management Welcome Email failed to execute: " + e.getMessage());
        }
    }
}