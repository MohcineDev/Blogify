package com.blog.demo.reports;
 
import org.springframework.stereotype.Service;
 
import com.blog.demo.globalExceptions.EntityNotFoundException;
import com.blog.demo.globalExceptions.InvalidReportException;
import com.blog.demo.posts.Post;
import com.blog.demo.posts.PostRepository;
import com.blog.demo.users.CurrentUserService;
import com.blog.demo.users.Role;
import com.blog.demo.users.User;
import com.blog.demo.users.UserRepository;
 
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReportService {

    public ReportRepository reportRepository;
    public UserRepository userRepository;
    public PostRepository postRepository;
    public CurrentUserService currentUserService;
  
    public Report CreateReport(Long reporterId, ReportRequest req) {
       
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new InvalidReportException("reporter not found"));
        User reportedUser = null;
        Post reportedPost = null;

        ReportStatus reportStatus = ReportStatus.PENDING;
        if (req.getReportedUserId() != null) {

            reportedUser = userRepository.findById(req.getReportedUserId())
                    .orElseThrow(() -> new EntityNotFoundException("reported user not found"));

            if (reporter.getId().equals(reportedUser.getId())) {
                throw new InvalidReportException("you can't report your self!");
            }

            /// prevent susers form deleting an admin
            // if (reportedUser.getRole() == Role.ADMIN) {
            //     throw new SecurityException("you can't report an admin");
            // }
        }

        if (req.getReportedPostId() != null) {
            User currentUser = currentUserService.getLoggedUser();

            reportedPost = postRepository.findById(req.getReportedPostId())
                    .orElseThrow(() -> new EntityNotFoundException("reported post not found"));

            if (reportedPost.isHided() && currentUser.getRole() == Role.USER) {
                throw new EntityNotFoundException("post not found"); 
            }
            if (reportedPost.getAuthor().getUsername().equals(currentUser.getUsername())) {
                throw new InvalidReportException("you can't report your post delete it if you want!");
            }
        }

        if (reportedUser == null && reportedPost == null) {
            throw new InvalidReportException("nothing reported");
        }
        

        Report report = new Report();

        report.setReporter(reporter);
        report.setStatus(reportStatus);
        report.setReportedUser(reportedUser);
        report.setReportedPost(reportedPost);
        report.setReason(req.getReason());

        return reportRepository.save(report);
    }

}
