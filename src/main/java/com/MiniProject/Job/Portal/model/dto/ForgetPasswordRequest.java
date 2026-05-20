package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;

@Data
public class ForgetPasswordRequest {
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
