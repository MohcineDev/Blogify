package com.blog.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterReqDTO req) {

        String res = authService.register(req);

        if (res.equals("User registered successfully")) {
            return ResponseEntity.ok(res);
        } else {
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping("/login")
    // use ? it might reutn text or json
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDTO req) {
        String token = authService.login(req.getIdentifier(), req.getPassword());

        if (token.equals("Invalid Credentials")) {
            return ResponseEntity.status(401).body("invalid username/email");
        }
        return ResponseEntity.ok(new JwtResponse(token));
    }

    //    // compact Java record — cleaner than a class
    record JwtResponse(String token) {
    }
}
