# Stage 1: Build
FROM gradle:8.5-jdk17 AS builder
WORKDIR /build

# Copy Gradle files
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Copy source code
COPY src ./src

# Build the application
RUN gradle bootJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy JAR from builder stage
COPY --from=builder /build/build/libs/*.jar app.jar

# Create storage directory and set ownership
RUN mkdir -p /app/storage && chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
