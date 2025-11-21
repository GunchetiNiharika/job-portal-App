package com.jobportal.controller;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.jobportal.Service.JobApplicationService;
import com.jobportal.Service.FileStorageService;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.User;
import com.jobportal.Repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService applicationService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public JobApplicationController(JobApplicationService applicationService,
                                    UserRepository userRepository,
                                    FileStorageService fileStorageService) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/apply/{jobId}")
    @PreAuthorize("hasRole('USER')")
    public String applyJob(@PathVariable Long jobId,
                           @RequestParam("resume") MultipartFile resume,
                           @AuthenticationPrincipal UserDetails currentUser,
                           RedirectAttributes redirectAttributes) {

        // Check if the user has already applied
       /* boolean alreadyApplied = applicationService.hasUserApplied(currentUser.getUsername(), jobId);
        if (alreadyApplied) {
            redirectAttributes.addFlashAttribute("error", "You have already applied for this job!");
            return "redirect:/user/jobs";
        }*/

        // Apply for the job
        applicationService.applyForJob(currentUser.getUsername(), jobId, resume);
        redirectAttributes.addFlashAttribute("su" +
                "ccess", "Application submitted successfully!");
        return "redirect:/user/jobs";
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public String myApplications(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        List<JobApplication> applications = applicationService.getApplicationsByUser(currentUser.getUsername());
        model.addAttribute("applications", applications);
        return "my_applications";
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public String viewApplicationsForJob(@PathVariable Long jobId, Model model) {
        List<JobApplication> applications = applicationService.getApplicationsForJob(jobId);
        model.addAttribute("applications", applications);
        return "job_applications";
    }

   /* public ResponseEntity<Resource> serveResume(@PathVariable String filename) throws MalformedURLException {
        Path file = fileStorageService.getFilePath(filename);

        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }*/
    @GetMapping("/resume/{filename:.+}")
    @PreAuthorize("hasAnyRole('USER','RECRUITER')")
    @ResponseBody
    public ResponseEntity<byte[]> serveResume(@PathVariable String filename) {
        try (S3ObjectInputStream inputStream = fileStorageService.getFile(filename)) {
            byte[] content = inputStream.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(content);

        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }



}
