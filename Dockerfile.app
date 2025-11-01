# Dockerfile for Event Management System Application
# This expects a pre-built JAR file in the target/ directory
# Build the JAR locally first with: ./mvnw clean package -DskipTests
# Or use the provided build-docker.sh script

FROM eclipse-temurin:21-jre
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the pre-built JAR file
COPY target/*.jar app.jar

# Create non-root user
RUN useradd -r -u 1001 spring && chown -R spring:spring /app
USER spring:spring

EXPOSE 8080

# Health check using actuator endpoint
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
