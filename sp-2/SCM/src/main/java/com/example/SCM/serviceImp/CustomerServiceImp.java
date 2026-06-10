package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.Response.CustomerResponseDTO;
import com.example.SCM.dto.mapper.CustomerMapper;
import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.entity.Customer;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.repository.CustomerRepository;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.service.CustomerService;
import com.example.SCM.service.UserService;
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
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService; // Using your custom UserService
    private final MailService mailService;
    private final PoliceStationRepository policeStationRepository;
    private final CustomerMapper customerMapper; // Injected instance mapper component

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public CustomerResponseDTO save(CustomerRequestDTO dto, MultipartFile file) {
        if (dto == null) {
            throw new IllegalArgumentException("Customer request data cannot be null");
        }

        // 1. Map and save User via UserService (handles password/roles processing internally)
        User user = customerMapper.toUserEntity(dto);
        User savedUser = userService.save(user);

        // 2. Fetch the optional PoliceStation location entity from the database
        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        // 3. Map into core Customer profile entity
        Customer customer = customerMapper.toCustomerEntity(dto, savedUser, policeStation);

        // 4. Handle Profile Image File Upload using your custom formatting layout
        if (file != null && !file.isEmpty()) {
            customer.setImage(uploadImage(file, dto.getName()));
        }

        // 5. Save final customer record profile
        Customer savedCustomer = customerRepository.save(customer);

        // 6. Send user registration verification mail without breaking transaction state
        sendWelcomeEmail(savedUser);

        return customerMapper.toResponseDTO(savedCustomer);
    }



    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponseDTO> getById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer profile not found with ID: " + id));

        // Delete core profile registration
        customerRepository.delete(customer);

        // Clean up linked credential record via userService to maintain system synchronization
        if (customer.getUser() != null) {
            userService.delete(customer.getUser().getId());
        }
    }

    /**
     * File management helper using cleaner format naming syntax like Rider service setup
     */
    private String uploadImage(MultipartFile file, String customerName) {
        try {
            Path path = Paths.get(uploadDir, "customer");

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            // Sanitizes spaces inside name strings while adding safety UUID token
            String cleanedName = customerName.trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Customer profile image upload failed: " + e.getMessage());
        }
    }

    /**
     * Safe Mail handler utility wrapper block
     */

        /**
         * Helper Method: Sends a professionally styled HTML verification mail to the customer.
         */
        private void sendWelcomeEmail(User user) {
            if (user == null || user.getEmail() == null) {
                return;
            }

            String subject = "Welcome to Our Service – Confirm Your Registration";

            // Using Java Text Blocks (available in modern Java) to keep HTML incredibly readable
            String mailText = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-index: 1.6; color: #333333; background-color: #f9f9f9; margin: 0; padding: 0; }
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
                // Invokes your MailService component method
                mailService.SenderGeneralMail(user.getEmail(), subject, mailText);
            } catch (MessagingException e) {
                // Logs mail transport issues cleanly to console logs without crashing DB transactions
                System.err.println("Advanced Email Layout failed to deliver: " + e.getMessage());
            }
        }
    }