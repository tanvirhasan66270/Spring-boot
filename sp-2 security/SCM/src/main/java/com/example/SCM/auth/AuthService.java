package com.example.SCM.auth;

import com.example.SCM.dto.request.LoginRequestDTO;
import com.example.SCM.dto.response.LoginResponseDTO;
import com.example.SCM.entity.User;
import com.example.SCM.repository.UserRepository;
import com.example.SCM.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;


    private final UserRepository userRepository;


    private final JwtUtil jwtUtil;


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
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        // =====================================================
        // STEP 4: Create response DTO
        //
        // This data is returned to frontend after login.
        // =====================================================

        LoginResponseDTO response = new LoginResponseDTO();

        response.setToken(token);
        // Token prefix used in API calls
        response.setTokenType("Bearer");

        // User basic information
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhoneNumber());


        response.setRole(user.getRole().name());

     return response;
    }


}