package com.blog.demo.likes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")


public class LikesController {
    private final  LikeService likeService;

    @PostMapping("/{postId}")
    public ResponseEntity<Void> toggleLike(@PathVariable Long postId) {
        likeService.toggleLike(postId); 
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long postId) {
        return  ResponseEntity.ok(likeService.countLikes(postId));
    }
    

}
