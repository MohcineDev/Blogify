package com.blog.demo.comments;

import java.time.LocalDateTime;
 

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResDTO {
  
    private final Long id;
    private final String authorUsername;
    private final String content;
    private final LocalDateTime createdAt; 
}
