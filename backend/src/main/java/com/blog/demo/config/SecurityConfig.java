package com.blog.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.blog.demo.security.JwtAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
     
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("yyyy");
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
            var corsConfig = new CorsConfiguration();

            corsConfig.setExposedHeaders(List.of("X-Account-Status", "Authorization")); // whitelist a header so js can read it from the response
            corsConfig.setAllowedOrigins(List.of("http://localhost:4200"));
            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfig.setAllowedHeaders(List.of("*")); 
            return corsConfig;
        }))
                .authorizeHttpRequests(auth -> auth  
                .requestMatchers( "/uploads/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll() 
                .anyRequest().authenticated()
        )
        
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // don't craeteor use http sessions relying only on JWTs
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//UsernamePasswordAuthenticationFilter traditional sessionlogin username and password --> create session
// token first the filter successfully sets the Authentication context so we skip the UsernamePasswordAuthenticationFilter
        return http.build();
    }

}

