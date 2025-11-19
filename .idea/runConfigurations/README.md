# IntelliJ IDEA Run Configurations

This directory contains IntelliJ IDEA run configurations for easy development and production deployment.

**Note**: These run configurations are pre-configured and work immediately after opening the project in IntelliJ IDEA. No additional setup is required - just select a configuration from the dropdown and click Run.

## Available Run Configurations

### 1. Development Mode ⭐ (Recommended for Daily Development)

**Purpose**: Run the application locally with automatic PostgreSQL Docker container management.

**Type**: Spring Boot Application run configuration

**Profile**: `dev`

**What it does**:
- Runs the Spring Boot application with the `dev` profile
- Automatically checks if PostgreSQL Docker container is running
- Creates and starts PostgreSQL container if not found (via DatabaseStartupListener)
- Uses development database on port 5432
- Enables hot-reload with Spring Boot DevTools
- Shows SQL logs for debugging
- Auto-opens browser at http://localhost:8080

**Requirements**:
- IntelliJ IDEA (Community or Ultimate Edition)
- Docker Desktop or Docker Engine must be installed and running
- Java 21 or higher

**Usage**:
1. Ensure Docker Desktop is running
2. Select "Development Mode" from the run configurations dropdown (top-right in IDEA)
3. Click the Run button (▶️) or press `Shift+F10`
4. Wait for the application to start
5. Browser will automatically open at http://localhost:8080

**What happens automatically**:
- DatabaseStartupListener checks for PostgreSQL container named `event-management-db-container`
- If container doesn't exist: Creates new container with postgres:17-alpine image
- If container exists but is stopped: Starts the container
- If container is already running: Uses it
- Application connects to database at `localhost:5432/eventmanagement`
- Test data is automatically loaded (configurable via `app.testdata.enabled=true`)

**Configuration (application-dev.properties)**:
```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/eventmanagement
vaadin.productionMode=false
vaadin.launch-browser=true
logging.level.ch.flossrennen.eventmanagement=DEBUG
spring.jpa.hibernate.ddl-auto=update
app.testdata.enabled=true
```

---

### 2. Production Mode (Local) 🟢 (For Testing Production Settings Locally)

**Purpose**: Run the application locally with production settings, without Docker.

**Type**: Spring Boot Application run configuration

**Profile**: `prod`

**What it does**:
- Runs the Spring Boot application with the `prod` profile
- Uses production settings (minimal logging, production Vaadin mode, etc.)
- Runs on port 8081 (different from development)
- Connects to production database on port 5433
- **Note**: DatabaseStartupListener is NOT active in production mode
- You must manually start the production PostgreSQL database

**Requirements**:
- IntelliJ IDEA
- Java 21 or higher
- PostgreSQL database running on port 5433 (see Manual Database Setup below)

**Usage**:
1. **First, manually start production PostgreSQL**:
   ```bash
   docker run -d \
     --name event-management-db-prod-container \
     -e POSTGRES_DB=eventmanagement_prod \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -p 5433:5432 \
     postgres:17-alpine
   ```
   
2. Select "Production Mode (Local)" from run configurations dropdown
3. Click Run (▶️) or press `Shift+F10`
4. Access application at http://localhost:8081

**Configuration (application-prod.properties)**:
```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5433/eventmanagement_prod
vaadin.productionMode=true
vaadin.launch-browser=false
logging.level.ch.flossrennen.eventmanagement=INFO
spring.jpa.hibernate.ddl-auto=validate
app.testdata.enabled=false
```

**Key Differences from Development**:
- Port 8081 (vs 8080 in dev)
- Database port 5433 (vs 5432 in dev)
- Production Vaadin mode (optimized, no hot-reload)
- Minimal logging
- Schema validation only (no automatic updates)
- No test data loaded by default
- No automatic Docker management

---

### 3. Production Mode (Docker) 🐳 (For Full Production Deployment Testing)

**Purpose**: Build and run the complete production stack with GraalVM native compilation using Docker Compose.

**Type**: Shell Script run configuration

