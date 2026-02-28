package com.blog.demo.users;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.blog.demo.globalExceptions.BannedUserException;
import com.blog.demo.globalExceptions.EntityNotFoundException;
import com.blog.demo.posts.PostRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CurrentUserService {
    
    /**
     * Helper: get currently authenticated User entity. Reads username from
     * SecurityContext, then loads the User from DB.
     */
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public User getLoggedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByUsername(username);
    }

    public boolean isPostOwner(Long postId) {
        var currentUser = getLoggedUser();
        return postRepository.findById(postId)
                .map(post -> post.getAuthor().getUsername().equals(currentUser.getUsername()))
                .orElse(false);
    }

    // fetch user by id
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->  new EntityNotFoundException("user not found"));
    }

    // fetch profile data
    public User getUserProfile(String username) {
        User user = getUserByUsername(username);

        User logedUser = getLoggedUser();

        if (logedUser.getRole() == Role.USER && user.isBanned()) {
            throw new BannedUserException();
        }
        return user;
    }
}
