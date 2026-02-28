package com.blog.demo.posts;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.demo.users.User;

public interface PostRepository extends JpaRepository<Post, Long> {
    // database access (CRUD
    // Spring JPA auto-generates the actual SQL from method names.
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // You never need to write SQL manually — Spring generates it based on the
    // method names.
    List<Post> findAllByOrderByCreatedAtDesc();

    // find posts where the creator is in the list"authors"
    List<Post> findByAuthorInAndHidedFalseOrderByCreatedAtDesc(List<User> authors, Pageable pageable);

    long countByAuthorId(Long authorId);
 
    List<Post> findByAuthorIdAndHidedFalseOrderByCreatedAtDesc(Long userId);

    List<Post> findByAuthor(User author);

    Page<Post> findAll(Pageable p);

    Long countByAuthorIdAndHidedFalse(Long userId);

}