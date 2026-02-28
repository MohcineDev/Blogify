package com.blog.demo.users;
 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
//load the user by username

    @Override
    public UserDetails loadUserByUsername(String identifier)   {
        return userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

    }
}
