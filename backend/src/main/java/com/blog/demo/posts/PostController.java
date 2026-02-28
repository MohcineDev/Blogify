package com.blog.demo.posts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blog.demo.users.CurrentUserService;
import com.blog.demo.users.User;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    /// talks to the front end takes http req an return ressponsie
    private final PostService postService;
    private final CurrentUserService currentUserService;

    // create
    // By setting consumes = MediaType.MULTIPART_FORM_DATA_VALUE, you tell Spring
    // qto
    // expect a multipart request
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@RequestPart("post") CreatePostDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Post post = postService.createPost(dto, files);
        return new ResponseEntity<>(post, HttpStatus.CREATED);

    }

    // posts from subscribed users
    @GetMapping("/feed")
    public ResponseEntity<List<PostResponseDTO>> getFeed(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        User current = currentUserService.getLoggedUser();

        List<Post> feedPosts = postService.getFeedPosts(current, page, size);
        List<PostResponseDTO> list = new ArrayList<>();

        for (Post p : feedPosts) {
            PostResponseDTO dTO = new PostResponseDTO(p, false);
            list.add(dTO);
        }
        return ResponseEntity.ok(list);
    }

    // read one
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> get(@PathVariable Long id) {
        PostResponseDTO p = postService.fetchPost(id);
        return ResponseEntity.ok(p);
    }

    // update jwt required
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> update(@PathVariable Long id,
            @RequestPart("post") Post post, // json part
            @RequestPart(value = "files", required = false) List<MultipartFile> files // the new files
    ) {
        Post updated = postService.updatePost(id, post, files);
        return ResponseEntity.ok(updated);
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // user profile posts 
    @GetMapping("/profile/{userId}")
    public ResponseEntity<List<PostResponseDTO>> listByAuthor(@PathVariable Long userId) {
        List<PostResponseDTO> userPosts = postService.listByAuthor(userId);
        return ResponseEntity.ok(userPosts);
    }

    //totall created posts
    @GetMapping("/{id}/count")
    public ResponseEntity<Long> countPosts(@PathVariable Long id) {
        return ResponseEntity.ok(postService.countPosts(id));
    }

}
