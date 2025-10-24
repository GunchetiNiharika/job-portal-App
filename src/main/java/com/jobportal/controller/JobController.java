package com.jobportal.controller;

import com.jobportal.entity.Job;
import com.jobportal.Repository.JobRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping
    public String listJobs(Model model) {
        model.addAttribute("jobs", jobRepository.findAll());
        return "jobs";
    }
    @GetMapping("/add")
    @PreAuthorize("hasRole('RECRUITER')")
    public String showJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "job_form";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('RECRUITER')")
    public String addJob(@ModelAttribute Job job) {
        jobRepository.save(job);
        return "redirect:/jobs";
    }
}
