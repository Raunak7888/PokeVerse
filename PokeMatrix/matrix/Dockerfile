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

# Expose the port (Matrix Service)
EXPOSE ${MATRIX_PORT}

# Run the Matrix Service with dynamic environment variables
ENTRYPOINT ["java", "-jar", "app.jar"]
