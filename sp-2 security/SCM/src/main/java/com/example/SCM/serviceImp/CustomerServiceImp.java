package com.example.SCM.serviceImp;

import com.example.SCM.Util.MailService;
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
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    @Override
    public CustomerResponseDTO save(CustomerRequestDTO dto, MultipartFile image) {

        // 🔗 ১. প্রথমে ডাটাবেজ থেকে থানা অবজেক্টটি লোড করে নিন (লজিক্যাল অর্ডার ফিক্স)
        PoliceStation policeStation = dto.getPoliceStationId() != null ?
                policeStationRepository.findById(dto.getPoliceStationId())
                .orElseThrow(() -> new RuntimeException("Target location police station node not found")) : null;

        // ২. নতুন ইউজার অবজেক্ট তৈরি করুন
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(com.example.SCM.role.Role.CUSTOMER);

        // 🔄 জাদুকরী লাইন: ইউজারের ভেতর থানা অ্যাসাইন করা হলো, যা ডাটাবেজের 'police_station_id' কলাম পূরণ করবে
        user.setPoliceStation(policeStation);

        // ৩. ইউজার ডাটাবেজে সেভ করুন
        User savedUser = userRepository.save(user);

        // ৪. কাস্টমার এনটিটি তৈরি ও ম্যাপিং করুন
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customerMapper.updateEntity(dto, customer, policeStation);

        // ৫. ইমেজ আপলোড হ্যান্ডলিং
        if (image != null && !image.isEmpty()) {
            String uploadedFileName = uploadImage(image, dto.getName());
            customer.setImage("uploads/customer/" + uploadedFileName);
        }

        Customer savedCustomer = customerRepository.save(customer);

        // welcome জানিয়ে ডাইনামিক HTML মেইল পাঠানো
        sendCustomerWelcomeEmail(savedCustomer);

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

        // কাস্টম ইমেজ আপডেট হ্যান্ডলিং
        if (image != null && !image.isEmpty()) {
            String uploadedFileName = uploadImage(image, dto.getName());
            customer.setImage("uploads/customer/" + uploadedFileName);
        }

        // 🔄 আপডেট করার সময়ও ইউজারের থানা সিঙ্ক নিশ্চিত করা
        if (customer.getUser() != null) {
            customer.getUser().setPoliceStation(policeStation);
            userRepository.save(customer.getUser());
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
        userRepository.delete(customer.getUser());
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

            String cleanedName = (customerName != null ? customerName : "customer").trim().replaceAll("\\s+", "_");
            String fileName = cleanedName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Customer profile image upload failed: " + e.getMessage());
        }
    }

    private void sendCustomerWelcomeEmail(Customer customer) {
        if (customer == null || customer.getUser() == null || customer.getUser().getEmail() == null) return;

        User authUser = customer.getUser();
        String subject = "Welcome to SCM Enterprise! Your Account is Ready 🎉";

        String mailText = """
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333333; background-color: #f4f6f9; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 30px auto; padding: 0; background-color: #ffffff; border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .header { background-color: #2E7D32; color: white; padding: 35px 25px; text-align: center; }
        .header h1 { margin: 0; font-size: 28px; font-weight: 600; }
        .header p { margin: 5px 0 0 0; opacity: 0.9; font-size: 15px; }
        .content { padding: 30px; }
        .welcome-box { background-color: #E8F5E9; border-left: 5px solid #2E7D32; padding: 18px; margin: 20px 0; border-radius: 4px; }
        .profile-details { width: 100%; border-collapse: collapse; margin: 20px 0; }
        .profile-details td { padding: 10px; border-bottom: 1px solid #f1f5f9; font-size: 14px; }
        .profile-details td.label { font-weight: bold; color: #64748b; width: 30%; }
        .btn-container { text-align: center; margin: 35px 0; }
        .btn { background-color: #2E7D32; color: white !important; padding: 12px 35px; text-decoration: none; font-weight: bold; border-radius: 6px; display: inline-block; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        .footer { font-size: 0.85em; color: #64748b; padding: 20px; background-color: #f8fafc; text-align: center; border-top: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <div class='container'>
        <div class='header'>
            <h1>Congratulations {{customerName}}!</h1>
            <p>Your SCM Portal Account is Successfully Activated</p>
        </div>
        <div class='content'>
            <p>Dear <b>{{customerName}}</b>,</p>
            <p>A warm welcome to <b>SCM Enterprise Cluster</b>! We are absolutely thrilled to have you onboard as a premium partner in our digital global logistics ecosystem.</p>
            
            <div class='welcome-box'>
                <p style='margin: 0; font-size: 15px; color: #1B5E20; font-weight: bold;'>Your account onboarding is complete.</p>
                <p style='margin: 5px 0 0 0; font-size: 13px; color: #475569;'>You can now log into your console node to dispatch customer purchase orders, manage real-time shipments, and monitor your unique delivery lifecycle tracks.</p>
            </div>

            <p><b>Your Registered SCM Network Credentials:</b></p>
            <table class='profile-details'>
                <tr>
                    <td class='label'>Authorized Name:</td>
                    <td>{{customerName}}</td>
                </tr>
                <tr>
                    <td class='label'>Primary Email/User:</td>
                    <td>{{userEmail}}</td>
                </tr>
                <tr>
                    <td class='label'>Contact Phone:</td>
                    <td>{{customerPhone}}</td>
                </tr>
                <tr>
                    <td class='label'>Registered Node Role:</td>
                    <td><span style='background-color:#E2E8F0; padding:3px 8px; border-radius:4px; font-size:12px; font-weight:bold;'>{{userRole}}</span></td>
                </tr>
            </table>

            <div class='btn-container'>
                <a href='http://localhost:4200/login' class='btn'>Log Into Your Client Dashboard</a>
            </div>

            <p>If you have any questions or require administrative assistance setting up your procurement matrix, our central network support desk is here for you 24/7.</p>
            <p>Best regards,<br><b>SCM Enterprise Administration Team</b></p>
        </div>
        <div class='footer'>
            &copy; {{currentYear}} SCM Global Logistics Network Cluster. All rights reserved.
        </div>
    </div>
</body>
</html>
""";

        mailText = mailText
                .replace("{{customerName}}", customer.getName() != null ? customer.getName() : "")
                .replace("{{userEmail}}", authUser.getEmail())
                .replace("{{customerPhone}}", customer.getPhone() != null ? customer.getPhone() : "")
                .replace("{{userRole}}", authUser.getRole() != null ? authUser.getRole().toString() : "CUSTOMER")
                .replace("{{currentYear}}", String.valueOf(java.time.Year.now().getValue()));

        try {
            mailService.SenderGeneralMail(authUser.getEmail(), subject, mailText);
            System.out.println("Customer Registration Congratulation Email successfully dispatched to node: " + authUser.getEmail());
        } catch (Exception e) {
            System.err.println("Registration Onboarding Email failed to execute: " + e.getMessage());
        }
    }
}