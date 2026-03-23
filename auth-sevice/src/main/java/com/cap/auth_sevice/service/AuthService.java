package com.cap.auth_sevice.service;


import com.cap.auth_sevice.dto.*;
import com.cap.auth_sevice.entity.Role;
import com.cap.auth_sevice.entity.User;
import com.cap.auth_sevice.exception.ApiException;
import com.cap.auth_sevice.repository.UserRepository;
import com.cap.auth_sevice.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper mapper;

    // REGISTER
    public void register(RegisterRequest request) {

        // check if user already exists
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email already registered");
        }

        // map DTO → Entity
        User user = mapper.map(request, User.class);

        // encode password
        user.setPassword(encoder.encode(request.getPassword()));

        // assign default role
        user.setRole(Role.USER);

        // save
        repository.save(user);
    }

    // LOGIN
    public AuthResponse login(AuthRequest request) {

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("User not found"));

        // check password
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid credentials");
        }

        // generate JWT
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(token);
    }
}