package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class SalesOfficerServiceImp implements SalesOfficerService {

    private final SalesOfficerRepository officerRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final SalesOfficerMapper officerMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public SalesOfficerResponseDTO save(SalesOfficerRequestDTO dto, MultipartFile file) {

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

        try {
            user.setRole(Role.valueOf("SALES_OFFICER"));
        } catch (IllegalArgumentException e) {
            user.setRole(Role.MANAGER);
        }

        User savedUser = userRepository.save(user);


        if (file != null && !file.isEmpty()) {
            String imagePath = uploadImage(file, dto.getName());
            dto.setAddress(imagePath);
        }

        SalesOfficer officer = officerMapper.toOfficerEntity(dto, savedUser, policeStation);
        if (file != null && !file.isEmpty()) {
            officer.setImage(dto.getAddress());
        }

        SalesOfficer savedOfficer = officerRepository.save(officer);
        sendWelcomeEmail(savedUser);


        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Credential password mandatory for secure identity allocation!");
        }

        return officerMapper.convertTOResponseDTO(savedOfficer);
    }

    @Override
    @Transactional
    public SalesOfficerResponseDTO update(Long id, SalesOfficerRequestDTO dto, MultipartFile file) {
        SalesOfficer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales Officer instance mismatch at key index: " + id));


        PoliceStation policeStation=officer.getPoliceStation();
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
            officer.setImage(newImagePath);
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

            String cleanedName = name.trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Sales staff filesystem attachment deployment failure: " + e.getMessage());
        }
    }

    private void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null) return;

        String subject = "SCM Gateway Activation – Commercial Distribution Network";
        String mailText = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f9f9f9; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                    .header { background-color: #4CAF50; color: white; padding: 25px; text-align: center; }
                    .header h2 { margin: 0; font-size: 24px; }
                    .content { padding: 30px; }
                    .btn-container { text-align: center; margin: 30px 0; }
                    .btn { background-color: #4CAF50; color: white !important; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block; }
                    .footer { font-size: 0.85em; color: #777777; padding: 20px; background-color: #f1f1f1; text-align: center; border-top: 1px solid #e0e0e0; }
                </style>
            </head>
            <body>
                <div class='container'>
                    <div class='header'><h2>SCM Sales Control Station</h2></div>
                    <div class='content'>
                        <p>Dear <b>%s</b>,</p>
                        <p>Your workspace environment configuration has been compiled as a <b>Sales Officer</b>.</p>
                        <p>Please click below to activate your order management and invoice authorization console:</p>
                        <div class='btn-container'>
                            <a href='http://localhost:8080/api/auth/activate?email=%s' class='btn'>Activate Sales Workstation</a>
                        </div>
                        <p>Best regards,<br><b>The SCM Corporate Operations Team</b></p>
                    </div>
                    <div class='footer'>&copy; %d SCM Distribution Control Core. All rights reserved.</div>
                </div>
            </body>
            </html>
            """.formatted(user.getName(), user.getEmail(), java.time.Year.now().getValue());

        try {
            mailService.senderGeneralMail(user.getEmail(), subject, mailText);
        } catch (Exception e) {
            System.err.println("Sales Officer Activation Engine exception caught: " + e.getMessage());
        }
    }
}