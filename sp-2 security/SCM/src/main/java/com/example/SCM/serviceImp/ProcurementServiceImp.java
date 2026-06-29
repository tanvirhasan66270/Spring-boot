package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.auth.AuthService; // 🌟 ইনপোর্ট করা হলো
import com.example.SCM.dto.mapper.ProcurementMapper;
import com.example.SCM.dto.request.ProcurementRequestDTO;
import com.example.SCM.dto.response.ProcurementResponseDTO;
import com.example.SCM.entity.Procurement;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.GenderStatus;
import com.example.SCM.enumClass.LanguageStatus;
import com.example.SCM.repository.ProcurementRepository;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.role.Role;
import com.example.SCM.service.ProcurementService;
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
public class ProcurementServiceImp implements ProcurementService {

    private final ProcurementRepository procurementRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final ProcurementMapper procurementMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public ProcurementServiceImp(ProcurementRepository procurementRepository, UserRepository userRepository,
                                 PoliceStationRepository policeStationRepository, ProcurementMapper procurementMapper,
                                 MailService mailService, PasswordEncoder passwordEncoder, @Lazy AuthService authService) {
        this.procurementRepository = procurementRepository;
        this.userRepository = userRepository;
        this.policeStationRepository = policeStationRepository;
        this.procurementMapper = procurementMapper;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public ProcurementResponseDTO save(ProcurementRequestDTO dto, MultipartFile file) {
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Credential password mandatory for procurement node recruitment!");
        }

        if (dto.getPassportNumber() != null && procurementRepository.existsByPassportNumber(dto.getPassportNumber())) {
            throw new RuntimeException("This Passport number is already registered under another officer!");
        }
        if (procurementRepository.existsByNidNumber(dto.getNidNumber())) {
            throw new RuntimeException("This NID number is already registered under another officer!");
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
        user.setRole(Role.PROCUREMENT);
        user.setActive(false);

        User savedUser = userRepository.save(user);
        user.setPoliceStation(policeStation);

        if (file != null && !file.isEmpty()) {
            String imagePath = uploadImage(file, dto.getName());

            dto.setAddress("uploads/procurement/" + imagePath);
        }

        Procurement procurement = procurementMapper.toProcurementEntity(dto, savedUser, policeStation);
        if (file != null && !file.isEmpty()) {
            procurement.setImage(dto.getAddress());
        }

        Procurement savedProcurement = procurementRepository.save(procurement);


        authService.sendVerificationEmail(savedUser.getEmail());

        return procurementMapper.convertTOResponseDTO(savedProcurement);
    }

    @Override
    @Transactional
    public ProcurementResponseDTO update(Long id, ProcurementRequestDTO dto, MultipartFile file) {
        Procurement procurement = procurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procurement profile instance missing at ID: " + id));

        PoliceStation policeStation = procurement.getPoliceStation();
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station node mismatch"));
            procurement.setPoliceStation(policeStation);
        }

        User user = procurement.getUser();
        if (user != null) {
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            user.setPoliceStation(policeStation);
            userRepository.save(user);
        }

        procurement.setAddress(dto.getAddress());
        procurement.setNidNumber(dto.getNidNumber());
        procurement.setPassportNumber(dto.getPassportNumber());
        procurement.setDesignation(dto.getDesignation());
        procurement.setActive(dto.isActive());

        if (dto.getDob() != null && !dto.getDob().isBlank()) {
            procurement.setDob(LocalDate.parse(dto.getDob()));
        }
        if (dto.getJoiningDate() != null && !dto.getJoiningDate().isBlank()) {
            procurement.setJoiningDate(LocalDate.parse(dto.getJoiningDate()));
        }
        if (dto.getGender() != null) {
            procurement.setGender(GenderStatus.valueOf(dto.getGender().toUpperCase()));
        }
        if (dto.getLanguage() != null) {
            procurement.setLanguage(LanguageStatus.valueOf(dto.getLanguage().toUpperCase()));
        }

        if (file != null && !file.isEmpty()) {
            String newImagePath = uploadImage(file, dto.getName());
            procurement.setImage("uploads/procurement/" + newImagePath);
        }

        return procurementMapper.convertTOResponseDTO(procurementRepository.save(procurement));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcurementResponseDTO> findAll() {
        return procurementRepository.findAllWithDetails().stream()
                .map(procurementMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProcurementResponseDTO> getById(Long id) {
        return procurementRepository.findById(id).map(procurementMapper::convertTOResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Procurement procurement = procurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target Procurement pointer missing"));
        procurementRepository.delete(procurement);
        if (procurement.getUser() != null) {
            userRepository.delete(procurement.getUser());
        }
    }

    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "procurement");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String cleanedName = "procurement";
            if (name != null) {
                cleanedName = name.trim()
                        .replaceAll("[^a-zA-Z0-9\\s]", "")
                        .replaceAll("\\s+", "_");
            }
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Procurement storage node allocation failure: " + e.getMessage());
        }
    }


    public void sendProcurementWelcomeEmail(Procurement procurement) {
        if (procurement == null || procurement.getUser() == null || procurement.getUser().getEmail() == null) return;

        User authUser = procurement.getUser();
        String subject = "Welcome to SCM Enterprise! Your Account is Ready ";

        String mailText = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f4f6f9; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 30px auto; padding: 0; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .header { background-color: #E91E63; color: white; padding: 35px 25px; text-align: center; }
        .header h1 { margin: 0; font-size: 28px; font-weight: 600; }
        .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 15px; }
        .content { padding: 30px; }
        .welcome-box { background-color: #fce4ec; border-left: 5px solid #E91E63; padding: 18px; margin: 20px 0; border-radius: 4px; }
        .profile-details { width: 100%; border-collapse: collapse; margin: 20px 0; }
        .profile-details td { padding: 10px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
        .profile-details td.label { font-weight: bold; color: #64748b; width: 30%; }
        .btn-container { text-align: center; margin: 35px 0; }
        .btn { background-color: #E91E63; color: white !important; padding: 12px 35px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
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
            <p>A warm welcome to <b>SCM Enterprise Cluster</b>! We are absolutely thrilled to have you onboard as a core sourcing and procurement management team member in our global logistics ecosystem.</p>
            
            <div class='welcome-box'>
                <p style='margin: 0; font-size: 15px; color: #880e4f; font-weight: bold;'>Your strategic procurement workstation is active.</p>
                <p style='margin: 5px 0 0 0; font-size: 13px; color: #475569;'>You can now log into your console node to supervise supplier sourcing matrices, manage purchase requisitions, and track active supply orders.</p>
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
                <a href='http://localhost:4200/login' class='btn'>Log Into Sourcing Console</a>
            </div>

            <p>If you require technical setup assistance or procurement ledger matrix guidelines, our central support desk is here for you.</p>
            <p>Best regards,<br><b>SCM Corporate Procurement Administration Team</b></p>
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
                .replace("{{userRole}}", authUser.getRole() != null ? authUser.getRole().toString() : "PROCUREMENT")
                .replace("{{currentYear}}", String.valueOf(java.time.Year.now().getValue()));

        try {
            mailService.senderGeneralMail(authUser.getEmail(), subject, mailText);
            System.out.println("Procurement Officer Registration Onboarding Email successfully dispatched to node: " + authUser.getEmail());
        } catch (Exception e) {
            System.err.println("Procurement Welcome Email failed to execute: " + e.getMessage());
        }
    }
}