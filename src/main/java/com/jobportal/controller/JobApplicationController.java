package com.jobportal.controller;

import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.User;
import com.jobportal.Repository.JobApplicationRepository;
import com.jobportal.Repository.JobRepository;
import com.jobportal.Repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobApplicationController(JobApplicationRepository applicationRepository,
                                    JobRepository jobRepository,
                                    UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // ✅ Allow both USER and RECRUITER to apply
    @PostMapping("/apply/{jobId}")
    public String applyJob(@PathVariable Long jobId,
                           @AuthenticationPrincipal UserDetails currentUser,
                           RedirectAttributes redirectAttributes) {

        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow();
        Job job = jobRepository.findById(jobId).orElseThrow();

        // ✅ Check if already applied
        boolean alreadyApplied = applicationRepository.findByUser(user)
                .stream()
                .anyMatch(app -> app.getJob().getId().equals(jobId));

        if (alreadyApplied) {
            redirectAttributes.addFlashAttribute("error", "You have already applied for this job.");
            return "redirect:/jobs";
        }

        // ✅ Save new application
        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setJob(job);
        application.setStatus("Applied");
        applicationRepository.save(application);

        redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
        return "redirect:/jobs";
    }

    // ✅ View user job applications
    @GetMapping
    public String myApplications(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User user = userRepository.findByUsername(currentUser.getUsername()).orElseThrow();
        model.addAttribute("applications", applicationRepository.findByUser(user));
        return "my_applications"; // make sure this file exists under templates/
    }
}
