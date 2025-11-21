package com.jobportal.Service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendApplicationConfirmation(String username, String jobTitle) {
        System.out.println("Email sent to " + username + " for job: " + jobTitle);
    }
}
