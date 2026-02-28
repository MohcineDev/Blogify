package com.blog.demo.users;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.subs.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController { 
    private final CurrentUserService currentUserService;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUserProfile() {
        User current = currentUserService.getLoggedUser();
        return ResponseEntity.ok(current);
    }

    // fetch profile data
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        User user = currentUserService.getUserProfile(username);
        return ResponseEntity.ok(user);
    }

    // explore page
    @GetMapping("/explore")
    public ResponseEntity<List<User>> exploreUsers(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User current = currentUserService.getLoggedUser();

        Pageable pageable = PageRequest.of(page, size);

        List<User> followed = subscriptionRepository.findUsersNotFollowedByCurrentUser(current.getId(), pageable);
       
        return ResponseEntity.ok(followed);
    }

}
