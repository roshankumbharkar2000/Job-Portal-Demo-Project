package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String email;
    private String oldPassword;
    private String newPassword;
}
