# Use Java 21 (matches your build)
FROM eclipse-temurin:21-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy the Spring Boot JAR into container
COPY target/jobportal-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Optional: set environment variables for placeholders
# These will be passed via Docker run or Docker build arguments
ENV DB_HOST=${DB_HOST}
ENV DB_PORT=${DB_PORT}
ENV DB_NAME=${DB_NAME}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV AWS_REGION=${AWS_REGION}
ENV S3_BUCKET=${S3_BUCKET}

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
