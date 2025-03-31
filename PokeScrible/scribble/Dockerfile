# Use OpenJDK 21 slim image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file (Ensure the JAR is built before this step)
COPY target/scribble-0.0.1-SNAPSHOT.jar scribble-service.jar

# Expose port (Dynamic via ENV variable)
EXPOSE 8085

# Run Scribble Service
CMD ["java", "-jar", "scribble-service.jar"]
