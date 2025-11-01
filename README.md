# Event Management System

A Spring Boot application for managing events, built with Vaadin UI framework and PostgreSQL database.

## Technologies

- Java 21
- Spring Boot 3.5.7
- Vaadin 24.9.4
- PostgreSQL
- Docker & Docker Compose

## Running with Docker (Recommended)

The easiest way to run the application is using Docker Compose, which will start both the application and the database.

### Quick Start

```bash
chmod +x build-and-run.sh
./build-and-run.sh
```

This script will build the application and start both the application and database containers.

**For detailed Docker instructions**, see [DOCKER.md](DOCKER.md) which includes:
- Complete setup guide
- Troubleshooting tips
- Development workflow
- Production considerations
- Data backup and restore

### Manual Docker Setup

If you prefer manual steps:

```bash
# Build the application
./mvnw clean package -DskipTests

# Start containers
docker compose up --build -d

# View logs
docker compose logs -f

# Stop containers
docker compose down
```

## Running Locally (without Docker)

### Prerequisites

- Java 21
- Maven 3.6+
- PostgreSQL database running on localhost:5432

### Steps

1. Start PostgreSQL and create a database named `eventmanagement`
2. Run the application:

```bash
./mvnw spring-boot:run
```

3. Access the application at http://localhost:8080

## Development

### Building the Application

```bash
./mvnw clean package
```

### Running Tests

```bash
./mvnw test
```

## Configuration

The application can be configured through environment variables:

- `SPRING_DATASOURCE_URL` - Database URL (default: jdbc:postgresql://localhost:5432/eventmanagement)
- `SPRING_DATASOURCE_USERNAME` - Database username (default: postgres)
- `SPRING_DATASOURCE_PASSWORD` - Database password (default: postgres)
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Hibernate DDL mode (default: update)

## Docker Images

The project includes multiple Dockerfiles for different purposes:

- `Dockerfile.app` - Multi-stage build for production (used by docker-compose)
- `Dockerfile` - GraalVM native image build (for optimal performance)
- `Dockerfile.dev` - Development image with debug support

## Health Check

The application exposes a health endpoint at:
- http://localhost:8080/actuator/health

## License

[Add your license information here]
