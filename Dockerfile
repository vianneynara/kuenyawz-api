# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS builder
LABEL authors="vianneynara"

# Set the working directory
WORKDIR /build

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Prepare uploads directory
FROM debian:stable-slim AS permissions
RUN mkdir -p /app/uploads
RUN chmod 777 /app/uploads

# Stage 3: Final distroless image
FROM gcr.io/distroless/java21-debian12:nonroot

# Set the working directory
WORKDIR /app

# Copy the application JAR from the builder stage
COPY --from=builder /build/target/kuenyawz-api-1.0.0.jar /app/kuenyawz-api.jar

# Copy the prepared uploads directory from the permissions stage
COPY --from=permissions /app/uploads /app/uploads

# Define and set default environment variables
ENV SERVER_PORT=8081

# Expose the internal application port
EXPOSE ${SERVER_PORT}

# Add a healthcheck for the container by calling the actuator
HEALTHCHECK --interval=30s --timeout=10s --retries=3 CMD curl --fail http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/kuenyawz-api.jar"]