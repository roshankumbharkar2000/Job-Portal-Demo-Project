package com.MiniProject.Job.Portal.configuration;
import com.MiniProject.Job.Portal.security.JWTFilter;
import com.MiniProject.Job.Portal.security.JwtTokenProvider;
import com.MiniProject.Job.Portal.security.JwtValidate;
import com.MiniProject.Job.Portal.services.ServiceImplementastion.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtValidate jwtValidate;
    private final CustomUserDetailsService customUserDetailsService;
    private final StringRedisTemplate redisTemplate;


    public SecurityConfig(JwtTokenProvider jwtTokenProvider, JwtValidate jwtValidate,
                          CustomUserDetailsService customUserDetailsService, StringRedisTemplate redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtValidate = jwtValidate;
        this.customUserDetailsService = customUserDetailsService;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public JWTFilter authenticationFilter() {
        return new JWTFilter(customUserDetailsService, jwtValidate, jwtTokenProvider, redisTemplate);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter authenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**", "/topic/**", "/app/**", "/employer-dashboard.html", "/static/**", "/js/**", "/css/**").permitAll()
                        .requestMatchers("/api/auth/register","/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/forgot-password","/api/auth/reset-password","/api/auth/change-password").permitAll()
                        .requestMatchers("/api/candidate/**").hasAuthority("ROLE_CANDIDATE")
                        .requestMatchers("/api/job/candidate/**").hasAuthority("ROLE_CANDIDATE")
                        .requestMatchers("/api/interview/candidate/**").hasAuthority("ROLE_CANDIDATE")
                        .requestMatchers("/api/job/employer/**").hasAuthority("ROLE_EMPLOYER")
                        .requestMatchers("/api/interview/employer/**").hasAuthority("ROLE_EMPLOYER")
                        .requestMatchers("/api/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

