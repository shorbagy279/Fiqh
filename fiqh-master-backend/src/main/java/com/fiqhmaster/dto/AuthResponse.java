package com.fiqhmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private UserDTO user;
    
    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }
}