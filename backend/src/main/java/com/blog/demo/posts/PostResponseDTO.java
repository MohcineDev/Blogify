package com.blog.demo.posts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.blog.demo.posts.media.MediaResponseDTO;

import lombok.Getter;

@Getter
public class PostResponseDTO {

    private final Long id;
    private final String title;
    private final String content;
    private final String authorUsername;
    private final LocalDateTime createdAt;
    private final int totalLikes;
    private final int totalComments;
    private final boolean LikedByCurrentUser;
    private final boolean hided;
    private final List<MediaResponseDTO> media;

    public PostResponseDTO(Post post, boolean likeByCuurentUser) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        authorUsername = post.getAuthor().getUsername();
        createdAt = post.getCreatedAt();
        hided = post.isHided();
        totalLikes = post.getTotalLikes();
        totalComments = post.getTotalComments();
        this.LikedByCurrentUser = likeByCuurentUser;

        // convert media list → dto list
        this.media = post.getMedia()
                .stream()
                .map(MediaResponseDTO::new)
                .collect(Collectors.toList());
    }

    public boolean isLikedByCurrentUser() {
        return LikedByCurrentUser;
    }

}