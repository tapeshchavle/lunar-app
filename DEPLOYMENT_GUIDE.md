# 🚀 Lunar Event Management - Deployment Guide

This guide covers deploying the Lunar Event Management System to various free cloud platforms with automatic CI/CD using GitHub Actions.

## 📋 Prerequisites

1. **GitHub Repository** - Push your code to GitHub
2. **Cloud Platform Account** - Choose from the platforms below
3. **Environment Variables** - Prepare your secrets

## 🌐 Free Cloud Platforms

### 1. Railway (Recommended)

**Why Railway?**
- ✅ Free tier with 500 hours/month
- ✅ Built-in PostgreSQL database
- ✅ Automatic deployments from GitHub
- ✅ Custom domains
- ✅ Easy environment variable management

#### Step-by-Step Deployment:

1. **Create Railway Account**
   ```bash
   # Visit https://railway.app
   # Sign up with GitHub
   ```

2. **Connect GitHub Repository**
   - Go to Railway Dashboard
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose your repository

3. **Add PostgreSQL Database**
   - In your project, click "New"
   - Select "Database" → "PostgreSQL"
   - Railway will automatically set `DATABASE_URL`

4. **Set Environment Variables**
   ```bash
   # In Railway Dashboard → Variables tab
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=your-super-secret-jwt-key-here
   RAZORPAY_KEY_ID=rzp_test_your_key_id
   RAZORPAY_KEY_SECRET=your_razorpay_secret
   RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-app-password
   APP_BASE_URL=https://your-app.railway.app
   ```

5. **Deploy**
   - Railway automatically builds and deploys
   - Your app will be available at `https://your-app.railway.app`

### 2. Render

**Why Render?**
- ✅ Free tier with 750 hours/month
- ✅ Built-in PostgreSQL
- ✅ Automatic SSL certificates
- ✅ Custom domains

#### Step-by-Step Deployment:

1. **Create Render Account**
   ```bash
   # Visit https://render.com
   # Sign up with GitHub
   ```

2. **Create Web Service**
   - Click "New" → "Web Service"
   - Connect your GitHub repository
   - Use these settings:
     - **Build Command**: `mvn clean package -DskipTests`
     - **Start Command**: `java -jar target/*.jar --spring.profiles.active=prod`
     - **Environment**: Java

3. **Create PostgreSQL Database**
   - Click "New" → "PostgreSQL"
   - Name: `lunar-postgres`
   - Render will set `DATABASE_URL` automatically

4. **Set Environment Variables**
   ```bash
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=your-super-secret-jwt-key-here
   RAZORPAY_KEY_ID=rzp_test_your_key_id
   RAZORPAY_KEY_SECRET=your_razorpay_secret
   RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-app-password
   APP_BASE_URL=https://your-app.onrender.com
   ```

### 3. Heroku

**Why Heroku?**
- ✅ Free tier (with limitations)
- ✅ Easy deployment
- ✅ Add-ons for PostgreSQL

#### Step-by-Step Deployment:

1. **Install Heroku CLI**
   ```bash
   # Windows
   winget install Heroku.HerokuCLI
   
   # macOS
   brew install heroku/brew/heroku
   
   # Linux
   curl https://cli-assets.heroku.com/install.sh | sh
   ```

2. **Login to Heroku**
   ```bash
   heroku login
   ```

3. **Create Heroku App**
   ```bash
   heroku create lunar-event-management
   ```

4. **Add PostgreSQL**
   ```bash
   heroku addons:create heroku-postgresql:hobby-dev
   ```

5. **Set Environment Variables**
   ```bash
   heroku config:set SPRING_PROFILES_ACTIVE=prod
   heroku config:set JWT_SECRET=your-super-secret-jwt-key-here
   heroku config:set RAZORPAY_KEY_ID=rzp_test_your_key_id
   heroku config:set RAZORPAY_KEY_SECRET=your_razorpay_secret
   heroku config:set RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
   heroku config:set MAIL_USERNAME=your-email@gmail.com
   heroku config:set MAIL_PASSWORD=your-app-password
   heroku config:set APP_BASE_URL=https://lunar-event-management.herokuapp.com
   ```

6. **Deploy**
   ```bash
   git push heroku main
   ```

### 4. Vercel

**Why Vercel?**
- ✅ Excellent for full-stack apps
- ✅ Automatic deployments
- ✅ Global CDN

#### Step-by-Step Deployment:

1. **Install Vercel CLI**
   ```bash
   npm i -g vercel
   ```

2. **Login to Vercel**
   ```bash
   vercel login
   ```

3. **Deploy**
   ```bash
   cd lunar-app
   vercel
   ```

