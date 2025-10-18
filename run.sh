#!/bin/bash
# ISP Management System - Quick Launch Script

cd "$(dirname "$0")"

echo "🚀 Starting ISP Management System..."
echo ""

# Check if JAR exists
if [ ! -f "target/isp-management-system-1.0.0-jar-with-dependencies.jar" ]; then
    echo "⚠️  JAR file not found. Building project..."
    mvn clean package -DskipTests -q
    echo "✅ Build complete!"
    echo ""
fi

# Launch application
echo "🎯 Launching application..."
java -jar target/isp-management-system-1.0.0-jar-with-dependencies.jar

echo ""
echo "Application closed."
