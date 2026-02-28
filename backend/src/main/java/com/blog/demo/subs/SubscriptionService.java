package com.blog.demo.subs;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blog.demo.users.CurrentUserService;
import com.blog.demo.users.User;
import com.blog.demo.users.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public void subscribe(Long targetUserId) {
        User current = currentUserService.getLoggedUser();

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

                if (current == target) {
                    throw new RuntimeException("can't subscribe to your self");
                }
        if (subscriptionRepository.existsBySubscriberAndSubscribedTo(current, target)) {
            throw new RuntimeException("already subscribed");
        }

        Subscription s = new Subscription();
        s.setSubscriber(current);
        s.setSubscribedTo(target);
        subscriptionRepository.save(s);

    }

    @Transactional //Database write operations (like save, update, delete) must be encapsulated within a transaction boundary to guarantee Atomicity (either the whole change succeeds or none of it does).
    public void unsubscribe(Long targetUserId) { 

        User current = currentUserService.getLoggedUser();
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        subscriptionRepository.deleteBySubscriberAndSubscribedTo(current, target);
    }

    public List<User> getSubscribedUsers() {
        User current = currentUserService.getLoggedUser();
        return subscriptionRepository.findSubscribedUsersBySubscriber(current.getId());
    }

    public long countSubscribers(Long id) {
        return subscriptionRepository.countBySubscribedToId(id);
    }

    public long countSubscribed(Long id) {
        return subscriptionRepository.countBySubscriberId(id);
    }

    // for notification
    public List<User> getSubscribersOfUser(Long authorId) {
        // User current = currentUserService.getLoggedUser();
        return subscriptionRepository.findSubscribersBySubscribedTo(authorId);
    }
}