4. **Set Environment Variables**
   - Go to Vercel Dashboard
   - Select your project
   - Go to Settings → Environment Variables
   - Add all required variables

## 🔄 GitHub Actions Setup

### 1. Repository Secrets

Go to your GitHub repository → Settings → Secrets and variables → Actions

Add these secrets:

```bash
# Railway
RAILWAY_TOKEN=your_railway_token

# Render
RENDER_API_KEY=your_render_api_key
RENDER_SERVICE_ID=your_service_id

# Heroku
HEROKU_API_KEY=your_heroku_api_key
HEROKU_EMAIL=your_email@example.com

# Vercel
VERCEL_TOKEN=your_vercel_token
VERCEL_ORG_ID=your_org_id
VERCEL_PROJECT_ID=your_project_id
```

### 2. Automatic Deployment

The GitHub Actions workflow will automatically:
- ✅ Run tests on every push
- ✅ Build the application
- ✅ Deploy to your chosen platform
- ✅ Run only on master branch pushes

## 🛠️ Environment Variables Reference

### Required Variables

```bash
# Database
DATABASE_URL=postgresql://user:password@host:port/database

# JWT Security
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Payment (Razorpay)
RAZORPAY_KEY_ID=rzp_test_your_key_id
RAZORPAY_KEY_SECRET=your_razorpay_secret
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Application
APP_BASE_URL=https://your-app-domain.com
SPRING_PROFILES_ACTIVE=prod
```

### Optional Variables

```bash
# CORS
CORS_ORIGINS=https://your-frontend.com,https://your-admin.com

# File Upload
FILE_UPLOAD_DIR=/tmp/uploads

# QR Code
QR_CODE_SIZE=300

# Logging
LOGGING_LEVEL=INFO
```

## 🚀 Quick Start Commands

### Local Development
```bash
# Clone repository
git clone https://github.com/yourusername/lunar-event-management.git
cd lunar-event-management/lunar-app

# Run setup script
chmod +x setup.sh
./setup.sh
```

### Production Deployment
```bash
# Push to GitHub (triggers automatic deployment)
git add .
git commit -m "Deploy to production"
git push origin master
```

## 🔍 Monitoring & Health Checks

### Health Check Endpoints
- **Health**: `GET /api/health`
- **Info**: `GET /api/actuator/info`
- **Metrics**: `GET /api/actuator/metrics`

### Logs
- **Railway**: Dashboard → Deployments → View Logs
- **Render**: Dashboard → Your Service → Logs
- **Heroku**: `heroku logs --tail`
- **Vercel**: Dashboard → Functions → View Logs

## 🛡️ Security Considerations

### Production Checklist
- [ ] Change default JWT secret
- [ ] Use strong database passwords
- [ ] Enable HTTPS only
- [ ] Set up proper CORS origins
- [ ] Configure rate limiting
- [ ] Set up monitoring alerts
- [ ] Regular security updates

### SSL Certificates
All platforms provide automatic SSL certificates:
- **Railway**: Automatic HTTPS
- **Render**: Automatic SSL
- **Heroku**: Automatic SSL
- **Vercel**: Automatic SSL

## 📊 Performance Optimization

### Database Optimization
```sql
-- Add indexes for better performance
CREATE INDEX idx_events_start_date ON events(start_date);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_tickets_booking_id ON tickets(booking_id);
```

### Application Optimization
- Enable caching
- Use connection pooling
- Optimize queries
- Monitor memory usage

## 🆘 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check DATABASE_URL format
   # Ensure database is running
   # Verify credentials
   ```

2. **Build Failed**
   ```bash
   # Check Java version (17+)
   # Verify Maven dependencies
   # Check for compilation errors
   ```

3. **Deployment Failed**
   ```bash
   # Check environment variables
   # Verify platform-specific settings
   # Check logs for errors
   ```

### Support
- Check platform documentation
- Review GitHub Actions logs
- Check application logs
- Create GitHub issue

## 📈 Scaling

### Free Tier Limits
- **Railway**: 500 hours/month
- **Render**: 750 hours/month
- **Heroku**: 550-1000 dyno hours/month
- **Vercel**: 100GB bandwidth/month

### Upgrade Options
- **Railway**: $5/month for unlimited
- **Render**: $7/month for always-on
- **Heroku**: $7/month for hobby
- **Vercel**: $20/month for Pro

---

**🎉 Congratulations!** Your Lunar Event Management System is now deployed and ready to handle real-world traffic!

**Next Steps:**
1. Set up your domain name
2. Configure email templates
3. Set up monitoring
4. Create your first event
5. Start selling tickets!
