package com.MiniProject.Job.Portal.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpResponceDto {

    private Boolean success;
    private String message;
}
