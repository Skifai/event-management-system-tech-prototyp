# Verification Guide for DatabaseStartupListener Fix

This guide explains how to verify that the IntelliJ IDEA run configuration fix is working correctly.

## Prerequisites

1. **Java 21** - The project requires Java 21 or higher
2. **Docker** - Docker Desktop or Docker Engine must be installed and running
3. **IntelliJ IDEA** - Community or Ultimate Edition
4. **Maven** - For building the project

## What Was Fixed

The `DatabaseStartupListener` and `DockerService` were updated to:
- Remove `@Slf4j` Lombok annotation (doesn't work before Spring context initialization)
- Remove `@Component` annotation (not needed for EnvironmentPostProcessor)
- Use direct `LoggerFactory.getLogger()` for logging

## How to Verify the Fix

### Step 1: Ensure Prerequisites

```bash
# Check Java version (should be 21 or higher)
java -version

# Check Docker is running
docker ps

# Check Docker version
docker --version
```

### Step 2: Clean Docker State

```bash
# Stop and remove any existing PostgreSQL container
docker stop event-management-db-container 2>/dev/null || true
docker rm event-management-db-container 2>/dev/null || true

# Verify no container exists
docker ps -a | grep event-management-db
```

### Step 3: Open Project in IntelliJ IDEA

1. Open IntelliJ IDEA
2. Open the project directory
3. Wait for IDEA to index the project
4. Ensure Java 21 SDK is configured:
   - File → Project Structure → Project
   - SDK: Java 21
   - Language Level: 21

### Step 4: Run Development Mode

1. In IDEA, locate the run configuration dropdown (top right)
2. Select **"Development Mode"**
3. Click the **Run** button (green play icon) or press `Shift+F10`

### Step 5: Watch the Logs

You should see the following log messages indicating the fix is working:

```
INFO  - Checking PostgreSQL availability before application startup...
INFO  - Docker client initialized successfully
INFO  - PostgreSQL container does not exist. Creating and starting it...
INFO  - Pulling PostgreSQL image: postgres:17-alpine
INFO  - Creating PostgreSQL container: event-management-db-container
INFO  - Starting PostgreSQL container
INFO  - PostgreSQL container created and started successfully
INFO  - PostgreSQL is ready for connections at localhost:5432
```

### Step 6: Verify Database Connection

After the application starts, you should see:

```
INFO  - HikariPool-1 - Starting...
INFO  - HikariPool-1 - Start completed.
```

And the application should be accessible at `http://localhost:8080`

### Step 7: Verify Docker Container

```bash
# Check that the PostgreSQL container is running
docker ps

# You should see:
# CONTAINER ID   IMAGE                COMMAND                  STATUS          PORTS                    NAMES
# <container_id> postgres:17-alpine   "docker-entrypoint.s…"   Up X seconds    0.0.0.0:5432->5432/tcp   event-management-db-container

# Check container logs
docker logs event-management-db-container

# Should show PostgreSQL startup messages
```

## Expected Behavior

### First Run (No Container Exists)
- DatabaseStartupListener detects no container exists
- Creates and starts new PostgreSQL container
- Waits 5 seconds for container to be ready
- Application connects successfully

### Subsequent Runs (Container Exists and Running)
- DatabaseStartupListener detects container is already running
- Skips container creation
- Application connects successfully

### Container Exists but Stopped
- DatabaseStartupListener detects stopped container
- Starts the existing container
- Waits 3 seconds for container to be ready
- Application connects successfully

## Troubleshooting

### Issue: "Docker is not available"

**Solution:**
```bash
# Start Docker Desktop or Docker Engine
# On macOS: Open Docker Desktop application
# On Linux: sudo systemctl start docker
# Verify: docker ps
```

### Issue: "Port 5432 already in use"

**Solution:**
```bash
# Find what's using port 5432
lsof -i :5432

# If it's another PostgreSQL instance, stop it:
# On macOS: brew services stop postgresql
# On Linux: sudo systemctl stop postgresql

# Or kill the process:
kill -9 <PID>
```

### Issue: "Release version 21 not supported"

**Solution:**
- Install Java 21:
  - Via IDEA: File → Project Structure → Project → SDK → Add SDK → Download JDK → Select version 21
  - Via SDKMAN: `sdk install java 21.0.1-tem`
  - Via Homebrew (macOS): `brew install openjdk@21`

### Issue: Application starts but shows connection errors

**Solution:**
```bash
# Wait for PostgreSQL to fully initialize (can take 5-10 seconds)
# Check if container is healthy:
docker logs event-management-db-container

# If container is having issues, restart it:
docker restart event-management-db-container

# Or remove and let it be recreated:
docker stop event-management-db-container
docker rm event-management-db-container
# Then restart the application
```

## Testing Different Scenarios

### Test 1: Auto-Create Container
```bash
# Ensure no container exists
docker rm -f event-management-db-container 2>/dev/null || true

# Run application
# Expected: Container should be created automatically
```

### Test 2: Auto-Start Stopped Container
```bash
# Create and stop container
docker run -d --name event-management-db-container postgres:17-alpine
docker stop event-management-db-container

# Run application
# Expected: Container should be started automatically
```

### Test 3: Use Existing Running Container
```bash
# Ensure container is running
docker start event-management-db-container 2>/dev/null || \
  docker run -d --name event-management-db-container \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=eventmanagement \
  -p 5432:5432 \
  postgres:17-alpine

# Run application
# Expected: Should detect running container and use it
```

### Test 4: Production Mode (Should NOT Auto-Start)
```bash
# Remove container
docker rm -f event-management-db-container 2>/dev/null || true

# Run with production profile
# In run configuration, set: SPRING_PROFILES_ACTIVE=prod
# Expected: Should skip Docker auto-start and warn about external PostgreSQL
```

## Success Criteria

✅ Application starts without database connection errors  
✅ PostgreSQL container is created/started automatically  
✅ Logs show clear messages about container management  
✅ Application is accessible at http://localhost:8080  
✅ Container persists across application restarts  
✅ No errors in Spring Boot logs related to database  

## Additional Resources

- [IDEA_SETUP.md](IDEA_SETUP.md) - Complete IDEA setup guide
- [.idea/runConfigurations/README.md](.idea/runConfigurations/README.md) - Run configurations documentation
- [DOCKER.md](DOCKER.md) - Docker-specific documentation

## Need Help?

If you encounter issues not covered in this guide:

1. Check the troubleshooting section above
2. Review application logs in IDEA console
3. Check Docker container logs: `docker logs event-management-db-container`
4. Verify Docker is running: `docker ps`
5. Check Docker daemon logs: `docker system info`

## Clean Up

To remove all containers and start fresh:

```bash
# Stop and remove development container
docker stop event-management-db-container 2>/dev/null || true
docker rm event-management-db-container 2>/dev/null || true

# Remove Docker volumes (WARNING: This deletes all data!)
docker volume rm event-management-system-tech-prototyp_db_data 2>/dev/null || true

# Clean Docker system (optional)
docker system prune -f
```
