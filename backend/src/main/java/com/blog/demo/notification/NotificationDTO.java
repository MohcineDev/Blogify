package com.blog.demo.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationDTO {

    Long Id;
    String message;
    Boolean isRead;
    Long postId;

}
