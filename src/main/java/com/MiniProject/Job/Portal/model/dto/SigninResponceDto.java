package com.MiniProject.Job.Portal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class SigninResponceDto {
    private String message = "Login Successful";
    private String token;
    private String type = "Bearer";
    private String email;

    public SigninResponceDto(String jwt, long ttlSeconds, String email) {
        this.token = jwt;
        this.email = email;
    }
}
