package com.blog.demo.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginReqDTO {

    @NotBlank(message = "username/email is required")
    private String identifier;

    @NotBlank(message = "password is required")
    private String password;
}
