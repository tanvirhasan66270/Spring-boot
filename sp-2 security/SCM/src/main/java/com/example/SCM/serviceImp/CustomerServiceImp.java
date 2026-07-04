package com.example.SCM.serviceImp;

import com.example.SCM.auth.AuthService;
import com.example.SCM.dto.mapper.CustomerMapper;
import com.example.SCM.dto.request.CustomerRequestDTO;
import com.example.SCM.dto.response.CustomerResponseDTO;
import com.example.SCM.entity.*;
import com.example.SCM.repository.*;
import com.example.SCM.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PoliceStationRepository policeStationRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;





    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    @Override
    public CustomerResponseDTO save(CustomerRequestDTO dto, MultipartFile image) {

        PoliceStation policeStation = dto.getPoliceStationId() != null ?
                policeStationRepository.findById(dto.getPoliceStationId())
                .orElseThrow(() -> new RuntimeException("Target location police station node not found")) : null;

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setActive(false);
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(com.example.SCM.role.Role.CUSTOMER);
        user.setPoliceStation(policeStation);

        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(savedUser);
        customerMapper.updateEntity(dto, customer, policeStation);

        if (image != null && !image.isEmpty()) {
            String uploadedFileName = uploadImage(image, dto.getName());
            customer.setImage("uploads/customer/" + uploadedFileName);
        }

        Customer savedCustomer = customerRepository.save(customer);

        authService.sendVerificationEmail(savedCustomer.getUser().getEmail());


        return customerMapper.convertTOResponseDTO(savedCustomer);

    }



    @Transactional
    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto, MultipartFile image) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer master record missing"));

        PoliceStation policeStation = dto.getPoliceStationId() != null ?
                policeStationRepository.findById(dto.getPoliceStationId()).orElse(customer.getPoliceStation()) : customer.getPoliceStation();

        customerMapper.updateEntity(dto, customer, policeStation);

        if (image != null && !image.isEmpty()) {
            String uploadedFileName = uploadImage(image, dto.getName());
            customer.setImage("uploads/customer/" + uploadedFileName);
        }

        User user = customer.getUser();
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

        return customerMapper.convertTOResponseDTO(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAllCustomersWithDetails()
                .stream()
                .map(customerMapper::convertTOResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<CustomerResponseDTO> getById(Long id) {
        return customerRepository.findByIdWithDetails(id)
                .map(customerMapper::convertTOResponseDTO);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Target customer node index missing"));
        customerRepository.delete(customer);
        if (customer.getUser() != null) {
            userRepository.delete(customer.getUser());
        }
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

            String cleanedName = (customerName != null ? customerName : "customer")
                    .trim()
                    .replaceAll("[\\\\/:*?\"<>|]", "_")   // Replace illegal filename characters
                    .replaceAll("\\s+", "_");             // Replace spaces
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Customer profile image upload failed: " + e.getMessage());
        }
    }


}