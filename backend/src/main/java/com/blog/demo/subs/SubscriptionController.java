package com.blog.demo.subs;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.users.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor

public class SubscriptionController {

    private final SubscriptionService subscriptionService;
   
    @PostMapping("/{userId}")
    public ResponseEntity<?> subscribe(@PathVariable Long userId) {
        subscriptionService.subscribe(userId);
        return ResponseEntity.ok(Map.of("msg", "subscribed successfully"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> unsubscribe(@PathVariable Long userId) { 
        subscriptionService.unsubscribe(userId);
        return ResponseEntity.ok(Map.of("msg", "unsubscribed successfully"));
    }

    @GetMapping
    public ResponseEntity<List<User>> getSubscribedUsers() {
        return ResponseEntity.ok(subscriptionService.getSubscribedUsers());
    }

    ///total subscribers and subscribed

    @GetMapping("/{id}/subscribers")
    public ResponseEntity<Long> countSubscribers(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.countSubscribers(id));
    }

    @GetMapping("/{id}/subscribed")
    public ResponseEntity<Long> countSubscribed(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.countSubscribed(id));
    }

}