**What it does**:
- Builds GraalVM native image using Docker (optimized for production)
- Starts separate production PostgreSQL on port 5433
- Starts application container with native binary
- Uses docker-compose.prod.yml configuration

**Requirements**:
- Docker Desktop or Docker Engine running
- At least 4GB RAM allocated to Docker
- At least 5GB free disk space

**Usage**:
1. Ensure Docker Desktop is running with sufficient resources
2. Select "Production Mode (Docker)" from run configurations dropdown
3. Click Run (▶️)
4. **Wait 5-15 minutes** for first build (subsequent builds are faster due to Docker cache)
5. Access application at http://localhost:8081

**What happens**:
- Runs: `docker compose -f docker-compose.prod.yml up --build -d`
- Builds GraalVM native image (slow but creates highly optimized binary)
- Creates production PostgreSQL container on port 5433
- Creates application container with native binary
- Both containers use Docker volumes for data persistence

**To stop**:
Use the "Stop Production Mode" run configuration or run manually:
```bash
docker compose -f docker-compose.prod.yml down
```

**Important Notes**:
- ⚠️ First build takes 5-15 minutes (native compilation is slow)
- ✅ Subsequent builds are faster with Docker caching
- ✅ Native binary starts much faster than JVM (< 1 second vs 3-5 seconds)
- ✅ Uses less memory than JVM version
- ⚠️ Separate database from development (port 5433)
- ✅ Database data persists in Docker volume `db_data_prod`

**View logs**:
```bash
docker compose -f docker-compose.prod.yml logs -f
```

---

### 4. Build Production JAR 📦

**Purpose**: Build production-ready JAR file with Vaadin frontend compiled and optimized.

**Type**: Maven run configuration

**Maven Profile**: `production`

**What it does**:
- Activates Maven `production` profile
- Compiles and optimizes Vaadin frontend
- Creates production JAR in `target/` directory
- JAR can be deployed independently without Docker

**Usage**:
1. Select "Build Production JAR" from run configurations dropdown
2. Click Run (▶️)
3. Wait for build to complete
4. JAR file created: `target/event-management-system-0.0.1-SNAPSHOT.jar`

**To run the JAR**:
```bash
java -jar -Dspring.profiles.active=prod target/event-management-system-0.0.1-SNAPSHOT.jar
```

**Use cases**:
- Creating artifacts for manual deployment
- Deploying to cloud platforms (Heroku, AWS, etc.)
- Running on servers without Docker
- CI/CD pipeline artifacts

---

### 5. Stop Production Mode 🛑

**Purpose**: Stop and remove production Docker containers.

**Type**: Shell Script run configuration

**What it does**:
- Runs: `docker compose -f docker-compose.prod.yml down`
- Stops production application container
- Stops production PostgreSQL container
- Removes containers (but preserves data in Docker volumes)

**Usage**:
1. Select "Stop Production Mode" from run configurations dropdown
2. Click Run (▶️)

**Note**: Database data persists in Docker volume and will be reused when you start production mode again.

---

## Quick Reference Table

| Configuration | Profile | Port | Database | Auto-Start DB | Use Case |
|--------------|---------|------|----------|---------------|----------|
| **Development Mode** | `dev` | 8080 | localhost:5432 | ✅ Yes | Daily development |
| **Production (Local)** | `prod` | 8081 | localhost:5433 | ❌ No (manual) | Test prod settings locally |
| **Production (Docker)** | `prod` | 8081 | Container:5433 | ✅ Yes (Docker) | Full production stack |
| **Build JAR** | N/A | N/A | N/A | N/A | Create deployable artifact |
| **Stop Production** | N/A | N/A | N/A | N/A | Clean up Docker containers |

---

## Database Ports Summary

- **Development**: PostgreSQL on `localhost:5432` → Database: `eventmanagement`
- **Production**: PostgreSQL on `localhost:5433` → Database: `eventmanagement_prod`

Both can run simultaneously without conflicts!

---

## How Spring Profiles Work

Spring profiles allow you to have different configurations for different environments. In this project:

