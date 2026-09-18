package com.cova.core.controller;

import com.cova.core.Constants;
import com.cova.core.INTERFACE.UserService;
import com.cova.core.dto.ApiResponse;
import com.cova.core.dto.CreateUserDTO;
import com.cova.core.dto.UserDto;
import com.cova.core.entities.User;
import com.cova.core.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody CreateUserDTO user) {

        log.info("creating user {}", user);

        User newUser = userService.createUser(user);

        UserDto newUserDto = new UserMapper().apply(newUser);
        ApiResponse<?> out = new ApiResponse<>(HttpStatus.OK.value(), Constants.SUCCESS, true, LocalDateTime.now(), newUserDto);

        return ResponseEntity.status(HttpStatus.OK).body(out);

    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getUser(Principal principal) {
        log.info("getting user profile user {}", principal);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDto userDto = new UserMapper().apply(user);
//        User user = userService.findOne(id);
        ApiResponse<UserDto> out = new ApiResponse<>(HttpStatus.OK.value(), Constants.SUCCESS, true, LocalDateTime.now(), userDto);
        return ResponseEntity.status(HttpStatus.OK).body(out);
    }

}
