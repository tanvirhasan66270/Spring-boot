package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.auth.AuthService;
import com.example.SCM.dto.mapper.QCInspectorMapper;
import com.example.SCM.dto.request.QCInspectorRequestDTO;
import com.example.SCM.dto.response.QCInspectorResponseDTO;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.QCInspector;
import com.example.SCM.entity.User;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.QCInspectorRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.role.Role;
import com.example.SCM.service.QCInspectorService;
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

public class QCInspectorServiceImp implements QCInspectorService {

    private final QCInspectorRepository qcInspectorRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final QCInspectorMapper qcInspectorMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final MailService mailService;

    public QCInspectorServiceImp(QCInspectorRepository qcInspectorRepository, UserRepository userRepository,
                                 PoliceStationRepository policeStationRepository, QCInspectorMapper qcInspectorMapper,
                                 PasswordEncoder passwordEncoder, @Lazy AuthService authService, MailService mailService) {
        this.qcInspectorRepository = qcInspectorRepository;
        this.userRepository = userRepository;
        this.policeStationRepository = policeStationRepository;
        this.qcInspectorMapper = qcInspectorMapper;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.mailService = mailService;
    }

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    @Override
    public QCInspectorResponseDTO save(QCInspectorRequestDTO dto, MultipartFile image) {

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
        user.setActive(false);
        user.setRole(Role.QC_INSPECTOR);
        user.setPoliceStation(policeStation);

        User savedUser = userRepository.save(user);

        QCInspector inspector = qcInspectorMapper.toQCInspectorEntity(dto, savedUser, policeStation);

        if (image != null && !image.isEmpty()) {
            String imagePath = uploadImage(image, dto.getName());
            inspector.setImage("uploads/qc_inspector/" + imagePath);
        }

        QCInspector savedInspector = qcInspectorRepository.save(inspector);

        authService.sendVerificationEmail(savedUser.getEmail());

        return qcInspectorMapper.convertTOResponseDTO(savedInspector);
    }

    @Transactional
    @Override
    public QCInspectorResponseDTO update(Long id, QCInspectorRequestDTO dto, MultipartFile image) {

        QCInspector inspector = qcInspectorRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("QC Inspector not found with ID: " + id));

        PoliceStation policeStation = inspector.getPoliceStation();
        if (dto.getPoliceStationId() != null && (policeStation == null || !dto.getPoliceStationId().equals(policeStation.getId()))) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Selected Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        qcInspectorMapper.updateEntity(dto, inspector, policeStation);

        if (image != null && !image.isEmpty()) {
            String newImagePath = uploadImage(image, dto.getName());
            inspector.setImage("uploads/qc_inspector/" + newImagePath);
        }

