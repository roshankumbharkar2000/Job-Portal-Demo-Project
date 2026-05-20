package com.MiniProject.Job.Portal.controller;


import com.MiniProject.Job.Portal.model.dto.ChangePasswordRequestDto;
import com.MiniProject.Job.Portal.model.dto.ForgetPasswordRequest;
import com.MiniProject.Job.Portal.model.dto.SignUpRequestDto;
import com.MiniProject.Job.Portal.model.dto.SignUpResponceDto;
import com.MiniProject.Job.Portal.model.entity.User;
import com.MiniProject.Job.Portal.model.entity.UserStatus;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.UserServiceImpl;
import com.MiniProject.Job.Portal.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImpl userService){
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<SignUpResponceDto> registerUser(@RequestBody SignUpRequestDto request) {
        System.out.println("Registering user: " + request.getEmail()); // Add logs
        User user = userService.registerUser(request);
        System.out.println("User registered with ID: " + user.getUserId());
        return ResponseEntity.ok(new SignUpResponceDto(true, "User registered successfully with id " + user.getUserId()));
    }

    //    @PreAuthorize("hasAuthority('CANDIDATE')")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgetPasswordRequest request) {
        String token = userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Reset token: " + token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.resetPassword(token, newPassword));
    }


    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'EMPLOYER', 'ADMIN')")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequestDto request,
            Authentication authentication) {
        String email = authentication.getName(); // Gets email from JWT
        String response = userService.changePassword(email, request);
        return ResponseEntity.ok(response);
    }


    //admin
    @GetMapping("/users")
    public Page<User> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getAllUsers(page, size);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserByUserId(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/update-user-status")
    public ResponseEntity<String> updateUserStatus(@RequestParam("userId") int userId,
                                                   @RequestParam("status") UserStatus userStatus) {
        boolean isUpdated = userService.updateUserStatus(userId, userStatus);
        return isUpdated ? ResponseEntity.ok("Status Updated Successfully...") :
                ResponseEntity.badRequest().body("Failed to updated status...");

    }

    @PatchMapping("/update-user/{userId}")
    public ResponseEntity<User> updateUserById(@PathVariable Long userId, @RequestBody User updatedUser) {
        User user = userService.updateUserById(userId, updatedUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable("userId") Long userId) {
        boolean deleted = userService.deleteUserById(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("Message", "Deleted Successfully");
        return ResponseEntity.ok(map);
    }

    @PutMapping("/suspicious-activity")
    public ResponseEntity<String> suspiciousActivity(@RequestBody User user) {
        boolean isUpdated = userService.suspiciousActivity(user.getUserId(), user.getNote());
        if (!isUpdated) {
            throw new UsernameNotFoundException("User with id " + user.getUserId() + " not found.");
        }

        return ResponseEntity.ok("User with id : " + user.getUserId() + " marked as suspicious");
    }
}
