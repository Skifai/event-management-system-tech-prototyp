# Docker Setup Guide

This guide explains how to run the Event Management System using Docker containers.

## Overview

The application uses Docker Compose to orchestrate two services:

1. **PostgreSQL Database** (`postgresdb`) - PostgreSQL 17 Alpine
2. **Spring Boot Application** (`app`) - Event Management System

## Prerequisites

Before starting, ensure you have:

- Docker Engine (version 20.10 or higher)
- Docker Compose (v2 or higher)
- Java 21 (for building the application JAR)
- Maven 3.6+ or the Maven wrapper (`./mvnw`)

## Quick Start

### Option 1: Using the Helper Script (Recommended)

The easiest way to get started:

```bash
chmod +x build-and-run.sh
./build-and-run.sh
```

This script will:
1. Build the application JAR using Maven
2. Build the Docker images
3. Start both containers
4. Display the URLs and helpful commands

### Option 2: Manual Steps

If you prefer to run the commands manually:

```bash
# Step 1: Build the application JAR
./mvnw clean package -DskipTests

# Step 2: Build and start the containers
docker compose up --build -d

# Step 3: View logs (optional)
docker compose logs -f
```

## Accessing the Application

Once the containers are running:

- **Application**: http://localhost:8080
- **Database**: localhost:5432
  - Database name: `eventmanagement`
  - Username: `postgres`
  - Password: `postgres`
- **Health Check**: http://localhost:8080/actuator/health

## Container Management

### View Running Containers

```bash
docker compose ps
```

### View Logs

```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f app
docker compose logs -f postgresdb
```

### Stop Containers

```bash
# Stop but keep data
docker compose down

# Stop and remove all data
docker compose down -v
```

### Restart Containers

```bash
docker compose restart
```

### Rebuild After Code Changes

```bash
# Rebuild the JAR
./mvnw clean package -DskipTests

# Rebuild and restart the app container
docker compose up --build -d app
```

## Environment Variables

The application can be configured using environment variables in `docker-compose.yml`:

### Database Configuration

- `SPRING_DATASOURCE_URL`: JDBC connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

### JPA Configuration

- `SPRING_JPA_HIBERNATE_DDL_AUTO`: Schema management strategy (default: `update`)
  - `create`: Drop and create schema on startup
  - `update`: Update schema if needed
  - `validate`: Validate schema, make no changes
  - `none`: Do nothing

## Health Checks

Both containers include health checks:

### Database Health Check

- Command: `pg_isready -U postgres`
- Interval: 10 seconds
- Timeout: 5 seconds
- Retries: 5

### Application Health Check

- URL: http://localhost:8080/actuator/health
- Interval: 30 seconds
- Timeout: 10 seconds
- Start period: 60 seconds
- Retries: 5

## Troubleshooting

### Application won't start

1. Check if the JAR was built successfully:
   ```bash
   ls -lh target/*.jar
   ```

2. Check application logs:
   ```bash
   docker compose logs app
   ```

3. Verify the database is healthy:
   ```bash
   docker compose ps
   ```

### Database connection issues

1. Ensure the database container is healthy:
   ```bash
   docker compose ps postgresdb
   ```

2. Check database logs:
   ```bash
   docker compose logs postgresdb
   ```

3. Test database connectivity:
   ```bash
   docker compose exec postgresdb psql -U postgres -d eventmanagement -c "SELECT 1;"
   ```

### Port conflicts

If ports 8080 or 5432 are already in use, you can change them in `docker-compose.yml`:

```yaml
services:
  app:
    ports:
      - "8081:8080"  # Change 8081 to your preferred port
  
  postgresdb:
    ports:
      - "5433:5432"  # Change 5433 to your preferred port
```

### Rebuild everything from scratch

```bash
# Stop and remove everything
docker compose down -v

# Remove images
docker compose rm -f
docker image rm event-management-system-tech-prototyp-app

# Rebuild
./mvnw clean package -DskipTests
docker compose up --build -d
```

## Development Workflow

For local development:

1. Make code changes
2. Rebuild the JAR: `./mvnw clean package -DskipTests`
3. Restart the app container: `docker compose up --build -d app`
4. View logs: `docker compose logs -f app`

Alternatively, for faster development, you can run the application locally without Docker:

1. Start only the database: `docker compose up -d postgresdb`
2. Run the application locally: `./mvnw spring-boot:run`

## Data Persistence

Database data is stored in a Docker volume named `event-management-system-tech-prototyp_db_data`.

To back up the database:

```bash
docker compose exec postgresdb pg_dump -U postgres eventmanagement > backup.sql
```

To restore from backup:

```bash
cat backup.sql | docker compose exec -T postgresdb psql -U postgres eventmanagement
```

## Production Considerations

For production use, consider:

1. **Use secure passwords**: Change the default `postgres` password
2. **Use environment files**: Create a `.env` file for sensitive configuration
3. **Enable SSL**: Configure PostgreSQL to use SSL connections
4. **Resource limits**: Add memory and CPU limits to containers
5. **Logging**: Configure proper log rotation and retention
6. **Monitoring**: Set up monitoring and alerting
7. **Backups**: Implement automated database backups

Example production `.env` file:

```env
POSTGRES_USER=produser
POSTGRES_PASSWORD=securepassword123
POSTGRES_DB=eventmanagement
SPRING_DATASOURCE_URL=jdbc:postgresql://postgresdb:5432/eventmanagement
SPRING_DATASOURCE_USERNAME=produser
SPRING_DATASOURCE_PASSWORD=securepassword123
```

Then reference in `docker-compose.yml`:

```yaml
services:
  postgresdb:
    env_file: .env
```

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Boot Docker Documentation](https://spring.io/guides/gs/spring-boot-docker/)
