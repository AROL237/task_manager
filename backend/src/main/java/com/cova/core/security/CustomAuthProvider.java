package com.cova.core.security;

import com.cova.core.INTERFACE.UserService;
import com.cova.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class CustomAuthProvider implements AuthenticationProvider {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (username == null || password == null) {
            throw new BadCredentialsException("Bad credentials, invalid username or password");
        }
        if (username.isEmpty() || password.isEmpty()) {
            throw new BadCredentialsException("Bad credentials, invalid username or password");
        }

        try {
            User user = (User) userService.loadUserByUsername(username.trim().toLowerCase());

            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("[AUTH] LOGIN USER: {} ", user.getEmail());

                UsernamePasswordAuthenticationToken context = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                return context;

            } else {
                throw new BadCredentialsException("Bad credentials, invalid login or password");
            }

        } catch (Exception e) {
            throw new BadCredentialsException("Bad credentials, invalid username or password");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.warn("[SUPPORTS] CustomAuthProvider supports {}", authentication);
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
