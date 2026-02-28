package com.blog.demo.subs;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.blog.demo.users.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsBySubscriberAndSubscribedTo(User subscriber, User subscribedTo);

    void deleteBySubscriberAndSubscribedTo(User subscriber, User subscribedTo);
 

    @Query("select s.subscribedTo from Subscription s where s.subscriber.id = :subscriberId")
    List<User> findSubscribedUsersBySubscriber(@Param("subscriberId") Long subscriberId);

    //count how many subscribed to
    long countBySubscriberId(Long userId);

    //count subscribers to that id
    long countBySubscribedToId(Long userId);

    @Modifying
    @Query("delete from Subscription s where s.subscriber.id = :userId OR s.subscribedTo.id = :userId")
    void deleteSubscriptionsByUserId(Long userId);

    @Query("select u from User u where u.id != :currentUserId and u not in (select s.subscribedTo from Subscription s where s.subscriber.id = :currentUserId)")
    List<User> findUsersNotFollowedByCurrentUser(Long currentUserId, Pageable pageable);

    @Query("select s.subscriber from Subscription s  where s.subscribedTo.id = :subscribedTo")
    List<User> findSubscribersBySubscribedTo(@Param("subscribedTo") Long subscribedTo);

}
