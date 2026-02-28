package com.blog.demo.comments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentReqDto {

    @NotBlank(message = "Comment is required")
    @Size(max = 100, message = "Comment can't exceed 100 chars")
    @Size(min = 2, message = "Comment min 2 chars")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
