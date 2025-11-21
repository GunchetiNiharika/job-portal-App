Overview:
JobPortal is a web-based job management platform where users can browse jobs, register/login, apply with resumes, and admins can manage postings. Built with Spring Boot, Spring MVC, Thymeleaf, and AWS cloud services.

Technologies & Learning Highlights

Backend: Java 21, Spring Boot, Spring MVC, Spring Security

Frontend: Thymeleaf, HTML, CSS, JavaScript

Database: PostgreSQL (AWS RDS or local)

Cloud Storage: AWS S3

Build & Deployment: Maven, Docker, AWS EC2

Skills Gained:

Implemented MVC architecture with Spring MVC

Built secure login and authorization using Spring Security

Integrated Thymeleaf templates for dynamic web pages

Managed file uploads to AWS S3

Connected to RDS PostgreSQL database

Packaged and deployed applications using Maven, Docker, and EC2

Setup Instructions
1. Prerequisites

Install Java 21

Install Maven

(Optional) Install Docker

AWS account for RDS and S3

2. Configure Application

Update src/main/resources/application.properties:

spring.datasource.url=jdbc:postgresql://<RDS_ENDPOINT>:5432/<DB_NAME>
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.security.enabled=true

aws.region=<AWS_REGION>
aws.s3.bucket=<S3_BUCKET_NAME>

3. Build & Run Locally
# Navigate to project folder
mvn clean package -DskipTests

# Run the application
java -jar target/jobportal-0.0.1-SNAPSHOT.jar


Access locally: http://localhost:8080

4. Docker Deployment (Optional)

Create a Dockerfile:

# Use Java 21
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/jobportal-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]


Build and run:

docker build -t jobportal-app .
docker run -p 8080:8080 jobportal-app


Access via: http://localhost:8080

5. AWS Deployment (Optional)

Copy JAR to EC2 instance:

scp -i your-key.pem target/jobportal-0.0.1-SNAPSHOT.jar ec2-user@<EC2_PUBLIC_IP>:/home/ec2-user/


SSH into EC2:

ssh -i your-key.pem ec2-user@<EC2_PUBLIC_IP>


Run the app on EC2:

java -jar jobportal-0.0.1-SNAPSHOT.jar


Ensure RDS and S3 access from EC2

Access via: http://<EC2_PUBLIC_IP>:8080
