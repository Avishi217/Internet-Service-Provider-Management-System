#!/bin/bash

# ISP Management System - Setup Script

echo "======================================"
echo "ISP Management System - Setup"
echo "======================================"
echo ""

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL is not installed. Please install MySQL first."
    exit 1
fi

echo "✅ MySQL found"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

echo "✅ Maven found"

# Check Java version
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install JDK 11 or higher."
    exit 1
fi

echo "✅ Java found"

# Set up database
echo ""
echo "Setting up database..."
echo "Creating database 'isp_management' and loading schema..."
echo "Please enter your MySQL root password when prompted:"

# Create database and load schema
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS isp_management;" && \
mysql -u root -p isp_management < database/schema.sql

if [ $? -eq 0 ]; then
    echo "✅ Database setup completed successfully!"
else
    echo "❌ Database setup failed. Please check your MySQL credentials."
    exit 1
fi

# Build the project
echo ""
echo "Building the project..."
mvn clean install

if [ $? -eq 0 ]; then
    echo "✅ Project built successfully!"
else
    echo "❌ Build failed. Please check for errors."
    exit 1
fi

echo ""
echo "======================================"
echo "Setup completed successfully! 🎉"
echo "======================================"
echo ""
echo "To run the application:"
echo "  mvn exec:java -Dexec.mainClass=\"com.isp.ui.views.LoginWindow\""
echo ""
echo "Or run the JAR file:"
echo "  java -jar target/isp-management-system-1.0.0-jar-with-dependencies.jar"
echo ""
echo "Default login credentials:"
echo "  Admin - Phone: 9999999999, Password: password123"
echo "  Use phone number to login (OTP-based authentication)"
echo ""
