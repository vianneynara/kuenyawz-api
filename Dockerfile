# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS builder
LABEL authors="vianneynara"

# Set the working directory
WORKDIR /build

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# This might not work and not readable by the spring boot
# Instead, use compose to define the environment variables.
COPY .env ./.env

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Get runtime image
# https://github.com/GoogleContainerTools/distroless/tree/main/java
FROM gcr.io/distroless/java21-debian12:nonroot

# Set the working directory
WORKDIR /app

# Copy the application JAR from the builder stage to /app/
COPY --from=builder /build/target/kuenyawz-api-1.0.0.jar /app/kuenyawz-api.jar

# Create a volume for uploads (product-images)
VOLUME /app/uploads

# Define and set default environment variables
ENV SERVER_PORT=8081

# Expose the internal application port
EXPOSE ${SERVER_PORT}

# Add a healthcheck for the container by calling the actuator
HEALTHCHECK --interval=30s --timeout=10s --retries=3 CMD curl --fail http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/kuenyawz-api.jar"]
