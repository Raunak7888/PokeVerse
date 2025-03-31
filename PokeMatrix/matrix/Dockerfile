# Use OpenJDK 21 slim image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file (Ensure the JAR is built before this step)
COPY target/matrix-0.0.1-SNAPSHOT.jar matrix-service.jar

# Expose port (Dynamic via ENV variable)
EXPOSE 8083

# Run Matrix Service
CMD ["java", "-jar", "matrix-service.jar"]
