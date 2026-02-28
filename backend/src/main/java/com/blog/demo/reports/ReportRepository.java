package com.blog.demo.reports;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select COUNT(DISTINCT r.reportedPost) from Report r  where r.reportedPost is not null ")
    Long countReportedPosts();

    @Query("select count(distinct r.reportedUser) from Report r where r.reportedUser is not null ")
    Long countReportedUser();
    // @Query("select new com.blog.demo.dashboard.DashboardReportDto(id, reason,
    // reportedUser.id, reportedUser.username, reportedPost.id, timestamp) from
    // Report r where reportedUser is not null")

    @Query("select r from Report r where r.reportedUser is not null")
    List<Report> getReportedUsers();

    /// --- POSTS

    // @Query("select r from Report r where r.reportedPost is not null AND r.status
    // = 'PENDING'")
    // List<Report> getPendingReportedPosts();

    // @Query("select r from Report r where r.reportedPost is not null AND r.status
    // = 'RESOLVED'")
    // List<Report> getResolvedReportedPosts();

    // @Query("select r from Report r where r.reportedPost is not null AND r.status
    // = 'REOPENED'")
    // List<Report> getReopenedReportedPosts();

    // @Query("select r from Report r where r.reportedPost is not null AND r.status
    // = 'DISMISSED'")
    // List<Report> getDismissedReportedPosts();

    @Query("select r from Report r where r.reportedPost is not null AND r.status = :status")
    List<Report> getReportedPostsByStatus(ReportStatus status);

    @Modifying
    @Query("update Report r SET r.status = :newStatus where r.reportedUser.id = :userId and r.status = 'PENDING'")
    int resolvePendingUserReports(Long userId, ReportStatus newStatus);

    // POSTS
    @Modifying
    @Query("update Report r SET r.status = :newStatus where r.reportedPost.id = :postId AND r.status IN :oldStatus")
    int resolvePendingPostReports(Long postId, ReportStatus newStatus, Set<ReportStatus> oldStatus);

    @Modifying
    @Query("update Report r set r.status = :newStatus where r.reportedPost.id = :postId AND r.status = 'RESOLVED'")
    int reopenResolvedPostReports(Long postId, ReportStatus newStatus);

    @Modifying
    @Query("update Report r set r.status = 'DISMISSED' where r.reportedPost.id = :postId AND r.status IN :oldStatus")
    int dismissPostReport(Long postId, Set<ReportStatus> oldStatus);

    /// --- USER
    @Query("select r from Report r where r.reportedUser is not null AND r.status = :status")
    List<Report> getReportedUsersByStatus(ReportStatus status);

    @Modifying
    @Query("update Report r set r.status = 'RESOLVED' where r.reportedUser.id = :userId AND r.status IN :oldStatus")
    int banUser(Long userId, Set<ReportStatus> oldStatus);

    @Modifying
    @Query("update Report r set r.status = :newStatus where r.reportedUser.id = :userId AND r.status = 'RESOLVED'")
    int reopenResolvedUserReports(Long userId, ReportStatus newStatus);

    @Modifying
    @Query("update Report r set r.status = 'DISMISSED' where r.reportedUser.id = :userId AND r.status IN :oldStatus")
    int dismissUserReport(Long userId, Set<ReportStatus> oldStatus);

    @Modifying
    @Query("delete from Report r where r.reportedPost.id = :postId")
    void deleteReportByPostId(Long postId);

    @Modifying
    @Query("delete from Report r where r.reportedUser.id = :userId")
    void deleteReportByUserId(Long userId);

    
}
