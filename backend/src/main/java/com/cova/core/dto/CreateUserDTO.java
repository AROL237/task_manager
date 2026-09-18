package com.cova.core.dto;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Getter
@Setter
public class CreateUserDTO implements Serializable {

    @NotBlank
    @Size(min = 4, max = 255, message = "provide a valid 'firstName' min length 4")
    private String firstName;
    private String lastName;
    @Email(message = "provide a valid email address")
    private String email;
    @NotBlank

    @Size(min = 6, max = 255, message = "provide a valid 'password' min length 6")
    private String password;
    @NotBlank
    @Size(min = 1, max = 1, message = "provide valid 'gender' M, Male or F, Female")
    @Pattern(regexp = "^[MFmf]$", message = "Gender must be M,m or F, ")
    private String gender;

}
