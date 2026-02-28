package com.blog.demo.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.users.User;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.blog.demo.users.CurrentUserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/notif")
public class NotificationController {

    NotificationService notificationService;
    CurrentUserService currentUserService;

    //get user notifications
    @GetMapping()
    public ResponseEntity<List<NotificationDTO>> getNotification() {
        User current = currentUserService.getLoggedUser();
        List<NotificationDTO> notifs = notificationService.getNotifs(current.getId());
        return ResponseEntity.ok(notifs);
    }


    // toggle read/ unread
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleRead(@PathVariable Long id) {
        User current = currentUserService.getLoggedUser();
        notificationService.toggleRead(id, current.getId());
        return ResponseEntity.ok().build();
    }

    //count unread notifications
    @GetMapping("/unread")
    public ResponseEntity<Long> countUnread() {
        User current = currentUserService.getLoggedUser();
        Long count = notificationService.countUnread(current.getId());
        return ResponseEntity.ok(count);
    }


    //delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotif(@PathVariable Long id) {
        User current = currentUserService.getLoggedUser();
        notificationService.deleteNotif(id, current.getId());
        return ResponseEntity.ok().build();
    }

    // mark read
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        User current = currentUserService.getLoggedUser();
        notificationService.markRead(id, current.getId());
        return ResponseEntity.ok().build();
    }
}
