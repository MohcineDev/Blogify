package com.blog.demo.comments;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.blog.demo.posts.Post;
import com.blog.demo.users.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // This tells Spring "When you are inside a Like/Comment, do not go back into the Post."
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Post post;


    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private User Author;

    @NotBlank(message = "Comment is required")
    @Size(max=100, message="Comment can't exceed 100 chars")
    @Size(min=2, message="Comment min 2 chars")
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();
  

}
