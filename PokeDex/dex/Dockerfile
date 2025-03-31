# Use OpenJDK 21 slim image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file (Ensure the JAR is built before this step)
COPY target/dex-0.0.1-SNAPSHOT.jar dex-service.jar

# Expose port (Dynamic via ENV variable)
EXPOSE 8084

# Run Dex Service
CMD ["java", "-jar", "dex-service.jar"]
