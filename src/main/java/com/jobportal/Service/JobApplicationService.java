package com.jobportal.Service;

import com.jobportal.Repository.JobApplicationRepository;
import com.jobportal.Repository.JobRepository;
import com.jobportal.Repository.UserRepository;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.User;
import com.jobportal.enums.ApplicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class JobApplicationService {

    private final UserRepository userRepo;
    private final JobRepository jobRepo;
    private final JobApplicationRepository appRepo;
    private final FileStorageService fileStorage;
    private final EmailService emailService;

    // Constructor-based dependency injection
    public JobApplicationService(UserRepository userRepo,
                                 JobRepository jobRepo,
                                 JobApplicationRepository appRepo,
                                 FileStorageService fileStorage,
                                 EmailService emailService) {
        this.userRepo = userRepo;
        this.jobRepo = jobRepo;
        this.appRepo = appRepo;
        this.fileStorage = fileStorage;
        this.emailService = emailService;
    }

    // Apply for a job with resume
    public void applyForJob(String username, Long jobId, MultipartFile resume) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setJob(job);

        if (resume != null && !resume.isEmpty()) {
            // Store file and save only the filename, not full path
            String filename = fileStorage.storeFile(resume);
            application.setResumePath(filename);
        }

        try {
            emailService.sendApplicationConfirmation(user.getUsername(), job.getTitle());
            application.setEmailConfirmed(true);
        } catch (Exception e) {
            application.setEmailConfirmed(false);
        }
        appRepo.save(application);
    }

    public List<JobApplication> getApplicationsForJob(Long jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));
        return appRepo.findByJob(job);
    }

    public List<JobApplication> getApplicationsByUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return appRepo.findByUser(user);
    }

    public void updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        JobApplication application = appRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID"));

        application.setStatus(status);
        appRepo.save(application);
    }

    /*public boolean hasUserApplied(String username, Long jobId) {
        return appRepo.existsByUserUsernameAndJobId(username, jobId);
    }*/

}
