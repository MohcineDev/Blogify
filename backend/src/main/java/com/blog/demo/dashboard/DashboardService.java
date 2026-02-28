package com.blog.demo.dashboard;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.blog.demo.globalExceptions.ActionNotAuthorizedException; 
import com.blog.demo.posts.Post;
import com.blog.demo.posts.PostRepository;
import com.blog.demo.posts.PostService;
import com.blog.demo.reports.ReportRepository;
import com.blog.demo.reports.ReportStatus;
import com.blog.demo.subs.SubscriptionRepository;
import com.blog.demo.users.CurrentUserService;
import com.blog.demo.users.Role;
import com.blog.demo.users.User;
import com.blog.demo.users.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DashboardService {

    public ReportRepository reportRepository;
    public UserRepository userRepository;
    public PostRepository postRepository;
    public SubscriptionRepository subscriptionRepository;
    public PostService postService;
    public CurrentUserService currentUserService;

    @Transactional
    public void hidePostAndResolveReports(Long id) {

        Post post = postService.getPost(id);
        post.setHided(true);
        postRepository.save(post);

        Set<ReportStatus> reportStatusToResolve = Set.of(ReportStatus.PENDING, ReportStatus.REOPENED);

        // resolve all pending reports
        reportRepository.resolvePendingPostReports(id, ReportStatus.RESOLVED, reportStatusToResolve);
    }

    @Transactional
    public void unhidePostAndReopenReports(Long id) {
        Post post = postService.getPost(id);
        post.setHided(false);
        postRepository.save(post);

        // reopen reports
        reportRepository.reopenResolvedPostReports(id, ReportStatus.REOPENED);
    }

    @Transactional
    public void dismissPostReports(Long id) {
        postService.getPost(id); 
        Set<ReportStatus> reportStatusToDismiss = Set.of(ReportStatus.PENDING, ReportStatus.REOPENED);

        reportRepository.dismissPostReport(id, reportStatusToDismiss);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // ------- USER -------
    @Transactional
    public void banUserAndResolveReports(Long id) {

        User user = currentUserService.getUserById(id);

        if (user.getRole() == Role.ADMIN) {
            throw new ActionNotAuthorizedException("you can't ban an admin");
        }

        user.setBanned(true);

        userRepository.save(user);
        Set<ReportStatus> reportStatusToResolve = Set.of(ReportStatus.PENDING, ReportStatus.REOPENED);

        reportRepository.banUser(id, reportStatusToResolve);
    }

    @Transactional
    public void unbanUserAndReopenReports(Long id) {
        User user = currentUserService.getUserById(id);
        user.setBanned(false);
        userRepository.save(user);
        // reportRepository.unbanUser(id);
        reportRepository.reopenResolvedUserReports(id, ReportStatus.REOPENED);

    }

    @Transactional
    public void dismissUserReports(Long id) {
        currentUserService.getUserById(id);

        Set<ReportStatus> reportStatusToDismiss = Set.of(ReportStatus.PENDING, ReportStatus.REOPENED);

        reportRepository.dismissUserReport(id, reportStatusToDismiss);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = currentUserService.getUserById(id);

        if (user.getRole() == Role.ADMIN) {
            throw new ActionNotAuthorizedException("you can't delete an admin");
        }

         userRepository.deleteById(id);
        // reportRepository.deleteReportByUserId(id);
        //subscriptionRepository.deleteSubscriptionsByUserId(id);

    }
}
