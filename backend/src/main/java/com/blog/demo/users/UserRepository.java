package com.blog.demo.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

///fetches user from the db
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRoleNot(Role role, Pageable pageable);

    // find by username or emailmail
    Optional<User> findByUsernameOrEmail(String username, String email);

}
