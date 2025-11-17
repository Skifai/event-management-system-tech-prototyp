# IntelliJ IDEA Run Configurations

This directory contains IntelliJ IDEA run configurations for easy development and production deployment.

## Available Run Configurations

### 1. Development Mode
**Purpose**: Run the application locally with automatic PostgreSQL Docker container management.

**What it does**:
- Automatically checks if PostgreSQL Docker container is running
- Creates and starts PostgreSQL container if not found
- Uses development database on port 5432
- Enables hot-reload with Spring Boot DevTools
- Shows SQL logs for debugging

**Requirements**:
- Docker Desktop or Docker Engine must be installed and running
- Java 21 or higher

**Usage**:
1. Open IntelliJ IDEA
2. Select "Development Mode" from the run configurations dropdown
3. Click Run (or press Shift+F10)

The application will automatically:
- Check for PostgreSQL container named `event-management-db-container`
- Start it if stopped, or create it if it doesn't exist
- Connect to database at `localhost:5432`
- Start the Spring Boot application at `http://localhost:8080`

### 2. Production Mode (Native)
**Purpose**: Build and run the application in production mode with GraalVM native compilation.

**What it does**:
- Builds GraalVM native image using Docker
- Uses separate production database on port 5433
- Runs in production mode with optimized settings
- Deploys via docker-compose

**Requirements**:
- Docker Desktop or Docker Engine must be installed and running

**Usage**:
1. Open IntelliJ IDEA
2. Select "Production Mode (Native)" from the run configurations dropdown
3. Click Run (or press Shift+F10)

The application will:
- Build the native image (this may take several minutes on first run)
- Start production PostgreSQL container on port 5433
- Start the application container
- Make the application available at `http://localhost:8081`

**Important Notes**:
- First build may take 5-15 minutes for GraalVM native compilation
- Production database is separate from development database
- Uses `docker-compose.prod.yml` configuration
- Database data persists in Docker volume `db_data_prod`

### 3. Build Production JAR
**Purpose**: Build production-ready JAR with Vaadin frontend compiled.

**What it does**:
- Activates the `production` Maven profile
- Compiles and optimizes Vaadin frontend
- Creates production JAR in `target/` directory

**Usage**:
1. Select "Build Production JAR" from run configurations
2. Click Run

Output: `target/event-management-system-0.0.1-SNAPSHOT.jar`

### 4. Stop Production Mode
**Purpose**: Stop and remove production containers.

**Usage**:
1. Select "Stop Production Mode" from run configurations
2. Click Run

This will stop and remove:
- Production application container
- Production PostgreSQL container

Note: Database data is preserved in the Docker volume.

## Database Ports

- **Development**: PostgreSQL on `localhost:5432` (database: `eventmanagement`)
- **Production**: PostgreSQL on `localhost:5433` (database: `eventmanagement_prod`)

## Troubleshooting

### Development Mode Issues

**Problem**: PostgreSQL container fails to start
- **Solution**: Check if Docker is running: `docker ps`
- **Solution**: Check if port 5432 is already in use: `lsof -i :5432`
- **Solution**: Manually start container: `docker start event-management-db-container`

**Problem**: Application can't connect to database
- **Solution**: Wait a few seconds for PostgreSQL to be ready
- **Solution**: Check container logs: `docker logs event-management-db-container`

### Production Mode Issues

**Problem**: Native image build fails
- **Solution**: Ensure you have enough disk space (5GB+ free)
- **Solution**: Ensure Docker has enough memory allocated (4GB+ recommended)

**Problem**: Port 8081 or 5433 already in use
- **Solution**: Stop existing containers: `docker compose -f docker-compose.prod.yml down`
- **Solution**: Or modify ports in `docker-compose.prod.yml`

## Manual Commands

### Development Database
```bash
# Start development PostgreSQL
docker start event-management-db-container

# Stop development PostgreSQL
docker stop event-management-db-container

# View logs
docker logs -f event-management-db-container

# Connect to database
docker exec -it event-management-db-container psql -U postgres -d eventmanagement
```

### Production Deployment
```bash
# Start production environment
docker compose -f docker-compose.prod.yml up --build -d

# View logs
docker compose -f docker-compose.prod.yml logs -f

# Stop production environment
docker compose -f docker-compose.prod.yml down

# Connect to production database
docker exec -it event-management-db-prod-container psql -U postgres -d eventmanagement_prod
```

## Environment Variables

You can customize database credentials by setting environment variables:

**Development** (in run configuration or `.env` file):
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

**Production** (in `docker-compose.prod.yml` or `.env` file):
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `POSTGRES_DB`

## Next Steps

After starting the application:
1. Open browser to `http://localhost:8080` (dev) or `http://localhost:8081` (prod)
2. Check application health: `/actuator/health`
3. View application logs in the IntelliJ IDEA console
