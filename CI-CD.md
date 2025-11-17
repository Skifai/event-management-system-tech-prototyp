# CI/CD Pipeline Documentation

## Overview

This project uses GitHub Actions for continuous integration and continuous delivery. The pipeline is split into multiple focused workflows for better maintainability and clarity.

## Pipeline Structure

The CI/CD pipeline consists of **4 separate workflow files**:

### 1. CI Workflow (`ci.yml`) - Always runs
**File**: `.github/workflows/ci.yml`

- **Trigger**: On every push and pull request to any branch
- **Purpose**: Build and test the application
- **Status Badge**: [![CI - Build and Test](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/ci.yml/badge.svg)](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/ci.yml)
- **Steps**:
  1. Checkout repository
  2. Set up Java 21 (Temurin distribution)
  3. Run tests with Maven: `mvn -B -U clean test`
  4. Generate JaCoCo coverage report
  5. Upload coverage to Codecov (optional, requires configuration)

**Why H2 for Tests?**
- Tests use H2 in-memory database (configured in `src/test/resources/application.properties`)
- No external database service required
- Faster test execution
- Consistent environment across local development and CI

### 2. Package Workflow (`package.yml`) - Conditional
**File**: `.github/workflows/package.yml`

- **Trigger**: Only on `main` or `develop` branches
- **Purpose**: Create deployable JAR artifact
- **Status Badge**: [![Package - Create JAR Artifact](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/package.yml/badge.svg)](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/package.yml)
- **Steps**:
  1. Checkout repository
  2. Set up Java 21
  3. Run tests: `mvn -B -U clean test`
  4. Package application: `mvn -B -DskipTests package`
  5. Upload JAR artifact (retained for 7 days)

### 3. Docker Dev Workflow (`docker-dev.yml`) - Optional
**File**: `.github/workflows/docker-dev.yml`

- **Trigger**: Disabled by default (configure to run on `develop` branch)
- **Purpose**: Build and push development Docker image
- **Status**: Disabled - To enable:
  1. Uncomment the `on:` trigger in the workflow file
  2. Configure Docker Hub secrets:
     - `DOCKER_USERNAME`
     - `DOCKER_PASSWORD`
  3. Set the `IMAGE_NAME` environment variable
  4. Remove the `if: false` condition
- **Steps**:
  1. Checkout code
  2. Set up Java 21 and build JAR
  3. Set up Docker Buildx
  4. Log in to Docker Hub
  5. Build and push development image with tags

### 4. Docker Prod Workflow (`docker-prod.yml`) - Optional
**File**: `.github/workflows/docker-prod.yml`

- **Trigger**: Disabled by default (configure to run on `main` branch)
- **Purpose**: Build and push production native image with GraalVM
- **Status**: Disabled - To enable:
  1. Uncomment the `on:` trigger in the workflow file
  2. Configure Docker Hub secrets:
     - `DOCKER_USERNAME`
     - `DOCKER_PASSWORD`
  3. Set the `IMAGE_NAME` environment variable
  4. Remove the `if: false` condition
- **Steps**:
  1. Checkout code
  2. Set up GraalVM
  3. Build native image
  4. Set up Docker Buildx
  5. Log in to Docker Hub
  6. Build and push production image with tags

## Benefits of Separate Workflows

✅ **Separation of Concerns**: Each workflow has a single, clear purpose
✅ **Easier Maintenance**: Smaller files are easier to understand and modify
✅ **Better Triggers**: Each workflow can have appropriate triggers
✅ **Selective Execution**: Can run specific workflows independently
✅ **Clearer Pipeline Status**: Each workflow shows its specific status

## Configuration

### Required Secrets
None for basic build and test. Optional:
- `CODECOV_TOKEN` - For code coverage reporting (if using Codecov)
- `DOCKER_USERNAME` - For Docker image publishing
- `DOCKER_PASSWORD` - For Docker image publishing

### Maven Cache
The pipeline uses Maven dependency caching to speed up builds:
```yaml
cache: 'maven'
```

## Test Configuration

### Local Test Database
Tests use H2 in-memory database as configured in:
```
src/test/resources/application.properties
```

Configuration:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

### Why No PostgreSQL Service in CI?
- **Simplicity**: H2 in-memory database requires no setup
- **Speed**: Faster test execution without network overhead
- **Consistency**: Same test database locally and in CI
- **Cost**: No additional service resources required

**Note**: Production uses PostgreSQL (configured in `application.properties` and `docker-compose.yml`)

## Running Locally

### Run Tests (same as CI)
```bash
mvn -B -U clean test
```

### Generate Coverage Report
```bash
mvn jacoco:report
# View report at: target/site/jacoco/index.html
```

### Create Package
```bash
mvn -B -DskipTests clean package
# JAR file at: target/event-management-system-0.0.1-SNAPSHOT.jar
```

## Pipeline Status

### Success Criteria
- All 36 unit tests must pass
- Code must compile successfully
- No build errors

