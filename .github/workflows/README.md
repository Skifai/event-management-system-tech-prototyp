# GitHub Actions Workflows

This directory contains the CI/CD workflows for the Event Management System project. The workflows have been split into separate, focused files for better maintainability and clarity.

## Workflow Files

### 1. 🧪 CI - Build and Test (`ci.yml`)
**Status**: ✅ Active

- **Purpose**: Continuous Integration - Build and test the application
- **Triggers**: Every push and pull request to any branch
- **Actions**:
  - Checkout code
  - Set up Java 21 with Maven cache
  - Run tests: `mvn -B -U clean test`
  - Generate JaCoCo coverage report
  - Upload coverage to Codecov (optional)
- **Badge**: [![CI - Build and Test](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/ci.yml/badge.svg)](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/ci.yml)

### 2. 📦 Package - Create JAR Artifact (`package.yml`)
**Status**: ✅ Active

- **Purpose**: Package the application and create deployable JAR artifacts
- **Triggers**: Push to `main` or `develop` branches only
- **Actions**:
  - Checkout code
  - Set up Java 21 with Maven cache
  - Run tests: `mvn -B -U clean test`
  - Package application: `mvn -B -DskipTests package`
  - Upload JAR artifact (7 days retention)
- **Badge**: [![Package - Create JAR Artifact](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/package.yml/badge.svg)](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/package.yml)

### 3. 🐳 Docker Dev - Build Development Image (`docker-dev.yml`)
**Status**: ⏸️ Disabled (requires configuration)

- **Purpose**: Build and push development Docker images
- **Triggers**: Disabled by default (can be enabled for `develop` branch)
- **Actions**:
  - Checkout code
  - Set up Java 21 and build JAR
  - Set up Docker Buildx
  - Log in to Docker Hub
  - Build and push development image
- **To Enable**:
  1. Uncomment the `on:` trigger section
  2. Remove the `if: false` condition
  3. Configure Docker Hub secrets:
     - `DOCKER_USERNAME`
     - `DOCKER_PASSWORD`
  4. Update the `IMAGE_NAME` environment variable

### 4. 🚀 Docker Prod - Build Production Native Image (`docker-prod.yml`)
**Status**: ⏸️ Disabled (requires configuration)

- **Purpose**: Build and push production native images with GraalVM
- **Triggers**: Disabled by default (can be enabled for `main` branch)
- **Actions**:
  - Checkout code
  - Set up GraalVM
  - Build native image
  - Set up Docker Buildx
  - Log in to Docker Hub
  - Build and push production image with multiple tags
- **To Enable**:
  1. Uncomment the `on:` trigger section
  2. Remove the `if: false` condition
  3. Configure Docker Hub secrets:
     - `DOCKER_USERNAME`
     - `DOCKER_PASSWORD`
  4. Update the `IMAGE_NAME` environment variable

## Workflow Execution Flow

```
┌─────────────────────────────────────────────────────────────┐
│                      Every Push/PR                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
              ┌────────────────┐
              │   CI Workflow  │
              │  (ci.yml)      │
              │                │
              │ • Build        │
              │ • Test         │
              │ • Coverage     │
              └────────────────┘
                       │
                       │ (only on main/develop)
                       ▼
              ┌────────────────┐
              │ Package        │
              │ (package.yml)  │
              │                │
              │ • Test         │
              │ • Package      │
              │ • Upload JAR   │
              └────────────────┘
                       │
         ┌─────────────┴─────────────┐
         │                           │
         ▼ (develop)                 ▼ (main)
┌────────────────┐          ┌────────────────┐
│ Docker Dev     │          │ Docker Prod    │
│ (optional)     │          │ (optional)     │
│                │          │                │
│ • Build JAR    │          │ • GraalVM      │
│ • Docker Image │          │ • Native Image │
└────────────────┘          └────────────────┘
```

## Benefits of Separate Workflows

✅ **Separation of Concerns**: Each workflow has a single, clear purpose
✅ **Easier Maintenance**: Smaller files are easier to understand and modify
✅ **Better Triggers**: Each workflow can have appropriate triggers
✅ **Selective Execution**: Can run specific workflows independently
✅ **Clearer Pipeline Status**: Each workflow shows its specific status
✅ **Better Performance**: Workflows run only when needed
✅ **Easier Debugging**: Isolated workflows are easier to troubleshoot

## Secrets Required

### For CI and Package Workflows (Optional)
- `CODECOV_TOKEN` - For uploading coverage reports to Codecov

### For Docker Workflows (Required to enable)
- `DOCKER_USERNAME` - Docker Hub username
- `DOCKER_PASSWORD` - Docker Hub password or access token
- `GITHUB_TOKEN` - Automatically provided by GitHub Actions

## Local Development

To test workflows locally, you can use [act](https://github.com/nektos/act):

```bash
# Install act
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Test CI workflow
act push -W .github/workflows/ci.yml

# Test Package workflow
act push -W .github/workflows/package.yml -e <(echo '{"ref":"refs/heads/main"}')
```

## Troubleshooting

### Workflow not running
- Check that the trigger conditions match (branch name, event type)
- Verify the workflow file is in `.github/workflows/` directory
- Ensure YAML syntax is valid

### Docker workflows not running
- Verify that the workflows are enabled (triggers uncommented, `if: false` removed)
- Check that Docker Hub secrets are configured
- Ensure the `IMAGE_NAME` environment variable is set correctly

### Build failures
- Check the workflow run logs in the GitHub Actions tab
- Verify Java 21 compatibility
- Ensure all Maven dependencies are available

## Further Documentation

- [CI-CD.md](../../CI-CD.md) - Detailed CI/CD pipeline documentation
- [README.md](../../README.md) - Project overview and setup
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