### Profile: `dev` (Development)
- Used by: "Development Mode" run configuration
- Properties file: `application-dev.properties`
- Characteristics: Hot-reload, verbose logging, auto DB management, test data

### Profile: `prod` (Production)
- Used by: "Production Mode (Local)" and "Production Mode (Docker)"
- Properties file: `application-prod.properties`
- Characteristics: Optimized, minimal logging, manual DB management, no test data

### Default (No Profile)
- Properties file: `application.properties`
- Falls back to development-like settings
- Used when no profile is explicitly set

**How profiles are activated in run configurations**:
- Spring Boot Application configs: Use `Active profiles` field (e.g., `dev` or `prod`)
- The profile determines which `application-{profile}.properties` file is loaded
- Properties in profile-specific files override `application.properties`

---

## Troubleshooting

### Development Mode Issues

**Problem**: "Cannot connect to database"
- **Solution**: Ensure Docker Desktop is running
- **Check**: Run `docker ps` to verify container status
- **Manual start**: `docker start event-management-db-container`
- **Logs**: `docker logs event-management-db-container`

**Problem**: "Port 5432 already in use"
- **Cause**: Another PostgreSQL service or container is using port 5432
- **Solution 1**: Stop other PostgreSQL: `sudo service postgresql stop` (Linux/Mac)
- **Solution 2**: Stop conflicting container: `docker ps` → `docker stop <container-id>`
- **Solution 3**: Change dev database port in `application-dev.properties`

**Problem**: "Docker is not available"
- **Solution**: Start Docker Desktop
- **Verify**: Run `docker ps` in terminal
- **Note**: Application will start anyway but expects external PostgreSQL on port 5432

### Production Mode (Local) Issues

**Problem**: "Cannot connect to database"
- **Cause**: Production database not running on port 5433
- **Solution**: Start production PostgreSQL manually (see Production Mode (Local) section above)
- **Verify**: `docker ps | grep event-management-db-prod`

**Problem**: "Port 8081 already in use"
- **Solution**: Stop Production Mode (Docker) if running
- **Or**: Change `server.port` in `application-prod.properties`

### Production Mode (Docker) Issues

**Problem**: "Native image build fails"
- **Cause**: Insufficient Docker resources
- **Solution**: Docker Desktop → Settings → Resources → Increase Memory to 4GB+
- **Check**: Ensure 5GB+ free disk space

**Problem**: "Container name already in use"
- **Solution**: Run "Stop Production Mode" configuration
- **Or**: `docker compose -f docker-compose.prod.yml down`

**Problem**: Build is very slow (>15 minutes)
- **Note**: This is normal for first GraalVM native build
- **Tip**: Subsequent builds are much faster due to Docker layer caching
- **Alternative**: Use "Production Mode (Local)" for faster testing

---

## Manual Database Management

### Development Database Commands

```bash
# Start container
docker start event-management-db-container

# Stop container
docker stop event-management-db-container

# View logs
docker logs -f event-management-db-container

# Connect to database
docker exec -it event-management-db-container psql -U postgres -d eventmanagement

# Backup database
docker exec event-management-db-container pg_dump -U postgres eventmanagement > backup.sql

# Restore database
cat backup.sql | docker exec -i event-management-db-container psql -U postgres -d eventmanagement

# Reset database (delete all data)
docker exec event-management-db-container psql -U postgres -c "DROP DATABASE eventmanagement;"
docker exec event-management-db-container psql -U postgres -c "CREATE DATABASE eventmanagement;"
# Then restart application to recreate schema
```

### Production Database Commands

```bash
# Start production PostgreSQL manually (if not using Docker mode)
docker run -d \
  --name event-management-db-prod-container \
  -e POSTGRES_DB=eventmanagement_prod \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  -v db_data_prod:/var/lib/postgresql/data \
  postgres:17-alpine

# Connect to production database
docker exec -it event-management-db-prod-container psql -U postgres -d eventmanagement_prod

# View logs
docker logs -f event-management-db-prod-container

# Stop production database
docker stop event-management-db-prod-container
```

