package com.blog.demo.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.posts.Post;
import com.blog.demo.posts.PostRepository;
import com.blog.demo.posts.PostService;
import com.blog.demo.reports.Report;
import com.blog.demo.reports.ReportDTO;
import com.blog.demo.reports.ReportRepository;
import com.blog.demo.reports.ReportStatus;
import com.blog.demo.users.Role;
import com.blog.demo.users.User;
import com.blog.demo.users.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor 
@PreAuthorize("hasAuthority('ADMIN')") // Ensures only admin tokens can access these endpoint
public class DashboardController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final DashboardService dashboardService;
    private final PostService postService;

    // get users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        List<User> users = userRepository.findByRoleNot(Role.ADMIN, PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }

    // all posts
    @GetMapping("/posts")
    public ResponseEntity<List<PostDto>> getAllPosts(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage = postRepository.findAll(pageable);
        List<PostDto> postDtos = new ArrayList<>();

        for (Post p : postPage) {
            PostDto dto = new PostDto(
                    p.getId(),
                    p.getTitle(),
                    p.getAuthor().getUsername(),
                    p.isHided());
            postDtos.add(dto);
        }

        return ResponseEntity.ok(postDtos);
    }

    // total counts dashboard stats
    @GetMapping("/dashboard/total")
    public ResponseEntity<DashboardCounts> getTotal() {
        DashboardCounts dashboardCounts = new DashboardCounts();

        dashboardCounts.setReportedPosts(reportRepository.countReportedPosts());
        dashboardCounts.setPosts(postRepository.count());
        dashboardCounts.setUsers(userRepository.count());
        dashboardCounts.setReportedUsers(reportRepository.countReportedUser());

        return ResponseEntity.ok(dashboardCounts);
    }

    // --------- USER ACTIONS
    @PutMapping("user/ban/{userId}")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        dashboardService.banUserAndResolveReports(userId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PutMapping("user/unban/{userId}")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        dashboardService.unbanUserAndReopenReports(userId);
        return ResponseEntity.noContent().build(); // 204
    }

  
    @GetMapping("/reports/users")
    public ResponseEntity<List<ReportDTO>> getStatusReportedUsers(@RequestParam ReportStatus status) {

        ReportStatus statu = (status != null) ? status : ReportStatus.PENDING;
        List<Report> report = reportRepository.getReportedUsersByStatus(statu);

        List<ReportDTO> reportDTOs = new ArrayList<>();
        for (Report r : report) {
 
            String reportedUsername = (r.getReportedUser() != null) ? r.getReportedUser().getUsername() : null;
            Long reportedUserId = (r.getReportedUser() != null) ? r.getReportedUser().getId() : null;
            Boolean banned = (r.getReportedUser() != null) ? r.getReportedUser().isBanned() : false;

            ReportDTO dTO = new ReportDTO(
                    r.getId(),
                    r.getReason(),
                    r.getStatus().name(),
                    r.getTimestamp(),
                    r.getReporter().getUsername(),
                    null,
                    reportedUsername,
                    reportedUserId,
                    banned,
                    false
            );
            reportDTOs.add(dTO);
        }
        return ResponseEntity.ok(reportDTOs);
    }

    @PutMapping("user/dismiss/{userId}")
    public ResponseEntity<Void> dissmissUserReport(@PathVariable Long userId) {
        dashboardService.dismissUserReports(userId);
        return ResponseEntity.noContent().build(); // 204
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        dashboardService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // --------- POST ACTIONS
    /// hide psot
    @PutMapping("post/hide/{postId}")
    public ResponseEntity<Void> hidePost(@PathVariable Long postId) {
        dashboardService.hidePostAndResolveReports(postId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PutMapping("post/unhide/{postId}")
    public ResponseEntity<Void> unhidePost(@PathVariable Long postId) {
        dashboardService.unhidePostAndReopenReports(postId);
        return ResponseEntity.noContent().build(); // 204
    }

    @PutMapping("post/dismiss/{postId}")
    public ResponseEntity<Void> dissmissPostReport(@PathVariable Long postId) {
        dashboardService.dismissPostReports(postId);
        return ResponseEntity.noContent().build(); // 204
    }

    // DELETE POST
    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> deletePosts(@PathVariable Long id) {
        
        postService.deletePost(id);
        return ResponseEntity.noContent().build(); 
    }

    // - - - - - POSTS
    // FILTER getReportedPosts By Status 
    @GetMapping("/reports/posts")
    public ResponseEntity<List<ReportDTO>> getStatusReportedPosts(@RequestParam ReportStatus status) {

        ReportStatus statu = (status != null) ? status : ReportStatus.PENDING;
        List<Report> report = reportRepository.getReportedPostsByStatus(statu);

        List<ReportDTO> reportDTOs = new ArrayList<>();
        for (Report r : report) {

            Long reportedPost = (r.getReportedPost() != null) ? r.getReportedPost().getId() : null;
            Boolean hided = (r.getReportedPost() != null) ? r.getReportedPost().isHided() : false; 

            ReportDTO dTO = new ReportDTO(
                    r.getId(),
                    r.getReason(),
                    r.getStatus().name(),
                    r.getTimestamp(),
                    r.getReporter().getUsername(),
                    reportedPost,
                    null,
                    null,
                    false,
                    hided
            );
            reportDTOs.add(dTO);
        }
        return ResponseEntity.ok(reportDTOs);
    }

}
