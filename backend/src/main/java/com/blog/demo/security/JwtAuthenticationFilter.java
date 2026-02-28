package com.blog.demo.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.blog.demo.auth.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(req, res);
            return;
        }

        String jwt = authHeader.substring(7);
        try {
            String username = jwtService.extractUsername(jwt);
            // // Proceed only if username is found and no user is authenticated yet
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {

                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    if (userDetails instanceof com.blog.demo.users.User userEntity) {
                        // validate token
                        if (jwtService.isTokenValid(jwt, userEntity)) {

                            // check if user is banned
                            if (!userDetails.isEnabled()) {
                                // user found, token valid but user is banned or deleted
                                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          
                                return;
                            }

                            //This object packages the authenticated user (userDetails) and their granted roles (authorities) into the format that Spring Security recognizes.
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                                    null,
                                    userDetails.getAuthorities());

                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                            //set the Authentication object into the SecurityContext
                            //provides access to the SecurityContext, which is typically bound to the current thread of execution
                            //This is the final step where the validated user's identity is stored in the context. Once this is done, Spring Security considers the request authenticated for the rest of the processing chain.
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } else {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
                            return;
                        }
                    }
                } catch (UsernameNotFoundException e) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
                    return;
                }
            }
        } catch (ExpiredJwtException e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            return;
        }

        filterChain.doFilter(req, res);
    }
}
