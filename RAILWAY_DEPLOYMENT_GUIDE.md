# 🚀 Complete Railway Deployment Guide

## 📋 Prerequisites

Before deploying to Railway, make sure you have:
- ✅ GitHub repository with your code
- ✅ Railway account (free at [railway.app](https://railway.app))
- ✅ PostgreSQL database (Railway provides this)
- ✅ Environment variables ready

## 🎯 Step-by-Step Deployment

### **Step 1: Create Railway Account**

1. **Go to Railway**: Visit [railway.app](https://railway.app)
2. **Sign Up**: Click "Start a New Project"
3. **Connect GitHub**: Choose "Deploy from GitHub repo"
4. **Authorize**: Allow Railway to access your GitHub repositories

### **Step 2: Create New Project**

1. **New Project**: Click "New Project"
2. **Select Repository**: Choose your `lunar-app` repository
3. **Deploy**: Railway will automatically detect it's a Java/Maven project

### **Step 3: Add PostgreSQL Database**

1. **Add Service**: Click "+ New" in your project
2. **Database**: Select "Database" → "PostgreSQL"
3. **Wait**: Railway will provision a PostgreSQL database
4. **Note Credentials**: Save the database connection details

### **Step 4: Configure Environment Variables**

In your Railway project dashboard:

1. **Go to Variables**: Click on your app service → "Variables" tab
2. **Add Variables**: Add these environment variables:

```bash
# Database Configuration
DATABASE_URL=postgresql://username:password@host:port/database
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_LOB_NON_CONTEXTUAL_CREATION=true
SPRING_JPA_PROPERTIES_HIBERNATE_HBM2DDL_AUTO=update

# Security Configuration
JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=7200000

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,http://localhost:3000
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=*

# File Upload Configuration
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB
FILE_UPLOAD_DIR=./uploads

# Email Configuration (Gmail Example)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

# Payment Configuration (Razorpay)
RAZORPAY_KEY_ID=rzp_test_your_key_id
RAZORPAY_KEY_SECRET=your_razorpay_secret
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret

# QR Code Configuration
QR_CODE_SIZE=200
QR_CODE_FORMAT=PNG

# Cache Configuration
SPRING_CACHE_TYPE=caffeine
SPRING_CACHE_CAFFEINE_SPEC=maximumSize=1000,expireAfterWrite=1h

# Logging Configuration
LOGGING_LEVEL_COM_LUNAR=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=WARN
LOGGING_LEVEL_ORG_HIBERNATE_SQL=WARN
LOGGING_LEVEL_ORG_HIBERNATE_TYPE_DESCRIPTOR_SQL_BASICBINDER=WARN

# Actuator Configuration
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=when-authorized
MANAGEMENT_ENDPOINT_INFO_ENABLED=true
MANAGEMENT_ENDPOINT_METRICS_ENABLED=true

# Server Configuration
SERVER_PORT=8080
SPRING_APPLICATION_NAME=lunar-event-management
```

### **Step 5: Configure Build Settings**

1. **Build Command**: Railway auto-detects Maven, but you can verify:
   ```bash
   Build Command: mvn clean package -DskipTests
   Start Command: java -jar target/demo-0.0.1-SNAPSHOT.jar
   ```

2. **Port**: Railway automatically sets `PORT` environment variable
3. **Health Check**: Railway will check `/actuator/health`

### **Step 6: Deploy**

1. **Deploy**: Click "Deploy" or push to your `master` branch
2. **Monitor**: Watch the build logs in Railway dashboard
3. **Wait**: First deployment takes 2-3 minutes

### **Step 7: Get Your App URL**

1. **Domain**: Railway provides a free domain like `your-app-name.railway.app`
2. **Custom Domain**: You can add your own domain later
3. **HTTPS**: Railway provides free SSL certificates

## 🔧 Railway Configuration Files

### **railway.json** (Already created)
```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS"
  },
  "deploy": {
    "startCommand": "java -jar target/demo-0.0.1-SNAPSHOT.jar",
    "healthcheckPath": "/actuator/health",
    "healthcheckTimeout": 100,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

## 🗄️ Database Setup

### **Automatic Setup**
- Railway PostgreSQL is automatically configured
- Tables are created automatically via Hibernate
- No manual database setup required

### **Manual Database Access** (if needed)
1. **Database Service**: Click on your PostgreSQL service
2. **Connect**: Use the connection string provided
3. **Query Tool**: Use Railway's built-in query tool

## 🔐 Environment Variables Setup

### **Required Variables** (Minimum for basic functionality):
```bash
DATABASE_URL=postgresql://...
JWT_SECRET=your-secret-key
RAZORPAY_KEY_ID=rzp_test_...
RAZORPAY_KEY_SECRET=your_secret
```

### **Optional Variables** (for full functionality):
```bash
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
CORS_ALLOWED_ORIGINS=https://your-frontend.com
```

## 🚀 Deployment Commands

### **Manual Deployment**:
```bash
# Push to trigger deployment
git add .
git commit -m "Deploy to Railway"
git push origin master
```

### **Check Deployment Status**:
1. Go to Railway dashboard
2. Click on your project
3. Check "Deployments" tab
4. View build logs

## 🔍 Troubleshooting

### **Common Issues**:

1. **Build Fails**:
   - Check build logs in Railway dashboard
   - Ensure all dependencies are in `pom.xml`
   - Verify Java version (Railway uses Java 17)

2. **Database Connection Fails**:
   - Check `DATABASE_URL` environment variable
   - Verify PostgreSQL service is running
   - Check database credentials

3. **App Won't Start**:
   - Check startup logs
   - Verify all required environment variables
   - Check port configuration

4. **Environment Variables Not Working**:
   - Ensure variables are set in Railway dashboard
   - Check variable names (case-sensitive)
   - Restart the service after adding variables

### **Debug Commands**:
```bash
# Check logs
railway logs

# Check environment variables
railway variables

# Restart service
railway restart
```

## 📊 Monitoring & Maintenance

### **Health Checks**:
- **Health Endpoint**: `https://your-app.railway.app/actuator/health`
- **Metrics**: `https://your-app.railway.app/actuator/metrics`
- **Info**: `https://your-app.railway.app/actuator/info`

### **Logs**:
- View real-time logs in Railway dashboard
- Download logs for analysis
- Set up log monitoring

### **Scaling**:
- Railway automatically scales based on traffic
- Upgrade plan for more resources
- Set up monitoring alerts

## 🎯 Post-Deployment Checklist

- [ ] ✅ App is accessible via Railway URL
- [ ] ✅ Database connection working
- [ ] ✅ Health endpoint responding
- [ ] ✅ Environment variables set correctly
- [ ] ✅ Logs showing no errors
- [ ] ✅ API endpoints responding
- [ ] ✅ CORS configured for frontend
- [ ] ✅ Email service working (if configured)
- [ ] ✅ Payment integration working (if configured)

## 🔄 Continuous Deployment

### **Automatic Deployments**:
- Push to `master` branch triggers deployment
- Railway automatically builds and deploys
- Zero-downtime deployments

### **Manual Deployments**:
- Use Railway CLI for manual deployments
- Deploy specific branches
- Rollback to previous versions

## 💰 Pricing

### **Free Tier**:
- ✅ $5 credit per month
- ✅ 500 hours of usage
- ✅ 1GB RAM
- ✅ 1GB storage
- ✅ Custom domains
- ✅ SSL certificates

### **Pro Tier** ($5/month):
- ✅ Unlimited usage
- ✅ More resources
- ✅ Priority support
- ✅ Advanced features

## 🎉 Success!

Your Lunar Event Management System is now deployed on Railway! 

**Your app URL**: `https://your-app-name.railway.app`
**API Base URL**: `https://your-app-name.railway.app/api`

### **Next Steps**:
1. Test all API endpoints
2. Set up your frontend to use the Railway URL
3. Configure email and payment services
4. Set up monitoring and alerts
5. Add custom domain (optional)

Happy coding! 🚀
