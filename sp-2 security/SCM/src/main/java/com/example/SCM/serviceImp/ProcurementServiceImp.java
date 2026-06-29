package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
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
public class ProcurementServiceImp implements ProcurementService {

    private final ProcurementRepository procurementRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final ProcurementMapper procurementMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public ProcurementResponseDTO save(ProcurementRequestDTO dto, MultipartFile file) {
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Credential password mandatory for procurement node recruitment!");
        }

        //ডাটাবেজ ক্র্যাশ এড়াতে পাসপোর্ট এবং এনআইডি ডুপ্লিকেট চেক
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
        User savedUser = userRepository.save(user);
        user.setPoliceStation(policeStation);



        if (file != null && !file.isEmpty()) {
            String imagePath = uploadImage(file, dto.getName());
            dto.setAddress(imagePath);
        }

        Procurement procurement = procurementMapper.toProcurementEntity(dto, savedUser, policeStation);
        if (file != null && !file.isEmpty()) {
            procurement.setImage(dto.getAddress());
        }

        Procurement savedProcurement = procurementRepository.save(procurement);
        sendWelcomeEmail(savedUser);

        return procurementMapper.convertTOResponseDTO(savedProcurement);
    }

    @Override
    @Transactional
    public ProcurementResponseDTO update(Long id, ProcurementRequestDTO dto, MultipartFile file) {
        Procurement procurement = procurementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procurement profile instance missing at ID: " + id));


        PoliceStation policeStation=procurement.getPoliceStation();
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
            procurement.setImage(newImagePath);
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

            String cleanedName = name.trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Procurement storage node allocation failure: " + e.getMessage());
        }
    }

    private void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null) return;

        String subject = "SCM Sourcing Gate Deployment Activation Notification";
        String mailText = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f9f9f9; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                    .header { background-color: #E91E63; color: white; padding: 25px; text-align: center; }
                    .header h2 { margin: 0; font-size: 24px; }
                    .content { padding: 30px; }
                    .btn-container { text-align: center; margin: 30px 0; }
                    .btn { background-color: #E91E63; color: white !important; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block; }
                    .footer { font-size: 0.85em; color: #777777; padding: 20px; background-color: #f1f1f1; text-align: center; border-top: 1px solid #e0e0e0; }
                </style>
            </head>
            <body>
                <div class='container'>
                    <div class='header'><h2>SCM Sourcing & Procurement Terminal</h2></div>
                    <div class='content'>
                        <p>Dear <b>%s</b>,</p>
                        <p>Your workspace credential stack has been deployed as a <b>Procurement Officer</b>.</p>
                        <p>Please click down under to authorize your secure procurement matrix console:</p>
                        <div class='btn-container'>
                            <a href='http://localhost:8080/api/auth/activate?email=%s' class='btn'>Activate Procurement Node</a>
                        </div>
                        <p>Best regards,<br><b>The SCM Corporate Operations Team</b></p>
                    </div>
                    <div class='footer'>&copy; %d SCM Global Sourcing Network. All rights reserved.</div>
                </div>
            </body>
            </html>
            """.formatted(user.getName(), user.getEmail(), java.time.Year.now().getValue());

        try {
            mailService.senderGeneralMail(user.getEmail(), subject, mailText);
        } catch (Exception e) {
            System.err.println("Procurement Activation Mail engine crash: " + e.getMessage());
        }
    }
}