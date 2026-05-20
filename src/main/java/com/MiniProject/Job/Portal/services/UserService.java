package com.MiniProject.Job.Portal.services;

import com.MiniProject.Job.Portal.model.dto.ChangePasswordRequestDto;
import com.MiniProject.Job.Portal.model.dto.SignUpRequestDto;
import com.MiniProject.Job.Portal.model.dto.SigninRequestDto;
import com.MiniProject.Job.Portal.model.dto.SigninResponceDto;
import com.MiniProject.Job.Portal.model.entity.User;
import com.MiniProject.Job.Portal.model.entity.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    User registerUser(SignUpRequestDto request);

    SigninResponceDto loginUser(SigninRequestDto request);

//    void logoutUser(String token);

    ResponseEntity<String> logoutUser(HttpServletRequest request);

    String forgotPassword(String email);

    String resetPassword(String token, String newPassword);

    String changePassword(String email, ChangePasswordRequestDto dto);

    Page<User> getAllUsers(int page, int size);

    List<User> getUsersByRole(String role);

    String approveUser(Long userId);

    String suspendUser(Long userId);

    String reactivateUser(Long userId);

    User getUserById(Long userId);

    boolean updateUserStatus(int userId, UserStatus userStatus);

    User updateUserById(Long userId, User request);

    boolean deleteUserById(Long userId);

    boolean suspiciousActivity(Long userId, String note);
}