---

## Environment Variables

You can customize database credentials without modifying properties files:

### In Run Configuration

1. Edit run configuration
2. Add Environment Variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_JPA_HIBERNATE_DDL_AUTO`

### Example:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/my_custom_db
SPRING_DATASOURCE_USERNAME=myuser
SPRING_DATASOURCE_PASSWORD=mypassword
```

---

## Best Practices

### For Daily Development
1. ✅ Use **Development Mode** run configuration
2. ✅ Keep Docker Desktop running in background
3. ✅ Let DatabaseStartupListener handle PostgreSQL automatically
4. ✅ Use browser DevTools for frontend debugging
5. ✅ Check console logs for SQL queries and application logs

### For Testing Production Settings
1. ✅ Use **Production Mode (Local)** for quick testing
2. ✅ Manually start production PostgreSQL on port 5433
3. ✅ Test with production-like settings without long build times
4. ✅ Verify application behavior with minimal logging

### For Full Production Simulation
1. ✅ Use **Production Mode (Docker)** when you need the complete stack
2. ⚠️ Be patient during first build (5-15 minutes)
3. ✅ Test Docker deployment process
4. ✅ Verify native binary performance
5. ✅ Use "Stop Production Mode" when done

### What NOT to Do
1. ❌ Don't run both Development and Production (Docker) simultaneously on same machine
2. ❌ Don't use production profile for daily development (too slow)
3. ❌ Don't modify run configuration XMLs directly (use IDEA UI)
4. ❌ Don't commit database credentials to version control
5. ❌ Don't use development database for production testing

---

## Next Steps After Starting

After successfully starting the application:

1. **Access the application**: 
   - Development: http://localhost:8080
   - Production: http://localhost:8081

2. **Check application health**:
   - Health endpoint: http://localhost:8080/actuator/health
   - Info endpoint: http://localhost:8080/actuator/info

3. **View logs**:
   - Development: In IntelliJ IDEA console (verbose)
   - Production (Docker): `docker compose -f docker-compose.prod.yml logs -f`

4. **Access database** (if needed):
   - Use commands from "Manual Database Management" section above
   - Or use database client (DBeaver, pgAdmin, etc.)

5. **Test the application**:
   - Navigate through the Vaadin UI
   - Test CRUD operations
   - Verify test data is loaded (development mode)

---

## Recent Improvements (November 2024)

### Changed from Maven to Spring Boot Application Configurations

**What changed**:
- Development Mode now uses native Spring Boot Application run configuration
- Uses IDEA's `Active profiles` field instead of environment variables
- Better integration with IntelliJ IDEA Spring Boot tools
- Profile activation is more reliable and visible in IDEA

**Why this is better**:
- ✅ Native IDEA integration (Spring Boot dashboard, beans view, etc.)
- ✅ Profile selection visible in UI
- ✅ Better debugging experience
- ✅ Faster startup (no Maven overhead)
- ✅ Consistent with IntelliJ IDEA best practices

**Migration from old approach**:
If you were using the old Maven-based configurations:
1. Delete old run configurations from IDEA
2. Pull latest changes from repository
3. Reopen project in IDEA
4. New run configurations will appear automatically

---

## Additional Resources

- **Main Documentation**: [../README.md](../README.md)
- **IDEA Setup Guide**: [../IDEA_SETUP.md](../IDEA_SETUP.md)
- **Docker Documentation**: [../DOCKER.md](../DOCKER.md)
- **CI/CD Pipeline**: [../CI-CD.md](../CI-CD.md)

---

## Support

If you encounter issues:

1. Check this README troubleshooting section
2. Verify prerequisites (Java 21, Docker running)
3. Check container status: `docker ps -a`
4. Check application logs in IDEA console
5. Check Docker logs: `docker logs <container-name>`

For additional help, create an issue on GitHub with:
- Error message or problem description
- Run configuration name you're trying to use
- Output from: `docker --version`, `java -version`, `docker ps`
- Operating system and version
- IntelliJ IDEA version
