package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.auth.AuthService;
import com.example.SCM.dto.mapper.SalesOfficerMapper;
import com.example.SCM.dto.request.SalesOfficerRequestDTO;
import com.example.SCM.dto.response.SalesOfficerResponseDTO;
import com.example.SCM.entity.SalesOfficer;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.GenderStatus;
import com.example.SCM.enumClass.LanguageStatus;
import com.example.SCM.repository.SalesOfficerRepository;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.role.Role;
import com.example.SCM.service.SalesOfficerService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SalesOfficerServiceImp implements SalesOfficerService {

    private final SalesOfficerRepository officerRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final SalesOfficerMapper officerMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public SalesOfficerServiceImp(SalesOfficerRepository officerRepository, UserRepository userRepository,
                                  PoliceStationRepository policeStationRepository, SalesOfficerMapper officerMapper,
                                  MailService mailService, PasswordEncoder passwordEncoder, @Lazy AuthService authService) {
        this.officerRepository = officerRepository;
        this.userRepository = userRepository;
        this.policeStationRepository = policeStationRepository;
        this.officerMapper = officerMapper;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public SalesOfficerResponseDTO save(SalesOfficerRequestDTO dto, MultipartFile file) {

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Credential password mandatory for secure identity allocation!");
        }

        if (officerRepository.existsByNidNumber(dto.getNidNumber())) {
            throw new RuntimeException("This NID number is already assigned to another staff profile!");
        }

        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station node missing with ID: " + dto.getPoliceStationId()));
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPoliceStation(policeStation);
        user.setActive(false);

        try {
            user.setRole(Role.valueOf("SALES_OFFICER"));
        } catch (IllegalArgumentException e) {
            user.setRole(Role.MANAGER);
        }

        User savedUser = userRepository.save(user);

        if (file != null && !file.isEmpty()) {
            String imagePath = uploadImage(file, dto.getName());
            dto.setAddress("uploads/sales_officer/" + imagePath);
        }

        SalesOfficer officer = officerMapper.toOfficerEntity(dto, savedUser, policeStation);
        if (file != null && !file.isEmpty()) {
            officer.setImage(dto.getAddress());
        }

        SalesOfficer savedOfficer = officerRepository.save(officer);

        authService.sendVerificationEmail(savedUser.getEmail());

        return officerMapper.convertTOResponseDTO(savedOfficer);
    }

    @Override
    @Transactional
    public SalesOfficerResponseDTO update(Long id, SalesOfficerRequestDTO dto, MultipartFile file) {
        SalesOfficer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales Officer instance mismatch at key index: " + id));

        PoliceStation policeStation = officer.getPoliceStation();
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station link failure"));
            officer.setPoliceStation(policeStation);
        }

        User user = officer.getUser();
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

        officer.setAddress(dto.getAddress());
        officer.setNidNumber(dto.getNidNumber());
        officer.setDesignation(dto.getDesignation());
        officer.setActive(dto.isActive());

        if (dto.getDob() != null && !dto.getDob().isBlank()) {
            officer.setDob(LocalDate.parse(dto.getDob()));
        }
        if (dto.getJoiningDate() != null && !dto.getJoiningDate().isBlank()) {
            officer.setJoiningDate(LocalDate.parse(dto.getJoiningDate()));
        }
        if (dto.getGender() != null) {
            officer.setGender(GenderStatus.valueOf(dto.getGender().toUpperCase()));
        }
        if (dto.getLanguage() != null) {
            officer.setLanguage(LanguageStatus.valueOf(dto.getLanguage().toUpperCase()));
        }

        if (file != null && !file.isEmpty()) {
            String newImagePath = uploadImage(file, dto.getName());
            officer.setImage("uploads/sales_officer/" + newImagePath);
        }

        return officerMapper.convertTOResponseDTO(officerRepository.save(officer));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOfficerResponseDTO> findAll() {
        return officerRepository.findAllWithDetails().stream()
                .map(officerMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesOfficerResponseDTO> getById(Long id) {
        return officerRepository.findById(id).map(officerMapper::convertTOResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SalesOfficer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target operational block pointer missing"));
        officerRepository.delete(officer);
        if (officer.getUser() != null) {
            userRepository.delete(officer.getUser());
        }
    }

    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "sales_officer");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String cleanedName = "sales_officer";
            if (name != null) {
                cleanedName = name.trim()
                        .replaceAll("[^a-zA-Z0-9\\s]", "")
                        .replaceAll("\\s+", "_");
            }
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Sales staff filesystem attachment deployment failure: " + e.getMessage());
        }
    }

    public void sendSalesOfficerWelcomeEmail(SalesOfficer officer) {
        if (officer == null || officer.getUser() == null || officer.getUser().getEmail() == null) return;

        User authUser = officer.getUser();
        String subject = "Welcome to SCM Enterprise! Your Account is Ready ";

        String mailText = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f4f6f9; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 30px auto; padding: 0; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .header { background-color: #4CAF50; color: white; padding: 35px 25px; text-align: center; }
        .header h1 { margin: 0; font-size: 28px; font-weight: 600; }
        .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 15px; }
        .content { padding: 30px; }
        .welcome-box { background-color: #e8f5e9; border-left: 5px solid #4CAF50; padding: 18px; margin: 20px 0; border-radius: 4px; }
        .profile-details { width: 100%; border-collapse: collapse; margin: 20px 0; }
        .profile-details td { padding: 10px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
        .profile-details td.label { font-weight: bold; color: #64748b; width: 30%; }
        .btn-container { text-align: center; margin: 35px 0; }
        .btn { background-color: #4CAF50; color: white !important; padding: 12px 35px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .footer { font-size: 0.85em; color: #64748b; padding: 20px; background-color: #f8fafc; text-align: center; border-top: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <div class='container'>
        <div class='header'>
            <h1>Congratulations {{officerName}}!</h1>
            <p>Your SCM Portal Account is Successfully Activated</p>
        </div>
        <div class='content'>
            <p>Dear <b>{{officerName}}</b>,</p>
            <p>A warm welcome to <b>SCM Enterprise Cluster</b>! We are absolutely thrilled to have you onboard as a core sales and commercial distribution member in our global logistics network ecosystem.</p>
            
            <div class='welcome-box'>
                <p style='margin: 0; font-size: 15px; color: #1b5e20; font-weight: bold;'>Your commercial distribution workstation is live.</p>
                <p style='margin: 5px 0 0 0; font-size: 13px; color: #475569;'>You can now log into your workstation console to manage commercial distribution pipelines, process order cycles, and track customer accounts in real-time.</p>
            </div>

            <p><b>Your Registered SCM Network Credentials:</b></p>
            <table class='profile-details'>
                <tr>
                    <td class='label'>Authorized Name:</td>
                    <td>{{officerName}}</td>
                </tr>
                <tr>
                    <td class='label'>Primary Email/User:</td>
                    <td>{{userEmail}}</td>
                </tr>
                <tr>
                    <td class='label'>Contact Phone:</td>
                    <td>{{officerPhone}}</td>
                </tr>
                <tr>
                    <td class='label'>Registered Node Role:</td>
                    <td><span style='background-color:#E2E8F0; padding:3px 8px; border-radius:4px; font-size:12px; font-weight:bold;'>{{userRole}}</span></td>
                </tr>
            </table>

            <div class='btn-container'>
                <a href='http://localhost:4200/login' class='btn'>Launch Sales Workstation</a>
            </div>

            <p>If you require node deployment guidance or experience technical access anomalies, our central system support grid is standing by.</p>
            <p>Best regards,<br><b>SCM Corporate Sales Operations Administration</b></p>
        </div>
        <div class='footer'>
            &copy; {{currentYear}} SCM Global Logistics Network Cluster. All rights reserved.
        </div>
    </div>
</body>
</html>
""";

        mailText = mailText
                .replace("{{officerName}}", authUser.getName() != null ? authUser.getName() : "")
                .replace("{{userEmail}}", authUser.getEmail())
                .replace("{{officerPhone}}", authUser.getPhoneNumber() != null ? authUser.getPhoneNumber() : "")
                .replace("{{userRole}}", authUser.getRole() != null ? authUser.getRole().toString() : "SALES_OFFICER")
                .replace("{{currentYear}}", String.valueOf(java.time.Year.now().getValue()));

        try {
            mailService.senderGeneralMail(authUser.getEmail(), subject, mailText);
            System.out.println("Sales Officer Registration Onboarding Email successfully dispatched to node: " + authUser.getEmail());
        } catch (Exception e) {
            System.err.println("Sales Officer Welcome Email failed to execute: " + e.getMessage());
        }
    }
}