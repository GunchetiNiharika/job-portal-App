package com.jobportal.controller;

import com.jobportal.Repository.JobRepository;
import com.jobportal.Repository.UserRepository;
import com.jobportal.Service.JobApplicationService;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.User;
import com.jobportal.enums.ApplicationStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/recruiter")
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterController {

    private final JobRepository jobRepository;
    private final JobApplicationService jobApplicationService;
    private final UserRepository userRepository;

    public RecruiterController(JobRepository jobRepository,
                               JobApplicationService jobApplicationService,
                               UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.jobApplicationService = jobApplicationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User recruiter = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Job> postedJobs = jobRepository.findByPostedBy(recruiter);
        model.addAttribute("postedJobs", postedJobs);

        // Reuse existing job form template object
        model.addAttribute("jobForm", new Job());

        return "recruiter_dashboard";  // use this Thymeleaf template
    }

    @PostMapping("/add-job")
    public String addJob(@ModelAttribute("jobForm") Job job,
                         @AuthenticationPrincipal UserDetails currentUser) {

        User recruiter = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        job.setPostedBy(recruiter);
        jobRepository.save(job);

        return "redirect:/recruiter/dashboard"; // back to dashboard
    }

    @GetMapping("/jobs/{jobId}/applications")
    public String viewApplications(@PathVariable Long jobId, Model model) {
        List<JobApplication> applications = jobApplicationService.getApplicationsForJob(jobId);
        model.addAttribute("applications", applications);
        return "job_applications";
    }

    @PostMapping("/applications/{applicationId}/status")
    public String updateApplicationStatus(@PathVariable Long applicationId,
                                          @RequestParam ApplicationStatus status) {

        jobApplicationService.updateApplicationStatus(applicationId, status);

        // Redirect back to the application list for the job
        return "redirect:/recruiter/dashboard";
    }

}