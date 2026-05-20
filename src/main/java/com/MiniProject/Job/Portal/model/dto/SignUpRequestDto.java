package com.MiniProject.Job.Portal.model.dto;


import lombok.Data;

@Data
public class SignUpRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String country;
    private String countryCode;
    private String phoneNo;
    private String role;

}
