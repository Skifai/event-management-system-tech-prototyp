#!/bin/bash

# Build and run script for the Event Management System

set -e

echo "========================================"
echo "Building Event Management System"
echo "========================================"

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed"
    exit 1
fi

# Check if Docker Compose is available
if ! docker compose version &> /dev/null; then
    echo "Error: Docker Compose is not available"
    exit 1
fi

echo "Step 1: Building application JAR..."
# Check if mvnw exists and use it, otherwise fall back to mvn
if [ -f "./mvnw" ]; then
    ./mvnw clean package -DskipTests
else
    echo "Maven wrapper not found, trying mvn..."
    mvn clean package -DskipTests
fi

# Verify JAR was created
if ! ls target/*.jar 1> /dev/null 2>&1; then
    echo "Error: JAR file not found in target/"
    exit 1
fi

echo "Step 2: Building Docker images..."
docker compose build

echo "Step 3: Starting containers..."
docker compose up -d

echo ""
echo "========================================"
echo "Event Management System is starting!"
echo "========================================"
echo ""
echo "Application will be available at: http://localhost:8080"
echo "Database is available at: localhost:5432"
echo ""
echo "To view logs: docker compose logs -f"
echo "To stop: docker compose down"
echo "To stop and remove data: docker compose down -v"
echo ""