        User user = inspector.getUser();
        if (user != null) {
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhone());
            user.setPoliceStation(policeStation);

            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            userRepository.save(user);
        }

        return qcInspectorMapper.convertTOResponseDTO(qcInspectorRepository.save(inspector));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QCInspectorResponseDTO> findAll() {
        return qcInspectorRepository.findAllInspectors().stream()
                .map(qcInspectorMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QCInspectorResponseDTO> getById(Long id) {
        return qcInspectorRepository.findByIdWithDetails(id)
                .map(qcInspectorMapper::convertTOResponseDTO);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        QCInspector inspector = qcInspectorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QC Inspector not found with ID: " + id));

        User user = inspector.getUser();

        qcInspectorRepository.delete(inspector);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "qc_inspector");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();

            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String cleanedName = "inspector";
            if (name != null) {
                cleanedName = name.trim()
                        .replaceAll("[^a-zA-Z0-9\\s]", "")
                        .replaceAll("\\s+", "_");
            }
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed for QC Inspector profile: " + e.getMessage(), e);
        }
    }


    public void sendQCInspectorWelcomeEmail(QCInspector inspector) {
        if (inspector == null || inspector.getUser() == null || inspector.getUser().getEmail() == null) return;

        User authUser = inspector.getUser();
        String subject = "Welcome to SCM Enterprise! Your Account is Ready ";

        String mailText = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f4f6f9; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 30px auto; padding: 0; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .header { background-color: #009688; color: white; padding: 35px 25px; text-align: center; }
        .header h1 { margin: 0; font-size: 28px; font-weight: 600; }
        .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 15px; }
        .content { padding: 30px; }
        .welcome-box { background-color: #e0f2f1; border-left: 5px solid #009688; padding: 18px; margin: 20px 0; border-radius: 4px; }
        .profile-details { width: 100%; border-collapse: collapse; margin: 20px 0; }
        .profile-details td { padding: 10px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
        .profile-details td.label { font-weight: bold; color: #64748b; width: 30%; }
        .btn-container { text-align: center; margin: 35px 0; }
        .btn { background-color: #009688; color: white !important; padding: 12px 35px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .footer { font-size: 0.85em; color: #64748b; padding: 20px; background-color: #f8fafc; text-align: center; border-top: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <div class='container'>
        <div class='header'>
            <h1>Congratulations {{inspectorName}}!</h1>
            <p>Your SCM Portal Account is Successfully Activated</p>
        </div>
        <div class='content'>
            <p>Dear <b>{{inspectorName}}</b>,</p>
            <p>A warm welcome to <b>SCM Enterprise Cluster</b>! We are absolutely thrilled to have you onboard as a core Quality Control and Inspection team member in our global logistics network ecosystem.</p>
            
            <div class='welcome-box'>
                <p style='margin: 0; font-size: 15px; color: #004d40; font-weight: bold;'>Your inspection node terminal is deployed.</p>
                <p style='margin: 5px 0 0 0; font-size: 13px; color: #475569;'>You can now log into your workstation console to process batch testing protocols, authorize quality clear certificates, and review real-time warehouse audit footprints.</p>
            </div>

            <p><b>Your Registered SCM Network Credentials:</b></p>
            <table class='profile-details'>
                <tr>
                    <td class='label'>Authorized Name:</td>
                    <td>{{inspectorName}}</td>
                </tr>
                <tr>
                    <td class='label'>Primary Email/User:</td>
                    <td>{{userEmail}}</td>
                </tr>
                <tr>
                    <td class='label'>Contact Phone:</td>
                    <td>{{inspectorPhone}}</td>
                </tr>
                <tr>
                    <td class='label'>Registered Node Role:</td>
                    <td><span style='background-color:#E2E8F0; padding:3px 8px; border-radius:4px; font-size:12px; font-weight:bold;'>{{userRole}}</span></td>
                </tr>
            </table>

            <div class='btn-container'>
                <a href='http://localhost:4200/login' class='btn'>Launch Quality Workstation</a>
            </div>

            <p>If you face any security node synchronization errors or need assistance deploying your matrix, our operations desk is online.</p>
            <p>Best regards,<br><b>SCM Corporate Quality Assurance Administration</b></p>
        </div>
        <div class='footer'>
            &copy; {{currentYear}} SCM Global Logistics Network Cluster. All rights reserved.
        </div>
    </div>
</body>
</html>
""";

        mailText = mailText
                .replace("{{inspectorName}}", authUser.getName() != null ? authUser.getName() : "")
                .replace("{{userEmail}}", authUser.getEmail())
                .replace("{{inspectorPhone}}", authUser.getPhoneNumber() != null ? authUser.getPhoneNumber() : "")
                .replace("{{userRole}}", authUser.getRole() != null ? authUser.getRole().toString() : "QC_INSPECTOR")
                .replace("{{currentYear}}", String.valueOf(java.time.Year.now().getValue()));

        try {
            mailService.senderGeneralMail(authUser.getEmail(), subject, mailText);
            System.out.println("QC Inspector Registration Onboarding Email successfully dispatched to node: " + authUser.getEmail());
        } catch (Exception e) {
            System.err.println("QC Inspector Welcome Email failed to execute: " + e.getMessage());
        }
    }
}