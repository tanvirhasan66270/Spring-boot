package com.example.SCM.serviceImp;


import com.example.SCM.Util.MailService;
import com.example.SCM.dto.Response.CustomerResponseDTO;
import com.example.SCM.dto.mapper.CustomerMapper;
import com.example.SCM.entity.Customer;
import com.example.SCM.entity.PoliceStation;
import com.example.SCM.entity.User;
import com.example.SCM.repository.CustomerRepository;
import com.example.SCM.repository.PoliceStationRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.service.CustomerService;
import com.example.SCM.service.UserService;
import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.SCM.dto.request.CustomerRequestDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final UserService userService; // Using your custom UserService
    private final MailService mailService;
    private final PoliceStationRepository policeStationRepository;
    private final CustomerMapper customerMapper;


    @Value("${image.upload.dir}")
    private String uploadDir;


    @Transactional
    @Override
    public CustomerResponseDTO save(CustomerRequestDTO dto, MultipartFile file) {
        if (dto == null) {
            throw new IllegalArgumentException("Customer request data cannot be null");
        }

        User user = customerMapper.toUserEntity(dto);
        User savedUser = userService.save(user);

        PoliceStation policeStation = null;
        if (dto.getPoliceStationId() != null) {
            policeStation = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("Police Station not found with ID: " + dto.getPoliceStationId()));
        }

        Customer customer = customerMapper.toCustomerEntity(dto, savedUser, policeStation);

        if (file != null && !file.isEmpty()) {
            customer.setImage(uploadImage(file, dto.getName()));
        }

        Customer savedCustomer = customerRepository.save(customer);

        sendWelcomeEmail(savedUser);

        return CustomerMapper.toResponseDTO(savedCustomer);
    }

    @Override
    public List<CustomerResponseDTO> getAll() {
        return List.of();
    }


    @Transactional(readOnly = true)
    @Override
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(CustomerMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rider not found"));
        return CustomerMapper.toResponseDTO(customer);
    }

    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto, MultipartFile image) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Update User fields
        User user = customer.getUser();
        if (dto.getName() != null)  user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhoneNumber(dto.getPhone());
        userRepository.save(user);

        // Update profile fields
        if (dto.getAddress() != null) customer.setAddress(dto.getAddress());
        if (dto.getGender() != null)  customer.setGender(dto.getGender());
        if (dto.getDob() != null && !dto.getDob().isBlank()) {
            try {
                customer.setDob(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDob()));
            } catch (ParseException e) {
                throw new RuntimeException("Invalid date format. Use yyyy-MM-dd");
            }
        }

        if (dto.getPoliceStationId() != null) {
            PoliceStation ps = policeStationRepository.findById(dto.getPoliceStationId())
                    .orElseThrow(() -> new RuntimeException("PoliceStation not found"));
            customer.setPoliceStation(ps);
            user.setPoliceStation(ps);
        }

        if (image != null && !image.isEmpty()) {
            customer.setImage(uploadImage(image, user.getName()));
        }

        Customer saved = customerRepository.save(customer);

        return CustomerMapper.toResponseDTO(
                customerRepository.findByIdWithDetails(saved.getId()).orElse(saved)
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Customer profile not found with ID: " + id));

        customerRepository(customer);

        if (customer.getUser() != null) {
            userService.delete(customer.getUser().getId());
        }
    }

    private void customerRepository(Customer customer) {
    }


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

            String cleanedName = customerName.trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName));
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Customer profile image upload failed: " + e.getMessage());
        }
    }


    private void sendWelcomeEmail(User user) {
        if (user != null && user.getEmail() != null) {
            try {
                String subject = "Welcome to SCM System!";
                String body = "<h3>Registration Complete</h3>" +
                        "<p>Hello <b>" + user.getName() + "</b>,</p>" +
                        "<p>Your customer profile registration is successful!</p>";

                mailService.SenderGeneralMail(user.getEmail(), subject, body);
            } catch (MessagingException e) {
                System.err.println("SMTP notification failed to dispatch: " + e.getMessage());
            }
        }
    }
}