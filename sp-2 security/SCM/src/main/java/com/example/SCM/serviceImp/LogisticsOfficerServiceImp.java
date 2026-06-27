package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.LogisticsOfficerMapper;
import com.example.SCM.dto.request.LogisticsOfficerRequestDTO;
import com.example.SCM.dto.response.LogisticsOfficerResponseDTO;
import com.example.SCM.entity.Logistics_Officer;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.GenderStatus;
import com.example.SCM.enumClass.LanguageStatus;
import com.example.SCM.repository.LogisticsOfficerRepository;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.role.Role;
import com.example.SCM.service.ActivityLogService;
import com.example.SCM.service.LogisticsOfficerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class LogisticsOfficerServiceImp implements LogisticsOfficerService {

    private final LogisticsOfficerRepository officerRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final LogisticsOfficerMapper officerMapper;
    private final MailService mailService;
    private final ActivityLogService activityLogService;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;



    @Override
    @Transactional
    public LogisticsOfficerResponseDTO save(LogisticsOfficerRequestDTO dto, MultipartFile file) {
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Credential password cannot be empty for recruitment allocation!");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setRole(Role.LOGISTICS_OFFICER);

        User savedUser = userRepository.save(user);

        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not resolved with ID: " + dto.getPoliceStationId()));
        }

        if (file != null && !file.isEmpty()) {
            String imagePath = uploadImage(file, dto.getContactPerson());
            dto.setAddress(imagePath);
        }

        Logistics_Officer officer = officerMapper.toOfficerEntity(dto, savedUser, policeStation);
        if (file != null && !file.isEmpty()) {
            officer.setImage(dto.getAddress());
        }



        Logistics_Officer savedOfficer = officerRepository.save(officer);
        sendWelcomeEmail(savedUser);

        return officerMapper.convertTOResponseDTO(savedOfficer);
    }

    @Override
    @Transactional
    public LogisticsOfficerResponseDTO update(Long id, LogisticsOfficerRequestDTO dto, MultipartFile file) {
        Logistics_Officer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Logistics Officer instance not found at ID: " + id));

        User user = officer.getUser();
        if (user != null) {
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(dto.getPassword());
            }
            userRepository.save(user);
        }

        officer.setContactPerson(dto.getContactPerson());
        officer.setAddress(dto.getAddress());
        officer.setNidNumber(dto.getNidNumber());
        officer.setPassportNumber(dto.getPassportNumber());
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
            String newImagePath = uploadImage(file, dto.getContactPerson());
            officer.setImage(newImagePath);
        }

        if (dto.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station node mismatch"));
            officer.setPoliceStation(policeStation);
        }

        return officerMapper.convertTOResponseDTO(officerRepository.save(officer));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogisticsOfficerResponseDTO> findAll() {
        return officerRepository.findAllWithDetails().stream()
                .map(officerMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LogisticsOfficerResponseDTO> getById(Long id) {
        return officerRepository.findById(id).map(officerMapper::convertTOResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Logistics_Officer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target Logistics Officer instance key mapping missing"));
        officerRepository.delete(officer);
    }

    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "logistics_officer");
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
            throw new RuntimeException("Officer layout deployment file system fault: " + e.getMessage());
        }
    }

    private void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null) return;

        String subject = "SCM Gateway Activation – Logistics Corporate Administration";
        String mailText = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f9f9f9; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                    .header { background-color: #FF5722; color: white; padding: 25px; text-align: center; }
                    .header h2 { margin: 0; font-size: 24px; }
                    .content { padding: 30px; }
                    .btn-container { text-align: center; margin: 30px 0; }
                    .btn { background-color: #FF5722; color: white !important; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block; }
                    .footer { font-size: 0.85em; color: #777777; padding: 20px; background-color: #f1f1f1; text-align: center; border-top: 1px solid #e0e0e0; }
                </style>
            </head>
            <body>
                <div class='container'>
                    <div class='header'><h2>SCM Control Tower Portal</h2></div>
                    <div class='content'>
                        <p>Dear <b>%s</b>,</p>
                        <p>Your workspace node profile has been compiled successfully as a <b>Logistics Operations Officer</b>.</p>
                        <p>Please authorize your server workspace node credentials via the activation token down under:</p>
                        <div class='btn-container'>
                            <a href='http://localhost:8080/api/auth/activate?email=%s' class='btn'>Authorize Control Station</a>
                        </div>
                        <p>Best regards,<br><b>The SCM Corporate Operations</b></p>
                    </div>
                    <div class='footer'>&copy; %d SCM Logistics Gateway Control. All rights reserved.</div>
                </div>
            </body>
            </html>
            """.formatted(user.getName(), user.getEmail(), java.time.Year.now().getValue());

        try {
            mailService.SenderGeneralMail(user.getEmail(), subject, mailText);
        } catch (Exception e) {
            System.err.println("Officer Activation Mail layout broken: " + e.getMessage());
        }
    }
}