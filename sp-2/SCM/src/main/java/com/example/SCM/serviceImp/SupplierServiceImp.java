package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.mapper.SupplierMapper;
import com.example.SCM.dto.response.SupplierResponseDTO;
import com.example.SCM.dto.request.SupplierRequestDTO;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.Supplier;
import com.example.SCM.entity.User;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.SupplierRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.service.SupplierService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImp implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final SupplierMapper supplierMapper;
    private final MailService mailService;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public SupplierResponseDTO save(SupplierRequestDTO dto, MultipartFile file) {
        //  পাসওয়ার্ড এবং কনফার্ম পাসওয়ার্ড ভ্যালিডেশন
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Password cannot be empty!");
        }

        //  ম্যাপার দিয়ে Auth User তৈরি ও ডাটাবেজে সেভ
        User user = supplierMapper.toUserEntity(dto);
        User savedUser = userRepository.save(user);

        //  পুলিশ স্টেশন ডাটা খুঁজে বের করা
        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        //  মাল্টিপার্ট ফাইল (ইমেজ) আপলোড হ্যান্ডেল করা
        if (file != null && !file.isEmpty()) {
            // ফিক্সড: পাস করা হলো dto.getName() ফাইল নেমিং এর জন্য
            String imagePath = uploadImage(file, dto.getName());
            dto.setImage(imagePath);
        }

        //  সাপ্লায়ার এনটিটি তৈরি ও সেভ করা
        Supplier supplier = supplierMapper.toSupplierEntity(dto, savedUser, policeStation);
        Supplier savedSupplier = supplierRepository.save(supplier);

        // ফিক্সড: নতুন সাপ্লায়ার সেভ হওয়ার পর তাকে ওয়েলকাম ইমেইল পাঠানো হলো
        sendWelcomeEmail(savedUser);

        return supplierMapper.toResponseDTO(savedSupplier);
    }

    @Transactional
    @Override
    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto, MultipartFile file) {
        //  ডাটাবেজে সাপ্লায়ার চেক করা
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        //  রিলেটেড ইউজার অ্যাকাউন্ট আপডেট করা
        User user = supplier.getUser();
        if (user != null) {
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(dto.getPassword());
//
            }
            userRepository.save(user);
        }

        //  সাপ্লায়ার প্রোফাইল ফিল্ডস আপডেট
        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setNidNumber(dto.getNidNumber());
        supplier.setPassportNumber(dto.getPassportNumber());
        supplier.setGender(dto.getGender());

        if (dto.getDob() != null && !dto.getDob().trim().isEmpty()) {
            try {
                supplier.setDob(String.valueOf(java.sql.Date.valueOf(dto.getDob())));
            } catch (IllegalArgumentException e) {
                try {
                    supplier.setDob(String.valueOf(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dto.getDob())));
                } catch (Exception ex) {
                    // Ignore parsing failure
                }
            }
        }

        supplier.setRating(dto.getRating());
        supplier.setAverageLeadTimeDays(dto.getAverageLeadTimeDays());

        //  নতুন ইমেজ আপলোড দিলে আগেরটা ওভাররাইট/নতুন করে সেট করা
        if (file != null && !file.isEmpty()) {
            String newImagePath = uploadImage(file, dto.getName());
            supplier.setImage(newImagePath);
        }

        //  পুলিশ স্টেশন রিলেশন আপডেট করা
        if (dto.getPoliceStationId() != null) {
            PoliceStation policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not found"));
            supplier.setPoliceStation(policeStation);
        }

        Supplier updatedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDTO(updatedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDTO> findAll() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierResponseDTO> getById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + id));

        supplierRepository.delete(supplier);
        if (supplier.getUser() != null) {
            userRepository.delete(supplier.getUser());
        }
    }


    private String uploadImage(MultipartFile file, String supplierName) {
        try {
            Path path = Paths.get(uploadDir, "supplier");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            // ফিক্সড: অজানা customerName এর বদলে এখন মেথড প্যারামিটারের supplierName ব্যবহার হবে
            String cleanedName = supplierName.trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Supplier profile image upload failed: " + e.getMessage());
        }
    }


    private void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null) {
            return;
        }

        String subject = "Welcome to Our Service – Confirm Your Registration";

        String mailText = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f9f9f9; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; padding: 0; background-color: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
                    .header { background-color: #4CAF50; color: white; padding: 25px; text-align: center; }
                    .header h2 { margin: 0; font-size: 24px; font-weight: 600; }
                    .content { padding: 30px; }
                    .btn-container { text-align: center; margin: 30px 0; }
                    .btn { background-color: #4CAF50; color: white !important; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block; transition: background-color 0.3s ease; }
                    .btn:hover { background-color: #45a049; }
                    .footer { font-size: 0.85em; color: #777777; padding: 20px; background-color: #f1f1f1; text-align: center; border-top: 1px solid #e0e0e0; }
                </style>
            </head>
            <body>
                <div class='container'>
                    <div class='header'>
                        <h2>Welcome to Our Platform</h2>
                    </div>
                    <div class='content'>
                        <p>Dear <b>%s</b>,</p>
                        <p>Thank you for registering with us. We are excited to have you on board!</p>
                        <p>Please confirm your email address to activate your account and get started.</p>
                        
                        <div class='btn-container'>
                            <a href='http://localhost:8080/api/auth/activate?email=%s' class='btn'>Activate Account</a>
                        </div>
                        
                        <p>If you have any questions or need help, feel free to reach out to our support team.</p>
                        <p>Best regards,<br><b>The Support Team</b></p>
                    </div>
                    <div class='footer'>
                        &copy; %d YourCompany. All rights reserved.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(user.getName(), user.getEmail(), java.time.Year.now().getValue());

        try {
            mailService.SenderGeneralMail(user.getEmail(), subject, mailText);
        } catch (MessagingException e) {
            System.err.println("Advanced Email Layout failed to deliver: " + e.getMessage());
        }
    }
}