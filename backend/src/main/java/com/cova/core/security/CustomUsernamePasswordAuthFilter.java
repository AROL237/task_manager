package com.cova.core.security;

import com.cova.core.Constants;
import com.cova.core.dto.ApiResponse;
import com.cova.core.entities.User;
import com.cova.core.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    public JwtService jwtService;


    public CustomUsernamePasswordAuthFilter(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
       setAuthenticationManager(authenticationManager);
       setFilterProcessesUrl("/api/auth/login");
    }




    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String  message = failed.getMessage();
        ApiResponse<Void> out  = new ApiResponse<>(HttpServletResponse.SC_UNAUTHORIZED,message,false,LocalDateTime.now(),null);

        response.getOutputStream().write(new ObjectMapper().writeValueAsString(out).getBytes());
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        if (authResult.isAuthenticated()) {
            User user = (User) authResult.getPrincipal();
            try {
                if (user != null) {

                    String accessToken = jwtService.generateToken(user);

                    Map<String, String> map = new HashMap<>();
                    map.put("accessToken", accessToken);
                    ApiResponse<Map<String, String>> res = new ApiResponse<Map<String, String>>(200, Constants.SUCCESS, true, LocalDateTime.now(), map);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    response.setContentType("application/json;charset=UTF-8");
                    response.getOutputStream().write(new ObjectMapper().writeValueAsString(res).getBytes());
                }
            } catch (Exception e) {
                log.error("[AUTH] an error occurred: {}", e.getMessage(), e);
            }

        } else {
            SecurityContextHolder.getContext().setAuthentication(authResult);
        }
    }
}
