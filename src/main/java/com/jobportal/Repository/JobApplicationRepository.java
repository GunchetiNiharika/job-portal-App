package com.jobportal.Repository;

import com.jobportal.entity.JobApplication;
import com.jobportal.entity.User;
import com.jobportal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUser(User user);
    List<JobApplication> findByJob(Job job);
}
