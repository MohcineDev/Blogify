package com.blog.demo.posts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.blog.demo.comments.Comment;
import com.blog.demo.likes.Like;
import com.blog.demo.posts.media.PostMedia;
import com.blog.demo.users.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;

    @NotBlank(message="title is required")
    @Size(min=3, max=100)
    String title;

    @NotBlank(message="content is required") // Bean Validation annotations
    @Size(min=3, max=500, message="content is between 3 and 500")
    @Column(columnDefinition = "text")
    String content;

    @Column(name = "hided", nullable = false)
    private boolean hided = false;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    User author;


    // @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "post")
    private List<PostMedia> media = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    @JsonManagedReference // for 1001 error infinite loop this tells Spring "You are allowed to go from Post to these items."
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    private final List<Like> likes = new ArrayList<>();

    @Transient // means these fields aren’t saved in the DB — they’re computed each time you fetch a post
    public int getTotalLikes() {
        return likes != null ? likes.size() : 0;
    }

    @Transient // This tells JPA "Don't try to save this to a database column"
    private List<String> keptMediaUrls;
 
    @Transient
    public int getTotalComments() {
        return comments != null ? comments.size() : 0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
