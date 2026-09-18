package com.cova.core.dto;

import com.cova.core.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable, Function<User, UserDto>  {
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

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private char gender;


}
