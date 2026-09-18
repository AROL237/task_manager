package com.cova.core.mapper;

import com.cova.core.dto.UserDto;
import com.cova.core.entities.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserMapper implements Function<User, UserDto> {
    public UserMapper() {

    }

    @Override
    public UserDto apply(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender()
        );
    }
}
