package com.jobportal.s3Config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    // Only the region and bucket are required
    @Value("${aws.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        // The SDK will automatically use the EC2 IAM role if no explicit credentials are provided
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .build();
    }
}
