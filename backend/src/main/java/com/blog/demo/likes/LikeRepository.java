package com.blog.demo.likes;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByPostId(Long postId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
}