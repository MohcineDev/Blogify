package com.blog.demo.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterReqDTO {

    @Size(min = 3, max = 20)
    @NotBlank(message = "user name is required")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only letters and numbers")
    private String username;

    @Size(min = 6, max = 30)
    @NotBlank(message = "email is required")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$", message = "email must be valid email")
    private String email;

    @Size(min = 4, max = 20, message = "password 4 - 20")
    @NotBlank(message = "password is required")
    private String password;

}
