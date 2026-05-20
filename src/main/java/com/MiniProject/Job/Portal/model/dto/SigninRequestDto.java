package com.MiniProject.Job.Portal.model.dto;

import lombok.Data;
import org.springframework.stereotype.Component;


@Data
@Component
public class SigninRequestDto {

    private String email;
    private String password;


}
