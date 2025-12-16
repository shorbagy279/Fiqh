package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.User;
import com.fiqhmaster.exception.DuplicateResourceException;
import com.fiqhmaster.exception.UnauthorizedException;
import com.fiqhmaster.repository.UserRepository;
import com.fiqhmaster.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("البريد الإلكتروني مسجل بالفعل");
        }
        
        User user = userService.registerUser(request);
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        UserDTO userDTO = userService.toDTO(user);
        
        log.info("User registered successfully: {}", user.getEmail());
        return new AuthResponse(token, userDTO);
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("البريد الإلكتروني أو كلمة المرور غير صحيحة"));
        
        if (!user.getIsActive()) {
            throw new UnauthorizedException("الحساب غير مفعّل");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("البريد الإلكتروني أو كلمة المرور غير صحيحة");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getId());
        UserDTO userDTO = userService.toDTO(user);
        
        log.info("User logged in successfully: {}", user.getEmail());
        return new AuthResponse(token, userDTO);
    }
    
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(Long userId) {
        User user = userService.findById(userId);
        return userService.toDTO(user);
    }
}
