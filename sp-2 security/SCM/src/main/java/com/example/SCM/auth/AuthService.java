package com.example.SCM.auth;

import com.example.SCM.Util.MailService;
import com.example.SCM.dto.request.ForgotPasswordRequestDTO;
import com.example.SCM.dto.request.LoginRequestDTO;
import com.example.SCM.dto.request.ResetPasswordRequestDTO;
import com.example.SCM.dto.response.LoginResponseDTO;
import com.example.SCM.entity.Customer;
import com.example.SCM.entity.Logistics_Officer;
import com.example.SCM.entity.Manager;
import com.example.SCM.entity.Procurement;
import com.example.SCM.entity.QCInspector;
import com.example.SCM.entity.SalesOfficer;
import com.example.SCM.entity.Supplier;
import com.example.SCM.entity.User;
import com.example.SCM.repository.CustomerRepository;
import com.example.SCM.repository.LogisticsOfficerRepository;
import com.example.SCM.repository.ManagerRepository;
import com.example.SCM.repository.ProcurementRepository;
import com.example.SCM.repository.QCInspectorRepository;
import com.example.SCM.repository.SalesOfficerRepository;
import com.example.SCM.repository.SupplierRepository;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.security.JwtUtil;
import com.example.SCM.service.CustomerService;
import com.example.SCM.service.LogisticsOfficerService;
import com.example.SCM.service.ManagerService;
import com.example.SCM.service.ProcurementService;
import com.example.SCM.service.QCInspectorService;
import com.example.SCM.service.SalesOfficerService;
import com.example.SCM.service.SupplierService;
import jakarta.mail.MessagingException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final LogisticsOfficerRepository logisticsOfficerRepository;
    private final LogisticsOfficerService logisticsOfficerService;
    private final ManagerRepository managerRepository;
    private final ManagerService managerService;
    private final ProcurementRepository procurementRepository;
    private final ProcurementService procurementService;
    private final QCInspectorRepository qcInspectorRepository;
    private final QCInspectorService qcInspectorService;
    private final SalesOfficerRepository salesOfficerRepository;
    private final SalesOfficerService salesOfficerService;

    private final SupplierRepository supplierRepository;
    private final SupplierService supplierService;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder, MailService mailService, CustomerRepository customerRepository,
                       @Lazy CustomerService customerService, LogisticsOfficerRepository logisticsOfficerRepository,
                       @Lazy LogisticsOfficerService logisticsOfficerService, ManagerRepository managerRepository,
                       @Lazy ManagerService managerService, ProcurementRepository procurementRepository,
                       @Lazy ProcurementService procurementService, QCInspectorRepository qcInspectorRepository,
                       @Lazy QCInspectorService qcInspectorService, SalesOfficerRepository salesOfficerRepository,
                       @Lazy SalesOfficerService salesOfficerService, SupplierRepository supplierRepository,
                       @Lazy SupplierService supplierService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.customerRepository = customerRepository;
        this.customerService = customerService;
        this.logisticsOfficerRepository = logisticsOfficerRepository;
        this.logisticsOfficerService = logisticsOfficerService;
        this.managerRepository = managerRepository;
        this.managerService = managerService;
        this.procurementRepository = procurementRepository;
        this.procurementService = procurementService;
        this.qcInspectorRepository = qcInspectorRepository;
        this.qcInspectorService = qcInspectorService;
        this.salesOfficerRepository = salesOfficerRepository;
        this.salesOfficerService = salesOfficerService;
        this.supplierRepository = supplierRepository;
        this.supplierService = supplierService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
        } catch (Exception e) {
            System.out.println("Exception Class = " + e.getClass().getName());
            System.out.println("Exception Message = " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhoneNumber());
        response.setRole(user.getRole().name());

        return response;
    }

    public void sendVerificationEmail(String mail) {
        User user = userRepository.findByEmail(mail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + mail));

        if (user.isActive()) {
            throw new RuntimeException("Account is already verified");
        }

        String token = jwtUtil.generateVerificationToken(user.getEmail());

        try {
            mailService.sendVerificationEmail(user.getEmail(), user.getName(), token);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

    @Transactional
    public void verifyEmail(String token) {

        if (!jwtUtil.isValidForPurpose(token, "EMAIL_VERIFICATION")) {
            throw new RuntimeException("Invalid or expired verification link");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isActive()) {
            throw new RuntimeException("Account is already verified.Please log in to your dashboard.");
        }

        user.setActive(true);
        userRepository.save(user);

        com.example.SCM.role.Role userRole = user.getRole();

        switch (userRole) {
            case CUSTOMER:
                Customer customer = customerRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Customer profile link missing"));
                customerService.sendCustomerWelcomeEmail(customer);
                break;

            case LOGISTICS_OFFICER:
                Logistics_Officer officer = logisticsOfficerRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Logistics Officer profile link missing"));
                logisticsOfficerService.sendLogisticsOfficerWelcomeEmail(officer);
                break;

            case MANAGER:
                Manager manager = managerRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Manager profile link missing"));
                managerService.sendManagerWelcomeEmail(manager);
                break;

            case PROCUREMENT:
                Procurement procurement = procurementRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Procurement profile link missing"));
                procurementService.sendProcurementWelcomeEmail(procurement);
                break;

            case QC_INSPECTOR:
                QCInspector inspector = qcInspectorRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("QC Inspector profile link missing"));
                qcInspectorService.sendQCInspectorWelcomeEmail(inspector);
                break;

            case SALES_OFFICER:
                SalesOfficer salesOfficer = salesOfficerRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Sales Officer profile link missing"));
                salesOfficerService.sendSalesOfficerWelcomeEmail(salesOfficer);
                break;

            case SUPPLIER:
                Supplier supplier = supplierRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new RuntimeException("Supplier profile link missing"));
                supplierService.sendSupplierWelcomeEmail(supplier);
                break;

            case DRIVER:
                System.out.println("Driver verified successfully: " + user.getEmail());
                break;

            case COMMERCIAL_OFFICER:
            case ADMIN:
                System.out.println("Internal Staff/Admin verified successfully: " + user.getEmail());
                break;

            default:
                throw new RuntimeException("Unknown system role detected");
        }
    }

    public void forgotPassword(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with email: " + dto.getEmail()));

        String token = jwtUtil.generateResetToken(user.getEmail());

        try {
            mailService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }

    public void resetPassword(ResetPasswordRequestDTO dto) {
        if (!jwtUtil.isValidForPurpose(dto.getToken(), "PASSWORD_RESET")) {
            throw new RuntimeException("Invalid or expired reset link");
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 4) {
            throw new RuntimeException("Password must be at least 4 characters");
        }

        String email = jwtUtil.extractEmail(dto.getToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}