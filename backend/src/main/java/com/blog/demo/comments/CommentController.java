package com.blog.demo.comments;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {

    private final CommentService commentService;


    // insert new comment
    @PostMapping("/{postId}")
    public ResponseEntity<CommentResDTO> addComment(@PathVariable Long postId,
            @Valid @RequestBody CommentReqDto payload) {

        Comment com = commentService.addComment(postId, payload.getContent());

        CommentResDTO commentResDTO = new CommentResDTO(com.getId(),
         com.getAuthor().getUsername(),
                com.getContent(),
                 com.getCreatedAt());

        return ResponseEntity.ok(commentResDTO);
    }


    // get all comments
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResDTO>> getComments(@PathVariable Long postId) {

        List<CommentResDTO> commentResDTOs = commentService.getCommentsForPost(postId);
        return ResponseEntity.ok(commentResDTOs);
    }

    // delete a comment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();

    }

}
