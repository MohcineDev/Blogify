package com.blog.demo.posts.media;

public class MediaResponseDTO {

    private final Long id;
    private final String url;
    private final MediaType type;

    public MediaResponseDTO(PostMedia media) {
        this.id = media.getId();
        this.url = media.getUrl();
        this.type = media.getType();
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public MediaType getType() {
        return type;
    }
}
