package com.cova.core.INTERFACE;

import com.cova.core.dto.CreateUserDTO;
import com.cova.core.entities.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {


    User findByEmail(String email);

    User createUser(CreateUserDTO dto);

    User findOne( long id);
}
