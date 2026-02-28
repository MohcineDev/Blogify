package com.blog.demo.comments;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.blog.demo.globalExceptions.ActionNotAuthorizedException;
import com.blog.demo.globalExceptions.EntityNotFoundException;
import com.blog.demo.posts.Post;
import com.blog.demo.posts.PostService;
import com.blog.demo.users.CurrentUserService;
import com.blog.demo.users.Role;
import com.blog.demo.users.User;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CurrentUserService currentUserService;

    public Comment addComment(Long postId, String content) {
        User user = currentUserService.getLoggedUser();
        Post post = postService.getPost(postId);

        /// admin can do anything
        if (post.isHided() && user.getRole() == Role.USER) {
            throw new EntityNotFoundException("post not found");
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    // get all comments
    @Transactional
    public List<CommentResDTO> getCommentsForPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        List<CommentResDTO> commDTO = new ArrayList<>();

        for (Comment com : comments) {
            CommentResDTO crdto = new CommentResDTO(com.getId(), com.getAuthor().getUsername(), com.getContent(),
                    com.getCreatedAt());
            commDTO.add(crdto);
        }
        return commDTO;
    }

    private Comment getComment(Long id) {
        Comment com = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("comment not found"));
        return com;
    }

    // delete a comment
    public void delete(Long id) {
        Comment com = getComment(id);
        User currentUser = currentUserService.getLoggedUser();

//check if post is hided
        if (com.getPost().isHided() && currentUser.getRole() == Role.USER) {
            throw new EntityNotFoundException("post not found");
        }

        // check if the current uer is the womment owner
        boolean isOwner = com.getAuthor().getId().equals(currentUser.getId());

        if (!isOwner) {
            throw new ActionNotAuthorizedException("delete only by the owner");
        }
        commentRepository.deleteById(id);
    }
}
