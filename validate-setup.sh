#!/bin/bash
# Validation script for independent container management setup

echo "================================"
echo "Validating Container Management Setup"
echo "================================"
echo ""

# Check if docker-compose.db.yml exists
echo "1. Checking docker-compose.db.yml..."
if [ -f "docker-compose.db.yml" ]; then
    echo "   ✅ docker-compose.db.yml exists"
else
    echo "   ❌ docker-compose.db.yml NOT found"
    exit 1
fi

# Validate docker-compose file syntax
echo "2. Validating docker-compose syntax..."
if docker compose -f docker-compose.db.yml config > /dev/null 2>&1; then
    echo "   ✅ docker-compose.db.yml syntax is valid"
else
    echo "   ❌ docker-compose.db.yml has syntax errors"
    exit 1
fi

# Check Run Configurations
echo "3. Checking IDEA Run Configurations..."
if [ -f ".idea/runConfigurations/Start_Databases.xml" ]; then
    echo "   ✅ Start_Databases.xml exists"
else
    echo "   ❌ Start_Databases.xml NOT found"
    exit 1
fi

if [ -f ".idea/runConfigurations/Stop_Databases.xml" ]; then
    echo "   ✅ Stop_Databases.xml exists"
else
    echo "   ❌ Stop_Databases.xml NOT found"
    exit 1
fi

# Check documentation
echo "4. Checking documentation..."
if [ -f "ANLEITUNG_APP_START.md" ]; then
    echo "   ✅ ANLEITUNG_APP_START.md exists"
else
    echo "   ❌ ANLEITUNG_APP_START.md NOT found"
    exit 1
fi

if [ -f "IMPLEMENTATION_SUMMARY_CONTAINER_MANAGEMENT.md" ]; then
    echo "   ✅ IMPLEMENTATION_SUMMARY_CONTAINER_MANAGEMENT.md exists"
else
    echo "   ❌ IMPLEMENTATION_SUMMARY_CONTAINER_MANAGEMENT.md NOT found"
    exit 1
fi

# Check Java files
echo "5. Checking Java source modifications..."
if grep -q "isPostgresContainerRunning(String containerName)" src/main/java/ch/flossrennen/eventmanagementsystem/service/DockerService.java; then
    echo "   ✅ DockerService has new method"
else
    echo "   ❌ DockerService missing new method"
    exit 1
fi

if grep -q "Please start the database containers first" src/main/java/ch/flossrennen/eventmanagementsystem/config/DatabaseStartupListener.java; then
    echo "   ✅ DatabaseStartupListener updated with new messages"
else
    echo "   ❌ DatabaseStartupListener not properly updated"
    exit 1
fi

echo ""
echo "================================"
echo "✅ All validations passed!"
echo "================================"
echo ""
echo "Setup Summary:"
echo "  - docker-compose.db.yml: Ready for container management"
echo "  - IDEA Run Configs: Start/Stop Databases ready"
echo "  - Documentation: Complete (German + English)"
echo "  - Code changes: DatabaseStartupListener + DockerService"
echo ""
echo "Next steps:"
echo "  1. Start containers: docker compose -f docker-compose.db.yml up -d"
echo "  2. In IDEA: Run 'Development Mode'"
echo ""
