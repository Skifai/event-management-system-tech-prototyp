# Implementation Summary: IDEA Run Configuration Improvements

**Date**: November 19, 2024  
**Issue**: "Implement the best Way to start the app in IDEA with a dev and prod profile. The automatic start which is implemented now doesn't work."

## Problem Analysis

The original issue stated that "the automatic start which is implemented now doesn't work." After analyzing the codebase, the following issues were identified:

1. **Run Configuration Type**: Development Mode used Maven run configuration (`spring-boot:run`) instead of IntelliJ IDEA's native Spring Boot Application type
2. **Profile Activation**: Profile was set via environment variable `SPRING_PROFILES_ACTIVE` instead of IDEA's native `Active profiles` field
3. **Limited Options**: Only two modes (Development and Production Docker) - no quick way to test production settings locally
4. **Documentation Inconsistency**: Documentation mentioned dev profile but configuration didn't explicitly set it
5. **Integration Issues**: Maven-based approach doesn't integrate well with IDEA's Spring Boot tooling

## Solution Implemented

### 1. Changed Run Configuration Types

**Before**: Maven Run Configuration
```xml
<configuration type="MavenRunConfiguration">
  <goals>
    <option value="spring-boot:run" />
  </goals>
  <envs>
    <env name="SPRING_PROFILES_ACTIVE" value="dev" />
  </envs>
</configuration>
```

**After**: Spring Boot Application Configuration
```xml
<configuration type="SpringBootApplicationConfigurationType">
  <option name="ACTIVE_PROFILES" value="dev" />
  <module name="event-management-system" />
  <option name="SPRING_BOOT_MAIN_CLASS" value="ch.flossrennen.eventmanagementsystem.EventManagementSystemApplication" />
</configuration>
```

**Benefits**:
- ✅ Native IDEA integration (Spring Boot Dashboard, Beans View)
- ✅ Better debugging experience
- ✅ Faster startup (no Maven overhead)
- ✅ Profile visible in IDEA UI
- ✅ Consistent with IntelliJ IDEA best practices

### 2. Created Three Distinct Modes

#### Development Mode
- **Type**: Spring Boot Application
- **Profile**: `dev`
- **Port**: 8080
- **DB**: Auto-started Docker container on port 5432
- **Use**: Daily development work

#### Production Mode (Local)
- **Type**: Spring Boot Application (NEW!)
- **Profile**: `prod`
- **Port**: 8081
- **DB**: Manually started Docker container on port 5433
- **Use**: Quick testing of production settings without Docker build

#### Production Mode (Docker)
- **Type**: Shell Script (renamed from "Production Mode (Native)")
- **Profile**: `prod`
- **Port**: 8081
- **DB**: Docker Compose managed on port 5433
- **Use**: Full production stack with GraalVM Native Image

### 3. Updated Application Properties

**application-prod.properties**:
```properties
# Changed from 8080 to 8081
server.port=8081
```

This ensures production mode runs on a different port than development, allowing both to run simultaneously for comparison.

### 4. Comprehensive Documentation Updates

Updated three key documentation files:

1. **IDEA_SETUP.md**:
   - Complete rewrite with clearer structure
   - Added "Why Spring Boot Application?" section
   - Improved troubleshooting section
   - Added quick reference tables

2. **.idea/runConfigurations/README.md**:
   - Expanded from 185 lines to 377 lines
   - Added detailed explanations for each configuration
   - Added troubleshooting for each mode
   - Added manual database management commands
   - Added best practices section

3. **README.md**:
   - Updated IDEA section to reflect new approach
   - Added Production Mode (Local) documentation
   - Updated comparison table to include three modes
   - Added configuration type information

## Technical Details

### Run Configuration Files

**Created**:
- `.idea/runConfigurations/Production_Mode__Local_.xml` (NEW)

**Modified**:
- `.idea/runConfigurations/Development_Mode.xml` (Changed from Maven to Spring Boot App)
- `.idea/runConfigurations/Production_Mode__Native_.xml` → Renamed to `Production_Mode__Docker_.xml`

**Unchanged**:
- `.idea/runConfigurations/Build_Production_JAR.xml` (Still uses Maven, as appropriate)
- `.idea/runConfigurations/Stop_Production_Mode.xml` (Shell script)

### Spring Profiles

The implementation now properly uses two profiles:

#### Profile: `dev`
- File: `application-dev.properties`
- Features:
  - Automatic PostgreSQL Docker management via DatabaseStartupListener
  - Hot-reload with DevTools
  - Verbose logging (DEBUG)
  - Automatic schema updates (`ddl-auto=update`)
  - Test data automatically loaded
  - Port 8080
  - Database port 5432

#### Profile: `prod`
- File: `application-prod.properties`
- Features:
  - No automatic database management
  - No DevTools
  - Minimal logging (INFO/WARN)
  - Schema validation only (`ddl-auto=validate`)
  - No test data
  - Port 8081
  - Database port 5433

