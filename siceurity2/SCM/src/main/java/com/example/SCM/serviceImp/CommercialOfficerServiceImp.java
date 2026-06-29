package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.CommercialOfficerMapper;
import com.example.SCM.dto.request.CommercialOfficerRequestDTO;
import com.example.SCM.dto.response.CommercialOfficerResponseDTO;
import com.example.SCM.entity.CommercialOfficer;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.enumClass.GenderStatus;
import com.example.SCM.enumClass.LanguageStatus;
import com.example.SCM.repository.CommercialOfficerRepository;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.role.Role;
import com.example.SCM.service.CommercialOfficerService;
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
public class CommercialOfficerServiceImp implements CommercialOfficerService {

    private final CommercialOfficerRepository officerRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final CommercialOfficerMapper officerMapper;
    private final MailService mailService;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    @Override
    public CommercialOfficerResponseDTO save(CommercialOfficerRequestDTO dto, MultipartFile file) {
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Credential password mandatory for commercial workstation deployment!");
        }

        if (dto.getPassportNumber() != null && officerRepository.existsByPassportNumber(dto.getPassportNumber())) {
            throw new RuntimeException("This Passport number is already registered under another commercial officer!");
        }
        if (officerRepository.existsByNidNumber(dto.getNidNumber())) {
            throw new RuntimeException("This NID number is already registered under another commercial officer!");
        }

        // save user first
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setRole(Role.COMMERCIAL_OFFICER);
        User savedUser = userRepository.save(user);

        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not resolved with ID: " + dto.getPoliceStationId()));
        }

        // upload image
        if (file != null && !file.isEmpty()) {
            String imagePath = uploadImage(file, dto.getName());
            dto.setAddress(imagePath);
        }

        CommercialOfficer officer = officerMapper.toOfficerEntity(dto, savedUser, policeStation);
        if (file != null && !file.isEmpty()) {
            officer.setImage(dto.getAddress());
        }

        CommercialOfficer savedOfficer = officerRepository.save(officer);
        sendWelcomeEmail(savedUser);

        return officerMapper.convertTOResponseDTO(savedOfficer);
    }

    @Transactional
    @Override
    public CommercialOfficerResponseDTO update(Long id, CommercialOfficerRequestDTO dto, MultipartFile file) {
        CommercialOfficer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commercial Officer instance not found at ID: " + id));

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
            String newImagePath = uploadImage(file, dto.getName());
            officer.setImage(newImagePath);
        }

        if (dto.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station node mismatch"));
            officer.setPoliceStation(policeStation);
        }

        return officerMapper.convertTOResponseDTO(officerRepository.save(officer));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommercialOfficerResponseDTO> findAll() {
        return officerRepository.findAllWithDetails()
                .stream()
                .map(officerMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<CommercialOfficerResponseDTO> getById(Long id) {
        return officerRepository.findById(id)
                .map(officerMapper::convertTOResponseDTO);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        CommercialOfficer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target Commercial Officer key tracking missing"));
        officerRepository.delete(officer);
    }

    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "commercial_officer");

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
            throw new RuntimeException("Commercial file system node upload fault: " + e.getMessage());
        }
    }

    private void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null) return;

        String subject = "SCM Gateway Activation – Commercial Operations Command";
        String mailText = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f9f9f9; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                    .header { background-color: #009688; color: white; padding: 25px; text-align: center; }
                    .header h2 { margin: 0; font-size: 24px; }
                    .content { padding: 30px; }
                    .btn-container { text-align: center; margin: 30px 0; }
                    .btn { background-color: #009688; color: white !important; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block; }
                    .footer { font-size: 0.85em; color: #777777; padding: 20px; background-color: #f1f1f1; text-align: center; border-top: 1px solid #e0e0e0; }
                </style>
            </head>
            <body>
                <div class='container'>
                    <div class='header'><h2>SCM Commercial Terminal</h2></div>
                    <div class='content'>
                        <p>Dear <b>%s</b>,</p>
                        <p>Your workspace environment configuration has been deployed as a <b>Commercial Officer</b>.</p>
                        <p>Please click down under to authorize your financial letters of credit and invoice matrix console:</p>
                        <div class='btn-container'>
                            <a href='http://localhost:8085/api/auth/activate?email=%s' class='btn'>Activate Commercial Workspace</a>
                        </div>
                        <p>Best regards,<br><b>The SCM Corporate Operations Team</b></p>
                    </div>
                    <div class='footer'>&copy; %d SCM Global LC & Commercial Control. All rights reserved.</div>
                </div>
            </body>
            </html>
            """.formatted(user.getName(), user.getEmail(), java.time.Year.now().getValue());

        try {
            mailService.SenderGeneralMail(user.getEmail(), subject, mailText);
        } catch (Exception e) {
            System.err.println("Commercial Activation Mail lay-node broken: " + e.getMessage());
        }
    }

}