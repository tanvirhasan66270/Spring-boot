package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder; // ➕ ইম্পোর্ট করা হলো
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerServiceImp implements ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final ManagerMapper managerMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

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
        user.setActive(true);
        user.setPoliceStation(policeStation);

        User savedUser = userRepository.save(user);

        if (image != null && !image.isEmpty()) {
            String imagePath = uploadImage(image, dto.getName());
            dto.setAddress(imagePath);
        }

        Manager manager = managerMapper.toManagerEntity(dto, savedUser, policeStation);
        if (image != null && !image.isEmpty()) {
            manager.setImage(dto.getAddress());
        }

        Manager savedManager = managerRepository.save(manager);
        sendWelcomeEmail(savedUser);

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
        manager.setActive(dto.isActive());

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
            manager.setImage(newImagePath);
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

            String cleanedName = name.trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Manager system file upload fault: " + e.getMessage());
        }
    }

    private void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null) return;

        String subject = "SCM Control Center Gateway Activation";
        String mailText = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f9f9f9; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                    .header { background-color: #2196F3; color: white; padding: 25px; text-align: center; }
                    .header h2 { margin: 0; font-size: 24px; }
                    .content { padding: 30px; }
                    .btn-container { text-align: center; margin: 30px 0; }
                    .btn { background-color: #2196F3; color: white !important; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block; }
                    .footer { font-size: 0.85em; color: #777777; padding: 20px; background-color: #f1f1f1; text-align: center; border-top: 1px solid #e0e0e0; }
                </style>
            </head>
            <body>
                <div class='container'>
                    <div class='header'><h2>SCM Management Portal</h2></div>
                    <div class='content'>
                        <p>Dear <b>%s</b>,</p>
                        <p>Your administration profile node has been deployed successfully as an enterprise <b>Manager</b>.</p>
                        <p>Please authorize your administrative secure node using the activation gateway below:</p>
                        <div class='btn-container'>
                            <a href='http://localhost:8080/api/auth/activate?email=%s' class='btn'>Authorize System Node</a>
                        </div>
                        <p>Best regards,<br><b>The SCM Corporate Operations</b></p>
                    </div>
                    <div class='footer'>&copy; %d SCM Logistics Control Hub. All rights reserved.</div>
                </div>
            </body>
            </html>
            """.formatted(user.getName(), user.getEmail(), java.time.Year.now().getValue());

        try {
            mailService.senderGeneralMail(user.getEmail(), subject, mailText);
        } catch (Exception e) {
            System.err.println("Manager Activation Mail layout broken: " + e.getMessage());
        }
    }
}