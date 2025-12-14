package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.User;
import com.fiqhmaster.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = userService.registerUser(request);
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        UserDTO userDTO = userService.toDTO(user);
        return new AuthResponse(token, userDTO);
    }
    
    public AuthResponse login(AuthRequest request) {
        User user = userService.findByEmail(request.getEmail());
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        UserDTO userDTO = userService.toDTO(user);
        return new AuthResponse(token, userDTO);
    }
    
    public UserDTO getCurrentUser(Long userId) {
        User user = userService.findById(userId);
        return userService.toDTO(user);
    }
}