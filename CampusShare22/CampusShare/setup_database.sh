#!/bin/bash
# CampusShare Database Setup Script
# Run this script to set up the MySQL database

echo "=================================="
echo "CampusShare Database Setup"
echo "=================================="
echo ""

# Check if MySQL is running
if ! command -v mysql &> /dev/null; then
    echo "❌ ERROR: MySQL is not installed or not in PATH"
    echo "   Please install MySQL and try again"
    exit 1
fi

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SCHEMA_FILE="$SCRIPT_DIR/campusshare_schema.sql"

if [ ! -f "$SCHEMA_FILE" ]; then
    echo "❌ ERROR: campusshare_schema.sql not found at: $SCHEMA_FILE"
    exit 1
fi

echo "📁 Schema file: $SCHEMA_FILE"
echo ""
echo "This script will:"
echo "  1. Create 'campusshare' database"
echo "  2. Create 8 tables (department, user, course, material, etc.)"
echo "  3. Insert sample data (4 users, 3 courses, 3 materials)"
echo ""

# Prompt for MySQL password
read -s -p "🔐 Enter MySQL root password: " MYSQL_PASSWORD
echo ""

# Execute the schema
echo "Executing schema..."
mysql -u root -p"$MYSQL_PASSWORD" < "$SCHEMA_FILE"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Database setup SUCCESSFUL!"
    echo ""
    echo "📋 Sample Credentials for Testing:"
    echo "   Admin    | username: admin        | password: admin123"
    echo "   Teacher  | username: prof_smith   | password: teacher123"
    echo "   Student  | username: john_student | password: student123"
    echo ""
    echo "🚀 Next steps:"
    echo "   1. Start CampusShare: java -jar target/CampusShare.jar"
    echo "   2. Login with one of the credentials above"
    echo "   3. Test the material approval workflow"
    echo ""
else
    echo ""
    echo "❌ Database setup FAILED!"
    echo "   Check the error messages above"
    echo "   Verify MySQL is running and password is correct"
    exit 1
fi
