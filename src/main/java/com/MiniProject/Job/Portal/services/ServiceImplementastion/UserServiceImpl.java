package com.MiniProject.Job.Portal.services.ServiceImplementastion;

import com.MiniProject.Job.Portal.model.dto.ChangePasswordRequestDto;
import com.MiniProject.Job.Portal.model.dto.SignUpRequestDto;
import com.MiniProject.Job.Portal.model.dto.SigninRequestDto;
import com.MiniProject.Job.Portal.model.dto.SigninResponceDto;
import com.MiniProject.Job.Portal.model.entity.*;
import com.MiniProject.Job.Portal.repository.AccessTokenRepository;
import com.MiniProject.Job.Portal.repository.PasswordResetTokenRepository;
import com.MiniProject.Job.Portal.repository.UserRepo;
import com.MiniProject.Job.Portal.security.JwtTokenProvider;
import com.MiniProject.Job.Portal.services.MailService;
import com.MiniProject.Job.Portal.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepo candidateRepo;
    private final PasswordResetTokenRepository tokenRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;
    private final MailService mailService;

    private final String TOKEN_PREFIX = "valid_token:";

    public UserServiceImpl(UserRepo userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserRepo candidateRepo, PasswordResetTokenRepository tokenRepository, PasswordResetTokenRepository tokenRepository1, AccessTokenRepository accessTokenRepository, AuthenticationManager authenticationManager, StringRedisTemplate redisTemplate, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.candidateRepo = candidateRepo;
        this.tokenRepository = tokenRepository1;
        this.accessTokenRepository = accessTokenRepository;
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.mailService = mailService;
    }
    @Override
    public User registerUser(SignUpRequestDto request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email address already in use.");
        }
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCountry(request.getCountry());
        user.setCountryCode(request.getCountryCode());
        user.setPhoneNo(String.valueOf(request.getPhoneNo()));
        user.setRole(Role.valueOf(request.getRole()));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setRole(Role.valueOf(request.getRole()));
        // createdBy, lastUpdatedAt etc. can be set accordingly.

        userRepository.save(user);
        try {
            mailService.sendRegistrationMail(user.getEmail(), user.getFirstName());
            System.out.println("Registration email sent.");
        } catch (Exception e) {
            System.err.println("Failed to send registration email: " + e.getMessage());
        }
        return user;


    }


    @Override
    public SigninResponceDto loginUser(SigninRequestDto request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String jwt = jwtTokenProvider.generateToken(auth);
        SecurityContextHolder.getContext().setAuthentication(auth);
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        //Store in Redis with TTL = token’s remaining validity
        long ttlSeconds = jwtTokenProvider.getRemainingValidity(jwt) / 1_000;
        redisTemplate.opsForValue()
                .set(TOKEN_PREFIX + jwt, request.getEmail(), ttlSeconds, TimeUnit.SECONDS);

        // save in DB for fallback
        AccessToken record = new AccessToken();
        record.setToken(jwt);
        record.setIssuedAt(Instant.now());
        accessTokenRepository.save(record);

        return new SigninResponceDto(jwt, ttlSeconds, request.getEmail());
    }


    @Override
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Authorization token is missing or invalid.");
        }

        String redisKey = "valid_token:" + token;

        boolean tokenInRedis = Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
        boolean tokenInDb = accessTokenRepository.existsByToken(token);

        if (!tokenInRedis && !tokenInDb) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Already logged out");
        }

        // Remove from Redis (use the redisKey)
        redisTemplate.delete(redisKey);

        // Remove from DB
        accessTokenRepository.deleteByToken(token);

        return ResponseEntity.ok("Logged out successfully");
    }


    @Override
    public String forgotPassword(String email) {
        User user = candidateRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate Reset Token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);

//        tokenRepository.deleteByUser(user); // Remove old tokens
        tokenRepository.save(resetToken);

        return (token);
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));


        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        candidateRepo.save(user);

        tokenRepository.delete(resetToken);

        return "Password reset successful!";
    }

    @Override
    public String changePassword(String email, ChangePasswordRequestDto dto) {
        User user = candidateRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            System.out.println("Old password is incorrect");
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        candidateRepo.save(user);

        return "Password changed successfully";
    }

    public User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        throw new RuntimeException("User not authenticated");
    }


    //admin
    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(Role.valueOf(role));
    }

    @Override
    public String approveUser(Long userId) {
        User user = getUserById(userId);
        user.setIsVerified(true);
        userRepository.save(user);
        return "User approved successfully";
    }

    @Override
    public String suspendUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);
        return "User suspended successfully";
    }

    @Override
    public String reactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(true);
        userRepository.save(user);
        return "User reactivated successfully";
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    @Override
    public boolean updateUserStatus(int userId, UserStatus userStatus) {
        User user = userRepository.findById((long) userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + userId + " not found"));

        user.setUserStatus(userStatus);
        userRepository.save(user);
        return true;
    }


    @Override
    public User updateUserById(Long userId, User request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + userId + " not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNo(request.getPhoneNo());
        user.setRole(request.getRole());
        user.setIsActive(request.getIsActive());
        user.setIsVerified(request.getIsVerified());
        user.setCountry(request.getCountry());
        user.setCountryCode(request.getCountryCode());
        user.setNote(request.getNote());
        user.setLastUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public boolean deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("User with ID " + userId + " not found.");
        }

        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public boolean suspiciousActivity(Long userId, String note) {
        User user = userRepository.findById(userId).get();
        if (user != null) {
            user.setNote(note);
            userRepository.save(user);
            return true;
        }
        return false;

    }

}
