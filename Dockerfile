# Multi-stage build for Production
# Stage 1: Build application with Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy only pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies (cached layer if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application with production profile
RUN mvn clean package -Pproduction -DskipTests -B

# Stage 2: Create optimized runtime image
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Event Management System"
LABEL description="Flossrennen Event Management System - Production Container"

WORKDIR /app

# Install wget for healthcheck
RUN apk add --no-cache wget && \
    rm -rf /var/cache/apk/*

# Copy JAR from build stage
COPY --from=build /app/target/event-management-system-*.jar app.jar

# Create non-root user for security
RUN addgroup -S -g 1001 spring && \
    adduser -S -u 1001 -G spring spring && \
    chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# JVM optimization for container environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
