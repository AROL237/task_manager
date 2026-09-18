package com.cova.core.security;

import com.cova.core.INTERFACE.UserService;
import com.cova.core.security.jwt.JwtAuthFilter;
import com.cova.core.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class AppSecurityFilterChain {

    private final JwtService jwtService;
    private final JwtAuthFilter jwtAuthFilter;
    private final UserService userService;
    private final CustomAuthProvider provider;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {


//        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService, userService);
        CustomUsernamePasswordAuthFilter customUsernamePasswordAuthFilter = new CustomUsernamePasswordAuthFilter(jwtService, authenticationManager);

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(provider)
                .authorizeHttpRequests(
                        authRequest ->

                                authRequest
                                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(customUsernamePasswordAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable
                )
                .build();
    }

}
