package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.DriverMapper;
import com.example.SCM.dto.request.DriverRequestDTO;
import com.example.SCM.dto.response.DriverResponseDTO;
import com.example.SCM.entity.Driver;
import com.example.SCM.entity.User;
import com.example.SCM.entity.Warehouse;
import com.example.SCM.repository.DriverRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.repository.WarehouseRepository;
import com.example.SCM.service.DriverService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImp implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final DriverMapper driverMapper;
    private final MailService mailService;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    @Override
    public DriverResponseDTO save(DriverRequestDTO dto, MultipartFile file) {
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Secure password token mandatory for login provision!");
        }

        User user = driverMapper.toUserEntity(dto);
        User savedUser = userRepository.save(user);

        Set<Warehouse> warehouses = new HashSet<>();
        if (dto.getWarehouseIds() != null && !dto.getWarehouseIds().isEmpty()) {
            warehouses = new HashSet<>(warehouseRepository.findAllById(dto.getWarehouseIds()));
        }

        if (file != null && !file.isEmpty()) {
            String imagePath = uploadImage(file, dto.getDriverName());
            dto.setImage(imagePath);
        }

        Driver driver = driverMapper.toDriverEntity(dto, savedUser, warehouses);
        Driver savedDriver = driverRepository.save(driver);

        sendWelcomeEmail(savedUser);

        return driverMapper.toResponseDTO(savedDriver);
    }

    @Transactional
    @Override
    public DriverResponseDTO update(Long id, DriverRequestDTO dto, MultipartFile file) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver node signature not resolved at ID: " + id));

        User user = driver.getUser();
        if (user != null) {
            user.setName(dto.getDriverName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(dto.getPassword());
            }
            userRepository.save(user);
        }

        driver.setDriverName(dto.getDriverName());
        driver.setPhone(dto.getPhone());
        driver.setAddress(dto.getAddress());
        driver.setNidNumber(dto.getNidNumber());
        driver.setGender(dto.getGender());
        driver.setEmail(dto.getEmail());
        driver.setVehicleType(dto.getVehicleType());
        driver.setVehicleNumber(dto.getVehicleNumber());
        driver.setDob(dto.getDob());
        driver.setRating(dto.getRating());
        driver.setTotalDeliveries(dto.getTotalDeliveries());
        driver.setTotalEarnings(dto.getTotalEarnings());
        driver.setActive(dto.getActive());

        if (file != null && !file.isEmpty()) {
            String newImagePath = uploadImage(file, dto.getDriverName());
            driver.setImage(newImagePath);
        }

        if (dto.getWarehouseIds() != null) {
            Set<Warehouse> updatedWarehouses = new HashSet<>(warehouseRepository.findAllById(dto.getWarehouseIds()));
            driver.setWarehouses(updatedWarehouses);
        }

        return driverMapper.toResponseDTO(driverRepository.save(driver));
    }

    @Transactional(readOnly = true)
    @Override
    public List<DriverResponseDTO> findAll() {
        return driverRepository.findAllWithDetails()
                .stream()
                .map(driverMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DriverResponseDTO> getById(Long id) {
        return driverRepository.findById(id).map(driverMapper::toResponseDTO);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target Driver domain key not found"));

        driverRepository.delete(driver);
    }

    private String uploadImage(MultipartFile file, String driverName) {
        try {
            Path path = Paths.get(uploadDir, "driver");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String cleanedName = driverName.trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Driver file upload sequence dropped: " + e.getMessage());
        }
    }

    private void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null) return;

        String subject = "SCM Gateway Activation – Driver Fleet Workspace";
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
        <div class='header'><h2>SCM Logistics Portal</h2></div>
        <div class='content'>
        <p>Dear <b>%s</b>,</p>
        <p>Your logistics operations gateway profile has been deployed to the enterprise resource platform.</p>
        <p>Please authorize your driver device terminal node via the secure URL token below:</p>
        <div class='btn-container'>
        <a href='http://localhost:8080/api/auth/activate?email=%s' class='btn'>Authorize Fleet Terminal</a>
        </div>
        <p>Best regards,<br><b>The SCM Logistics Operations</b></p>
        </div>
        <div class='footer'>&copy; %d SCM Global Supply Chain. All rights reserved.</div>
        </div>
        </body>
        </html>
        """.formatted(user.getName(), user.getEmail(), java.time.Year.now().getValue());

        try {
            mailService.SenderGeneralMail(user.getEmail(), subject, mailText);
        } catch (MessagingException e) {
            System.err.println("Driver Gateway Email delivery fault: " + e.getMessage());
        }
    }

}