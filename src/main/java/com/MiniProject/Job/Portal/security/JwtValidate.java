package com.MiniProject.Job.Portal.security;

import com.MiniProject.Job.Portal.repository.AccessTokenRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JwtValidate {
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final AccessTokenRepository tokenRepository;


    @Autowired
    public JwtValidate(JwtTokenProvider jwtTokenProvider, StringRedisTemplate redisTemplate, AccessTokenRepository tokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
        this.tokenRepository = tokenRepository;
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            // Signature + Expiry + Subject match
            String username = jwtTokenProvider.getUsernameFromToken(token);
            if (!username.equals(userDetails.getUsername())) return false;
            if (jwtTokenProvider.isTokenExpired(token)) return false;

            // ALLOW-LIST: First check Redis
            if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
                return true;
            }

            // Fallback to DB
            if (tokenRepository.existsByToken(token)) {
                // Re-populate Redis with remaining time
                long ttlSeconds = jwtTokenProvider.getRemainingValidity(token) / 1000;
                redisTemplate.opsForValue().set(token, "", ttlSeconds, TimeUnit.SECONDS);
                return true;
            }

            // Not found → Invalid
            return false;

        } catch (Exception e) {
            throw new JwtException("Token validation failed: " + e.getMessage());
        }
    }
}
