package com.jobportal.controller;

import com.jobportal.Repository.JobApplicationRepository;
import com.jobportal.Repository.JobRepository;
import com.jobportal.Repository.UserRepository;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/jobs")
public class UserJobController {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public UserJobController(JobRepository jobRepository,
                             JobApplicationRepository jobApplicationRepository,
                             UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listJobs(Model model,
                           @AuthenticationPrincipal UserDetails currentUser,
                           @RequestParam(value = "search", required = false) String search) {

        // 1️⃣ Fetch all jobs (filtered if search is provided)
        List<Job> jobs = (search != null && !search.isEmpty())
                ? jobRepository.searchJobs(search) // Custom search query in JobRepository
                : jobRepository.findAll();

        // 2️⃣ Fetch the User entity from username
        User userEntity = userRepository.findByUsername(currentUser.getUsername()).get();
        if (userEntity == null) {
            // Handle rare case: logged-in user not found in DB
            model.addAttribute("error", "User not found!");
            return "jobs";
        }

        // 3️⃣ Fetch all applications for this user
        List<JobApplication> userApplications = jobApplicationRepository.findByUser(userEntity);

        // 4️⃣ Collect applied job IDs
        List<Long> appliedJobIds = userApplications.stream()
                .map(app -> app.getJob().getId())
                .collect(Collectors.toList());

        // 5️⃣ Pass data to Thymeleaf template
        model.addAttribute("jobs", jobs);
        model.addAttribute("appliedJobIds", appliedJobIds); // for disabling Apply button
        model.addAttribute("search", search); // retain search keyword in UI

        return "jobs"; // Thymeleaf template
    }
}
