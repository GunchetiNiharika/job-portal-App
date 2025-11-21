package com.jobportal.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
   /* private final Path uploadDir = Paths.get("uploads");

    public FileStorageService() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public String storeFile(MultipartFile file) {
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try {
            Path path = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return filename;  // only filename, not path
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    public Path getFilePath(String filename) {
        return uploadDir.resolve(filename);
    }*/
  @Autowired
   private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public FileStorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    // Upload file to S3
    public String storeFile(MultipartFile file) {
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
        return key;
    }

    // Download file from S3
    public S3ObjectInputStream getFile(String key) {
        S3Object object = amazonS3.getObject(bucketName, key);
        return object.getObjectContent();
    }
}

