package com.blog.demo.likes;

import org.springframework.stereotype.Service;
 
import com.blog.demo.globalExceptions.EntityNotFoundException;
import com.blog.demo.posts.Post;
import com.blog.demo.posts.PostService;
import com.blog.demo.users.CurrentUserService;
import com.blog.demo.users.Role;
import com.blog.demo.users.User;
  
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final CurrentUserService currentUserService;
    private final PostService postService;

    public void toggleLike(Long postId) {
        User currentUser = currentUserService.getLoggedUser();

        Like existing = likeRepository.findByPostIdAndUserId(postId, currentUser.getId())
                .orElse(null);

        Post post = postService.getPost(postId);
        if (post.isHided() && currentUser.getRole() == Role.USER) {
            
            throw new EntityNotFoundException("post not found");
            
        }

        if (existing != null) {
            likeRepository.delete(existing); // unlike
        } else {

            Like like = new Like();
            like.setPost(post);
            like.setUser(currentUser);
            likeRepository.save(like);
        }
    }

    public long countLikes(Long postId) {
        return likeRepository.countByPostId(postId);
    }
}
