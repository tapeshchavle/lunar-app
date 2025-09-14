#!/bin/bash

# Lunar Event Management System Setup Script
# This script sets up the development environment for the Lunar Event Management System

set -e

echo "🚀 Setting up Lunar Event Management System..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Java 17 is installed
check_java() {
    print_status "Checking Java installation..."
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge 17 ]; then
            print_success "Java $JAVA_VERSION is installed"
        else
            print_error "Java 17 or higher is required. Current version: $JAVA_VERSION"
            exit 1
        fi
    else
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
}

# Check if Maven is installed
check_maven() {
    print_status "Checking Maven installation..."
    if command -v mvn &> /dev/null; then
        MAVEN_VERSION=$(mvn -version | head -n 1 | cut -d' ' -f3)
        print_success "Maven $MAVEN_VERSION is installed"
    else
        print_error "Maven is not installed. Please install Maven 3.6 or higher."
        exit 1
    fi
}

# Check if Docker is installed
check_docker() {
    print_status "Checking Docker installation..."
    if command -v docker &> /dev/null; then
        DOCKER_VERSION=$(docker --version | cut -d' ' -f3 | cut -d',' -f1)
        print_success "Docker $DOCKER_VERSION is installed"
    else
        print_warning "Docker is not installed. You can still run the application locally."
    fi
}

# Check if Docker Compose is installed
check_docker_compose() {
    print_status "Checking Docker Compose installation..."
    if command -v docker-compose &> /dev/null; then
        COMPOSE_VERSION=$(docker-compose --version | cut -d' ' -f3 | cut -d',' -f1)
        print_success "Docker Compose $COMPOSE_VERSION is installed"
    else
        print_warning "Docker Compose is not installed. You can still run the application locally."
    fi
}

# Create environment file
create_env_file() {
    print_status "Creating environment file..."
    cat > .env << EOF
# Database Configuration
POSTGRES_DB=lunar_events
POSTGRES_USER=lunar_user
POSTGRES_PASSWORD=lunar_password

# JWT Configuration
JWT_SECRET=lunar-event-management-secret-key-2024-very-long-and-secure
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Email Configuration (Update with your email credentials)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# Razorpay Configuration (Update with your Razorpay credentials)
RAZORPAY_KEY_ID=rzp_test_your_key_id
RAZORPAY_KEY_SECRET=your_razorpay_secret
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret

# Application Configuration
APP_BASE_URL=http://localhost:8080
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
EOF
    print_success "Environment file created (.env)"
}

# Build the application
build_application() {
    print_status "Building the application..."
    mvn clean install -DskipTests
    print_success "Application built successfully"
}

# Create uploads directory
create_uploads_directory() {
    print_status "Creating uploads directory..."
    mkdir -p uploads/qr-codes
    print_success "Uploads directory created"
}

# Start PostgreSQL with Docker
start_postgres() {
    print_status "Starting PostgreSQL with Docker..."
    if command -v docker &> /dev/null; then
        docker run -d \
            --name lunar-postgres \
            -e POSTGRES_DB=lunar_events \
            -e POSTGRES_USER=lunar_user \
            -e POSTGRES_PASSWORD=lunar_password \
            -p 5432:5432 \
            postgres:15-alpine
        print_success "PostgreSQL started on port 5432"
    else
        print_warning "Docker not available. Please start PostgreSQL manually."
    fi
}

# Wait for PostgreSQL to be ready
wait_for_postgres() {
    print_status "Waiting for PostgreSQL to be ready..."
    if command -v docker &> /dev/null; then
        until docker exec lunar-postgres pg_isready -U lunar_user -d lunar_events; do
            print_status "Waiting for PostgreSQL..."
            sleep 2
        done
        print_success "PostgreSQL is ready"
    fi
}

# Create database tables
create_database_tables() {
    print_status "Creating database tables..."
    print_status "Database tables will be created automatically on first startup"
    print_success "Database setup completed"
}

# Start the application
start_application() {
    print_status "Starting the application..."
    mvn spring-boot:run &
    APP_PID=$!
    echo $APP_PID > app.pid
    print_success "Application started with PID: $APP_PID"
    print_status "Application will be available at: http://localhost:8080"
}

# Display setup completion message
display_completion() {
    echo ""
    echo "🎉 Lunar Event Management System setup completed!"
    echo ""
    echo "📋 Next steps:"
    echo "1. Update the .env file with your actual credentials"
    echo "2. The application is running at: http://localhost:8080"
    echo "3. API documentation is available at: http://localhost:8080/api/health"
    echo "4. Check the logs for any issues"
    echo "5. Push to GitHub master branch to trigger automatic deployment"
    echo ""
    echo "🔧 Useful commands:"
    echo "  - Stop the application: kill \$(cat app.pid)"
    echo "  - View logs: tail -f logs/application.log"
    echo "  - Run tests: mvn test"
    echo "  - Build Docker image: docker build -t lunar-app ."
    echo "  - Start with Docker Compose: docker-compose up -d"
    echo "  - Deploy to production: git push origin master"
    echo ""
    echo "📚 Documentation:"
    echo "  - API Documentation: API_DOCUMENTATION.md"
    echo "  - README: README.md"
    echo ""
}

# Main setup function
main() {
    echo "🌙 Welcome to Lunar Event Management System Setup"
    echo "=================================================="
    echo ""
    
    check_java
    check_maven
    check_docker
    check_docker_compose
    create_env_file
    build_application
    create_uploads_directory
    start_postgres
    wait_for_postgres
    create_database_tables
    start_application
    display_completion
}

# Run main function
main "$@"
