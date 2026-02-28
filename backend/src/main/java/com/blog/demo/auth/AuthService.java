package com.blog.demo.auth;
 
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.demo.globalExceptions.DuplicateUserException;
import com.blog.demo.users.Role;
import com.blog.demo.users.User;
import com.blog.demo.users.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterReqDTO req) {

        String username = req.getUsername().toLowerCase();
        String email = req.getEmail().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException("Username already taken");
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("email already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return "User registered successfully";

    }

    public String login(String identifier, String password) {

        User user = userRepository.findByUsernameOrEmail(identifier.toLowerCase(), identifier.toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("invalid username or email"));

        // check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("invalid combination");
        }

        // check if banned
        if (!user.isEnabled()) {
            throw  new  DisabledException("Account is Banned");
        }
        // generate token
        return jwtService.generateToken(user);
    }
}
