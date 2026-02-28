package com.blog.demo.dashboard;

import java.util.List;

import lombok.Getter;

@Getter
public class PostDto {

    private final Long id;
    private final String title;
    private String content;
    private final String author;
    private final boolean hided;
    private List<String> mediaUrls;

    public PostDto(Long id, String title, String author, boolean hided) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.hided = hided;
    }

}
