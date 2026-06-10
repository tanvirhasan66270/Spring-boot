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

    @Value("${file.upload-dir:F:/spring/Assats}")
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
    public Customer save(Customer c, MultipartFile file) {
        return null;
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
    private void sendWelcomeEmail(User user) {
        if (user != null && user.getEmail() != null) {
            try {
                String subject = "Welcome to SCM System!";
                String body = "<h3>Registration Complete</h3>" +
                        "<p>Hello <b>" + user.getName() + "</b>,</p>" +
                        "<p>Your customer profile registration is successful!</p>";

                mailService.SenderGeneralMail(user.getEmail(), subject, body);
            } catch (MessagingException e) {
                // Log email delivery exceptions but do not trigger full database rollbacks
                System.err.println("SMTP notification failed to dispatch: " + e.getMessage());
            }
        }
    }
}