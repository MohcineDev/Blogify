package com.blog.demo.notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        select new com.blog.demo.notification.NotificationDTO(
        n.id, n.message, n.isRead, n.post.id)   from Notification n 
        where n.user.id = :userId
    """)

    List<NotificationDTO> findAllByUserId(Long userId);

    // @Query("select n from Notification n where n.user.id = :userId")
    // List<Notification> findAllByUserId(Long userId);
    //toggle notification
    @Query("select n from Notification n where id = :notifId and n.user.id = :userId")
    Optional<Notification> findByIdAndUserId(@Param("notifId") Long notifId, @Param("userId") Long userId);

    @Query("select count(n) from Notification n where n.user.id = :userId and n.isRead = false")
    Long countUnread(Long userId);


    @Modifying
    @Query("delete from Notification n where n.post.id = :postId")
    void deleteNotificationByPostId(Long postId);
}
