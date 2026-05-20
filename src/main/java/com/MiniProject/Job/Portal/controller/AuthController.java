package com.MiniProject.Job.Portal.controller;

import com.MiniProject.Job.Portal.model.dto.*;
import com.MiniProject.Job.Portal.model.entity.AccessToken;
//import com.MiniProject.Job.Portal.repository.BlacklistedTokenRepository;
import com.MiniProject.Job.Portal.security.JwtTokenProvider;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.UserServiceImpl;
import com.MiniProject.Job.Portal.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SigninRequestDto signinRequestDto;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserServiceImpl userService, SigninRequestDto signinRequestDto, JwtTokenProvider tokenProvider
                         ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.signinRequestDto = signinRequestDto;
        this.tokenProvider = tokenProvider;
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody SigninRequestDto request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }




    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = (request.getHeader("Authorization"));
        userService.logoutUser(request);
        return ResponseEntity.ok("Logged out successfully.");
    }


}
