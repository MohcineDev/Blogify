package com.blog.demo.reports;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.demo.users.CurrentUserService;
import com.blog.demo.users.Role;
import com.blog.demo.users.User;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor

@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportRepository reportRepository;
    CurrentUserService currentUserService;

    @PostMapping
    public Report createReport(@Valid @RequestBody ReportRequest req) {
        User reporter = currentUserService.getLoggedUser();

        return reportService.CreateReport(reporter.getId(), req);
    }

    @GetMapping
    public List<Report> getReports(@AuthenticationPrincipal User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new SecurityException("Not allowed");
        }
        return reportRepository.findAll();
    }
}
