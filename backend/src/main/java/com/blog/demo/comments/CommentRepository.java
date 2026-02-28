package com.blog.demo.comments;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
