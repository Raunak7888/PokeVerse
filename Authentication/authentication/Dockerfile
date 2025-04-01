# Build Stage (Multi-Stage Build for Smaller Image)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy project files
COPY . .

# Build the project without running tests
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port (Authentication Service)
EXPOSE ${AUTH_PORT}

# Run the Authentication Service with dynamic environment variables
ENTRYPOINT ["java", "-jar", "app.jar"]
