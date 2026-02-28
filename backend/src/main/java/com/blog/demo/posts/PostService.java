package com.blog.demo.posts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.demo.globalExceptions.ActionNotAuthorizedException;
import com.blog.demo.globalExceptions.EntityNotFoundException;
import com.blog.demo.likes.LikeRepository;
import com.blog.demo.notification.NotificationService;
import com.blog.demo.posts.media.PostMedia;
import com.blog.demo.posts.media.PostMediaService;
import com.blog.demo.subs.SubscriptionRepository;
import com.blog.demo.subs.SubscriptionService;
import com.blog.demo.users.CurrentUserService;
import com.blog.demo.users.Role;
import com.blog.demo.users.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CurrentUserService currentUserService;
    private final SubscriptionRepository subscriptionRepository;
    private final PostMediaService postMediaService;
    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;

    @Transactional
    public Post createPost(CreatePostDTO dto, List<MultipartFile> files) {
        User currentUser = currentUserService.getLoggedUser();
        validateFiles(files, 0);

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(currentUser);

        // Save the Post first to get an ID
        Post savedPost = postRepository.save(post);

        // Process and attach media
        if (files != null && !files.isEmpty()) {
            postMediaService.saveMediaForNEwPost(savedPost, files);
        }

        List<User> usersToNotify = subscriptionService.getSubscribersOfUser(post.getAuthor().getId());

        /// create notification
        notificationService.create(post, usersToNotify);
        return savedPost;
    }

    /// get single post
    public Post getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("post not found"));
        return post;
    }

    // chck if liked by current user to display it in the UI
    boolean isLiked(Long currentUserId, Long postId) {
        if (currentUserId != null) {
            return likeRepository.existsByPostIdAndUserId(postId, currentUserId);
        }
        return false;
    }

    //  get single post
    public PostResponseDTO fetchPost(Long id) {
        Post post = getPost(id);

        User currentUser = currentUserService.getLoggedUser();
        if (post.isHided() && currentUser.getRole() == Role.USER) {
            throw new EntityNotFoundException("post not found");
        }

        boolean likedByCurrentUser = isLiked(currentUser.getId(), post.getId());

        PostResponseDTO res = new PostResponseDTO(post, likedByCurrentUser);

        return res;
    }

    @Transactional
    public Post updatePost(Long id, Post updated, List<MultipartFile> files) {
        Post existing = getPost(id);

        int existMediaCount = 0;
        if (updated.getKeptMediaUrls() != null) {
            existMediaCount = updated.getKeptMediaUrls().size();
        }
        validateFiles(files, existMediaCount);

        User current = currentUserService.getLoggedUser();
        boolean isOwner = existing.getAuthor().getId().equals(current.getId());

        if (existing.isHided() && current.getRole() == Role.USER) {

            throw new EntityNotFoundException("post not found");

        }

        if (!isOwner) {
            throw new ActionNotAuthorizedException(
                    "Forbidden : only owner can update it ");
        }

        // dentify files to delete from the folder 
        List<String> urlsToRemove = new ArrayList<>();

        for (PostMedia m : existing.getMedia()) {
            String url = m.getUrl();
            if (updated.getKeptMediaUrls() != null) {

                if (!updated.getKeptMediaUrls().contains(url)) {
                    urlsToRemove.add(url);
                }
            }
        }

        // delete file
        for (String url : urlsToRemove) {
            postMediaService.deleteFileByUrl(url);
        }
        // sync data
        // remove old media not in the kept list
        if (updated.getKeptMediaUrls() != null) {
            existing.getMedia().removeIf(m -> !updated.getKeptMediaUrls().contains(m.getUrl()));
        }

        // add new media
        if (files != null && !files.isEmpty()) {
            postMediaService.saveMediaForNEwPost(existing, files);
        }

        // update allowed fields
        existing.setTitle(updated.getTitle());
        existing.setContent(updated.getContent());
        existing.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(existing);
    }

    /// Delete a post
    @Transactional
    public void deletePost(Long id) {
        Post existPost = getPost(id);
        User currentuser = currentUserService.getLoggedUser();

        boolean isOwner = existPost.getAuthor().getId().equals(currentuser.getId());
        boolean isAdmin = currentuser.getRole() == Role.ADMIN;

        // if the admin hides the post the author of that post can't delete it
        if (!isOwner && !isAdmin) {
            throw new ActionNotAuthorizedException("delete only be owner / admin");
        } else if (isOwner && existPost.isHided()) {
            throw new ActionNotAuthorizedException("post is not found/ hided");
        }

        // notificationRepository.deleteNotificationByPostId(id);
        // reportRepository.deleteReportByPostId(id);
        postRepository.deleteById(id);
    }

    // all posts later only be subscription 
    public List<Post> getFeedPosts(User currentUser, int page, int size) {
        List<User> followedUsers = subscriptionRepository.findSubscribedUsersBySubscriber(currentUser.getId());

        // to include current user's posts too
        // followedUsers.add(currentUser); 
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByAuthorInAndHidedFalseOrderByCreatedAtDesc(followedUsers, p);
    }


    // profile posts
    public List<PostResponseDTO> listByAuthor(Long authorId) {
        List<Post> userPosts = postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
        List<PostResponseDTO> list = new ArrayList<>();

        for (Post p : userPosts) {
            if (!p.isHided()) {
                boolean likedByCurrentUser = isLiked(authorId, p.getId());

                PostResponseDTO dTO = new PostResponseDTO(p, likedByCurrentUser);
                list.add(dTO);
            }
        }
        return list;
    }

    public Long countPosts(Long creatorId) {
        return postRepository.countByAuthorIdAndHidedFalse(creatorId);
    }

// files validator crate  - udpate
    private void validateFiles(List<MultipartFile> files, int existignFiles) {
        long maxReqSize = 25 * 1024 * 1024;

        int newFiles = 0;
        if (files != null) {
            newFiles = files.size();
        }

        //count files
        if (newFiles + existignFiles > 5) {
            throw new IllegalArgumentException("max media is 5");
        }

        // files size 20MB
        if (files != null) {
            int totalSize = 0;

            for (MultipartFile file : files) {
                totalSize += file.getSize();
                if (totalSize > maxReqSize) {
                    throw new IllegalArgumentException("max size is 20MB");
                }

            }
        }
    }

}
