# Render Deployment Guide

Your application is now production-ready! Here's how to deploy it to Render:

## Prerequisites
- Your code has been committed to a Git repository (GitHub, GitLab, or Bitbucket)
- You have a Render account at https://render.com

## Deployment Steps

### 1. Create a New Web Service on Render
1. Go to https://dashboard.render.com
2. Click "New +" → "Web Service"
3. Connect your Git repository

### 2. Configure the Service
- **Name**: `billflow-backend` (or your preferred name)
- **Region**: Choose the region closest to your users
- **Branch**: `main` (or your default branch)
- **Root Directory**: Leave empty (use root directory)
- **Runtime**: Java 17
- **Build Command**: `mvn clean package -DskipTests`
- **Start Command**: `java -jar target/billing-backend-1.0.0.jar`

### 3. Environment Variables
Render will automatically read the environment variables from your `render.yaml` file:
- `SPRING_PROFILES_ACTIVE=production`
- `JAVA_OPTS=-Xmx512m -Xms256m`
- `SPRING_DATASOURCE_URL=jdbc:postgresql://ep-spring-water-a4pdzvcy-pooler.us-east-1.aws.neon.tech:5432/battery-pg-instance?sslmode=require&channel_binding=require`
- `SPRING_DATASOURCE_USERNAME=neondb_owner`
- `SPRING_DATASOURCE_PASSWORD=npg_6ma8OnjdxcYb`
- `CORS_ALLOWED_ORIGINS=http://localhost:3000,https://your-frontend-domain.com`

### 4. Deploy
1. Click "Create Web Service"
2. Render will automatically build and deploy your application
3. The deployment may take 5-10 minutes for the first build

### 5. Verify Deployment
Once deployed, your application will be available at:
- URL: `https://your-service-name.onrender.com`
- Health Check: `https://your-service-name.onrender.com/actuator/health`

## Post-Deployment

### Update CORS Configuration
After getting your Render URL, update the CORS configuration:
1. Go to your Render dashboard
2. Navigate to your service
3. Update the `CORS_ALLOWED_ORIGINS` environment variable to include your Render URL:
   ```
   https://your-service-name.onrender.com,http://localhost:3000
   ```

### Monitor Logs
- Check the "Logs" section in your Render dashboard for deployment progress
- Monitor application logs for any issues

## Troubleshooting

### Common Issues:
1. **Build Fails**: Check Java version and Maven build logs
2. **Database Connection**: Verify your Neon PostgreSQL credentials
3. **Health Check Fails**: Ensure `/actuator/health` endpoint is accessible

### Database Connection Test:
Test your database connection by visiting:
`https://your-service-name.onrender.com/actuator/health`

This should return a JSON response with `"status": "UP"` if everything is working correctly.

## Success! 🎉
Your production-ready Spring Boot application is now deployed on Render with:
- ✅ Spring Boot Actuator for monitoring
- ✅ PostgreSQL database connection
- ✅ Health checks for Render
- ✅ Production-optimized configuration
- ✅ CORS support for frontend integration