package com.blog.demo.notification;

import java.util.List;
 
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.blog.demo.posts.Post;
import com.blog.demo.users.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service  // darori

public class NotificationService {

    public final NotificationRepository notificationRepository;

    //create a new notification
    public void create(Post post, List<User> userToNotify) {
        for (User u : userToNotify) {

            Notification n = new Notification();
            n.setIsRead(false);
            n.setUser(u);
            n.setPost(post);
            n.setMessage("hey 👋 new post is created");
            notificationRepository.save(n);
        }
    }

    //get user notifications
    public List<NotificationDTO> getNotifs(Long userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    // toggle read/ unread
    public void toggleRead(Long notifId, Long userId) {
        Notification n = getNotif(notifId, userId);
        n.setIsRead(!n.getIsRead());
        notificationRepository.save(n);
    }

    //count unread notifications
    public Long countUnread(Long userId) {
        return notificationRepository.countUnread(userId);
    }

    //delete notification
    public ResponseEntity<Void> deleteNotif(Long notifId, Long userId) {
        Notification n = getNotif(notifId, userId);
        notificationRepository.delete(n);
        return ResponseEntity.ok().build();
    }

    // toggle read/ unread
    public void markRead(Long notifId, Long userId) {
        Notification n = getNotif(notifId, userId);
        n.setIsRead(true);
        notificationRepository.save(n);
    }

    // helper
    // getNotif
    public Notification getNotif(Long notifId, Long userId) {
        Notification n = notificationRepository.findByIdAndUserId(notifId, userId)
                .orElseThrow(() -> new EntityNotFoundException("notif not found"));
        return n;
    }

}
