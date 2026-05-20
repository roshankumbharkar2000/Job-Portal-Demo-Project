package com.MiniProject.Job.Portal.security;

import com.MiniProject.Job.Portal.services.ServiceImplementastion.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//public class JWTFilter extends OncePerRequestFilter {
//
//    private final CustomUserDetailsService customUserDetailsService;
//    private final JwtValidate jwtValidate;
//    private final JwtTokenProvider jwtTokenProvider;
//    private final StringRedisTemplate redisTemplate;
//
//    public JWTFilter(CustomUserDetailsService customUserDetailsService,
//                     JwtValidate jwtValidate,
//                     JwtTokenProvider jwtTokenProvider,
//                     StringRedisTemplate redisTemplate) {
//        this.customUserDetailsService = customUserDetailsService;
//        this.jwtValidate = jwtValidate;
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.redisTemplate = redisTemplate;
//    }
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String token = getJWTFromRequest(request);
//
//        if (!StringUtils.hasText(token)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            // Check Redis cache
//            String redisKey = "valid_token:" + token;
//
//            Boolean existsInRedis = redisTemplate.hasKey(redisKey);
//            System.out.println(existsInRedis);
//
//            if (!Boolean.TRUE.equals(existsInRedis) || !jwtTokenProvider.isTokenStoredInDatabase(token)) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token. Please login again.");
//                return;
//            }
//
//            // Extract roles from token
//            List<String> roles = jwtTokenProvider.getRolesFromToken(token);
//            List<GrantedAuthority> authorities = roles.stream()
//                    .map(role -> (GrantedAuthority) () -> role)
//                    .collect(Collectors.toList());
//            String username = jwtTokenProvider.getUsernameFromToken(token);
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//            if (jwtValidate.validateToken(token, userDetails)) {
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, authorities);
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//
//        } catch (ExpiredJwtException e) {
//            SecurityContextHolder.clearContext();
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired. Please login again.");
//            return;
//        } catch (MalformedJwtException | SignatureException e) {
//            SecurityContextHolder.clearContext();
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token signature.");
//            return;
//        } catch (JwtException e) {
//            SecurityContextHolder.clearContext();
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT error: " + e.getMessage());
//            return;
//        } catch (Exception e) {
//            SecurityContextHolder.clearContext();
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed.");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//
//    private String getJWTFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}

public class JWTFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtValidate jwtValidate;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public JWTFilter(CustomUserDetailsService customUserDetailsService,
                     JwtValidate jwtValidate,
                     JwtTokenProvider jwtTokenProvider,
                     StringRedisTemplate redisTemplate) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtValidate = jwtValidate;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Skip JWT authentication for public endpoints (e.g. /register, /login)
        if (isPublicEndpoint(request)) {
            System.out.println("Skipping JWT validation for public endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        String token = getJWTFromRequest(request);

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Check Redis cache

            String redisKey = "valid_token:" + token;

            Boolean existsInRedis = redisTemplate.hasKey(redisKey);
            System.out.println(existsInRedis);

            if (!Boolean.TRUE.equals(existsInRedis) || !jwtTokenProvider.isTokenStoredInDatabase(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token. Please login again.");
                return;
            }

            // Extract roles from token
            List<String> roles = jwtTokenProvider.getRolesFromToken(token);
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> (GrantedAuthority) () -> role)
                    .collect(Collectors.toList());
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtValidate.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired. Please login again.");
            return;
        } catch (MalformedJwtException | SignatureException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token signature.");
            return;
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT error: " + e.getMessage());
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Method to check if the request is for a public endpoint
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.equals("/api/auth/register") || uri.equals("/api/auth/login") ||
                uri.equals("/api/auth/forgot-password") || uri.equals("/api/auth/reset-password") ||
                uri.equals("/api/auth/change-password");
    }
}