### DatabaseStartupListener Behavior

The `DatabaseStartupListener` (implemented as `EnvironmentPostProcessor`) checks the active profile:

```java
String[] activeProfiles = environment.getActiveProfiles();
boolean isProduction = Arrays.asList(activeProfiles).contains("prod");

if (isProduction) {
    log.info("Running in production mode - skipping Docker PostgreSQL auto-start");
    return;
}
```

This ensures:
- ✅ In development (`dev` profile): Automatically manages PostgreSQL Docker container
- ❌ In production (`prod` profile): Does not interfere with database setup

## Benefits of the New Approach

### 1. Better Developer Experience
- One-click start from IDEA
- Clear profile selection visible in UI
- Native Spring Boot tooling integration
- Faster startup (no Maven overhead)

### 2. Flexibility
- Three modes for different use cases
- Can run dev and prod simultaneously
- Easy to create custom profiles

### 3. Consistency
- Follows IntelliJ IDEA best practices
- Matches Spring Boot project conventions
- Clear separation between dev and prod

### 4. Documentation
- Comprehensive guides for all scenarios
- Troubleshooting for common issues
- Clear migration path from old approach

## Migration Guide

For users with existing run configurations:

1. **No action required**: New configurations are automatically available
2. **Old configurations**: Can be deleted from IDEA (will be recreated from repository)
3. **Custom configurations**: Update to use Spring Boot Application type instead of Maven

## Testing Recommendations

To verify the implementation works correctly:

### Development Mode
```bash
# 1. Ensure Docker is running
docker ps

# 2. Start from IDEA: Development Mode
# 3. Verify:
#    - App starts on port 8080
#    - PostgreSQL container is created/started
#    - Test data is loaded
#    - Hot-reload works with DevTools
```

### Production Mode (Local)
```bash
# 1. Start production database manually
docker run -d \
  --name event-management-db-prod-container \
  -e POSTGRES_DB=eventmanagement_prod \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  postgres:17-alpine

# 2. Start from IDEA: Production Mode (Local)
# 3. Verify:
#    - App starts on port 8081
#    - Connects to database on port 5433
#    - No test data loaded
#    - Production logging level
```

### Production Mode (Docker)
```bash
# 1. Start from IDEA: Production Mode (Docker)
# 2. Wait for build to complete (5-15 minutes first time)
# 3. Verify:
#    - App starts on port 8081
#    - GraalVM native binary is created
#    - Separate database on port 5433
#    - Docker containers running
```

## Files Changed

### Modified
- `.idea/runConfigurations/Development_Mode.xml`
- `.idea/runConfigurations/Production_Mode__Native_.xml` → `.idea/runConfigurations/Production_Mode__Docker_.xml`
- `.idea/runConfigurations/README.md`
- `IDEA_SETUP.md`
- `README.md`
- `src/main/resources/application-prod.properties`

### Created
- `.idea/runConfigurations/Production_Mode__Local_.xml`

### Unchanged
- `.idea/runConfigurations/Build_Production_JAR.xml`
- `.idea/runConfigurations/Stop_Production_Mode.xml`
- `src/main/java/ch/flossrennen/eventmanagementsystem/config/DatabaseStartupListener.java`
- `src/main/java/ch/flossrennen/eventmanagementsystem/service/DockerService.java`
- `src/main/resources/META-INF/spring.factories`

## Backward Compatibility

- ✅ Existing DatabaseStartupListener code unchanged
- ✅ Existing application properties files work as before
- ✅ Docker Compose configurations unchanged
- ✅ Maven commands still work (`./mvnw spring-boot:run`)
- ✅ JAR execution still works (`java -jar ...`)

## Future Improvements (Optional)

Potential enhancements for future iterations:

1. **Health Checks**: Add database health check before app startup
2. **Database Migrations**: Integrate Flyway or Liquibase for production
3. **Configuration Profiles**: Add more profiles (test, staging, etc.)
4. **Monitoring**: Add run configurations for monitoring tools
5. **Docker Compose Dev**: Alternative dev mode using docker-compose for full stack

## Conclusion

The implementation successfully addresses the issue by:

1. ✅ Using proper Spring Boot Application run configurations
2. ✅ Clearly defining dev and prod profiles
3. ✅ Providing three distinct modes for different use cases
4. ✅ Comprehensive documentation for all scenarios
5. ✅ Following IntelliJ IDEA and Spring Boot best practices

The new approach provides a better developer experience, clearer configuration options, and proper integration with IntelliJ IDEA's Spring Boot tooling.

## References

- [IntelliJ IDEA Spring Boot Documentation](https://www.jetbrains.com/help/idea/spring-boot.html)
- [Spring Boot Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [Spring Boot Run Configurations in IntelliJ IDEA](https://www.jetbrains.com/help/idea/run-debug-configuration-spring-boot.html)
