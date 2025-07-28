# Multi-stage build for smaller image size
FROM maven:3.9.4-openjdk-17-slim AS build

WORKDIR /app

# Copy POM file and download dependencies (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Create directories
RUN mkdir -p /app /var/log/eureka-server
RUN chown -R appuser:appuser /app /var/log/eureka-server

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar eureka-server.jar

# Change ownership
RUN chown appuser:appuser eureka-server.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8761/actuator/health || exit 1

# JVM options for production
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar eureka-server.jar"]