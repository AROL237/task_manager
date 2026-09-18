package com.cova.core.service;

import com.cova.core.INTERFACE.UserService;
import com.cova.core.dto.CreateUserDTO;
import com.cova.core.entities.User;
import com.cova.core.entities.repo.UserRepo;
import com.cova.core.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return this.findByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null)
            throw new RuntimeException("email is null");

        try {
            String search = email.toLowerCase().trim();
            User user = userRepo.findByEmailIgnoreCase(search);

            if (user != null) {
                log.info("user found : {}", user.getFullName());
                return user;
            } else
                throw new UsernameNotFoundException("email not found");
        } catch (Exception e) {
            log.error("An error has occurred: -- {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public User createUser(CreateUserDTO dto) {
        User user = new User();

        try {
            String pwd = passwordEncoder.encode(dto.getPassword());
            user.setEmail(dto.getEmail().toLowerCase().trim());
            user.setPassword(pwd);
            user.setFirstName(dto.getFirstName().toLowerCase().trim());
            user.setGender(dto.getGender().toUpperCase().trim().charAt(0));
            if (dto.getLastName() != null)
                user.setLastName(dto.getLastName().toLowerCase().trim());

            user = userRepo.save(user);
            return user;

        } catch (DataIntegrityViolationException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException("email already exists");
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public User findOne(long id) {

        try {
            return userRepo.findById(id).orElseThrow();
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "invalid value id is mull");
        } catch (NoSuchElementException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "user not found id: " + id);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
