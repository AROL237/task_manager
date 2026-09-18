package com.cova.core.security.jwt;


import com.cova.core.Constants;
import com.cova.core.INTERFACE.UserService;
import com.cova.core.dto.ApiResponse;
import com.cova.core.entities.User;
import com.cova.core.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;


@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        response.setContentType("application/json");
        try {
            String path = request.getServletPath();
            String method = request.getMethod();
            String ip = request.getRemoteAddr();
            String host = request.getRemoteHost();
            log.warn("  {}  {}  {}  ", method, host, path);

            //  FORMAT /v1/login:POST

            String route = String.join(":", path, method);

            if (Constants.PUBLIC_ROUTS.contains(route)) {
                filterChain.doFilter(request, response);
                return;
            }


            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring("Bearer ".length());

                //todo: verify token is valid
                String username = jwtService.extractUsername(token);
                if (username == null)
                    throw new BadCredentialsException("Invalid token");

                User user = (User) userService.loadUserByUsername(username.trim());

                if (!jwtService.isTokenValid(token, user))
                    throw new BadCredentialsException("Invalid token");

                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
                filterChain.doFilter(request, response);

            } else {
                throw new BadCredentialsException("Missing Authorization header Bearer token");
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            ApiResponse<Void> out = new ApiResponse<>(HttpServletResponse.SC_UNAUTHORIZED, msg, false, LocalDateTime.now(), null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(out));
        }
    }
}