### Failure Scenarios
1. **Test Failures**: Any test failure will fail the build
2. **Compilation Errors**: Code must compile with Java 21
3. **Dependency Resolution**: All Maven dependencies must resolve

## Coverage Reporting

JaCoCo generates test coverage reports:
- Service Layer: 49% coverage
- Model Layer: 100% coverage
- Overall: 11% coverage

View locally: `target/site/jacoco/index.html` after running tests

## Artifacts

### Build Artifacts
- **JAR File**: `target/event-management-system-0.0.1-SNAPSHOT.jar`
- **Coverage Report**: `target/site/jacoco/`
- **Test Results**: `target/surefire-reports/`

### Retention
- JAR artifacts are retained for 7 days on GitHub Actions
- Test results are available in the Actions run summary

## Troubleshooting

### Common Issues

**Problem**: Tests fail with database connection errors
- **Solution**: Ensure H2 dependency is in `pom.xml` (scope: test)

**Problem**: Build fails with "JAVA_HOME not set"
- **Solution**: GitHub Actions sets this automatically with `setup-java` action

**Problem**: Maven dependencies fail to download
- **Solution**: Check internet connectivity, retry build (Maven mirrors may be temporarily unavailable)

**Problem**: JaCoCo coverage check fails
- **Solution**: Coverage threshold set to 50% in `pom.xml`, currently at 49% for services

## Extending the Pipeline

### Enable Docker Development Builds

1. Open `.github/workflows/docker-dev.yml`
2. Uncomment the `on:` trigger section at the top
3. Remove the `if: false` condition from the job
4. Add Docker Hub secrets to GitHub repository settings:
   - `DOCKER_USERNAME`
   - `DOCKER_PASSWORD`
5. Update the `IMAGE_NAME` environment variable:
   ```yaml
   env:
     IMAGE_NAME: yourusername/event-management-system
   ```

### Enable Docker Production Builds

1. Open `.github/workflows/docker-prod.yml`
2. Uncomment the `on:` trigger section at the top
3. Remove the `if: false` condition from the job
4. Add Docker Hub secrets to GitHub repository settings:
   - `DOCKER_USERNAME`
   - `DOCKER_PASSWORD`
5. Update the `IMAGE_NAME` environment variable:
   ```yaml
   env:
     IMAGE_NAME: yourusername/event-management-system
   ```

### Add SonarCloud Analysis

Create a new workflow file `.github/workflows/code-quality.yml`:
```yaml
name: Code Quality Analysis

on:
  push:
    branches:
      - main
      - develop
  pull_request:
    branches:
      - main

jobs:
  code-quality:
    name: SonarCloud Scan
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - name: SonarCloud Scan
        uses: SonarSource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

### Add Deployment Pipeline

Create a new workflow file `.github/workflows/deploy.yml`:
```yaml
name: Deploy to Production

on:
  push:
    branches:
      - main

jobs:
  deploy:
    name: Deploy Application
    runs-on: ubuntu-latest
    needs: package  # Note: This would need to be coordinated with package.yml
    steps:
      - name: Download artifact
        uses: actions/download-artifact@v4
        with:
          name: application-jar
      - name: Deploy to Production
        # Add your deployment steps here
        run: echo "Deploy to production server"
```

## Performance

### Build Times
- **Clean Build with Tests**: ~25-60 seconds
- **Cached Build with Tests**: ~15-25 seconds
- **Package Only (skip tests)**: ~10-15 seconds

### Optimization Tips
1. Maven cache is enabled automatically
2. Tests run in parallel where possible
3. H2 database is in-memory (no I/O overhead)
4. Dependencies are downloaded once per cache period

## Best Practices

1. **Always run tests locally** before pushing
2. **Keep builds fast** - currently ~25 seconds
3. **Monitor test coverage** - aim for >50%
4. **Fix failing builds immediately** - don't let them accumulate
5. **Review build logs** for warnings even if build succeeds

## Security

### CodeQL Analysis
CodeQL security scanning has been run locally:
- **Result**: 0 vulnerabilities found ✅
- **Scan**: Java code analysis for common security issues

To add CodeQL to CI pipeline:
```yaml
- name: Initialize CodeQL
  uses: github/codeql-action/init@v2
  with:
    languages: java
- name: Perform CodeQL Analysis
  uses: github/codeql-action/analyze@v2
```

## Related Documentation

- [README.md](../README.md) - Project overview and setup
- [DOCKER.md](../DOCKER.md) - Docker deployment guide
- [IMPLEMENTATION_SUMMARY.md](../IMPLEMENTATION_SUMMARY.md) - Complete implementation details
- [pom.xml](../pom.xml) - Maven configuration

## Support

For CI/CD issues:
1. Check GitHub Actions logs
2. Verify Java 21 compatibility
3. Ensure H2 dependency is present for tests
4. Confirm Maven wrapper is executable (`chmod +x mvnw`)
