# CI/CD Pipeline Documentation

## Overview

This project uses GitHub Actions for continuous integration and continuous delivery. The pipeline automatically builds, tests, and optionally packages the application on every push and pull request.

## Pipeline Structure

### Jobs

#### 1. Build Job (Always runs)
- **Trigger**: On every push and pull request to any branch
- **Purpose**: Build and test the application
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

#### 2. Package Job (Conditional)
- **Trigger**: Only on `main` or `develop` branches after successful build
- **Purpose**: Create deployable JAR artifact
- **Steps**:
  1. Checkout repository
  2. Set up Java 21
  3. Package application: `mvn -B -DskipTests clean package`
  4. Upload JAR artifact (retained for 7 days)

#### 3. Docker Jobs (Commented out - Optional)
Docker image building is prepared but commented out. To enable:
- Uncomment the `docker-dev` or `docker-native` job
- Configure Docker Hub secrets:
  - `DOCKER_USERNAME`
  - `DOCKER_PASSWORD`
- Set the `IMAGE_NAME` environment variable

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

### Enable Docker Builds

1. Uncomment desired Docker job in `.github/workflows/ci-cd.yml`
2. Add Docker Hub secrets to GitHub repository settings
3. Set IMAGE_NAME environment variable:
```yaml
env:
  IMAGE_NAME: yourusername/event-management-system
```

### Add SonarCloud Analysis

Uncomment and configure the `code-quality` job:
```yaml
code-quality:
  name: Code Quality Analysis
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

### Deploy to Production

Add deployment job after successful package:
```yaml
deploy:
  needs: package
  runs-on: ubuntu-latest
  if: github.ref == 'refs/heads/main'
  steps:
    - name: Deploy to Production
      # Add your deployment steps
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
