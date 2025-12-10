# Multi-stage build for Production with GraalVM Native Image
# Stage 1: Build application with Maven and GraalVM
FROM ghcr.io/graalvm/native-image-community:21 AS build

WORKDIR /app

# Install required build tools
RUN microdnf install -y findutils

# Copy only pom.xml first to leverage Docker cache
COPY pom.xml .

# Download dependencies (cached layer if pom.xml hasn't changed)
RUN ./mvnw dependency:go-offline -B || true

# Copy Maven wrapper if it exists
COPY .mvn .mvn
COPY mvnw .

# Copy source code
COPY src ./src

# Build native image with production profile
RUN ./mvnw -Pnative,production native:compile -DskipTests -B

# Stage 2: Create minimal runtime image
FROM alpine:latest

LABEL maintainer="Event Management System"
LABEL description="Flossrennen Event Management System - Production Container"

WORKDIR /app

# Install required runtime libraries and wget for healthcheck
RUN apk add --no-cache \
    gcompat \
    libstdc++ \
    wget \
    && rm -rf /var/cache/apk/*

# Copy native executable from build stage
COPY --from=build /app/target/event-management-system app

# Create non-root user for security
RUN addgroup -S -g 1001 spring && \
    adduser -S -u 1001 -G spring spring && \
    chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Run native application
ENTRYPOINT ["./app"]
