package com.blog.demo.dashboard;

import lombok.Getter;

@Getter
public class UserDto {

    private Long id;
    private String Username;
    private String email;

    public UserDto(Long id, String username, String email) {
        this.id = id;
        Username = username;
        this.email = email;
    }
 

}
