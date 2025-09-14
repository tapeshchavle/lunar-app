#!/bin/bash

# 🚀 Railway Deployment Setup Script
# This script helps you set up your Lunar Event Management System on Railway

echo "🚀 Lunar Event Management - Railway Deployment Setup"
echo "=================================================="

# Check if Railway CLI is installed
if ! command -v railway &> /dev/null; then
    echo "❌ Railway CLI not found. Installing..."
    
    # Install Railway CLI based on OS
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        brew install railway
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        curl -fsSL https://railway.app/install.sh | sh
    else
        echo "❌ Unsupported OS. Please install Railway CLI manually:"
        echo "   Visit: https://docs.railway.app/develop/cli"
        exit 1
    fi
else
    echo "✅ Railway CLI found"
fi

# Login to Railway
echo "🔐 Logging into Railway..."
railway login

# Create new project
echo "📦 Creating new Railway project..."
railway init

# Add PostgreSQL database
echo "🗄️ Adding PostgreSQL database..."
railway add postgresql

# Set environment variables
echo "⚙️ Setting up environment variables..."

# Database variables (Railway automatically sets DATABASE_URL)
railway variables set SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
railway variables set SPRING_JPA_HIBERNATE_DDL_AUTO=update
railway variables set SPRING_JPA_SHOW_SQL=false
railway variables set SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# Security variables
railway variables set JWT_SECRET=$(openssl rand -base64 32)
railway variables set JWT_EXPIRATION=3600000
railway variables set JWT_REFRESH_EXPIRATION=7200000

# CORS variables
railway variables set CORS_ALLOWED_ORIGINS="http://localhost:3000,https://your-frontend-domain.com"
railway variables set CORS_ALLOWED_METHODS="GET,POST,PUT,DELETE,OPTIONS"
railway variables set CORS_ALLOWED_HEADERS="*"

# File upload variables
railway variables set SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
railway variables set SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB

# Payment variables (you need to set these manually)
echo "💳 Payment Configuration Required:"
echo "   Set these variables manually in Railway dashboard:"
echo "   - RAZORPAY_KEY_ID=rzp_test_your_key_id"
echo "   - RAZORPAY_KEY_SECRET=your_razorpay_secret"
echo "   - RAZORPAY_WEBHOOK_SECRET=your_webhook_secret"

# Email variables (you need to set these manually)
echo "📧 Email Configuration Required:"
echo "   Set these variables manually in Railway dashboard:"
echo "   - SPRING_MAIL_HOST=smtp.gmail.com"
echo "   - SPRING_MAIL_PORT=587"
echo "   - SPRING_MAIL_USERNAME=your-email@gmail.com"
echo "   - SPRING_MAIL_PASSWORD=your-app-password"

# QR Code variables
railway variables set QR_CODE_SIZE=200
railway variables set QR_CODE_FORMAT=PNG

# Cache variables
railway variables set SPRING_CACHE_TYPE=caffeine
railway variables set SPRING_CACHE_CAFFEINE_SPEC="maximumSize=1000,expireAfterWrite=1h"

# Logging variables
railway variables set LOGGING_LEVEL_COM_LUNAR=INFO
railway variables set LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=WARN
railway variables set LOGGING_LEVEL_ORG_HIBERNATE_SQL=WARN

# Actuator variables
railway variables set MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
railway variables set MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=when-authorized

# Server variables
railway variables set SERVER_PORT=8080
railway variables set SPRING_APPLICATION_NAME=lunar-event-management

echo "✅ Environment variables set!"

# Deploy the application
echo "🚀 Deploying application..."
railway up

echo ""
echo "🎉 Deployment Complete!"
echo "======================"
echo ""
echo "Your app is now deployed on Railway!"
echo ""
echo "Next steps:"
echo "1. Go to Railway dashboard to get your app URL"
echo "2. Set up payment and email variables manually"
echo "3. Test your API endpoints"
echo "4. Connect your frontend to the Railway URL"
echo ""
echo "Health check: https://your-app.railway.app/actuator/health"
echo "API docs: https://your-app.railway.app/api"
echo ""
echo "Happy coding! 🚀"
