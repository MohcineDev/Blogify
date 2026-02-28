package com.blog.demo.users;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; 
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {
//user details is used directly by Spring Security's core authentication components (like the AuthenticationProvider and the SecurityContext)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false) 
    private String email;

    // This tells Jackson:
    // “You can write to this field from incoming JSON (deserialization).”
    // “But don’t include it in outgoing JSON responses (serialization).”
    // @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // USER or ADMIN

    @Column(name = "banned", nullable = false, columnDefinition = "boolean default false")
    private boolean banned = false;

    //is the user active and allowed to authenticate?
    @Override
    public boolean isEnabled() {
        return !banned;
    }

    // implment methods from userDetails
    // When checking access rules like hasRole("ADMIN")
    // Returns the user's roles or permissions as a collection of GrantedAuthority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    // If you set to false, user can’t log in (used in enterprise apps)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Useful when locking users after too many failed attempts
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    // Used for password rotation policies
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
